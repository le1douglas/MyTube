package le1.mytube.presentation.ui.playlist;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.content.Intent;

import java.util.ArrayList;

import le1.mytube.data.database.DatabaseConstants;
import le1.mytube.data.database.playlist.Playlist;
import le1.mytube.data.database.youTubeSong.YouTubeSong;
import le1.mytube.domain.application.MyTubeApplication;
import le1.mytube.domain.repos.Repo;
import le1.mytube.presentation.ui.base.BaseContract;
import le1.mytube.presentation.ui.musicPlayer.MusicPlayerActivity;

public class PlaylistPresenter extends AndroidViewModel implements PlaylistContract.ViewModel {

    private Repo repository;
    private Application application;
    private PlaylistContract.View contractView;

    public PlaylistPresenter(Application application) {
        super(application);
        repository = new Repo(application);
        this.application = application;
    }

    @Override
    public void setContractView(BaseContract.View contractView) {
        this.contractView = (PlaylistContract.View) contractView;
    }

    @Override
    public void loadSongsInPlaylist(String playlistName) {
        try {
            ArrayList<YouTubeSong> youTubeSongs;
            if (playlistName.equals(DatabaseConstants.TB_NAME)) {
                youTubeSongs = (ArrayList<YouTubeSong>) repository.getAllSongs();
            } else {
                youTubeSongs = repository.getSongsInPlaylist(playlistName);
            }

            if (youTubeSongs.size() > 0) {
                contractView.onSongLoaded(youTubeSongs);
            } else {
                contractView.onNoSongLoaded();

            }
        } catch (Exception e) {
            contractView.onSongLoadingError();
            e.printStackTrace();
        }

    }

    @Override
    public void deleteSong(YouTubeSong youtubeSong) {
        repository.deleteSong(youtubeSong);
        //TODO make getallsongsCount
        if (repository.getAllSongs().size() == 0) {
           contractView.onNoSongLoaded();
        }

    }

    @Override
    public void removeSongFromPlaylist(Playlist playlist, YouTubeSong youTubeSong) {
       /* repository.removeSongFromPlaylist(youTubeSong, playlistName);
        if (repository.getSongsInPlaylist(playlistName).size() == 0) {
            view.displayNoSongs();
        }*/

    }


    @Override
    public void onListLongItemClick(Playlist playlist, YouTubeSong youtubeSong, int position) {
        if (playlist.getName().equals(DatabaseConstants.TB_NAME)) {
            contractView.showOfflineDeleteSongDialog(youtubeSong, position);
        } else {
            contractView.showStandardDeleteSongDialog(youtubeSong, position);
        }
    }

    @Override
    public void onListItemClick(YouTubeSong youtubeSong) {
        Intent i =new Intent(application, MusicPlayerActivity.class);
        i.putExtra(MyTubeApplication.KEY_SONG, youtubeSong);
        application.startActivity(i);
    }


    @Override
    protected void onCleared() {
        super.onCleared();
        repository.onDestroy();
    }



}
