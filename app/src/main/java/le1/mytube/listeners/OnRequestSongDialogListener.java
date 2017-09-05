package le1.mytube.listeners;

import le1.mytube.mvpModel.songs.YouTubeSong;

/**
 * Created by Leone on 04/09/17.
 */

public interface OnRequestSongDialogListener {

    void onOfflineDeleteSongDialog(YouTubeSong youTubeSong, int position);

    void onStandardDeleteSongDialog(YouTubeSong youTubeSong, int position);
}
