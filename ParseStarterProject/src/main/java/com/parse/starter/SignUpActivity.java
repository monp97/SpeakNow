package com.parse.starter;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;


import com.andreabaccega.widget.FormEditText;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.parse.SignUpCallback;
import com.pnikosis.materialishprogress.ProgressWheel;
import test.jinesh.captchaimageviewlib.CaptchaImageView;


public class SignUpActivity extends AppCompatActivity {


    private static final String TAG ="Hello" ;
    String gender = "";
    FormEditText captchaInput;
    CaptchaImageView captchaImageView;
    ImageView refreshButton;

    ProgressWheel progressWheel;
    LinearLayout linearLayout;





    public void maleSet(View view) {
        gender = "male";
    }

    public void femaleSet(View view) {
        gender = "female";
    }

    public void signupBackend(String name, String email, String address, String password, String mobile) {




        ParseUser user = new ParseUser();
        user.setUsername(name);
        user.setPassword(password);
        user.setEmail(email);
        user.put("address", address);
        user.put("mobile", mobile);
        user.put("loggedIn", "false");
        user.put("gender", gender);

        user.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
            }
        });

        user.signUpInBackground(new SignUpCallback() {

            @Override
            public void done(ParseException e) {

                if (e == null) {

                    Intent intent = new Intent(getApplicationContext(), LogInActivity.class);
                    startActivity(intent);
                } else {
                    final ProgressWheel progressWheel = findViewById(R.id.progress_wheel);
                    LinearLayout linearLayout = findViewById(R.id.linear1);
                    linearLayout.setVisibility(View.VISIBLE);
                    progressWheel.setVisibility(View.INVISIBLE);
                    progressWheel.stopSpinning();
                    Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                }

            }
        });
    }


    public void startLogIn(View view) {
        Intent intent = new Intent(getApplicationContext(), LogInActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);



        captchaImageView= (CaptchaImageView) findViewById(R.id.image_captcha);

        refreshButton=findViewById(R.id.regen);


        captchaImageView.post(new Runnable() {
            @Override
            public void run() {
                captchaImageView.setImageBitmap(loadBitmapFromView(captchaImageView));
            }
        });


        refreshButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                captchaImageView.regenerate();
            }
        });


    }


    public void onClickNext(View v) {


        final FormEditText fname = findViewById(R.id.input_name);
        final FormEditText email = findViewById(R.id.input_email);
        final FormEditText address = findViewById(R.id.input_address);
        final FormEditText password = findViewById(R.id.input_password);

        captchaInput=findViewById(R.id.input_captcha);


        FormEditText repassword = findViewById(R.id.input_repassword);
        final FormEditText mobile = findViewById(R.id.input_mobile);
        progressWheel = findViewById(R.id.progress_wheel);
        linearLayout = findViewById(R.id.linear1);



        FormEditText[] allFields = {fname, email, address, password, repassword, mobile};
        boolean allValid = true;
        for (FormEditText field : allFields) {
            allValid = field.testValidity() && allValid;
        }

        if (!(repassword.getText().toString()).equals(password.getText().toString())) {
            repassword.setError("Your password doesn't match!");
            Log.i("pass", repassword.getText().toString() + "  " + password.getText().toString());
            allValid = false;
        }
        if(captchaInput.getText().toString().equals(""))
        {
            Toast.makeText(this,"please fill captcha",Toast.LENGTH_SHORT).show();
            captchaImageView.regenerate();
            allValid=false;
        }
        if(!captchaInput.getText().toString().equals(captchaImageView.getCaptchaCode().toString()))
        {

            Toast.makeText(this,"please fill the correct captcha",Toast.LENGTH_SHORT).show();
            captchaInput.setText("");
            allValid=false;
        }
        if (allValid) {


            linearLayout.setVisibility(View.INVISIBLE);
            progressWheel.setVisibility(View.VISIBLE);
            progressWheel.spin();

            new android.os.Handler().postDelayed(
                    new Runnable() {
                        public void run() {
                            // On complete call either onLoginSuccess or onLoginFailed
                            signupBackend(fname.getText().toString(), email.getText().toString(), address.getText().toString(), password.getText().toString(), mobile.getText().toString());


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



    public static Bitmap loadBitmapFromView(View v) {
        Bitmap b = Bitmap.createBitmap( v.getWidth(), v.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(b);
        v.layout(0, 0, v.getWidth(), v.getHeight());
        v.draw(c);
        return b;
    }

}
