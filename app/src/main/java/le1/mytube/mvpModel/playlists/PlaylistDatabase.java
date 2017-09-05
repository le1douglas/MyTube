package le1.mytube.mvpModel.playlists;

import java.util.List;

/**
 * Created by Leone on 25/08/17.
 */

public interface PlaylistDatabase {

    List<Playlist> getAllPlaylists();

    Playlist getPlaylistByName(String name);

    int getAllPlaylistCount();

    void addPlaylist(String... names);

    void deletePlaylist(String... names);

    void deleteAllPlaylist();

    List<String> getAllPlaylistsName();
}
