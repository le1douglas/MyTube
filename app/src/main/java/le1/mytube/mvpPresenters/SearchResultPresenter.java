package le1.mytube.mvpPresenters;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;

import le1.mytube.listeners.OnExecuteTaskCallback;
import le1.mytube.mvpModel.Repo;
import le1.mytube.mvpModel.songs.YouTubeSong;

/**
 * Created by Leone on 05/09/17.
 */

public class SearchResultPresenter extends AndroidViewModel {
    private Repo repo;

    public SearchResultPresenter(Application application) {
        super(application);
        repo=new Repo(application);
    }

   public void startSong(YouTubeSong youTubeSong){
       repo.startSong(youTubeSong);
   }

   public void getSearchResults(String query, OnExecuteTaskCallback onExecuteTaskCallback){
       repo.loadYouTubeSearchResult(query, onExecuteTaskCallback);
   }

    @Override
    protected void onCleared() {
        super.onCleared();
        repo.onDestroy();
    }
}