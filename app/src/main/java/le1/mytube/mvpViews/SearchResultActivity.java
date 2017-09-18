package le1.mytube.mvpViews;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import le1.mytube.R;
import le1.mytube.adapters.VideoResultAdapter;
import le1.mytube.listeners.OnExecuteTaskCallback;
import le1.mytube.mvpModel.database.song.YouTubeSong;
import le1.mytube.mvpPresenters.SearchResultPresenter;
import le1.mytube.services.MusicServiceConstants;

import static le1.mytube.mvpViews.MainActivity.changeStatusBarColor;

public class SearchResultActivity extends AppCompatActivity implements AdapterView.OnItemClickListener, View.OnTouchListener, OnExecuteTaskCallback {
    ListView videoResultListView;
    ProgressBar loadingIcon;
    VideoResultAdapter videoResultAdapter;
    ArrayList<YouTubeSong> youTubeSongArray;
    EditText searchEditText;
    SearchResultPresenter presenter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_result);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbarSearch);
        changeStatusBarColor("#DBDBDB", this);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        presenter= ViewModelProviders.of(this).get(SearchResultPresenter.class);

        loadingIcon = (ProgressBar) findViewById(R.id.loadingIcon);

        videoResultListView = (ListView) findViewById(R.id.videoResult);
        youTubeSongArray = new ArrayList<>();
        videoResultAdapter = new VideoResultAdapter(this, presenter, youTubeSongArray);
        videoResultListView.setAdapter(videoResultAdapter);
        videoResultListView.setOnItemClickListener(this);

        searchEditText = (EditText) findViewById(R.id.SearchEditText);
        searchEditText.setOnTouchListener(this);

        presenter.getSearchResults(getIntent().getStringExtra("QUERY"), this);
        searchEditText.setText(getIntent().getStringExtra("QUERY"));
        searchEditText.setSelection(searchEditText.length());

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        TextView idView = view.findViewById(R.id.id);
        TextView videoTitle = view.findViewById(R.id.title);
        YouTubeSong youTubeSong = new YouTubeSong.Builder(idView.getText().toString(), videoTitle.getText().toString()).build();
        Intent i =new Intent(this, MusicPlayerActivity.class);
        i.putExtra(MusicServiceConstants.KEY_SONG, youTubeSong);
        this.startActivity(i);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        Intent i = new Intent(this, SearchActivity.class);
        i.putExtra("QUERY", searchEditText.getText().toString());
        startActivity(i);
        return super.onTouchEvent(event);
    }

    @Override
    public void onBeforeExecutingTask() {
        loadingIcon.setVisibility(View.VISIBLE);
        videoResultListView.setVisibility(View.GONE);
    }

    @Override
    public void onDuringExecutingTask() {

    }

    @Override
    public void onAfterExecutingTask(Object result) {
        videoResultListView.setVisibility(View.VISIBLE);
        loadingIcon.setVisibility(View.GONE);
        if (result != null) {
            videoResultListView.smoothScrollToPosition(0);
            try {
                JSONObject jsonObject= new JSONObject((String) result);
                JSONArray itemArray = jsonObject.getJSONArray("items");
                videoResultAdapter.clear();
                if (itemArray.length() > 0) {

                    for (int i = 0; i < itemArray.length(); i++) {
                        JSONObject videoRoot = itemArray.getJSONObject(i);
                        JSONObject id = videoRoot.getJSONObject("id");
                        JSONObject snippet = videoRoot.getJSONObject("snippet");
                        JSONObject thumbnails = snippet.getJSONObject("thumbnails");
                        JSONObject thumbnailImage = thumbnails.getJSONObject("medium");

                        String imageString = thumbnailImage.getString("url");
                        String idString = id.getString("videoId");
                        String titleString = snippet.getString("title");

                        youTubeSongArray.add(new YouTubeSong.Builder(idString, titleString)
                                .image(Uri.parse(imageString))
                                .build());
                        videoResultAdapter.add("WITHOUT THIS IT DOESN'T WORK AND I DON'T KNOW WHY");
                    }
                    videoResultAdapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(SearchResultActivity.this, "No results found", Toast.LENGTH_SHORT).show();
                }


            } catch (JSONException e) {
                e.printStackTrace();
            }

        } else {
            Toast.makeText(SearchResultActivity.this, "null", Toast.LENGTH_SHORT).show();
        }
    }
}
