# ImportExportDb
This  Repository help to Import Export SQLite Database 

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
        implementation 'com.github.droidhubworld:ImportExportDb:0.0.1'
}

```
 ###### Step 2. Export DB
 ```
DbExporterHelper exportDbUtil = new DbExporterHelper.Builder(getActivity(), SHARED_PREF_NAME + ".xml", backupFolderPath, this).build();
exportDbUtil.exportDb("/data/{your packeg name}/", true);
