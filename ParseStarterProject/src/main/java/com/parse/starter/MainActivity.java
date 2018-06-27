/*
 * Copyright (c) 2015-present, Parse, LLC.
 * All rights reserved.
 *
 * This source code is licensed under the BSD-style license found in the
 * LICENSE file in the root directory of this source tree. An additional grant
 * of patent rights can be found in the PATENTS file in the same directory.
 */
package com.parse.starter;



import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.parse.FindCallback;
import com.parse.ParseAnalytics;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.List;

import it.sephiroth.android.library.bottomnavigation.BottomNavigation;


public class MainActivity extends AppCompatActivity implements SpeakFragment.OnFragmentInteractionListener,WriteFragment.OnFragmentInteractionListener,ProfileFragment.OnFragmentInteractionListener,page2Fragment.OnFragmentInteractionListener,page3Fragment.OnFragmentInteractionListener {


    static BottomNavigation bottomNavigation;


  @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    setContentView(R.layout.activity_main);


     final ParseQuery<ParseUser> parseQuery=ParseUser.getQuery();

      parseQuery.whereEqualTo("email","shekhar@gmail.com");

    parseQuery.findInBackground(new FindCallback<ParseUser>() {

      @Override
      public void done(List<ParseUser> objects, ParseException e) {


          if(e==null)
          {
              if(objects.size()>0)
              {

                  for(ParseUser parseUser:objects)
                  {
                      if(parseUser.getString("loggedIn").equals("false"))
                      {
                          Log.i("first log in user::::",parseUser.getUsername());

                          parseUser.put("loggedIn","true");

                          parseUser.saveInBackground();

                          MediaPlayer mediaPlayer=MediaPlayer.create(getApplicationContext(),R.raw.speakmain);
                          mediaPlayer.start();


                      }
                  }

              }
              else
              {
                  Log.i("hey you!","hi");
              }
          }
          else
          {
              Log.i("error",e.getMessage());
          }

      }

    });

     bottomNavigation=findViewById(R.id.BottomNavigation);

      bottomNavigation.setDefaultSelectedIndex(0);

      loadFragment(new SpeakFragment());


      final LinearLayout linearLayout=findViewById(R.id.linear1);

      bottomNavigation.setOnMenuItemClickListener(new BottomNavigation.OnMenuItemSelectionListener() {
          @Override
          public void onMenuItemSelect(@IdRes int i, int i1, boolean b) {


              if(i1==0)
              {

                  linearLayout.setVisibility(View.VISIBLE);
                  loadFragment(new SpeakFragment());



              }
              else if(i1==1)
              {
                  linearLayout.setVisibility(View.INVISIBLE);
                  loadFragment(new WriteFragment());

              }
              else if(i1==2)
              {
                  linearLayout.setVisibility(View.INVISIBLE);
                  Intent intent=new Intent(getApplicationContext(),SearchActivity.class);
                         startActivity(intent);
              }
              else if(i1==3)
              {
                  linearLayout.setVisibility(View.INVISIBLE);
                  loadFragment(new ProfileFragment());
              }



          }

          @Override
          public void onMenuItemReselect(@IdRes int i, int i1, boolean b) {



              if(i1==0)
              {


                  linearLayout.setVisibility(View.VISIBLE);
                  loadFragment(new SpeakFragment());
              }
              else if(i1==1)
              {


                  linearLayout.setVisibility(View.INVISIBLE);
                  loadFragment(new WriteFragment());

              }
              else if(i1==2)
              {


                  linearLayout.setVisibility(View.INVISIBLE);
                  Intent intent=new Intent(getApplicationContext(),SearchActivity.class);
                  startActivity(intent);

              }
              else if(i1==3)
              {


                  linearLayout.setVisibility(View.INVISIBLE);
                  loadFragment(new ProfileFragment());
              }


          }
      });

    ParseAnalytics.trackAppOpenedInBackground(getIntent());




  }



    public void loadFragment(Fragment fragment) {
// create a FragmentManager
         FragmentManager fm = getSupportFragmentManager();
// create a FragmentTransaction to begin the transaction and replace the Fragment
        FragmentTransaction fragmentTransaction = fm.beginTransaction();
// replace the FrameLayout with new Fragment
        fragmentTransaction.replace(R.id.frame, fragment);
        fragmentTransaction.commit(); // save the changes

    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    public void speak(View v)
    {
        new SpeakFragment().speak1(v);
    }

    public void speakPage2(View v)
    {

        new page2Fragment().speak1(v);

    }

    public void speakPage3(View v)
    {
        new page3Fragment().speak1(v);
    }

    public void save(View v)
    {
        ParseObject parseObject=new ParseObject("SavedSentences");

        final EditText editText=findViewById(R.id.full_text);

        parseObject.put("email",LogInActivity.loggedIn);

        parseObject.put("sentence",editText.getText().toString());

        parseObject.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {

                Log.i("saved","succesfull");
                editText.setText("");
            }
        });

    }

    public void cancel(View v)
    {
        EditText editText=findViewById(R.id.full_text);

        editText.setText("");
    }
}