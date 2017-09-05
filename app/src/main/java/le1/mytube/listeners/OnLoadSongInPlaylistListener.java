package le1.mytube.listeners;

import java.util.ArrayList;

import le1.mytube.mvpModel.songs.YouTubeSong;

/**
 * Created by Leone on 04/09/17.
 */

public interface OnLoadSongInPlaylistListener {
    void onSongLoaded(ArrayList<YouTubeSong> songsInPlaylist);

    void onNoSongLoaded();

    void onSongLoadingError();

}
