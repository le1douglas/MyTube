package le1.mytube.mvpViews;

import java.util.ArrayList;

/**
 * Created by Leone on 02/07/17.
 */

public interface MainActivityInterface {

    void displayPlaylist(ArrayList<String> playlists);

    void displayNoPlaylist();

    void displayErrorPlaylist();

    void displayHandleAudioFocus();

    void displayNoHandleAudioFocus();
}
