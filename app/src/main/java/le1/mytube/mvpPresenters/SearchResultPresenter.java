package le1.mytube.mvpPresenters;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.widget.Toast;

import le1.mytube.listeners.OnExecuteTaskCallback;
import le1.mytube.mvpModel.Repo;
import le1.mytube.mvpModel.database.song.YouTubeSong;

/**
 * Created by Leone on 05/09/17.
 */

public class SearchResultPresenter extends AndroidViewModel{
    private Repo repo;
    private Application application;

    public SearchResultPresenter(Application application) {
        super(application);
        repo=new Repo(application);
        this.application = application;
    }

   public void startSong(YouTubeSong youTubeSong){
       repo.addSongToQueueStart(youTubeSong);
       repo.playNextSongInQueue();
   }

   public void getSearchResults(String query, OnExecuteTaskCallback onExecuteTaskCallback){
       repo.loadYouTubeSearchResult(query, onExecuteTaskCallback);
   }

    @Override
    protected void onCleared() {
        super.onCleared();
        repo.onDestroy();
    }

    public void downloadSong(YouTubeSong youTubeSong) {
        youTubeSong.download(application);
    }

    public void addSongToQueue(YouTubeSong youTubeSong) {
        Toast.makeText(application, "addSongToQueueStart", Toast.LENGTH_SHORT).show();
        repo.addSongToQueueStart(youTubeSong);
    }

}