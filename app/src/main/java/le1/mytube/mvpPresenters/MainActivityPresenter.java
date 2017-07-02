package le1.mytube.mvpPresenters;

import java.util.ArrayList;

import le1.mytube.mvpModel.Repository;
import le1.mytube.mvpViews.MainActivityInterface;

/**
 * Created by Leone on 02/07/17.
 */

public class MainActivityPresenter {
    public MainActivityInterface view;
    public Repository repository;

    public MainActivityPresenter(Repository repository) {
        this.repository = repository;
    }

    public void bind(MainActivityInterface view) {
        this.view = view;
    }

    public void unbind() {
        this.view = null;
    }

    public void loadPlaylists() {
        ArrayList<String> list;
        try {
            list = repository.getAllPlaylistsName();
            if (list.size() > 0) {
                view.displayPlaylist(list);
            } else {
                view.displayNoPlaylist();
            }
        } catch (Exception e) {
            view.displayErrorPlaylist();
            e.printStackTrace();
        }
    }

    public void loadSharedPreferences() {
        if (repository.getHandleAudiofocus()) {
            view.displayHandleAudioFocus();
        } else {
            view.displayNoHandleAudioFocus();
        }


    }
}
