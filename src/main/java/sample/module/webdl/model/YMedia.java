package sample.module.webdl.model;

public class YMedia {
    YAudio audio;
    YVideo video;
    public YMedia(YVideo video, YAudio audio)
    {
        this.video=video;
        this.audio=audio;
    }
    public YAudio getAudio() {
        return audio;
    }

    public void setAudio(YAudio audio) {
        this.audio = audio;
    }

    public YVideo getVideo() {
        return video;
    }

    public void setVideo(YVideo video) {
        this.video = video;
    }
}
