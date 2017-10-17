package le1.mytube.mvpModel.database.iTag;

import android.arch.persistence.room.Entity;

/**
 * Created by leone on 16/10/17.
 */@Entity
public class ITag {

    private enum VIDEO_CONTAINER {
        THREEGP,
        MP4,
        WEBM,
        M4A,
        TS
    }

    private enum VIDEO_ENCODING{
        MPEG_4_Visual,
        H264,
        VP8,
        VP9,
    }

    private enum VIDEO_PROFILE{
        SIMPLE,
        BASELINE,
        HIGH,
        UNKNOWN,
        MAIN,
        PROFILE_0,
        PROFILE_2,
    }

    private enum AUDIO_ENCODING{
        AAC,
        VORBIS,
        OPUS,
    }

    private int value;
    private boolean isDash;
    private VIDEO_CONTAINER container;
    private int videoResolution;
    private boolean isHFR;
    private boolean isHDR;
    private VIDEO_ENCODING videoEncoding;
    private VIDEO_PROFILE videoProfile;
    private float videoBitrate;
    private AUDIO_ENCODING audioEncoding;
    private int audioBitrate;

    private ITag(int value,
                 boolean isDash,
                 VIDEO_CONTAINER container,
                 int videoResolution,
                 boolean isHFR,
                 boolean isHDR,
                 VIDEO_ENCODING videoEncoding,
                 VIDEO_PROFILE videoProfile,
                 Float videoBitrate,
                 AUDIO_ENCODING audioEncoding,
                 Integer audioBitrate) {

        this.value = value;
        this.isDash = isDash;
        this.container = container;
        this.videoResolution = videoResolution;
        this.isHFR = isHFR;
        this.isHDR = isHDR;
        this.videoEncoding = videoEncoding;
        this.videoProfile = videoProfile;
        this.videoBitrate = videoBitrate;
        this.audioEncoding = audioEncoding;
        this.audioBitrate = audioBitrate;
    }

    public int getValue() {
        return value;
    }

    public boolean isDash() {
        return isDash;
    }

    public int getVideoResolution() {
        return videoResolution;
    }

    public boolean isHFR() {
        return isHFR;
    }

    public boolean isHDR() {
        return isHDR;
    }

    public float getVideoBitrate() {
        return videoBitrate;
    }

    public int getAudioBitrate() {
        return audioBitrate;
    }

    public VIDEO_CONTAINER getContainer() {
        return container;
    }

    public VIDEO_ENCODING getVideoEncoding() {
        return videoEncoding;
    }

    public VIDEO_PROFILE getVideoProfile() {
        return videoProfile;
    }

    public AUDIO_ENCODING getAudioEncoding() {
        return audioEncoding;
    }

    /**
    * data taken from https://en.wikipedia.org/w/index.php?title=YouTube&oldid=800910021#Quality_and_formats
     */

    public ITag get(int value){
        switch (value){
            case 17:
                return new ITag(value, false, VIDEO_CONTAINER.THREEGP, 144, false, false,
                VIDEO_ENCODING.MPEG_4_Visual, VIDEO_PROFILE.SIMPLE, 0.05f, AUDIO_ENCODING.AAC, 24);
            case 36:
                return new ITag(value, false, VIDEO_CONTAINER.THREEGP, 240, false, false,
                        VIDEO_ENCODING.MPEG_4_Visual, VIDEO_PROFILE.SIMPLE, 0.175f, AUDIO_ENCODING.AAC, 32);
            case 18:
                return new ITag(value, false, VIDEO_CONTAINER.MP4, 360, false, false,
                        VIDEO_ENCODING.H264, VIDEO_PROFILE.BASELINE, 0.5f, AUDIO_ENCODING.AAC, 96);
            case 22:
                return new ITag(value, false, VIDEO_CONTAINER.MP4, 720, false, false,
                        VIDEO_ENCODING.H264, VIDEO_PROFILE.HIGH, 2f, AUDIO_ENCODING.AAC, 192);
            case 43:
                return new ITag(value, false, VIDEO_CONTAINER.WEBM, 360, false, false,
                        VIDEO_ENCODING.VP8, VIDEO_PROFILE.UNKNOWN, 0.5f, AUDIO_ENCODING.VORBIS, 128);


            case 160:
                return new ITag(value, true, VIDEO_CONTAINER.MP4, 144, false, false,
                        VIDEO_ENCODING.H264, VIDEO_PROFILE.MAIN, 0.1f, null, null);
            case 133:
                return new ITag(value, true, VIDEO_CONTAINER.MP4, 240, false, false,
                        VIDEO_ENCODING.H264, VIDEO_PROFILE.MAIN, 0.2f, null, null);
            case 134:
                return new ITag(value, true, VIDEO_CONTAINER.MP4, 360, false, false,
                        VIDEO_ENCODING.H264, VIDEO_PROFILE.MAIN, 0.5f, null, null);
            case 135:
                return new ITag(value, true, VIDEO_CONTAINER.MP4, 480, false, false,
                        VIDEO_ENCODING.H264, VIDEO_PROFILE.MAIN, 0.9f, null, null);
            case 136:
                return new ITag(value, true, VIDEO_CONTAINER.MP4, 720, false, false,
                        VIDEO_ENCODING.H264, VIDEO_PROFILE.MAIN, 1.8f, null, null);


            default:
                throw new IllegalArgumentException("iTag with value " + value+ " does not exists");
        }
    }

}
