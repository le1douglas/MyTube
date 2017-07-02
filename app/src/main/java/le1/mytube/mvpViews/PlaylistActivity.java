package le1.mytube.mvpViews;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

import le1.mytube.MusicService;
import le1.mytube.R;
import le1.mytube.YouTubeSong;
import le1.mytube.adapters.PlaylistAdapter;
import le1.mytube.mvpModel.Repository;
import le1.mytube.mvpPresenters.PlaylistPresenter;

import static le1.mytube.mvpUtils.DatabaseConstants.TB_NAME;
import static le1.mytube.mvpViews.MainActivity.isMyServiceRunning;


public class PlaylistActivity extends AppCompatActivity implements PlaylistInterface, ListView.OnItemClickListener, ListView.OnItemLongClickListener {

    private BaseAdapter adapter;
    private PlaylistPresenter presenter;
    String playlistName;
    ArrayList<YouTubeSong> songList= new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_playlist);
        playlistName = getIntent().getStringExtra("TITLE");
        Toolbar tb = (Toolbar) findViewById(R.id.toolbarPlaylist);
        tb.setTitle(playlistName);
        tb.setTitleTextColor(Color.WHITE);
        setSupportActionBar(tb);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        ListView listView = (ListView) findViewById(R.id.playlistList);
        adapter = new PlaylistAdapter(this, songList);
        listView.setAdapter(adapter);

        Repository repository = new Repository(this);
        presenter = new PlaylistPresenter(repository);
        presenter.bind(this);
        presenter.loadSongsInPlaylist(playlistName);

        listView.setOnItemClickListener(this);
        listView.setOnItemLongClickListener(this);
    }

    @Override
    protected void onDestroy() {
        presenter.unbind();
        super.onDestroy();

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
      if (!isMyServiceRunning(this, MusicService.class)) {
            Intent intent = new Intent(this, MusicService.class);
            intent.putExtra("song",  (YouTubeSong) adapter.getItem(position));
            intent.putExtra("local",  true);
            startService(intent);
        } else {
            MusicService.startSong(this, (YouTubeSong) adapter.getItem(position), true);
        }


    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {

        final YouTubeSong youTubeSong = (YouTubeSong) adapter.getItem(position);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setTitle("Remove \""+ youTubeSong.getTitle()+ "\" ?");

        if (playlistName.equals(TB_NAME)) {
            alertDialogBuilder.setMessage("You won't be able to listen to this song offline anymore");
        }else{
            alertDialogBuilder.setMessage("You won't be able to listen to this song offline anymore. You can remove the song from the playlist without deleting it");

            alertDialogBuilder.setNeutralButton("Delete from playlist",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            presenter.removeSongFromPlaylist(youTubeSong, playlistName);
                            songList.remove(position);
                            adapter.notifyDataSetChanged();
                        }
                    });
        }
        alertDialogBuilder.setPositiveButton("Delete",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        presenter.deleteSong(youTubeSong);
                        songList.remove(position);
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

    @Override
    public void displaySongs(ArrayList<YouTubeSong> songList) {
        this.songList.addAll(songList);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void displayNoSongs() {
        //TODO add nice view
        Toast.makeText(this, "Downloaded song will be saved here", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void displayErrorSongs() {
        Toast.makeText(this, "Something went wrong", Toast.LENGTH_SHORT).show();
    }
}
