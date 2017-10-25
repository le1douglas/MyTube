package le1.mytube.mvpViews;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;

import le1.mytube.R;
import le1.mytube.adapters.AutocompleteAdapter;
import le1.mytube.listeners.OnExecuteTaskCallback;
import le1.mytube.mvpPresenters.SearchPresenter;

import static le1.mytube.ui.main.MainActivity.changeStatusBarColor;

public class SearchActivity extends AppCompatActivity implements TextWatcher, AdapterView.OnItemClickListener, TextView.OnEditorActionListener, OnExecuteTaskCallback {
    ArrayList<String> arrayOfResults;
    AutocompleteAdapter autocompleteAdapter;
    EditText searchEditText;
    SearchPresenter presenter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbarSearch);
        changeStatusBarColor("#DBDBDB", this);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        presenter= ViewModelProviders.of(this).get(SearchPresenter.class);

        searchEditText = (EditText) findViewById(R.id.SearchEditText);
        searchEditText.addTextChangedListener(this);
        searchEditText.setOnEditorActionListener(this);
        searchEditText.requestFocus();

        ListView autocompleteListView = (ListView) findViewById(R.id.AutocompleteList);
        arrayOfResults = new ArrayList<>();
        autocompleteAdapter = new AutocompleteAdapter(this, arrayOfResults, searchEditText);
        autocompleteListView.setAdapter(autocompleteAdapter);
        autocompleteListView.setOnItemClickListener(this);

    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (intent.getStringExtra("QUERY")!=null){
            searchEditText.setText(intent.getStringExtra("QUERY"));
            searchEditText.setSelection(searchEditText.length());
        }
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        presenter.getAutocompleteSuggestions(s.toString(), this);
    }

    @Override
    public void afterTextChanged(Editable s) {

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent i = new Intent(SearchActivity.this, SearchResultActivity.class);
        TextView textView= view.findViewById(R.id.text);
        i.putExtra("QUERY", textView.getText().toString());
        startActivity(i);
    }

    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        if (actionId == EditorInfo.IME_ACTION_SEARCH) {
            Intent i = new Intent(SearchActivity.this, SearchResultActivity.class);
            i.putExtra("QUERY", v.getText().toString());
            startActivity(i);
            return true;
        }
        return false;
    }


    @Override
    public void onBeforeExecutingTask() {

    }

    @Override
    public void onDuringExecutingTask() {

    }

    @Override
    public void onAfterExecutingTask(Object result) {
        autocompleteAdapter.clear();
        if (!(result == null || result.equals(""))) {
            try {
                JSONArray root = new JSONArray((String) result);
                JSONArray suggestionArray = root.getJSONArray(1);
                for (int i = 0; i < suggestionArray.length(); i++) {
                    String suggestion = suggestionArray.getString(i);
                    autocompleteAdapter.add(suggestion);
                }
                autocompleteAdapter.notifyDataSetChanged();
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }
}
