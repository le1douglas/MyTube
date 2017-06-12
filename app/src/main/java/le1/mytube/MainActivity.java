package le1.mytube;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.CompoundButton;
import android.widget.Switch;


public class MainActivity extends AppCompatActivity {
    public static boolean modalitaPorno;
    MusicDB db;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        SharedPreferences sharedPref = this.getPreferences(Context.MODE_PRIVATE);
        final SharedPreferences.Editor editor = sharedPref.edit();

        Toolbar tb = (Toolbar) findViewById(R.id.toolbar);
        tb.setTitle(R.string.app_name);
        tb.setTitleTextColor(Color.WHITE);
        setSupportActionBar(tb);
        Switch modalitaPornoSwitch = (Switch) findViewById(R.id.modalitaPorno);
        modalitaPorno = sharedPref.getBoolean("modalitaPorno", false);
        modalitaPornoSwitch.setChecked(sharedPref.getBoolean("modalitaPorno", false));
        modalitaPornoSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                modalitaPorno = isChecked;
                editor.putBoolean("modalitaPorno", modalitaPorno);
                editor.apply();

            }
        });
        db= new MusicDB(this);
        db.open();


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
                Intent searchIntent = new Intent(this, SearchActivity.class);
                searchIntent.putExtra("FROM", "MainActivity");
                startActivity(searchIntent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    public static void changeStatusBarColor(String color, Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = activity.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.parseColor(color));
        }
    }


    public void stopService(View view) {
        stopService(new Intent(this, MusicService.class));
    }

    @Override
    protected void onDestroy() {
        db.close();
        super.onDestroy();
    }
    public void uselessButton(View view){
       if (view==findViewById(R.id.add)){
          db.addSong("1", "ulallalid", "ullalalalepath", 1,666);
           Log.d("TAG", db.getAllSongs());

       }else if (view==findViewById(R.id.del)){
           //db.clear();
           new DownloadSong().execute("https://r2---sn-hpa7znle.googlevideo.com/videoplayback?keepalive=yes&id=o-AJ6IyTkXFFsXgu5l-2SKd0ENEu8l0EYeHkYl2-vRzHty&mm=31&mn=sn-hpa7znle&ei=sr8-WenVE5epcuzwr6gJ&ms=au&mt=1497284440&mv=m&pl=22&ip=93.47.229.27&initcwndbps=583750&ipbits=0&beids=%5B9466592%5D&clen=3619571&sparams=clen%2Cdur%2Cei%2Cgir%2Cid%2Cinitcwndbps%2Cip%2Cipbits%2Citag%2Ckeepalive%2Clmt%2Cmime%2Cmm%2Cmn%2Cms%2Cmv%2Cpl%2Crequiressl%2Csource%2Cexpire&gir=yes&key=yt6&lmt=1478770967149059&source=youtube&dur=227.857&requiressl=yes&mime=audio%2Fmp4&itag=140&expire=1497306130&signature=AF734D9E79BB0BAA41047A52DF9EED60DA91410D.7853E70261764E9B8A8697A2A1E34CF8437930A7", "TitleOfThesong");
       }


    }

}
