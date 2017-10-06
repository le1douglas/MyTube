package le1.mytube.mvpViews;

import android.app.Activity;
import android.arch.lifecycle.ViewModelProviders;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

import le1.mytube.R;
import le1.mytube.listeners.OnCheckValidPlaylistNameListener;
import le1.mytube.listeners.OnLoadAudioFocusListener;
import le1.mytube.listeners.OnLoadPlaylistListener;
import le1.mytube.listeners.OnRequestPlaylistDialogListener;
import le1.mytube.mvpModel.database.DatabaseConstants;
import le1.mytube.mvpModel.playlists.Playlist;
import le1.mytube.mvpPresenters.MainPresenter;


public class MainActivity extends AppCompatActivity implements ListView.OnItemClickListener, ListView.OnItemLongClickListener, OnLoadPlaylistListener, OnLoadAudioFocusListener, OnRequestPlaylistDialogListener, OnCheckValidPlaylistNameListener {
    private static ArrayList<String> displayedList;
    private static ArrayAdapter<String> adapter;

    private AlertDialog alertDialog;
    private CompoundButton audioFocusSwitch;

    private MainPresenter presenter;


    public static void changeStatusBarColor(String color, Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = activity.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.parseColor(color));
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar tb = (Toolbar) findViewById(R.id.toolbar);
        tb.setTitle(R.string.app_name);
        tb.setTitleTextColor(Color.WHITE);
        setSupportActionBar(tb);

        presenter = ViewModelProviders.of(this).get(MainPresenter.class);

        ListView listView = (ListView) findViewById(R.id.playlistList);
        displayedList = new ArrayList<>();
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, displayedList);

        listView.setAdapter(adapter);
        listView.setOnItemClickListener(this);
        listView.setOnItemLongClickListener(this);

        presenter.loadPlaylists(this);



    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        MenuItem handleAudioFocusItem = menu.findItem(R.id.handleAudioFocus);
        audioFocusSwitch = (CompoundButton) MenuItemCompat.getActionView(handleAudioFocusItem);
        audioFocusSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                presenter.setHandleAudioFocus(isChecked);

            }
        });
        presenter.loadSharedPreferences(this);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.action_search:
                startActivity(new Intent(this, SearchActivity.class));
                return true;
            case R.id.printDB:
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(MainActivity.this);
                alertDialogBuilder.setTitle("QueueDatabase")
                        .setMessage(presenter.logDatabase());
                alertDialogBuilder.show();
                return true;
            case R.id.clearDB:
                presenter.clearDatabase();
                return true;
            case R.id.handleAudioFocus:
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void Add(View view) {
        presenter.onNewPlaylistButtonCLick(this);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent intent = new Intent(this, PlaylistActivity.class);
        intent.putExtra("TITLE", parent.getItemAtPosition(position).toString());
        startActivity(intent);


    }

    @Override
    public boolean onItemLongClick(final AdapterView<?> parent, View view, final int position, long id) {
        presenter.onItemLongClick(new Playlist(0,parent.getItemAtPosition(position).toString(), null, 0, 0), position, this);
        return true;
    }

    @Override
    public void onPlaylistLoaded(ArrayList<Playlist> playlists) {
        Toast.makeText(this, "Displaying playlists", Toast.LENGTH_SHORT).show();
        for (Playlist p : playlists) {
            displayedList.add(p.getName());
        }
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onNoPlaylistLoaded() {
        Toast.makeText(this, "No playlist to load", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onPlaylistLoadingError() {
        Toast.makeText(this, "Error loading playlists", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onAudioFocusTrue() {
        audioFocusSwitch.setChecked(true);
        audioFocusSwitch.setEnabled(true);
    }

    @Override
    public void onAudioFocusFalse() {
        Toast.makeText(this, "onAudioFocusFalse", Toast.LENGTH_SHORT).show();
        audioFocusSwitch.setChecked(true);
        audioFocusSwitch.setEnabled(true);
    }

    @Override
    public void onAudioFocusLoadingError() {
        Toast.makeText(this, "onAudioFocusERROR", Toast.LENGTH_SHORT).show();
        audioFocusSwitch.setEnabled(false);
    }

    @Override
    public void onPlaylistNameValid() {
        alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(true);
    }

    @Override
    public void onPlaylistNameInvalid() {
        alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);
    }

    @Override
    public void onNewPlaylistDialog() {
        final View layout = getLayoutInflater().inflate(R.layout.dialog_new_playlist, null);
        final EditText input = layout.findViewById(R.id.dialogEditText);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(MainActivity.this);
        alertDialogBuilder.setTitle("New Playlist");
        alertDialogBuilder.setView(layout);

        alertDialogBuilder.setPositiveButton("Create Playlist",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        String playlistName = input.getText().toString();

                        presenter.addPlaylist(playlistName);

                        displayedList.add(playlistName);
                        adapter.notifyDataSetChanged();
                    }
                });

        alertDialogBuilder.setNegativeButton("Cancel",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

        alertDialog = alertDialogBuilder.create();
        alertDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        alertDialog.show();

        presenter.checkValidPlaylistName(input.getText().toString(), MainActivity.this);

        input.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                presenter.checkValidPlaylistName(input.getText().toString(), MainActivity.this);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }

        });

    }

    @Override
    public void onOfflineDeletePlaylistDialog() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(MainActivity.this);
        alertDialogBuilder.setMessage("You cannot delete this");
        alertDialogBuilder.setPositiveButton("Ok",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
        alertDialogBuilder.show();
    }

    @Override
    public void onStandardDeletePlaylistDialog(final Playlist playlist, final int position) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(MainActivity.this);
        alertDialogBuilder.setTitle("Delete " + playlist.getName() + "?");
        alertDialogBuilder.setMessage("The songs inside this playlist will be available offline anyway. You can see all of your downloaded songs in \"" + DatabaseConstants.TB_NAME + "\"");
        alertDialogBuilder.setPositiveButton("Delete",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        displayedList.remove(position);
                        adapter.notifyDataSetChanged();
                        presenter.deletePlaylist(playlist.getName());
                    }
                });

        alertDialogBuilder.setNegativeButton("Cancel",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
        alertDialogBuilder.show();

    }
}
