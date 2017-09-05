package le1.mytube.services;

/**
 * Created by Leone on 29/08/17.
 */

public class MusicServiceConstants {

    private static final String ACTION_PREFIX = "le1.mytube.MusicServiceConstants.action.";
    public static final String ACTION_START_STREAMING = ACTION_PREFIX + "song_streaming";
    public static final String ACTION_START_LOCAL = ACTION_PREFIX + "song_local";
    public static final String ACTION_PLAY_PAUSE = ACTION_PREFIX + "play_pause";
    public static final String ACTION_PLAY = ACTION_PREFIX + "play";
    public static final String ACTION_PAUSE = ACTION_PREFIX + "pause";
    public static final String ACTION_REWIND = ACTION_PREFIX +"rewind";
    public static final String ACTION_FAST_FORWARD = ACTION_PREFIX+"fast_foward";
    public static final String ACTION_NEXT = ACTION_PREFIX+"next";
    public static final String ACTION_PREVIOUS = ACTION_PREFIX+"previous";
    public static final String ACTION_STOP = ACTION_PREFIX+"stop";

    public static final String KEY_SONG = "le1.mytube.MusicServiceConstants.key.song";


}
