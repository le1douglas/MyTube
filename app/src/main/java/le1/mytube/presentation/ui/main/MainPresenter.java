package le1.mytube.presentation.ui.main;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.content.Context;

import com.samsung.android.sdk.SsdkUnsupportedException;
import com.samsung.android.sdk.look.Slook;

import java.util.ArrayList;

import le1.mytube.data.database.DatabaseConstants;
import le1.mytube.data.database.playlist.Playlist;
import le1.mytube.domain.repos.Repo;
import le1.mytube.presentation.ui.base.BaseContract;

public class MainPresenter extends AndroidViewModel implements MainContract.ViewModel {
    private Repo repository;
    private MainContract.View contractView;
    private Slook slook = new Slook();

    public MainPresenter(Application application) {
        super(application);
        this.repository = new Repo(application);
    }

    @Override
    public void loadPlaylists() {
        try {
            ArrayList<Playlist> list = (ArrayList<Playlist>) repository.getAllPlaylists();
            if (list.size() > 0) {
                contractView.onPlaylistLoaded(list);
            } else {
                contractView.onNoPlaylistLoaded();
            }
        } catch (Exception e) {
            contractView.onPlaylistLoadingError();
        }

    }



    @Override
    protected void onCleared() {
        super.onCleared();
        repository.onDestroy();
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
        if (playlist.getName().equals(DatabaseConstants.TB_NAME)) {
            contractView.onOfflineDeletePlaylistDialog();
        } else {
            contractView.onStandardDeletePlaylistDialog(playlist, position);
        }
    }


    @Override
    public void setContractView(BaseContract.View contractView) {
        this.contractView = (MainContract.View) contractView;
    }

    @Override
    public void initializeEdge(Context context) {
        try {
            slook.initialize(context);
            if (slook.isFeatureEnabled(Slook.COCKTAIL_PANEL)) {
                contractView.onEdgeSupported();
            }else {
                contractView.onEdgeNotSupported(true);
            }
        } catch (SsdkUnsupportedException e) {
            e.printStackTrace();
            if (e.getType()==SsdkUnsupportedException.VENDOR_NOT_SUPPORTED)
                contractView.onEdgeNotSupported(false);
            else if (e.getType() == SsdkUnsupportedException.DEVICE_NOT_SUPPORTED)
                contractView.onEdgeNotSupported(true);
        }
    }
}
