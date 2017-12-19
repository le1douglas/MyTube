package le1.mytube.presentation.ui.searchResult;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
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

import java.util.ArrayList;
import java.util.List;

import le1.mytube.R;
import le1.mytube.data.database.youTubeSong.YouTubeSong;
import le1.mytube.domain.application.MyTubeApplication;
import le1.mytube.presentation.adapters.VideoResultAdapter;
import le1.mytube.presentation.ui.musicPlayer.MusicPlayerActivity;
import le1.mytube.presentation.ui.search.SearchActivity;

import static le1.mytube.presentation.ui.main.MainActivity.changeStatusBarColor;

public class SearchResultActivity extends AppCompatActivity implements SearchResultContract.View, AdapterView.OnItemClickListener, View.OnTouchListener {
    private ListView videoResultListView;
    private ProgressBar loadingIcon;
    private VideoResultAdapter videoResultAdapter;
    private ArrayList<YouTubeSong> youTubeSongArray;
    private EditText searchEditText;
    private SearchResultPresenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_result);

        Toolbar toolbar = findViewById(R.id.toolbarSearch);
        changeStatusBarColor("#DBDBDB", this);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        presenter = ViewModelProviders.of(this).get(SearchResultPresenter.class);
        presenter.setContractView(this);

        loadingIcon = findViewById(R.id.loadingIcon);

        videoResultListView = findViewById(R.id.videoResult);
        youTubeSongArray = new ArrayList<>();
        videoResultAdapter = new VideoResultAdapter(this, youTubeSongArray);
        videoResultAdapter.setNotifyOnChange(true);
        videoResultListView.setAdapter(videoResultAdapter);
        videoResultListView.setOnItemClickListener(this);

        searchEditText = findViewById(R.id.SearchEditText);
        searchEditText.setOnTouchListener(this);

        searchEditText.setText(getIntent().getStringExtra("QUERY"));
        searchEditText.setSelection(searchEditText.length());
        presenter.loadSearchResult(getIntent().getStringExtra("QUERY"));

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        TextView idView = view.findViewById(R.id.id);
        TextView videoTitle = view.findViewById(R.id.title);
        YouTubeSong youTubeSong = new YouTubeSong.Builder(idView.getText().toString(), videoTitle.getText().toString()).build();
        Intent i = new Intent(this, MusicPlayerActivity.class);
        i.putExtra(MyTubeApplication.KEY_SONG, youTubeSong);
        i.putExtra(MyTubeApplication.KEY_SHOULD_PLAY, true);
        i.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
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
    public void onStartLoading() {
        loadingIcon.setVisibility(View.VISIBLE);
        videoResultListView.setVisibility(View.GONE);
    }

    @Override
    public void onStopLoading() {
        videoResultListView.setVisibility(View.VISIBLE);
        loadingIcon.setVisibility(View.GONE);
    }

    @Override
    public void onSearchResultLoaded(List<YouTubeSong> songList) {
        videoResultAdapter.clear();
        videoResultListView.setSelection(0);
        for (YouTubeSong yts : songList) {
            youTubeSongArray.add(yts);
            videoResultAdapter.add("I HAVE TO DO THIS AND I DON'T KNOW WHY");

        }
        videoResultAdapter.notifyDataSetChanged();
    }

    @Override
    public void onNoSearchResultLoaded() {
        Toast.makeText(this, "no videos found", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onSearchResultError(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}
