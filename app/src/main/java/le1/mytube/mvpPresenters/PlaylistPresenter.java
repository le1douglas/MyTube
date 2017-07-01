package le1.mytube.mvpPresenters;

import java.util.ArrayList;

import le1.mytube.YouTubeSong;
import le1.mytube.mvpModel.ModelInterface;
import le1.mytube.mvpViews.PlaylistInterface;

import static le1.mytube.mvpUtils.DatabaseConstants.TB_NAME;

public class PlaylistPresenter {

    public PlaylistInterface view;
    public ModelInterface model;

    public PlaylistPresenter(ModelInterface model) {
        this.model = model;
    }

    public void bind(PlaylistInterface view) {
        this.view = view;
    }

    public void unbind() {
        this.view = null;
        model.closeDatabase();
    }

    public void loadSongsInPlaylist(String playlistName) {
        try {
            ArrayList<YouTubeSong> youTubeSongs;
            if (playlistName.equals(TB_NAME)) {
                youTubeSongs = model.getAllSongs();
            } else {
                youTubeSongs = model.getSongsInPlaylist(playlistName);
            }

            if (youTubeSongs.size() > 0) {
                view.displaySongs(youTubeSongs);
            } else {
                view.displayNoSongs();

            }
        } catch (Exception e) {
            /**every exception ends here, even {@link  le1.mytube.mvpViews.PlaylistActivity#displaySongs(ArrayList)}
             * and {@link   le1.mytube.mvpViews.PlaylistActivity#displayNoSongs()}
             */
            view.displayError();
            e.printStackTrace();
        }

    }

    public void deleteSong(YouTubeSong youtubeSong) {
        model.deleteSong(youtubeSong);
        //TODO think about a more lightweight method to get a playlist size
        if (model.getAllSongs().size() == 0) {
            view.displayNoSongs();
        }

    }

    public void removeSongFromPlaylist(YouTubeSong youTubeSong, String playlistName) {
        model.removeSongFromPlaylist(youTubeSong, playlistName);
        //TODO think about a more lightweight method to get a playlist size
        if (model.getSongsInPlaylist(playlistName).size() == 0) {
            view.displayNoSongs();
        }

    }
}
