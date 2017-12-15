package le1.mytube.presentation.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import le1.mytube.R;
import le1.mytube.data.database.youTubeSong.YouTubeSong;

public class PlaylistAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<YouTubeSong> arrayList;

    public PlaylistAdapter(Context context, ArrayList<YouTubeSong> arrayList) {
        this.context = context;
        this.arrayList = arrayList;
    }

    @Override
    public int getCount() {
        return arrayList.size();
    }

    @Override
    public YouTubeSong getItem(int position) {
        return arrayList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return getItem(position).hashCode();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.row_playlist, parent, false);
        }

        TextView title = convertView.findViewById(R.id.playlistSongTitle);
        title.setText(arrayList.get(position).getTitle());
        return convertView;
    }

}
