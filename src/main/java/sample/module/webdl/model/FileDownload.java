package sample.module.webdl.model;

import java.io.File;

public class FileDownload {
    private String name;
    private FileExtensionType fileExtensionType;
    private File file;
    private String url;
    private double contentSize;

    public double getContentSize() {
        return contentSize;
    }

    public void setContentSize(double contentSize) {
        this.contentSize = contentSize;
    }

    public FileExtensionType getFileExtensionType() {
        return fileExtensionType;
    }

    public void setFileExtensionType(FileExtensionType fileExtensionType) {
        this.fileExtensionType = fileExtensionType;
    }



    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }




}
