package le1.mytube.presentation.ui.search;

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
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import le1.mytube.R;
import le1.mytube.presentation.adapters.AutocompleteAdapter;
import le1.mytube.presentation.ui.searchResult.SearchResultActivity;

import static le1.mytube.presentation.ui.main.MainActivity.changeStatusBarColor;

public class SearchActivity extends AppCompatActivity implements SearchContract.View, TextWatcher, AdapterView.OnItemClickListener, TextView.OnEditorActionListener {
    ArrayList<String> arrayOfResults;
    AutocompleteAdapter autocompleteAdapter;
    EditText searchEditText;
    SearchPresenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        Toolbar toolbar = findViewById(R.id.toolbarSearch);
        changeStatusBarColor("#DBDBDB", this);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        presenter = ViewModelProviders.of(this).get(SearchPresenter.class);
        presenter.setContractView(this);

        searchEditText = findViewById(R.id.SearchEditText);
        searchEditText.addTextChangedListener(this);
        searchEditText.setOnEditorActionListener(this);
        searchEditText.requestFocus();

        ListView autocompleteListView = findViewById(R.id.AutocompleteList);
        arrayOfResults = new ArrayList<>();
        autocompleteAdapter = new AutocompleteAdapter(this, arrayOfResults, searchEditText);
        autocompleteListView.setAdapter(autocompleteAdapter);
        autocompleteListView.setOnItemClickListener(this);

    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (intent.getStringExtra("QUERY") != null) {
            searchEditText.setText(intent.getStringExtra("QUERY"));
            searchEditText.setSelection(searchEditText.length());
        }
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        presenter.loadAutocompleteSuggestions(s.toString());
    }

    @Override
    public void afterTextChanged(Editable s) {

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        TextView textView = view.findViewById(R.id.text);
        startSearchResultActivity(textView.getText().toString());
    }

    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        if (actionId == EditorInfo.IME_ACTION_SEARCH) {
            startSearchResultActivity(v.getText().toString());
            return true;
        }
        return false;
    }

    private void startSearchResultActivity(String query) {
        Intent i = new Intent(SearchActivity.this, SearchResultActivity.class);
        i.putExtra("QUERY", query);
        startActivity(i);

    }

    @Override
    public void onSearchResultLoaded(List<String> suggestions) {
        autocompleteAdapter.clear();
        autocompleteAdapter.addAll(suggestions);
        autocompleteAdapter.notifyDataSetChanged();
    }

    @Override
    public void onSearchResultError(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}
