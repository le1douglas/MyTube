package le1.mytube.adapters;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import at.huber.youtubeExtractor.VideoMeta;
import at.huber.youtubeExtractor.YouTubeExtractor;
import at.huber.youtubeExtractor.YtFile;
import le1.mytube.DownloadSong;
import le1.mytube.R;


public class VideoResultAdapter extends ArrayAdapter<String> {

    ArrayList<String> videoIdArray;
    ArrayList<Uri> imageUriArray;
    ArrayList<String> titleArray;
    Context context;

    public VideoResultAdapter(Context c, ArrayList<String> id, ArrayList<Uri> iUrl, ArrayList<String> vTitle) {
        super(c, 0, id);
        videoIdArray = id;
        imageUriArray = iUrl;
        context = c;
        titleArray = vTitle;
    }

    @Override
    public void clear() {
        videoIdArray.clear();
        imageUriArray.clear();
        titleArray.clear();
        this.notifyDataSetChanged();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final String id = videoIdArray.get(position);
        Uri uri = imageUriArray.get(position);
        final String title = titleArray.get(position);
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.video_row, parent, false);
        }
        TextView titleView = (TextView) convertView.findViewById(R.id.title);
        TextView idView = (TextView) convertView.findViewById(R.id.id);
        ImageButton addToQuequeButton = (ImageButton) convertView.findViewById(R.id.addToQueque);
        ImageView thumb = (ImageView) convertView.findViewById(R.id.thumb);

        titleView.setText(title);
        idView.setText(id);


        addToQuequeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, "Downloading song", Toast.LENGTH_SHORT).show();
                new YouTubeExtractor(context) {
                    @Override
                    public void onExtractionComplete(SparseArray<YtFile> ytFiles, VideoMeta vMeta) {
                        if (ytFiles != null) {
                            int itag = 140;
                            String downloadUrl = ytFiles.get(itag).getUrl();

                            new DownloadSong(context).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, downloadUrl, title, id);

                        }
                    }
                }.extract("http://youtube.com/watch?v=" + id, false, false);

            }
        });

        Picasso.with(context).load(uri).into(thumb);


        return convertView;
    }
}
