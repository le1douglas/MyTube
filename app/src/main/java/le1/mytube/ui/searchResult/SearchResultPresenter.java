package le1.mytube.ui.searchResult;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.net.Uri;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import le1.mytube.listeners.OnExecuteTaskCallback;
import le1.mytube.mvpModel.Repo;
import le1.mytube.mvpModel.database.song.YouTubeSong;
import le1.mytube.ui.base.BaseContract;

public class SearchResultPresenter extends AndroidViewModel implements SearchResultContract.ViewModel, OnExecuteTaskCallback {
    private Repo repo;
    private Application application;
    private SearchResultContract.View contractView;

    public SearchResultPresenter(Application application) {
        super(application);
        repo = new Repo(application);
        this.application = application;
    }

    public void loadSearchResult(String query) {
        repo.loadYouTubeSearchResult(query, this);
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        repo.onDestroy();
    }

    @Override
    public void setContractView(BaseContract.View contractView) {
        this.contractView = (SearchResultContract.View) contractView;
    }

    @Override
    public void downloadSong(YouTubeSong youTubeSong) {
        youTubeSong.download(application);
    }

    @Override
    public void addSongToQueue(YouTubeSong youTubeSong) {
        Toast.makeText(application, "addSongToQueueStart", Toast.LENGTH_SHORT).show();
    }


    @Override
    public void onBeforeTask() {

    }

    @Override
    public void onDuringTask() {

    }

    @Override
    public void onAfterTask(Object result) {
        //result returns null if query is empty
        if (result == null) {
            contractView.onStopLoading();
            return;
        }

        try {
            JSONObject jsonObject = new JSONObject((String) result);
            JSONArray itemArray = jsonObject.getJSONArray("items");
            if (itemArray.length() > 0) {
                List<YouTubeSong> youTubeSongList = new ArrayList<>();
                for (int i = 0; i < itemArray.length(); i++) {
                    JSONObject videoRoot = itemArray.getJSONObject(i);
                    JSONObject id = videoRoot.getJSONObject("id");
                    JSONObject snippet = videoRoot.getJSONObject("snippet");
                    JSONObject thumbnails = snippet.getJSONObject("thumbnails");
                    JSONObject thumbnailImage = thumbnails.getJSONObject("medium");

                    String imageString = thumbnailImage.getString("url");
                    String idString = id.getString("videoId");
                    String titleString = snippet.getString("title");


                    youTubeSongList.add(new YouTubeSong.Builder(idString, titleString)
                            .imageUri(Uri.parse(imageString))
                            .build());
                }
                contractView.onSearchResultLoaded(youTubeSongList);
                contractView.onStopLoading();
            } else {
                contractView.onNoSearchResultLoaded();
                contractView.onStopLoading();
            }

        } catch (JSONException e) {
            e.printStackTrace();
            contractView.onSearchResultError("error parsing Json");
            contractView.onStopLoading();
        }

    }
}