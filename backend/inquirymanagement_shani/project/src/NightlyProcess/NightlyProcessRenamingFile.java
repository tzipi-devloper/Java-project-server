package NightlyProcess;
import HandleStoreFiles.IForSaving;

import java.io.*;

public class NightlyProcessRenamingFile extends Thread{
    private String path;
    private String text;
    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public NightlyProcessRenamingFile(String path, String text) {
        this.path = path;
        this.text = text;
    }

    @Override
    public void run() {
        File folder = new File(path);
        if (!folder.exists() || !folder.isDirectory()) {
            System.out.println("error to find the Directory ");
            return;
        }
        File[] files = folder.listFiles();
        if (files == null) return;
        for(File file:files) {
            if (file.isFile()) {
                File newFile = new File(path + "\\" + text + file.getName());
                boolean changeName = file.renameTo(newFile);
                if (changeName) {
                    System.out.println("renamed to " + newFile.getName());
                } else {
                    System.out.println("error to rename: " + file.getName());
                }
            }
        }
    }
}
