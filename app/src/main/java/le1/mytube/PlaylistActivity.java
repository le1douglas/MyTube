package le1.mytube;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import le1.mytube.adapters.PlaylistAdapter;
import le1.mytube.database.MusicDB;

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

        listView = (ListView) findViewById(R.id.playlistList);

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
            intent.putExtra("song",  adapter.getItem(position));
            intent.putExtra("local",  true);
            startService(intent);
        } else {
            MusicService.startSong(this, adapter.getItem(position), true);
        }


    }

    String ellipsize(String input, int maxLength) {
        String ellip = "...";
        if (input == null || input.length() <= maxLength
                || input.length() < ellip.length()) {
            return input;
        }
        return input.substring(0, maxLength - ellip.length()).concat(ellip);
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
        YouTubeSong youTubeSong= adapter.getItem(position);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setTitle("Delete \"" + ellipsize(youTubeSong.getTitle(),23) + "\" ?");
        alertDialogBuilder.setMessage("You won't be able to listen to this song offline");
        alertDialogBuilder.setPositiveButton("Delete",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        db.deleteSong(adapter.getItem(position));

                        adapter.notifyDataSetChanged();
                    }
                });

        alertDialogBuilder.setNegativeButton("Cancel",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
        alertDialogBuilder.show();
        return true;
    }

}
