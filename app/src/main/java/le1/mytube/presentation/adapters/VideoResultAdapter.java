package le1.mytube.presentation.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
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

import le1.mytube.R;
import le1.mytube.data.database.youTubeSong.YouTubeSong;
import le1.mytube.presentation.ui.searchResult.SearchResultPresenter;


public class VideoResultAdapter extends ArrayAdapter<String> {

    private ArrayList<YouTubeSong> youTubeSongs;
    private Context context;
    private SearchResultPresenter presenter;

    public VideoResultAdapter(Context c, SearchResultPresenter presenter, ArrayList<YouTubeSong> youTubeSongs) {
        super(c, R.layout.row_video);
        this.youTubeSongs=youTubeSongs;
        context = c;

        this.presenter = presenter;
    }

    @Override
    public void clear() {
        super.clear();
        youTubeSongs.clear();
        this.notifyDataSetChanged();
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        final YouTubeSong youTubeSong = youTubeSongs.get(position);
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.row_video, parent, false);
        }

        TextView titleView = convertView.findViewById(R.id.title);
        TextView idView = convertView.findViewById(R.id.id);
        ImageButton download = convertView.findViewById(R.id.download);
        ImageButton addToQueue = convertView.findViewById(R.id.queue_add);
        ImageView thumb = convertView.findViewById(R.id.thumb);


        titleView.setText(youTubeSong.getTitle());
        idView.setText(youTubeSong.getId());

        download.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, "Downloading song", Toast.LENGTH_SHORT).show();
                presenter.downloadSong(youTubeSong);

            }
        });

        addToQueue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.addSongToQueue(youTubeSong);
            }
        });

        Picasso.with(context).load(youTubeSong.getImageUri()).into(thumb);
        return convertView;
    }
}
