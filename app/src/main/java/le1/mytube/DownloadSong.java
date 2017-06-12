package le1.mytube;

import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;

/**
 * Created by Leone on 12/06/17.
 */

public class DownloadSong extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //show notif
        }

        @Override
        protected String doInBackground(String... url) {
            int count;
            try {
                URL URL = new URL(url[0]);
                URLConnection connection = URL.openConnection();
                connection.connect();
                int lenghtOfFile = connection.getContentLength();
                Log.d("ANDRO_ASYNC", "Lenght of file: " + lenghtOfFile);
                InputStream input = new BufferedInputStream(URL.openStream());
                OutputStream output = new FileOutputStream(Environment.getExternalStorageDirectory()+"/song.mp3");
                byte data[] = new byte[1024];
                long total = 0;
                while ((count = input.read(data)) != -1) {
                    total += count;
                    publishProgress(""+(int)((total*100)/lenghtOfFile));
                    output.write(data, 0, count);
                }

                output.flush();
                output.close();
                input.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

    protected void onProgressUpdate(String... progress) {
        Log.d("ANDRO_ASYNC",progress[0]);
        //update notif
    }

    @Override
    protected void onPostExecute(String result) {
        //show completed notif
    }
}

