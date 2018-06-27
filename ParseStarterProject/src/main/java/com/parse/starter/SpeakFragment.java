package com.parse.starter;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.support.transition.Slide;
import android.support.transition.TransitionInflater;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.labo.kaji.fragmentanimations.CubeAnimation;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link SpeakFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link SpeakFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SpeakFragment extends Fragment  implements TextToSpeech.OnInitListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;


    static TextToSpeech tts;

    static Button button;

    private OnFragmentInteractionListener mListener;


    ImageView next;

    static View myFragmentView;

    static EditText editText;



    public SpeakFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SpeakFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SpeakFragment newInstance(String param1, String param2) {
        SpeakFragment fragment = new SpeakFragment();
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

         editText=getActivity().findViewById(R.id.full_text);

        myFragmentView = inflater.inflate(R.layout.fragment_speak, container, false);

        tts = new TextToSpeech(getActivity(), this);

        final Button b1=myFragmentView.findViewById(R.id.button_5);

        final Button b2=myFragmentView.findViewById(R.id.button_7);

        button=myFragmentView.findViewById(R.id.button_1);

        next=myFragmentView.findViewById(R.id.next);

        String message=findTime();

        b2.setText(message);

        ParseQuery<ParseUser> parseQuery=ParseUser.getQuery();

        parseQuery.whereEqualTo("email","shekhar@gmail.com");

        parseQuery.setLimit(1);

        parseQuery.findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> objects, ParseException e) {

                String name=objects.get(0).getUsername();

                b1.setText(name);

                Log.i("name set",name);

            }
        });



        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ((FrameLayout)myFragmentView.findViewById(R.id.frame1)).removeAllViews();

                changePageNext();

            }
        });

        return myFragmentView;

    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
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


    public String findTime()
    {
        Calendar c = Calendar.getInstance();
        int timeOfDay = c.get(Calendar.HOUR_OF_DAY);

        if(timeOfDay >= 0 && timeOfDay < 12){
            return "Good morning";
        }else if(timeOfDay >= 12 && timeOfDay < 16){
            return "good afternoon";
        }else if(timeOfDay >= 16 && timeOfDay < 21){
            return "good evening";
        }else if(timeOfDay >= 21 && timeOfDay < 24){
            return "good night";
        }

        return "hii";
    }

    public void changePageNext()
    {
        FragmentManager childFragMan = getChildFragmentManager();
        FragmentTransaction childFragTrans = childFragMan.beginTransaction();
        page2Fragment fragB = new page2Fragment();
        childFragTrans.add(R.id.frame1, fragB);
        childFragTrans.addToBackStack(null);
        childFragTrans.commit();
    }

    private void speakOut() {

        String text = button.getText().toString();

       // String text_present=editText.getText().toString();

        display(text);

        Log.i("hello",text);

        tts.speak(text, TextToSpeech.QUEUE_FLUSH,null);
    }


    public void speak1(View v)
    {
          int id=v.getId();
        button=myFragmentView.findViewById(id);
        speakOut();

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

    public static void display(String text)
    {

        editText.setText(editText.getText().toString()+" "+text);
    }




}
