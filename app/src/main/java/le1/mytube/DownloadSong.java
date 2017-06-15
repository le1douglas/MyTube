package le1.mytube;

import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;

import static le1.mytube.MainActivity.db;

/**
 * Created by Leone on 12/06/17.
 */

public class DownloadSong extends AsyncTask<String, String, File> {

    @Override
    protected void onPreExecute() {
        File myDirectory = new File(Environment.getExternalStorageDirectory(), "MyTube");
        if (!myDirectory.exists()) {
            myDirectory.mkdir();
        }
        super.onPreExecute();
        //show notif
    }

    @Override
    protected File doInBackground(String... info) {

        // 0 url
        // 1 title
        // 2 id
        // 3 path
        // 4 start
        // 5 end

        try {

            File file = new File(android.os.Environment.getExternalStorageDirectory(), "MyTube");
            if (!file.exists())
                file.mkdirs();

            URL url = new URL(info[0]);
            File f = new File(file, info[1] + ".mp3");


            URLConnection connection = url.openConnection();
            connection.connect();
            int lenghtOfFile = connection.getContentLength();

            InputStream input = new BufferedInputStream(url.openStream());
            OutputStream output = new FileOutputStream(f);


            byte data[] = new byte[1024];
            long total = 0;
            int count = 0;
            while ((count = input.read(data)) != -1) {
                total++;
                publishProgress("" + (int) ((total * 100) / lenghtOfFile));
                output.write(data, 0, count);
            }

            output.flush();
            output.close();
            input.close();

            db.addSong(info[1], info[2], f.getPath(), null, null);

            return f;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    protected void onProgressUpdate(String... progress) {
        Log.d("ANDRO_ASYNC", progress[0]);
        //update notif
    }

    @Override
    protected void onPostExecute(File file) {
        //TODO it does not work
        /*try {
            MP3File mp3file = new MP3File(file);
            Log.d("ID3v1", String.valueOf(mp3file.hasID3v1Tag()));
            Log.d("ID3v2", String.valueOf(mp3file.hasID3v2Tag()));
            Log.d("Lyrics3Tag", String.valueOf(mp3file.hasLyrics3Tag()));
            Log.d("BITRATE", String.valueOf(mp3file.getBitRate()));
            ID3v2_3 tag = new ID3v2_3();
            tag.setSongTitle("SONGTITLE");
            tag.setAlbumTitle("ALLEBUMME");
            mp3file.setID3v2Tag(tag);
            mp3file.save();
        } catch (Exception e) {
            e.printStackTrace();
        }*/

    }

}

