package le1.mytube.mvpViews;

import java.util.ArrayList;

import le1.mytube.YouTubeSong;

/**
 * Created by Leone on 30/06/17.
 */

public interface PlaylistInterface {

    void displaySongs(ArrayList<YouTubeSong> songList);

    void displayNoSongs();

    void displayErrorSongs();

}
