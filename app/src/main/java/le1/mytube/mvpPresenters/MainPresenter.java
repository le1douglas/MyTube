package le1.mytube.mvpPresenters;

import android.Manifest;
import android.app.Activity;
import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.content.Context;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import le1.mytube.listeners.OnCheckValidPlaylistNameListener;
import le1.mytube.listeners.OnLoadAudioFocusListener;
import le1.mytube.listeners.OnLoadPlaylistListener;
import le1.mytube.listeners.OnRequestPlaylistDialogListener;
import le1.mytube.mvpModel.Repo;
import le1.mytube.mvpModel.database.DatabaseConstants;
import le1.mytube.mvpModel.database.song.YouTubeSong;
import le1.mytube.mvpModel.playlists.Playlist;

public class MainPresenter extends AndroidViewModel{
    private Repo repository;
    public MainPresenter(Application application) {
        super(application);
        this.repository = new Repo(application);
    }
     /**
     * @param permission should be one of  Manifest.permission.X
     * @return true if permission is granted, false otherwise*/
    private boolean isPermissionGranted(String permission){
        return ContextCompat.checkSelfPermission(this.getApplication(), permission)
                == PackageManager.PERMISSION_GRANTED;
    }


    public void loadPlaylists(OnLoadPlaylistListener onLoadPlaylistListener) {
        try {
           ArrayList<Playlist> list = (ArrayList<Playlist>) repository.getAllPlaylists();
            if (list.size()>0){
                onLoadPlaylistListener.onPlaylistLoaded(list);
            }else {
                onLoadPlaylistListener.onNoPlaylistLoaded();
            }
       }catch (Exception e){
           onLoadPlaylistListener.onPlaylistLoadingError();
       }

    }

    public void loadSharedPreferences(OnLoadAudioFocusListener onLoadAudioFocusListener) {
        try {
            if (repository.getAudioFocus()) {
                onLoadAudioFocusListener.onAudioFocusTrue();
            } else {
                onLoadAudioFocusListener.onAudioFocusFalse();
            }
        }catch (Exception e){
            onLoadAudioFocusListener.onAudioFocusLoadingError();
        }

    }


    @Override
    protected void onCleared() {
        super.onCleared();
        repository.onDestroy();
    }

    public String logDatabase() {
        List<String> playlists = repository.getAllPlaylistsName();
        for (int i = 0; i < playlists.size(); i++) {
            Log.d("DBoperation", playlists.get(i));
        }
        Log.d("DBoperation", "-----Offline----");
        for (YouTubeSong song : repository.getAllSongs()) {
            Log.d("DBoperation", song.toString());
        }

        return "qulo";

    }

    public void clearDatabase() {
        repository.deleteAllSongs();
        repository.deleteAllPlaylists();
    }

    public void addPlaylist(String playlistName) {
        repository.addPlaylist(playlistName);
    }

    public void checkValidPlaylistName(String playlistName, OnCheckValidPlaylistNameListener onCheckValidPlaylistNameListener) {
        if (playlistName.equals("") || repository.getAllPlaylistsName().contains(playlistName.trim().toLowerCase())) {
            onCheckValidPlaylistNameListener.onPlaylistNameInvalid();
        } else {
            onCheckValidPlaylistNameListener.onPlaylistNameValid();
        }
    }

    public void deletePlaylist(String playlistName) {
        repository.deletePlaylist(playlistName);
    }


    public void onNewPlaylistButtonCLick(OnRequestPlaylistDialogListener onRequestDialogListener) {
       onRequestDialogListener.onNewPlaylistDialog();
    }

    public void onItemLongClick(Playlist playlist, int position, OnRequestPlaylistDialogListener onRequestDialogListener) {
        if (playlist.getName().equals(DatabaseConstants.TB_NAME)){
            onRequestDialogListener.onOfflineDeletePlaylistDialog();
        }else {
            onRequestDialogListener.onStandardDeletePlaylistDialog(playlist, position);
        }
    }

    public void setHandleAudioFocus(boolean handleAudioFocus) {
       repository.setAudioFocus(handleAudioFocus);
    }

    public void startMusicService(Activity activity) {
     //  repository.connectToMusicService(activity);
    }

}
