package le1.mytube.adapters;

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
import le1.mytube.mvpModel.songs.YouTubeSong;


public class VideoResultAdapter extends ArrayAdapter<String> {

    private ArrayList<YouTubeSong> youTubeSongs;
    private Context context;

    public VideoResultAdapter(Context c, ArrayList<YouTubeSong> youTubeSongs) {
        super(c, R.layout.row_video);
        this.youTubeSongs=youTubeSongs;
        context = c;
    }

    @Override
    public void clear() {
        super.clear();
        youTubeSongs.clear();
        this.notifyDataSetChanged();
    }

    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        final YouTubeSong youTubeSong = youTubeSongs.get(position);
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.row_video, parent, false);
        }

        TextView titleView = (TextView) convertView.findViewById(R.id.title);
        TextView idView = (TextView) convertView.findViewById(R.id.id);
        ImageButton addToQuequeButton = (ImageButton) convertView.findViewById(R.id.addToQueque);
        ImageView thumb = (ImageView) convertView.findViewById(R.id.thumb);

        titleView.setText(youTubeSong.getTitle());
        idView.setText(youTubeSong.getId());

        addToQuequeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, "Downloading song", Toast.LENGTH_SHORT).show();
                youTubeSong.download(context);

            }
        });

        Picasso.with(context).load(youTubeSong.getImage()).into(thumb);
        return convertView;
    }
}
