package le1.mytube.ui.main;

import java.util.ArrayList;

import le1.mytube.base.BaseContract;
import le1.mytube.mvpModel.playlists.Playlist;

interface MainContract extends BaseContract {

    interface View extends BaseContract.View{
        void onPlaylistLoaded(ArrayList<Playlist> playlists);
        void onNoPlaylistLoaded();
        void onPlaylistLoadingError();

        void onAudioFocusTrue();
        void onAudioFocusFalse();
        void onAudioFocusLoadingError();


        void onOfflineDeletePlaylistDialog();
        void onStandardDeletePlaylistDialog(Playlist playlist, int position);
    }

    interface ViewModel extends BaseContract.ViewModel {
        @Override
        void setContractView(BaseContract.View contractView);

        void loadPlaylists();
        void loadSharedPreferences();

        String logDatabase();
        void clearDatabase();

        void deletePlaylist(String playlistName);
        void addPlaylist(String playlistName);

        boolean isPlaylistNameValid(String playlistName);

        void showDeleteDialog(Playlist playlist, int position);

        void setHandleAudioFocus(boolean handleAudioFocus);
    }
}
