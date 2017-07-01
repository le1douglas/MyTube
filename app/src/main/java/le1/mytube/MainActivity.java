package le1.mytube;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
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

import java.util.ArrayList;

import le1.mytube.database.MusicDB;
import le1.mytube.mvpViews.PlaylistActivity;

import static le1.mytube.mvpUtils.DatabaseConstants.TB_NAME;


public class MainActivity extends AppCompatActivity implements ListView.OnItemClickListener, ListView.OnItemLongClickListener {
    public static boolean handleAudioFocus;
    MusicDB db;
    SharedPreferences sharedPref;
    SharedPreferences.Editor editor;
    ListView listView;
    public static ArrayList<String> list;
    public static ArrayAdapter<String> adapter;

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

        sharedPref = this.getPreferences(Context.MODE_PRIVATE);
        editor = sharedPref.edit();
        handleAudioFocus = sharedPref.getBoolean("handleAudioFocus", false);

        Toolbar tb = (Toolbar) findViewById(R.id.toolbar);
        tb.setTitle(R.string.app_name);
        tb.setTitleTextColor(Color.WHITE);
        setSupportActionBar(tb);

        listView = (ListView) findViewById(R.id.playlistList);
        list = new ArrayList<>();
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, list);


        listView.setAdapter(adapter);
        listView.setOnItemClickListener(this);
        listView.setOnItemLongClickListener(this);

        db = new MusicDB(this);
        db.open();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        MenuItem handleAudioFocusItem = menu.findItem(R.id.handleAudioFocus);
        final CompoundButton modalitaPornoSwitch = (CompoundButton) MenuItemCompat.getActionView(handleAudioFocusItem);
        modalitaPornoSwitch.setChecked(sharedPref.getBoolean("handleAudioFocus", true));
        modalitaPornoSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                handleAudioFocus = isChecked;
                editor.putBoolean("handleAudioFocus", handleAudioFocus);
                editor.commit();

            }
        });
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
                for (int i = 0; i < db.getAllTableNames().size(); i++) {
                    Log.d("DBoperation", db.getAllTableNames().get(i).toString());
                }
                Log.d("DBoperation", "-----Offline----");
                Log.d("DBoperation", db.getAllSongsString());

                return true;
            case R.id.clearDB:
                db.clear();
                return true;
            case R.id.handleAudioFocus:


                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onDestroy() {
        db.close();
        super.onDestroy();
    }

    public void Add(View view) {

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(MainActivity.this);
        alertDialogBuilder.setTitle("New Playlist");

        View layout = getLayoutInflater().inflate(R.layout.dialog_view, null);
        final EditText input = (EditText) layout.findViewById(R.id.dialogEditText);
        alertDialogBuilder.setView(layout);

        alertDialogBuilder.setPositiveButton("Create Playlist",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        String playlistName = input.getText().toString();

                        list.add(playlistName);
                        adapter.notifyDataSetChanged();
                        db.addTable(playlistName);
                        db.addSongToPlaylist(playlistName, new YouTubeSong(null, "UiyDmqO59QE", null, null,null));
                    }
                });

        alertDialogBuilder.setNegativeButton("Cancel",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

        final AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        alertDialog.show();
        alertDialog.getButton(DialogInterface.BUTTON_POSITIVE).setEnabled(false);

        input.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (TextUtils.isEmpty(s)||db.getAllTableNames().contains(s.toString())) {
                    alertDialog.getButton(
                            AlertDialog.BUTTON_POSITIVE).setEnabled(false);
                } else {
                    alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(true);
                }

            }

            @Override
            public void afterTextChanged(Editable s) {

            }

        });


    }


    public static boolean isMyServiceRunning(Context context, Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent playlistIntent = new Intent(this, PlaylistActivity.class);
        playlistIntent.putExtra("TITLE", parent.getItemAtPosition(position).toString());
        startActivity(playlistIntent);


    }

    @Override
    public boolean onItemLongClick(final AdapterView<?> parent, View view, final int position, long id) {
        final String playlistName = parent.getItemAtPosition(position).toString();
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(MainActivity.this);
        alertDialogBuilder.setTitle("Delete " + playlistName + "?");
        alertDialogBuilder.setMessage("The songs inside this playlist will be available offline anyway. You can see all of your downloaded songs in \"My music\"");
        alertDialogBuilder.setPositiveButton("Delete",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        list.remove(position);
                        adapter.notifyDataSetChanged();
                        db.deleteTable(playlistName);
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

    public void MyMusic(View view) {
        Intent playlistIntent = new Intent(this, PlaylistActivity.class);
        playlistIntent.putExtra("TITLE", TB_NAME);
        startActivity(playlistIntent);
    }
}
