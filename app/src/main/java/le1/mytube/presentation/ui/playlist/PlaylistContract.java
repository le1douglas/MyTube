package le1.mytube.presentation.ui.playlist;

import java.util.List;

import le1.mytube.data.database.playlist.Playlist;
import le1.mytube.data.database.youTubeSong.YouTubeSong;
import le1.mytube.presentation.ui.base.BaseContract;

/**
 * Created by leone on 09/11/17.
 */

interface PlaylistContract extends BaseContract {

    interface View extends BaseContract.View {
        void onSongLoaded(List<YouTubeSong> youTubeSongs);
        void onNoSongLoaded();
        void onSongLoadingError();
        void showOfflineDeleteSongDialog(YouTubeSong youtubeSong, int position);
        void showStandardDeleteSongDialog(YouTubeSong youtubeSong, int position);
    }

    interface ViewModel extends BaseContract.ViewModel {
        @Override
        void setContractView(BaseContract.View contractView);

        void loadSongsInPlaylist(String playlistName);

        void deleteSong(YouTubeSong youtubeSong);

        void removeSongFromPlaylist(Playlist playlist, YouTubeSong youTubeSong);

        void onListLongItemClick(Playlist playlist, YouTubeSong youtubeSong, int position);

        void onListItemClick(YouTubeSong youtubeSong);
    }
}
