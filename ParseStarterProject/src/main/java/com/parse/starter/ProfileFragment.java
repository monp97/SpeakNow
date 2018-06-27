package com.parse.starter;

import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import com.ibm.watson.developer_cloud.http.ServiceCallback;
import com.ibm.watson.developer_cloud.tone_analyzer.v3.ToneAnalyzer;
import com.ibm.watson.developer_cloud.tone_analyzer.v3.model.ToneAnalysis;
import com.ibm.watson.developer_cloud.tone_analyzer.v3.model.ToneOptions;
import com.jpardogo.android.flabbylistview.lib.FlabbyLayout;
import com.labo.kaji.fragmentanimations.CubeAnimation;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

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
import java.util.List;
import java.util.Locale;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ProfileFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ProfileFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ProfileFragment extends Fragment  implements TextToSpeech.OnInitListener{


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



                    Log.i("string",str);

                    //get a reference to the textview on the log.xml file.

                    // do some stuff....
                }

                //log.xml is your file.

                Log.i("Adapter set","Now!");

            } catch (JSONException e) {
                e.printStackTrace();
            }

        }

    }
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    ArrayList<String> arrayList=new ArrayList<>();

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    TextToSpeech tts;

    View myFragment;



    private OnFragmentInteractionListener mListener;

    android.widget.ListAdapter mAdapter;

    public ProfileFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ProfileFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ProfileFragment newInstance(String param1, String param2) {
        ProfileFragment fragment = new ProfileFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        myFragment=inflater.inflate(R.layout.fragment_profile, container, false);

        ParseQuery<ParseObject> parseQuery=ParseQuery.getQuery("SavedSentences");

        parseQuery.whereEqualTo("email",LogInActivity.loggedIn);

        final ListView listView=myFragment.findViewById(R.id.list);

        Button button=myFragment.findViewById(R.id.analyze);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Analyze();
            }
        });


        //https://gateway.watsonplatform.net/tone-analyzer/api/v3/tone?version=2016-05-19&text=%22dada%20is%20ill%22

        tts = new TextToSpeech(getActivity(), this);

        //final ArrayAdapter<String> arrayAdapter=new ArrayAdapter<String>(getActivity(),R.layout.sentences,R.id.text,arrayList);

        //listView.setAdapter(arrayAdapter);



        //getListView().setSelection(arrayList.size()/2);

        parseQuery.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {

                for(ParseObject object:objects)
                {
                    String sent=object.getString("sentence");
                    Log.i("Added",sent);
                    arrayList.add(sent);
                }

                mAdapter = new ListAdapter(getContext(),arrayList);
                listView.setAdapter(mAdapter);



                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                        String speak=arrayList.get(position).toString();

                        speakOut(speak);

                    }
                });

            }
        });



        return myFragment;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
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

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onInit(int status) {
        if ( status == TextToSpeech.SUCCESS ) {

            int result = tts.setLanguage(new Locale("hin"));

            if (result == TextToSpeech.LANG_MISSING_DATA
                    || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Log.e("TTS", "This Language is not supported");
            }

        } else {
            Log.e("TTS", "Initilization Failed!");
        }
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    @Override
    public Animation onCreateAnimation(int transit, boolean enter, int nextAnim) {
        return CubeAnimation.create(CubeAnimation.UP, enter,1000);
    }


    private void speakOut(String text) {

        tts.speak(text, TextToSpeech.QUEUE_FLUSH,null);
    }


  public void Analyze()
  {

      ToneAnalyzer service = new ToneAnalyzer("2017-09-21");

      service.setUsernameAndPassword("199159fc-a637-4793-a1f0-f0fc4358bf99", "33fCrkrpwP8m");

      //service.setEndPoint("https://gateway-fra.watsonplatform.net/tone-analyzer/api");

      ListView listView=myFragment.findViewById(R.id.list);

      String str="";

      for (int i=0;i!=listView.getChildCount();i++)
      {
         str+=arrayList.get(i)+",";
          Log.i("sentence",arrayList.get(i));
      }

      Log.i("sentence",str);

      ToneOptions toneOptions = new ToneOptions.Builder().text("I cannot swallow").build();

      service.tone(toneOptions).enqueue(new ServiceCallback<ToneAnalysis>() {


          @Override
          public void onResponse(ToneAnalysis response) {

              Log.i("Sentiment",response.toString());
          }

          @Override public void onFailure(Exception e) {

          }
      });

      Log.i("hellooo","how are");

  }

}
