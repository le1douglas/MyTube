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
import android.view.Window;
import android.view.WindowManager;
import android.widget.CompoundButton;
import android.widget.Toast;


public class MainActivity extends AppCompatActivity {
    public static boolean modalitaPorno;
    public static MusicDB db;
    SharedPreferences sharedPref;
    SharedPreferences.Editor editor;

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

        db = new MusicDB(this);
        db.open();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        MenuItem item = menu.findItem(R.id.modalitaPorno);

        final CompoundButton modalitaPornoSwitch = (CompoundButton) MenuItemCompat.getActionView(item);
        modalitaPornoSwitch.setChecked(modalitaPorno);
        modalitaPornoSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Toast.makeText(MainActivity.this, "CLICKED", Toast.LENGTH_SHORT).show();
                modalitaPorno = isChecked;
                editor.putBoolean("modalitaPorno", modalitaPorno);
                editor.apply();

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
                Log.d("TAG", db.getAllSongs());
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

}
