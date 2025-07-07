package testSaveFile;

import HandleStoreFiles.IForSaving;

public class PersonForTestSaving implements IForSaving {
    String id;
    String name;
    public PersonForTestSaving(String id,String name){
        this.id=id;
        this.name=name;
    }

    public PersonForTestSaving() {
    }

    public String getFolderName() {
        return getClass().getPackageName();
    }

    public String getFileName() {
        return getClass().getSimpleName()+id;
    }

    public String getData() {
        return id+","+name;
    }
}

