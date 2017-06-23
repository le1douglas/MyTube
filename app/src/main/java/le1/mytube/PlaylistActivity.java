package le1.mytube;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import le1.mytube.adapters.PlaylistAdapter;

import static le1.mytube.MainActivity.isMyServiceRunning;


public class PlaylistActivity extends AppCompatActivity implements ListView.OnItemClickListener, ListView.OnItemLongClickListener {
    ListView listView;
    MusicDB db;

    public static PlaylistAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_playlist);
        String title = getIntent().getStringExtra("TITLE");
        Toolbar tb = (Toolbar) findViewById(R.id.toolbarPlaylist);
        tb.setTitle(title);
        tb.setTitleTextColor(Color.WHITE);
        setSupportActionBar(tb);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);


        db = new MusicDB(PlaylistActivity.this);
        db.open();

        listView= (ListView) findViewById(R.id.playlistList);

        //TODO async
        adapter = new PlaylistAdapter(this, db.getAllSongs());


        listView.setAdapter(adapter);

        listView.setOnItemClickListener(this);
        listView.setOnItemLongClickListener(this);



    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        db.close();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (!isMyServiceRunning(this, MusicService.class)) {
            Intent intent = new Intent(this, MusicService.class);
            intent.putExtra("videoId", adapter.getItem(position).getId());
            intent.putExtra("title", adapter.getItem(position).getTitle());
            startService(intent);
        } else {
            MusicService.startSong(adapter.getItem(position),this);
        }

    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        return false;
    }

}
