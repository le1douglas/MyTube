package le1.mytube.ui.search;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

import le1.mytube.listeners.OnExecuteTaskCallback;
import le1.mytube.mvpModel.Repo;
import le1.mytube.ui.base.BaseContract;

/**
 * Created by Leone on 05/09/17.
 */

public class SearchPresenter extends AndroidViewModel implements SearchContract.ViewModel, OnExecuteTaskCallback{
    private Repo repo;
    private SearchContract.View contractView;

    public SearchPresenter(Application application) {
        super(application);
        repo=new Repo(application);
    }

    @Override
    public void setContractView(BaseContract.View contractView) {
        this.contractView = (SearchContract.View) contractView;
    }

    @Override
    public void loadAutocompleteSuggestions(String query){
        repo.loadAutocompleteSuggestions(query, this);
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        repo.onDestroy();
    }

    @Override
    public void onBeforeTask() {

    }

    @Override
    public void onDuringTask() {

    }

    @Override
    public void onAfterTask(Object result) {
        if (result!=null) {
            try {
                List<String> suggestionList = new ArrayList<>();
                JSONArray root = new JSONArray((String) result);
                JSONArray suggestionArray = root.getJSONArray(1);
                for (int i = 0; i < suggestionArray.length(); i++) {
                    String suggestion = suggestionArray.getString(i);
                    suggestionList.add(suggestion);
                }
                contractView.onSearchResultLoaded(suggestionList);
            } catch (JSONException e) {
                e.printStackTrace();
                contractView.onSearchResultError("error parsing Json");
            }

        }
    }
}
