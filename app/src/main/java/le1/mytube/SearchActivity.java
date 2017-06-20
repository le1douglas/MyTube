package le1.mytube;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;

import static le1.mytube.MainActivity.changeStatusBarColor;

public class SearchActivity extends AppCompatActivity implements TextWatcher, AdapterView.OnItemClickListener {
    ListView autocompleteListView;
    ArrayList arrayOfResults;
    AutocompleteAdapter autocompleteAdapter;
    ArrayList<String> idArray;
    ArrayList<Uri> uriArray;
    ArrayList titleArray;
    VideoResultAdapter videoResultAdapter;
    ListView videoResultListView;
    InputMethodManager imm;
    EditText searchEditText;
    ProgressBar loadingIcon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        //set toolbar boring stuff
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbarSearch);
        changeStatusBarColor("#DBDBDB", this);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        imm = (InputMethodManager) this.getSystemService(Activity.INPUT_METHOD_SERVICE);


        searchEditText = (EditText) findViewById(R.id.SearchEditText);
        autocompleteListView = (ListView) findViewById(R.id.AutocompleteList);
        videoResultListView = (ListView) findViewById(R.id.videoResult);
        loadingIcon = (ProgressBar) findViewById(R.id.loadingIcon);

        searchEditText.requestFocus();

        arrayOfResults = new ArrayList<>();
        idArray = new ArrayList<>();
        uriArray = new ArrayList<>();
        titleArray = new ArrayList<>();

        videoResultAdapter = new VideoResultAdapter(this, idArray, uriArray, titleArray);
        autocompleteAdapter = new AutocompleteAdapter(this, arrayOfResults, searchEditText);
        videoResultListView.setAdapter(videoResultAdapter);
        autocompleteListView.setAdapter(autocompleteAdapter);

        videoResultListView.setOnItemClickListener(this);
        autocompleteListView.setOnItemClickListener(this);
        searchEditText.addTextChangedListener(this);

        //on first lauch hide loading icon and show autocomplete suggestions
        loadingIcon.setVisibility(View.GONE);
        autocompleteListView.setVisibility(View.VISIBLE);


        searchEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    autocompleteListView.setVisibility(View.GONE);
                    videoResultListView.smoothScrollToPosition(0);
                    new SearchTask().execute(searchEditText.getText().toString());
                    imm.hideSoftInputFromWindow(searchEditText.getWindowToken(), 0);

                    return true;
                }
                return false;
            }
        });

        searchEditText.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    loadingIcon.setVisibility(View.GONE);
                    autocompleteListView.setVisibility(View.VISIBLE);
                    videoResultListView.setVisibility(View.GONE);
                    searchEditText.setSelection(searchEditText.length());
                    imm.showSoftInputFromInputMethod(searchEditText.getWindowToken(), 0);

                }

                return SearchActivity.super.onTouchEvent(event);
            }
        });


    }

    @Override
    public void onBackPressed() {
        if (autocompleteListView.getVisibility() == View.VISIBLE && videoResultListView.getCount() > 0) {
            autocompleteListView.setVisibility(View.GONE);
            videoResultListView.setVisibility(View.VISIBLE);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        if (TextUtils.isEmpty(s)) {
            autocompleteAdapter.clear();
        } else {
            new AutocompleteTask().execute(s.toString());
        }
    }

    @Override
    public void afterTextChanged(Editable s) {

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (parent == autocompleteListView) {
            TextView clickedSugestionTextView = (TextView) view.findViewById(R.id.text);
            searchEditText.setText(clickedSugestionTextView.getText().toString());
            searchEditText.setSelection(searchEditText.length());
            autocompleteListView.setVisibility(View.GONE);

            new SearchTask().execute(searchEditText.getText().toString());
            imm.hideSoftInputFromWindow(searchEditText.getWindowToken(), 0);

        } else if (parent == videoResultListView) {
            TextView idView = (TextView) view.findViewById(R.id.id);
            TextView videoTitle = (TextView) view.findViewById(R.id.title);
            String videoId = idView.getText().toString();
            Toast.makeText(this, "Loading...", Toast.LENGTH_SHORT).show();
            if (!isMyServiceRunning(MusicService.class)) {
                Intent intent = new Intent(SearchActivity.this, MusicService.class);
                intent.putExtra("videoId", videoId);
                intent.putExtra("title", videoTitle.getText().toString());
                startService(intent);
            } else {
                MusicService.startSong(videoId, videoTitle.getText().toString(), this);
            }
        }
    }

    public boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    private class AutocompleteTask extends AsyncTask<String, Void, String> {

        URL url;


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }


        @Override
        protected String doInBackground(String... params) {

            try {
                url = new URL("http://suggestqueries.google.com/complete/search?client=firefox&ds=yt&q=" + Uri.encode(params[0]));
                String JSON_string;
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                InputStream inputStream = httpURLConnection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                StringBuilder stringBuilder = new StringBuilder();
                while ((JSON_string = bufferedReader.readLine()) != null) {
                    stringBuilder.append(JSON_string + "\r\n");
                }

                bufferedReader.close();
                inputStream.close();
                httpURLConnection.disconnect();
                return stringBuilder.toString().trim();

            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;

        }

        @Override
        protected void onPostExecute(String result) {
            if (!(result == null || result.equals(""))) {
                try {
                    autocompleteAdapter.clear();
                    JSONArray root = new JSONArray(result);
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

    private class SearchTask extends AsyncTask<String, String, JSONObject> {

        final static int maxResults = 20;

        @Override
        protected void onPreExecute() {

            loadingIcon.setVisibility(View.VISIBLE);
            super.onPreExecute();
        }

        @Override
        protected JSONObject doInBackground(String... params) {

            HttpURLConnection urlConnection;

            URL url;
            try {

                final String encodedURL = URLEncoder.encode(params[0], "UTF-8");
                url = new URL("https://www.googleapis.com/youtube/v3/search?part=snippet&q=" + encodedURL + "&maxResults=" + maxResults + "&type=video&key=AIzaSyBqXp0Uo2ktJcMRpL_ZwF5inLTWZfsCYqY");


                urlConnection = (HttpURLConnection) url.openConnection();

                urlConnection.setRequestMethod("GET");
                urlConnection.setReadTimeout(10000 /* milliseconds */);
                urlConnection.setConnectTimeout(15000 /* milliseconds */);

                urlConnection.setDoOutput(true);

                urlConnection.connect();

                BufferedReader br = new BufferedReader(new InputStreamReader(url.openStream()));

                String jsonString;

                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = br.readLine()) != null) {
                    sb.append(line + "\n");
                }
                br.close();

                jsonString = sb.toString();


                return new JSONObject(jsonString);
            } catch (MalformedURLException e) {

                e.printStackTrace();
                return null;
            } catch (ProtocolException e) {

                e.printStackTrace();
                return null;
            } catch (IOException e) {

                e.printStackTrace();
                return null;
            } catch (JSONException e) {

                e.printStackTrace();
                return null;
            }


        }

        @Override
        protected void onPostExecute(JSONObject result) {
            if (result != null) {
                videoResultListView.smoothScrollToPosition(0);
                videoResultListView.setVisibility(View.VISIBLE);
                loadingIcon.setVisibility(View.GONE);
                //System.out.println("JSON: " + result);


                try {
                    JSONArray itemArray = result.getJSONArray("items");
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
                            String title = snippet.getString("title");

                            idArray.add(idString);
                            uriArray.add(Uri.parse(imageString));
                            titleArray.add(title);

                        }
                        videoResultAdapter.notifyDataSetChanged();
                    } else {
                        Toast.makeText(SearchActivity.this, "No results found", Toast.LENGTH_SHORT).show();
                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

        }
    }

}
