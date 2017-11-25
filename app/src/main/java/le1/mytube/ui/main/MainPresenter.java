package le1.mytube.ui.main;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import le1.mytube.base.BaseContract;
import le1.mytube.mvpModel.Repo;
import le1.mytube.mvpModel.database.DatabaseConstants;
import le1.mytube.mvpModel.database.song.YouTubeSong;
import le1.mytube.mvpModel.playlists.Playlist;
import le1.mytube.mvpModel.sharedPref.SharedPrefRepo;
import le1.mytube.mvpModel.sharedPref.SharedPrefRepoImpl;

public class MainPresenter extends AndroidViewModel implements MainContract.ViewModel {
    private Repo repository;
    private SharedPrefRepo sharedPrefRepo;
    private MainContract.View contractView;


    public MainPresenter(Application application) {
        super(application);
        this.repository = new Repo(application);
        sharedPrefRepo = new SharedPrefRepoImpl();
    }

    @Override
    public void loadPlaylists() {
        try {
           ArrayList<Playlist> list = (ArrayList<Playlist>) repository.getAllPlaylists();
            if (list.size()>0){
                contractView.onPlaylistLoaded(list);
            }else {
                contractView.onNoPlaylistLoaded();
            }
       }catch (Exception e){
            contractView.onPlaylistLoadingError();
       }

    }

    @Override
    public void loadSharedPreferences() {
        try {
            if (sharedPrefRepo.getAudioFocus()) {
                contractView.onAudioFocusTrue();
            } else {
                contractView.onAudioFocusFalse();
            }
        }catch (Exception e){
            contractView.onAudioFocusLoadingError();
        }
    }


    @Override
    protected void onCleared() {
        super.onCleared();
        repository.onDestroy();
    }


    @Override
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

    @Override
    public void clearDatabase() {
        repository.deleteAllSongs();
        repository.deleteAllPlaylists();
    }

    @Override
    public void addPlaylist(String playlistName) {
        repository.addPlaylist(playlistName);
    }


    @Override
    public boolean isPlaylistNameValid(String playlistName) {
        return !(playlistName.equals("") || repository.getAllPlaylistsName().contains(playlistName.trim().toLowerCase()));
    }

    @Override
    public void deletePlaylist(String playlistName) {
        repository.deletePlaylist(playlistName);
    }

    @Override
    public void showDeleteDialog(Playlist playlist, int position) {
        if (playlist.getName().equals(DatabaseConstants.TB_NAME)){
            contractView.onOfflineDeletePlaylistDialog();
        }else {
            contractView.onStandardDeletePlaylistDialog(playlist, position);
        }
    }

    @Override
    public void setHandleAudioFocus(boolean handleAudioFocus) {
       sharedPrefRepo.setAudioFocus(handleAudioFocus);
    }

    @Override
    public void setContractView(BaseContract.View contractView) {
        this.contractView = (MainContract.View) contractView;
    }
}
