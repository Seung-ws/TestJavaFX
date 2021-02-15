package sample.module.webdl.model;

public class YVideo {
    private String name;
    private String youtubeId;
    private String downloadUrl;
    private String mimeType;

    private String fps;
    private String qualityLabel;
    private    int contentSize;
    private FileExtensionType fileExtensionType;
    public FileExtensionType getFileExtensionType() {
        return fileExtensionType;
    }

    public void setFileExtensionType(FileExtensionType fileExtensionType) {
        this.fileExtensionType = fileExtensionType;
    }


    public int getContentSize() {
        return contentSize;
    }

    public void setContentSize(int contentSize) {
        this.contentSize = contentSize;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getYoutubeId() {
        return youtubeId;
    }

    public void setYoutubeId(String youtubeId) {
        this.youtubeId = youtubeId;
    }

    public String getDownloadUrl() {
        return downloadUrl;
    }

    public void setDownloadUrl(String downloadUrl) {
        this.downloadUrl = downloadUrl;
    }

    public String getMimeType() {
        return mimeType;
    }

    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }



    public String getFps() {
        return fps;
    }

    public void setFps(String fps) {
        this.fps = fps;
    }

    public String getQualityLabel() {
        return qualityLabel;
    }

    public void setQualityLabel(String qualityLabel) {
        this.qualityLabel = qualityLabel;
    }
}
