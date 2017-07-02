package le1.mytube.mvpPresenters;

import java.util.ArrayList;

import le1.mytube.YouTubeSong;
import le1.mytube.mvpModel.Repository;
import le1.mytube.mvpViews.PlaylistInterface;

import static le1.mytube.mvpUtils.DatabaseConstants.TB_NAME;

public class PlaylistPresenter {

    public PlaylistInterface view;
    public Repository repository;

    public PlaylistPresenter(Repository repository) {
        this.repository = repository;
    }

    public void bind(PlaylistInterface view) {
        this.view = view;
    }

    public void unbind() {
        this.view = null;
        repository.closeDatabase();
    }

    public void loadSongsInPlaylist(String playlistName) {
        try {
            ArrayList<YouTubeSong> youTubeSongs;
            if (playlistName.equals(TB_NAME)) {
                youTubeSongs = repository.getAllSongs();
            } else {
                youTubeSongs = repository.getSongsInPlaylist(playlistName);
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
            view.displayErrorSongs();
            e.printStackTrace();
        }

    }

    public void deleteSong(YouTubeSong youtubeSong) {
        repository.deleteSong(youtubeSong);
        //TODO think about a more lightweight method to get a playlist size
        if (repository.getAllSongs().size() == 0) {
            view.displayNoSongs();
        }

    }

    public void removeSongFromPlaylist(YouTubeSong youTubeSong, String playlistName) {
        repository.removeSongFromPlaylist(youTubeSong, playlistName);
        //TODO think about a more lightweight method to get a playlist size
        if (repository.getSongsInPlaylist(playlistName).size() == 0) {
            view.displayNoSongs();
        }

    }

}