package com.parse.starter;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.miguelcatalan.materialsearchview.MaterialSearchView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Locale;

public class SearchActivity extends AppCompatActivity  implements TextToSpeech.OnInitListener{

    private MaterialSearchView searchView;
    ArrayList<String> search=new ArrayList<>();
    ArrayAdapter arrayAdapter;
    static TextToSpeech tts;
    ListView listView;

    @Override
    public void onInit(int status) {

        if (status == TextToSpeech.SUCCESS) {


            int result = tts.setLanguage(new Locale("hin"));

            if (result == TextToSpeech.LANG_MISSING_DATA
                    || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Log.e("TTS", "This Language is not supported");
            }

        } else {
            Log.e("TTS", "Initilization Failed!");
        }
    }

    @Override
    public void onDestroy() {
        // Don't forget to shutdown tts!
        if (tts != null) {
            tts.stop();
            tts.shutdown();
        }
        super.onDestroy();
    }


    public class DownloadJson extends AsyncTask<String,Void,String>
    {

        @Override
        protected String doInBackground(String... params) {
            try {
                URL url=new URL(params[0]);
                HttpURLConnection connection=(HttpURLConnection)url.openConnection();
                connection.connect();
                InputStream input=connection.getInputStream();
                InputStreamReader reader=new InputStreamReader(input);

                String content="";
                int i;
                while((i=reader.read())!=-1)
                {
                    content+=(char)i;
                }

                Log.i("content",content+"");

                return content;
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(String s) {

            super.onPostExecute(s);

            Log.i("show",s);

            try {
                //in json { is for objet and [ is for array.
                //for mor einfo refer https://www.androidhive.info/2012/01/android-json-parsing-tutorial/

                //http://api.openweathermap.org/data/2.5/weather?q=Warangal&APPID=2e0ed1ef612cb289f311a1f75ade7c70


                JSONArray jsonarray=new JSONArray(s);

                for(int n = 0; n < jsonarray.length(); n++)
                {
                    JSONObject object = jsonarray.getJSONObject(n);

                    String str=object.getString("word");

                    search.add(str);

                    Log.i("string",str);

                    //get a reference to the textview on the log.xml file.

                    // do some stuff....
                }

                //log.xml is your file.


                arrayAdapter=new ArrayAdapter(SearchActivity.this,R.layout.list_view,R.id.textView,search);

                listView.setAdapter(arrayAdapter);

                arrayAdapter.notifyDataSetChanged();



                Log.i("Adapter set","Now!");

            } catch (JSONException e) {
                e.printStackTrace();
            }

        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

         listView = findViewById(R.id.search_list);

        tts = new TextToSpeech(getApplicationContext(), this);

        searchView = (MaterialSearchView) findViewById(R.id.search_view);
        searchView.setVoiceSearch(false);
        //searchView.setCursorDrawable(R.drawable.custom_cursor);
        searchView.setEllipsize(true);
        searchView.setSuggestions(getResources().getStringArray(R.array.query_suggestions));
        searchView.setOnQueryTextListener(new MaterialSearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                String url=new String("https://api.datamuse.com/words?ml="+query);

                DownloadJson downloadJson=new DownloadJson();

                downloadJson.execute(url);

               // Toast.makeText(SearchActivity.this,listView.getItemAtPosition(0).,Toast.LENGTH_SHORT).show();

                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                        String text=search.get(position);

                        speakOut(text);
                    }
                });

               Snackbar.make(findViewById(R.id.container), "Query: " + query, Snackbar.LENGTH_LONG)
                        .show();

                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                //Do some magic
                return false;
            }
        });

        searchView.setOnSearchViewListener(new MaterialSearchView.SearchViewListener() {

            @Override
            public void onSearchViewShown() {
                //Do some magic
                search.clear();
            }

            @Override
            public void onSearchViewClosed() {

                //Do some magic
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.item_search, menu);
        MenuItem item = menu.findItem(R.id.action_search);
        searchView.setMenuItem(item);
        return true;
    }

    @Override
    public void onBackPressed() {
        if (searchView.isSearchOpen()) {
            searchView.closeSearch();
        } else {

            super.onBackPressed();

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == MaterialSearchView.REQUEST_VOICE && resultCode == RESULT_OK) {
            ArrayList<String> matches = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            if (matches != null && matches.size() > 0) {
                String searchWrd = matches.get(0);
                if (!TextUtils.isEmpty(searchWrd)) {
                    searchView.setQuery(searchWrd, false);
                }
            }

            return;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void speakOut(String text) {

        tts.speak(text, TextToSpeech.QUEUE_FLUSH, null);
    }
}
