package le1.mytube.listeners;

import java.util.ArrayList;

import le1.mytube.mvpModel.playlists.Playlist;

/**
 * Created by Leone on 04/09/17.
 */

public interface OnLoadPlaylistListener {

    void onPlaylistLoaded(ArrayList<Playlist> playlists);

    void onNoPlaylistLoaded();

    void onPlaylistLoadingError();
}
