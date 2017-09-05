package le1.mytube.mvpModel;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import le1.mytube.listeners.OnExecuteTaskCallback;
import le1.mytube.mvpModel.playlists.Playlist;
import le1.mytube.mvpModel.playlists.PlaylistDatabase;
import le1.mytube.mvpModel.playlists.PlaylistDatabaseImpl;
import le1.mytube.mvpModel.songs.SongDatabase;
import le1.mytube.mvpModel.songs.YouTubeSong;
import le1.mytube.services.MusicService;

import static le1.mytube.services.MusicServiceConstants.ACTION_FAST_FORWARD;
import static le1.mytube.services.MusicServiceConstants.ACTION_NEXT;
import static le1.mytube.services.MusicServiceConstants.ACTION_PAUSE;
import static le1.mytube.services.MusicServiceConstants.ACTION_PLAY;
import static le1.mytube.services.MusicServiceConstants.ACTION_PLAY_PAUSE;
import static le1.mytube.services.MusicServiceConstants.ACTION_PREVIOUS;
import static le1.mytube.services.MusicServiceConstants.ACTION_REWIND;
import static le1.mytube.services.MusicServiceConstants.ACTION_START_LOCAL;
import static le1.mytube.services.MusicServiceConstants.ACTION_START_STREAMING;
import static le1.mytube.services.MusicServiceConstants.ACTION_STOP;
import static le1.mytube.services.MusicServiceConstants.KEY_SONG;


/**
 * Created by Leone on 18/08/17.
 */

//TODO see if implemeting dao works
public class Repo {

    private SongDatabase songDatabase;
    private PlaylistDatabase playlistDatabase;
    private Context context;
    private AutocompleteTask autocompleteTask;
    OnExecuteTaskCallback onExecuteTaskCallback;

    public Repo(Context context) {
        this.context = context;
        songDatabase = SongDatabase.getDatabase(this.context);
        playlistDatabase = PlaylistDatabaseImpl.getDatabase(this.context);
    }


    //--------DATABASE
    public void onDestroy() {
        songDatabase.close();
        context = null;
    }


    public void addSongs(YouTubeSong... youTubeSongs) {
        songDatabase.youTubeSongDao().addSongs(youTubeSongs);
    }

    public void updateSong(YouTubeSong... youTubeSongs) {
        songDatabase.youTubeSongDao().updateSong(youTubeSongs);
    }

    public void deleteSong(YouTubeSong youTubeSong) {
        songDatabase.youTubeSongDao().deleteSong(youTubeSong);
    }

    public void deleteAllSongs() {
        songDatabase.youTubeSongDao().deleteAllSongs();
    }

    public YouTubeSong getSongById(String id) {
        return songDatabase.youTubeSongDao().getSongById(id);
    }

    public List<YouTubeSong> getAllSongs() {
        return songDatabase.youTubeSongDao().getAllSongs();
    }


    public List<Playlist> getAllPlaylists() {
        return playlistDatabase.getAllPlaylists();
    }

    public List<String> getAllPlaylistsName() {
        return playlistDatabase.getAllPlaylistsName();
    }

    public Playlist getPlaylistByName(String name) {
        return playlistDatabase.getPlaylistByName(name);
    }


    public int getAllPlaylistCount() {
        return playlistDatabase.getAllPlaylistCount();
    }


    public void addPlaylist(String... names) {
        playlistDatabase.addPlaylist(names);
    }

    public void deletePlaylist(String... names) {
        playlistDatabase.deletePlaylist(names);
    }

    public void deleteAllPlaylists() {
        playlistDatabase.deleteAllPlaylist();
    }


    //--------SHAREDPREF
    public boolean getAudioFocus() {

        return true;
    }


    public void setAudioFocus(boolean audioFocus) {

    }


    //--------SERVICE

    private static boolean isMusicServiceRunning(Context context) {
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (MusicService.class.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    public void startMusicService() {
        if (!isMusicServiceRunning(context))
            context.startService(new Intent(context, MusicService.class));
    }


    public void startSong(YouTubeSong youTubeSong) {
        if (!isMusicServiceRunning(context))
            throw new IllegalStateException("startSong must be called after the service is started");

        Intent i = new Intent(context, MusicService.class);
        i.putExtra(KEY_SONG, new String[]{
                youTubeSong.getId(),
                youTubeSong.getTitle(),
                youTubeSong.getPath(),
                String.valueOf(youTubeSong.getStart()),
                String.valueOf(youTubeSong.getEnd())});

        if (youTubeSong.getPath() == null || youTubeSong.getPath().equals("")) {
            i.setAction(ACTION_START_STREAMING);
        } else {
            i.setAction(ACTION_START_LOCAL);
        }

        context.startService(i);
    }

    public void stopMusicService() {
        Intent i = new Intent(context, MusicService.class);
        i.setAction(ACTION_STOP);
        context.startService(i);
    }

    public void playOrPauseSong() {
        Intent i = new Intent(context, MusicService.class);
        i.setAction(ACTION_PLAY_PAUSE);
        context.startService(i);

    }

    public void playSong() {
        Intent i = new Intent(context, MusicService.class);
        i.setAction(ACTION_PLAY);
        context.startService(i);

    }

    public void pauseSong() {
        Intent i = new Intent(context, MusicService.class);
        i.setAction(ACTION_PAUSE);
        context.startService(i);

    }

    public void skipToPreviusSong() {
        Intent i = new Intent(context, MusicService.class);
        i.setAction(ACTION_PREVIOUS);
        context.startService(i);

    }

    public void skipToNextSong() {
        Intent i = new Intent(context, MusicService.class);
        i.setAction(ACTION_NEXT);
        context.startService(i);

    }

    public void rewindSong() {
        Intent i = new Intent(context, MusicService.class);
        i.setAction(ACTION_REWIND);
        context.startService(i);

    }

    public void fastForwardSong() {
        Intent i = new Intent(context, MusicService.class);
        i.setAction(ACTION_FAST_FORWARD);
        context.startService(i);

    }

    public ArrayList<YouTubeSong> getSongsInPlaylist(Playlist playlist) {
        ArrayList<YouTubeSong> array = new ArrayList<>();
        array.add(new YouTubeSong.Builder("aa", "ss").build());
        return array;
    }

    //TASKS

    public void loadAutocompleteSuggestions(String query, OnExecuteTaskCallback onExecuteTaskCallback) {
        this.onExecuteTaskCallback = onExecuteTaskCallback;
        if (autocompleteTask != null) autocompleteTask.cancel(true);
        autocompleteTask = (AutocompleteTask) new AutocompleteTask().execute(query);

    }

    public void loadYouTubeSearchResult(String query, OnExecuteTaskCallback onExecuteTaskCallback){
        this.onExecuteTaskCallback = onExecuteTaskCallback;
        new SearchTask().execute(query);
    }

    private class AutocompleteTask extends AsyncTask<String, Void, String> {
        URL url;

        @Override
        protected void onPreExecute() {
            onExecuteTaskCallback.onBeforeExecutingTask();
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params) {
            onExecuteTaskCallback.onDuringExecutingTask();
            Thread.currentThread().setName("le1.mytube.AutocompleteTask");
            try {
                if (!params[0].trim().equals("")) {
                    url = new URL("http://suggestqueries.google.com/complete/search?client=firefox&ds=yt&q=" + Uri.encode(params[0]));
                    String JSON_string;
                    HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                    InputStream inputStream = httpURLConnection.getInputStream();
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                    StringBuilder stringBuilder = new StringBuilder();
                    while ((JSON_string = bufferedReader.readLine()) != null) {
                        stringBuilder.append(JSON_string + "\r\n");
                    }

                    bufferedReader.close();
                    inputStream.close();
                    httpURLConnection.disconnect();
                    return stringBuilder.toString().trim();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;

        }

        @Override
        protected void onPostExecute(String result) {
            onExecuteTaskCallback.onAfterExecutingTask(result);
        }


    }

    private class SearchTask extends AsyncTask<String, String, String> {

        final static int maxResults = 20;

        @Override
        protected void onPreExecute() {
            onExecuteTaskCallback.onBeforeExecutingTask();

            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params) {
            onExecuteTaskCallback.onDuringExecutingTask();
            Thread.currentThread().setName("le1.mytube.SearchTask");
            HttpURLConnection urlConnection;

            URL url;
            try {

                final String encodedURL = URLEncoder.encode(params[0], "UTF-8");
                url = new URL("https://www.googleapis.com/youtube/v3/search?part=snippet&maxResults=" + maxResults + "&q=" + encodedURL + "&type=video&key=AIzaSyCwH2GDglQOh4CKMPU8LIc8Gu9jxGUCj2w");


                urlConnection = (HttpURLConnection) url.openConnection();

                urlConnection.setRequestMethod("GET");
                urlConnection.setReadTimeout(10000 /* milliseconds */);
                urlConnection.setConnectTimeout(15000 /* milliseconds */);
                urlConnection.setDoOutput(true);
                urlConnection.connect();

                BufferedReader br = new BufferedReader(new InputStreamReader(url.openStream()));


                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = br.readLine()) != null) {
                    sb.append(line);
                    sb.append("\n");
                }
                br.close();
                return sb.toString();
            } catch (MalformedURLException e) {
                e.printStackTrace();
                return null;
            } catch (ProtocolException e) {
                e.printStackTrace();
                return null;
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }


        }

        @Override
        protected void onPostExecute(String result) {
            onExecuteTaskCallback.onAfterExecutingTask(result);

        }
    }
}
