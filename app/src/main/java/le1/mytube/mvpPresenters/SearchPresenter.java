package le1.mytube.mvpPresenters;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;

import le1.mytube.listeners.OnExecuteTaskCallback;
import le1.mytube.mvpModel.Repo;

/**
 * Created by Leone on 05/09/17.
 */

public class SearchPresenter extends AndroidViewModel {
    private Repo repo;

    public SearchPresenter(Application application) {
        super(application);
        repo=new Repo(application);
    }

    public void getAutocompleteSuggestions(String query, OnExecuteTaskCallback onExecuteTaskCallback){
        repo.loadAutocompleteSuggestions(query, onExecuteTaskCallback);
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        repo.onDestroy();
    }
}
