# WiFiADB

![JetBrains IntelliJ Plugins](https://img.shields.io/jetbrains/plugin/v/13156-android-wifiadb)
![JetBrains IntelliJ plugins](https://img.shields.io/jetbrains/plugin/d/13156-android-wifiadb) 
![GitHub](https://img.shields.io/github/license/dengzii/WiFiADB) 
![GitHub](https://badgen.net/badge/icon/awesome?icon=awesome&label)
[![Star](https://badgen.net/github/stars/dengzii/WiFiADB)](#)

A IntelliJ IDE plugin use for connect Android device wireless.

# Installation

### 1. By release plugin file

 [Download Plugin](https://github.com/MrDenua/WiFiADB/releases)

- In your IntelliJ IDE, `Nav > File > Settings > Plugins > click the gear icon > Install plugin from disk`.

- Another way is copy this plugin file into the IDE's installation path `/your_ide_installation_dir/plugin/`, you should unzip plugin to this path, and restart IDE. 

### 2. By IntelliJ Plugin Repository

In your IntelliJ IDE, `Nav > File > Settings > Plugins > Search WiFiADB`

# Build

### Dependencies

IntelliJ IDEA (Community Edition) 19.1+

### Import Project

> This project is not a gradle or pom project, but a ` IntelliJ Platform Plugin` project, IDEA cannot import this project normally.

In IDEA Community (necessary) `File -> New -> Project from Exsiting Sources -> Create project from exsiting sources`, then click next until finish import.

Pressing the `Ctrl + Alt + Shift + S` to open **Project Structure** dialog, choose ** Project**  tab, change ** Project SDK**  to ** IntelliJ IDEA Community Edition IC-xxxx** , then apply change.

Then, edit `GenerateModuleFromTemplate.iml` in the project root directory, change the `type` attribute of module node to `PLUGIN_MODULE`, minimize the IDEA and restore it, the plugin project will be detected.

Finally, `Run -> Edit Configuretions -> Alt + Insert -> Plugin -> Apply`, the project configuration completed.

### Generate Plugin Jar

`Run -> Prepare Plugin Module xxx For Deployment`

# Change Log

**SEE RELEASES FOR MORE.**
- 2.8: Fix: dialog height anomaly.
- 2.7: Update: set the main dialog be modal.
- 2.6: Update: fixed default dialog width.
- 2.5: Feature: delete device config support.
- 2.4: Fix: unable to get device ip address.
- 2.3: Update: compatible with Android 10.
- 2.2: Feature: support configure adb path.
- 2.1: Fix bug: 'Cannot run program "adb"' problem on MacOS.
- 2.0: Update : custom column support, persist ui status, more menu option, more feature.
- 1.2: Fix bug: ui frozen when loading device and connecting device.
- 1.1: Fix bug: can't load device configured.
- 1.0: First release.

## Screenshot

![WiFiADB](https://raw.githubusercontent.com/MrDenua/WiFiADB/master/screen_shot/adb_wifi.png)
