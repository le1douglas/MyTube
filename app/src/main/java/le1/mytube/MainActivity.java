package le1.mytube;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.ListView;

import java.util.ArrayList;


public class MainActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {
    public static boolean modalitaPorno;
    public static MusicDB db;
    SharedPreferences sharedPref;
    SharedPreferences.Editor editor;
    ListView listView;
    ArrayList<String> list;
    ArrayAdapter<String> adapter;

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
        modalitaPorno = sharedPref.getBoolean("modalitaPorno", false);

        Toolbar tb = (Toolbar) findViewById(R.id.toolbar);
        tb.setTitle(R.string.app_name);
        tb.setTitleTextColor(Color.WHITE);
        setSupportActionBar(tb);

        listView= (ListView) findViewById(R.id.playlistList);
        list = new ArrayList<String>();
        list.add("qulo");
        list.add("qulo2");
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, list);


        listView.setAdapter(adapter);
        listView.setOnItemClickListener(this);

        db = new MusicDB(this);
        db.open();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        MenuItem item = menu.findItem(R.id.modalitaPorno);

        final CompoundButton modalitaPornoSwitch = (CompoundButton) MenuItemCompat.getActionView(item);
        modalitaPornoSwitch.setChecked(sharedPref.getBoolean("modalitaPorno", false));
        modalitaPornoSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                modalitaPorno = isChecked;
                editor.putBoolean("modalitaPorno", modalitaPorno);
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
                Log.d("TAG", db.getAllSongsInPlaylist("QULO"));
                return true;
            case R.id.clearDB:
                db.clear();
                return true;
            case R.id.modalitaPorno:
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
        list.add("add");
        adapter.notifyDataSetChanged();
        db.addTable("QULO");
        db.addSongToPlaylist("QULO", "thisismyid");
    }

    public void Remove(View view) {
        if (list.size() > 0) {
            list.remove(list.size()-1);
        }
        db.deleteTable("QULO");
        adapter.notifyDataSetChanged();
    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent playlistIntent = new Intent(this, PlaylistActivity.class);
        playlistIntent.putExtra("TITLE", parent.getItemAtPosition(position).toString());
        startActivity(playlistIntent);


    }
}
