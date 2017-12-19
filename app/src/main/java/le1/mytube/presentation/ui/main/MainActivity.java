package le1.mytube.presentation.ui.main;

import android.app.Activity;
import android.arch.lifecycle.ViewModelProviders;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
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
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

import le1.mytube.R;
import le1.mytube.data.database.DatabaseConstants;
import le1.mytube.data.database.playlist.Playlist;
import le1.mytube.presentation.ui.playlist.PlaylistActivity;
import le1.mytube.presentation.ui.search.SearchActivity;

import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

public class MainActivity extends AppCompatActivity implements MainContract.View, ListView.OnItemClickListener, ListView.OnItemLongClickListener {
    private static final int STORAGE_PERMISSION_REQUEST_CODE = 1;

    private static ArrayList<String> displayedList;
    private static ArrayAdapter<String> adapter;

    private AlertDialog alertDialog;

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
        Toolbar tb = findViewById(R.id.toolbar);
        tb.setTitle(R.string.app_name);
        tb.setTitleTextColor(Color.WHITE);
        setSupportActionBar(tb);

        presenter = ViewModelProviders.of(this).get(MainPresenter.class);
        presenter.setContractView(this);
        presenter.initializeEdge(this);

        ListView listView = findViewById(R.id.playlistList);
        displayedList = new ArrayList<>();
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, displayedList);

        listView.setAdapter(adapter);
        listView.setOnItemClickListener(this);
        listView.setOnItemLongClickListener(this);

        loadPlaylistsAfterPermission();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.action_search:
                startActivity(new Intent(this, SearchActivity.class));
                return true;
            case R.id.action_log:
                new AlertDialog.Builder(this).setTitle("Queue")
                        .setMessage(presenter.getQueueLog()).show();
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void Add(View view) {
        newPlaylistDialog();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent intent = new Intent(this, PlaylistActivity.class);
        intent.putExtra("TITLE", parent.getItemAtPosition(position).toString());
        startActivity(intent);


    }

    @Override
    public boolean onItemLongClick(final AdapterView<?> parent, View view, final int position, long id) {
        presenter.showDeleteDialog(new Playlist(0, parent.getItemAtPosition(position).toString(), null, 0, 0), position);
        return true;
    }

    @Override
    public void onPlaylistLoaded(ArrayList<Playlist> playlists) {
        for (Playlist p : playlists) {
            displayedList.add(p.getName());
        }
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onNoPlaylistLoaded() {
    }

    @Override
    public void onPlaylistLoadingError() {
        Toast.makeText(this, "Error loading playlists", Toast.LENGTH_SHORT).show();
    }

    public void loadPlaylistsAfterPermission() {
        if (ContextCompat.checkSelfPermission(this, WRITE_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this, READ_EXTERNAL_STORAGE)
                        == PackageManager.PERMISSION_GRANTED) {
            presenter.loadPlaylists();
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{WRITE_EXTERNAL_STORAGE},
                    STORAGE_PERMISSION_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case STORAGE_PERMISSION_REQUEST_CODE: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    presenter.loadPlaylists();
                } else {
                    Toast.makeText(this, "Storage permission is required to display playlists", Toast.LENGTH_SHORT).show();
                }
                break;

            }
            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    public void newPlaylistDialog() {
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

        //should return false as the string is empty at this stage
        if (presenter.isPlaylistNameValid(input.getText().toString()))
            alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(true);
        else alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);

        input.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (presenter.isPlaylistNameValid(input.getText().toString()))
                    alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(true);
                else alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);
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

    @Override
    public void onEdgeSupported() {
    }

    @Override
    public void onEdgeNotSupported(boolean isSamsungDevice) {
    }

}
