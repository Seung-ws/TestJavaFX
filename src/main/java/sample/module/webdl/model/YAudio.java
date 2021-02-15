package sample.module.webdl.model;

public class YAudio {
    private String name;
    private String youtubeId;
    private String downloadUrl;
    private String mimeType;
    private FileExtensionType fileExtensionType;
    private String audioSampleRate;

    private String audioChannels;
    private int contentSize;

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



    public String getAudioChannels() {
        return audioChannels;
    }

    public void setAudioChannels(String audioChannels) {
        this.audioChannels = audioChannels;
    }

    public String getAudioSampleRate() {
        return audioSampleRate;
    }

    public void setAudioSampleRate(String audioSampleRate) {
        this.audioSampleRate = audioSampleRate;
    }






}
