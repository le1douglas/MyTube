package le1.mytube.mvpModel;

import android.content.Context;

import java.util.ArrayList;

import le1.mytube.YouTubeSong;


public interface ModelInterface {

    void addSong(YouTubeSong youTubeSong);

    void updateSong(YouTubeSong oldYoutubeSong, YouTubeSong newYouTubeSong);

    void deleteSong(YouTubeSong youTubeSong);

    void deleteAllSongs();

    void createPlaylist(String playlistName);

    void deletePlaylist(String playlistName);

    void addSongToPlaylist(YouTubeSong youTubeSong, String playlistName);

    void removeSongFromPlaylist(YouTubeSong youTubeSong, String playlistName);

    ArrayList<YouTubeSong> getSongsInPlaylist(String playlistName);

    ArrayList<YouTubeSong> getAllSongs();

    ArrayList<String> getAllPlaylistsName();

    YouTubeSong getSongById(String id);

    void closeDatabase();

    void openDatabase(Context context);
}
