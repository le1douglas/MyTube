package le1.mytube.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import le1.mytube.R;
import le1.mytube.YouTubeSong;

/**
 * Created by Leone on 23/06/17.
 */

public class PlaylistAdapter extends BaseAdapter{
    Context context;
    ArrayList<YouTubeSong> arrayList;

    public PlaylistAdapter(Context context, ArrayList<YouTubeSong> arrayList) {
        this.context= context;
        this.arrayList= arrayList;
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

        if (convertView==null)
        {
            convertView= LayoutInflater.from(context).inflate(R.layout.playlist_row, null);
        }

        TextView title= (TextView) convertView.findViewById(R.id.playlistSongTitle);
        title.setText(arrayList.get(position).getTitle());
        return convertView;
    }

}
