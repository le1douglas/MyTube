package le1.mytube.services.musicService;

import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.Log;
import android.util.SparseArray;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import at.huber.youtubeExtractor.VideoMeta;
import at.huber.youtubeExtractor.YtFile;
import le1.mytube.mvpModel.database.song.YouTubeSong;

/**
 * Created by leone on 10/11/17.
 */

public class YouTubeSongResolutionsUtil {
    private static final String TAG = ("LE1_" + YouTubeSongResolutionsUtil.class.getSimpleName());
    private static List<YouTubeSong> songList = new ArrayList<>();

    /**
     * a YouTubeSong with metadata shared between resolutions
     */
    private static YouTubeSong masterYouTubeSong;

    static List<YouTubeSong> buildResolutionsList(@NonNull SparseArray<YtFile> itags, @NonNull VideoMeta videoMeta) {
        masterYouTubeSong = new YouTubeSong.Builder(videoMeta.getVideoId(), videoMeta.getTitle())
                .duration((int) videoMeta.getVideoLength())
                .imageUri(Uri.parse(videoMeta.getHqImageUrl()))
                .build();

        List<Integer> interestingItags = new ArrayList<>
                (Arrays.asList(140, 160, 133, 134, 135, 136, 137, 298, 299, 264));
        int key;
        for (int i = 0; i < itags.size(); i++) {
            key = itags.keyAt(i);
            if (interestingItags.contains(key)) {

                Log.d(TAG, "(" + i + ")itag at " + key + " = " + (itags.get(key)).getUrl());

                //one of the youtube song that will arrive to the ui
                YouTubeSong yts2add = new YouTubeSong.Builder(videoMeta.getVideoId(), videoMeta.getTitle())
                        .duration((int) videoMeta.getVideoLength())
                        .format((itags.get(key)).getFormat())
                        .imageUri(Uri.parse(videoMeta.getHqImageUrl()))
                        .streamingUri(Uri.parse(itags.get(key).getUrl()))
                        .build();

                songList.add(yts2add);
            }
        }
        return songList;
    }

    static void clearResolutionsList(){
        songList.clear();
    }

    static List<YouTubeSong> getResolutionsList(){
        return songList;
    }

    static YouTubeSong getMasterYouTubeSong(){
        return masterYouTubeSong;
    }
}
