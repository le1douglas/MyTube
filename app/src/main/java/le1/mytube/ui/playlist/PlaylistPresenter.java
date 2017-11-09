package le1.mytube.ui.playlist;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.content.Intent;

import java.util.ArrayList;

import le1.mytube.application.MyTubeApplication;
import le1.mytube.listeners.OnLoadSongInPlaylistListener;
import le1.mytube.listeners.OnRequestSongDialogListener;
import le1.mytube.mvpModel.Repo;
import le1.mytube.mvpModel.database.DatabaseConstants;
import le1.mytube.mvpModel.database.song.YouTubeSong;
import le1.mytube.mvpModel.playlists.Playlist;
import le1.mytube.ui.musicPlayer.MusicPlayerActivity;

public class PlaylistPresenter extends AndroidViewModel {

    private Repo repository;
    private Application application;

    public PlaylistPresenter(Application application) {
        super(application);
        repository = new Repo(application);
        this.application = application;
    }

    public void loadSongsInPlaylist(String playlistName, OnLoadSongInPlaylistListener onLoadSongInPlaylistListener) {
        try {
            ArrayList<YouTubeSong> youTubeSongs;
            if (playlistName.equals(DatabaseConstants.TB_NAME)) {
                youTubeSongs = (ArrayList<YouTubeSong>) repository.getAllSongs();
            } else {
                youTubeSongs = repository.getSongsInPlaylist(playlistName);
            }

            if (youTubeSongs.size() > 0) {
                onLoadSongInPlaylistListener.onSongLoaded(youTubeSongs);
            } else {
                onLoadSongInPlaylistListener.onNoSongLoaded();

            }
        } catch (Exception e) {
            onLoadSongInPlaylistListener.onSongLoadingError();
            e.printStackTrace();
        }

    }


    public void deleteSong(YouTubeSong youtubeSong, OnLoadSongInPlaylistListener onLoadSongInPlaylistListener) {
        repository.deleteSong(youtubeSong);
        //TODO make getallsongsCount
        if (repository.getAllSongs().size() == 0) {
           onLoadSongInPlaylistListener.onNoSongLoaded();
        }

    }

    public void removeSongFromPlaylist(Playlist playlist, YouTubeSong youTubeSong) {
       /* repository.removeSongFromPlaylist(youTubeSong, playlistName);
        //TODO think about a more lightweight method to get a playlist size
        if (repository.getSongsInPlaylist(playlistName).size() == 0) {
            view.displayNoSongs();
        }*/

    }


    public void onListLongItemClick(Playlist playlist, YouTubeSong youtubeSong, int position, OnRequestSongDialogListener onRequestSongDialogListener) {
        if (playlist.getName().equals(DatabaseConstants.TB_NAME)) {
            onRequestSongDialogListener.onOfflineDeleteSongDialog(youtubeSong, position);
        } else {
            onRequestSongDialogListener.onStandardDeleteSongDialog(youtubeSong, position);
        }
    }

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
