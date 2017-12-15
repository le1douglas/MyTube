package le1.mytube.data.database.playlist;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.widget.Toast;

import java.util.ArrayList;

import le1.mytube.data.database.youTubeSong.YouTubeSong;

/**
 * Created by Leone on 25/08/17.
 */

public class PlaylistDatabaseImpl implements PlaylistDatabase {

    private static PlaylistDatabase INSTANCE;

    private ContentResolver resolver;
    private Context context;
    private String[] columns;
    private final Uri PLAYLISTS_URI = MediaStore.Audio.Playlists.EXTERNAL_CONTENT_URI;

    private PlaylistDatabaseImpl(Context context) {
        this.context = context;
        resolver = this.context.getContentResolver();
        columns = new String[]{
                MediaStore.Audio.Playlists._ID,
                MediaStore.Audio.Playlists.NAME,
                MediaStore.Audio.Playlists.DATA,
                MediaStore.Audio.Playlists.DATE_ADDED,
                MediaStore.Audio.Playlists.DATE_MODIFIED
        };
    }

    public static PlaylistDatabase getDatabase(Context context) {
        if (INSTANCE == null) INSTANCE = new PlaylistDatabaseImpl(context);
        return INSTANCE;
    }

    @Override
    public ArrayList<Playlist> getAllPlaylists() {
        ArrayList<Playlist> playlistList = new ArrayList<>();
        final Cursor playLists = resolver.query(PLAYLISTS_URI, columns, null, null, null);
        if (playLists != null) {
            for (boolean hasItem = playLists.moveToFirst(); hasItem; hasItem = playLists.moveToNext()) {
                playlistList.add(new Playlist(
                        playLists.getInt(playLists.getColumnIndex(columns[0])),
                        playLists.getString(playLists.getColumnIndex(columns[1])),
                        playLists.getString(playLists.getColumnIndex(columns[2])),
                        playLists.getInt(playLists.getColumnIndex(columns[3])),
                        playLists.getInt(playLists.getColumnIndex(columns[4]))));
            }
            playLists.close();
        }
        return playlistList;

    }


    @Override
    public Playlist getPlaylistByName(String name) {
        Playlist playlist;
        final Cursor playLists = resolver.query(PLAYLISTS_URI, columns, columns[1] + " LIKE '" + name + "'", null, null);
        if (playLists != null && playLists.moveToFirst()) {
            playlist = new Playlist(
                    playLists.getInt(playLists.getColumnIndex(columns[0])),
                    playLists.getString(playLists.getColumnIndex(columns[1])),
                    playLists.getString(playLists.getColumnIndex(columns[2])),
                    playLists.getInt(playLists.getColumnIndex(columns[3])),
                    playLists.getInt(playLists.getColumnIndex(columns[4])));
            playLists.close();
            return playlist;
        }
        return null;
    }

    @Override
    public int getAllPlaylistCount() {
        final Cursor playLists = resolver.query(PLAYLISTS_URI, columns, null, null, null);
        if (playLists == null) {
            return 0;
        } else {
            int size = playLists.getCount();
            playLists.close();
            return size;
        }

    }

    @Override
    public void addPlaylist(String... playlistNames) {
        ContentValues v = new ContentValues();
        Uri uri;
        for (String playlistName : playlistNames) {
            v.put(MediaStore.Audio.Playlists.NAME, playlistName);
            uri = resolver.insert(PLAYLISTS_URI, v);
            if (uri == null) {
                Toast.makeText(context, "Error Creating Playlist", Toast.LENGTH_SHORT).show();
            } else {
                // necessary because somehow the MediaStoreObserver is not notified when adding a playlist
                resolver.notifyChange(Uri.parse("content://media"), null);
                Toast.makeText(context, "playlist created", Toast.LENGTH_SHORT).show();
            }
        }

    }

    @Override
    public void deletePlaylist(String... playlistNames) {
        try {

            final StringBuilder selection = new StringBuilder();
            selection.append(MediaStore.Audio.Playlists.NAME + (" IN ("));
            for (int i = 0; i < playlistNames.length; i++) {
                selection.append("'").append(playlistNames[i]).append("'");
                if (i < playlistNames.length - 1) {
                    selection.append(",");
                }
            }
            selection.append(")");


            int rowDeleted = resolver.delete(PLAYLISTS_URI, selection.toString(), null);

            if (playlistNames.length != rowDeleted) {
                Toast.makeText(context, "error in deleting " + String.valueOf(playlistNames.length - rowDeleted) + " playlist(s)", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(context, "playlist(s) deleted", Toast.LENGTH_SHORT).show();
            }

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(context, "error in deleting playlist(s)", Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public void deleteAllPlaylist() {
        int rowDeleted = resolver.delete(PLAYLISTS_URI, null, null);
        Toast.makeText(context, "deleted " + String.valueOf(rowDeleted) + " playlists", Toast.LENGTH_SHORT).show();

    }

    @Override
    public ArrayList<String> getAllPlaylistsName() {
        ArrayList<String> playlistNames = new ArrayList<>();
        final Cursor playLists = resolver.query(PLAYLISTS_URI, columns, null, null, null);
        if (playLists != null) {
            for (boolean hasItem = playLists.moveToFirst(); hasItem; hasItem = playLists.moveToNext()) {
                playlistNames.add(
                        playLists.getString(playLists.getColumnIndex(columns[1])));
            }
            playLists.close();
        }
        return playlistNames;
    }

    String audio_id = MediaStore.Audio.Playlists.Members.AUDIO_ID;
    String artist = MediaStore.Audio.Playlists.Members.ARTIST;
    String title = MediaStore.Audio.Playlists.Members.TITLE;
    String duration = MediaStore.Audio.Playlists.Members.DURATION;
    String location = MediaStore.Audio.Playlists.Members.DATA;

    @Override
    public ArrayList<YouTubeSong> getAllSongInPlaylist(String playlistName) {
        ArrayList<YouTubeSong> songList = new ArrayList<>();
        Uri newuri = MediaStore.Audio.Playlists.Members.getContentUri(
                "external", getPlaylistByName(playlistName).getId());

        String[] columns = {audio_id, duration, title, artist,
                location};
        Cursor c = resolver.query(newuri, columns, null, null, null);

        if (c != null && c.moveToFirst()) {
            for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
                songList.add(
                        new YouTubeSong.Builder(c.getString(c.getColumnIndex(audio_id)), c.getString(c.getColumnIndex(title)))
                                .path(c.getString(c.getColumnIndex(location)))
                                .startTime(0)
                                .endTime(c.getInt(c.getColumnIndex(duration)))
                                .build());
            }
        }
        return songList;
    }

}
