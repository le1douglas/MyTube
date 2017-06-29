package le1.mytube;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;

import le1.mytube.database.MusicDB;


public class DownloadSong extends AsyncTask<String, String, File> {

    private Context mContext;

    @Override
    protected void onPreExecute() {
        File myDirectory = new File(Environment.getExternalStorageDirectory(), "MyTube");
        if (!myDirectory.exists()) {
            myDirectory.mkdir();
        }
        super.onPreExecute();
        //TODO show notif
    }

    @Override
    protected File doInBackground(String... info) {
        Thread.currentThread().setName("le1.mytube.DownloadSong");

        try {

            File file = new File(android.os.Environment.getExternalStorageDirectory(), "MyTube");
            if (!file.exists()) {
                file.mkdirs();
            }

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
                publishProgress(String.valueOf(lenghtOfFile));
                output.write(data, 0, count);
            }

            output.flush();
            output.close();
            input.close();


            MusicDB db= new MusicDB(mContext);
            db.open();
            db.addSong(new YouTubeSong(info[1], info[2], f.getAbsolutePath(), null, null));
            db.close();

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
        Toast.makeText(mContext, "finished downloading song", Toast.LENGTH_SHORT).show();
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

    public DownloadSong(Context context) {
        mContext = context;
    }

}

