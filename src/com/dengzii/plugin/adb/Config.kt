package com.dengzii.plugin.adb

import com.intellij.ide.util.PropertiesComponent
import com.sun.jna.StringArray

/**
 * <pre>
 * author : dengzi
 * e-mail : denua@foxmail.com
 * github : https://github.com/MrDenua
 * time   : 2019/10/10
 * desc   :
 * </pre>
 */

object Config {

    fun getDevices(): List<Device> {

        val pro = PropertiesComponent.getInstance();
        val (a) = Device()

        return listOf()
    }
}

