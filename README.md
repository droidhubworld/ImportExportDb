# ImportExportDb
This  Repository help to Import Export SQLite Database 

[![](https://jitpack.io/v/droidhubworld/ImportExportDb.svg)](https://jitpack.io/#droidhubworld/ImportExportDb)


###### Step 1. Add it in your root build.gradle at the end of repositories:
```
allprojects {
   repositories {
     ...
     maven { url 'https://jitpack.io' }
    }
   }
]
```
 ###### Step 2. Add the dependency
 ```
dependencies {
        implementation 'com.github.droidhubworld:ImportExportDb:v1.2.0'
}

```
 ###### Step 3. Export DB
 ```
DbExporterHelper exportDbUtil = new DbExporterHelper.Builder(getActivity(), SHARED_PREF_NAME + ".xml", backupFolderPath, this).build();
exportDbUtil.exportDb("/data/{your packeg name}/", true);

```
 ###### Export To Zip
 ```
DbExporterHelper exportDbUtil = new DbExporterHelper.Builder(getActivity(), SHARED_PREF_NAME + ".xml", backupFolderPath, this).build();
exportDbUtil.exportToZip("/data/{your packeg name}/", {ZIP FILE NAME}, true);
