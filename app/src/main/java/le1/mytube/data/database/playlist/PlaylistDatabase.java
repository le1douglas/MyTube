package le1.mytube.data.database.playlist;

import java.util.ArrayList;

import le1.mytube.data.database.youTubeSong.YouTubeSong;

/**
 * Created by Leone on 25/08/17.
 */

public interface PlaylistDatabase {

    ArrayList<Playlist> getAllPlaylists();

    Playlist getPlaylistByName(String name);

    int getAllPlaylistCount();

    void addPlaylist(String... names);

    void deletePlaylist(String... names);

    void deleteAllPlaylist();

    ArrayList<String> getAllPlaylistsName();

    ArrayList<YouTubeSong> getAllSongInPlaylist(String name);
}
