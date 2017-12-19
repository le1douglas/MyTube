package le1.mytube.presentation.ui.main;

import android.content.Context;

import java.util.ArrayList;

import le1.mytube.data.database.playlist.Playlist;
import le1.mytube.presentation.ui.base.BaseContract;

interface MainContract extends BaseContract {

    interface View extends BaseContract.View{
        void onPlaylistLoaded(ArrayList<Playlist> playlists);
        void onNoPlaylistLoaded();
        void onPlaylistLoadingError();

        void onOfflineDeletePlaylistDialog();
        void onStandardDeletePlaylistDialog(Playlist playlist, int position);

        void onEdgeSupported();
        void onEdgeNotSupported(boolean isSamsungDevice);
    }

    interface ViewModel extends BaseContract.ViewModel {
        @Override
        void setContractView(BaseContract.View contractView);

        void loadPlaylists();

        void deletePlaylist(String playlistName);
        void addPlaylist(String playlistName);

        boolean isPlaylistNameValid(String playlistName);

        void showDeleteDialog(Playlist playlist, int position);

        void initializeEdge(Context context);

        String getQueueLog();
    }
}
