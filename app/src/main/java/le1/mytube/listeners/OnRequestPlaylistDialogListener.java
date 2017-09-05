package le1.mytube.listeners;

import le1.mytube.mvpModel.playlists.Playlist;

/**
 * Created by Leone on 04/09/17.
 */

public interface OnRequestPlaylistDialogListener {

        void onNewPlaylistDialog();

        void onOfflineDeletePlaylistDialog();

        void onStandardDeletePlaylistDialog(Playlist playlist, int position);

}
