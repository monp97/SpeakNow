package com.parse.starter;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.andreabaccega.widget.FormEditText;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.pnikosis.materialishprogress.ProgressWheel;

public class LogInActivity extends AppCompatActivity {

    static String loggedIn="shekhar@gmail.com";


    public void loginBackground(final String email, String password)
    {
        ParseUser.logInInBackground(email, password, new LogInCallback() {
            @Override
            public void done(ParseUser user, ParseException e) {

                if(e!=null)
                {
                    final ProgressWheel progressWheel=findViewById(R.id.progress_wheel);

                    LinearLayout linearLayout=findViewById(R.id.linear1);


                    linearLayout.setVisibility(View.VISIBLE);
                    progressWheel.setVisibility(View.INVISIBLE);

                    Toast.makeText(getApplicationContext(),e.getMessage(),Toast.LENGTH_SHORT).show();
                }
                else
                {

                    loggedIn=email;
                    Log.i("current user :",loggedIn);
                    Intent intent=new Intent(getApplicationContext(),MainActivity.class);
                    startActivity(intent);
                }
            }
        });

    }


    public void startSignUp(View view)
    {
        Intent intent=new Intent(getApplicationContext(),SignUpActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);

    }

    public void onClickNext(View v) {

        final FormEditText email=findViewById(R.id.input_email);
        final FormEditText password=findViewById(R.id.input_password);

        final ProgressWheel progressWheel=findViewById(R.id.progress_wheel);

        LinearLayout linearLayout=findViewById(R.id.linear1);

        linearLayout.setVisibility(View.INVISIBLE);
        progressWheel.setVisibility(View.VISIBLE);

        progressWheel.spin();

        FormEditText[] allFields= {email,password};
        boolean allValid = true;
        for (FormEditText field: allFields) {
            allValid = field.testValidity() && allValid;
        }

        if (allValid) {

            //if everything is valid by syntax...apply backend validation..

            new android.os.Handler().postDelayed(
                    new Runnable() {
                        public void run() {
                            // On complete call either onLoginSuccess or onLoginFailed
                            loginBackground(email.getText().toString(),password.getText().toString());
                            progressWheel.stopSpinning();


                        }
                    }, 3000);





            // YAY
        } else {
            // EditText are going to appear with an exclamation mark and an explicative message.
        }

    }


    @Override
    public void onBackPressed() {
        // disable going back to the MainActivity
        moveTaskToBack(true);
    }

}
