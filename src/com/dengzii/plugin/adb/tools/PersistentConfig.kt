package com.dengzii.plugin.adb.tools

import com.dengzii.plugin.adb.tools.PersistentConfig.ObjectSerializer
import com.google.gson.Gson
import com.google.gson.JsonParseException
import com.intellij.ide.util.PropertiesComponent
import com.intellij.openapi.project.Project
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KClass
import kotlin.reflect.KProperty

/**
 * Make data persistence more easy.
 *
 * Delegate field's getter/setter to [PropertiesComponent], the complex type will serialize/deserialize
 * use gson by default, you can custom [ObjectSerializer] instead it.
 *
 * @param propertiesComp    The [PropertiesComponent]
 * @param project           The project use for obtain [PropertiesComponent], ignored when propertiesComp is non-null.
 * @param keySuffix         The key suffix use for persist.
 * @param objectSerializer  The serialize/deserialize factory for complex type.
 *
 * @author https://github.com/dengzii
 */
open class PersistentConfig(
        propertiesComp: PropertiesComponent? = null,
        project: Project? = null,
        private val keySuffix: String = "KEY",
        private val objectSerializer: ObjectSerializer = JsonObjectSerializer()
) {

    private val propertiesComponent: PropertiesComponent = propertiesComp ?: if (project == null) {
        PropertiesComponent.getInstance()
    } else {
        PropertiesComponent.getInstance(project)
    }

    /**
     * Return the delegate for field need to persist.
     *
     * @param defaultValue  The default when load value failed.
     * @param keyName       The key name use for persist.
     *
     * @return [PropertyDelegate]
     */
    inline fun <reified T> persistentProperty(defaultValue: T, keyName: String? = null): PropertyDelegate<T> {
        return when (defaultValue) {
            is Int?,
            is Boolean?,
            is Float?,
            is String?,
            is Array<*>? -> {
                PropertyDelegate(defaultValue, T::class, keyName)
            }
            else -> PropertyDelegate(defaultValue, T::class, keyName)
        }
    }

    /**
     * The interface defines how to serializer/deserializer the object not primitive type.
     */
    interface ObjectSerializer {
        fun serialize(obj: Any, clazz: KClass<*>): String
        fun deserialize(str: String, clazz: KClass<*>): Any
    }

    /**
     * The json serializer/deserializer use gson.
     */
    class JsonObjectSerializer : ObjectSerializer {

        private val gson: Gson = Gson().newBuilder()
                .setLenient()
                .serializeNulls()
                .create()

        @Throws(JsonParseException::class)
        override fun serialize(obj: Any, clazz: KClass<*>): String {
            return gson.toJson(obj)
        }

        @Throws(JsonParseException::class)
        override fun deserialize(str: String, clazz: KClass<*>): Any {
            return gson.fromJson(str, clazz.java)
        }
    }

    /**
     * This class is a delegate class for a field need to persist.
     *
     * @param default   The default when read property failed, the following situation will return [default]:
     *                      1, The key does not exist.
     *                      2, Read value successful but serialize/deserialize failed.
     *                      3, An exception was caught.
     * @param clazz     The KClass of field.
     * @param keyName   The key name, the field name is used when null.
     */
    inner class PropertyDelegate<T : Any?>
    constructor(
            private val default: T,
            private val clazz: KClass<*>,
            private val keyName: String? = null
    ) : ReadWriteProperty<PersistentConfig, T?> {

        @Suppress("UNCHECKED_CAST")
        override fun getValue(thisRef: PersistentConfig, property: KProperty<*>): T? {
            val keyName = getKeyName(property)
            return try {
                with(thisRef.propertiesComponent) {
                    when (clazz) {
                        Int::class -> getInt(keyName, default as Int)
                        Boolean::class -> getBoolean(keyName, default as Boolean)
                        String::class -> getValue(keyName, default as String)
                        Float::class -> getFloat(keyName, default as Float)
                        Array::class -> getValues(keyName)
                        // deserialize to object
                        else -> {
                            val v = getValue(keyName)
                            if (v != null) {
                                objectSerializer.deserialize(v, clazz)
                            } else {
                                default
                            }
                        }
                    }
                } as T
            } catch (ignore: TypeCastException) {
                default
            } catch (e: Throwable) {
                e.printStackTrace()
                default
            }
        }

        override fun setValue(thisRef: PersistentConfig, property: KProperty<*>, value: T?) {
            val keyName = getKeyName(property)
            with(thisRef.propertiesComponent) {
                when (value) {
                    is Int -> setValue(keyName, value, default as Int)
                    is Boolean -> setValue(keyName, value, default as Boolean)
                    is String -> setValue(keyName, value, default as String)
                    is Float -> setValue(keyName, value, default as Float)
                    is Array<*> -> {
                        val arr = value.filterIsInstance<String>().toTypedArray()
                        setValues(keyName, arr)
                    }
                    null -> unsetValue(keyName)
                    else -> {
                        try {
                            val serialized = objectSerializer.serialize(value, clazz)
                            setValue(keyName, serialized)
                        } catch (e: Throwable) {
                            throw RuntimeException("Type unsupported.", e)
                        }
                    }
                }
            }
        }

        private fun getKeyName(property: KProperty<*>): String {
            return "${keySuffix}_${keyName ?: property.name}"
        }
    }
}