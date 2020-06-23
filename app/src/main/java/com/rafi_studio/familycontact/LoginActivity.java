package com.rafi_studio.familycontact;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.InputType;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;
import com.google.android.material.snackbar.Snackbar;

public class LoginActivity extends AppCompatActivity {

    private ConstraintLayout constraintLayout_yes_or_not;
    private ConstraintLayout linearLayout_notAnswer;
    private ConstraintLayout constraintLayout_angryAnswer;
    private LinearLayout exit_app;
    private Button yes;
    private Button no;

    private LinearLayout linearLayout_answer;
    private LinearLayout linearLayout_login;
    private EditText userName;
    private EditText password;
    private CheckBox showPassword;
    private Button login;

    private ProgressDialog progressDialog;

    private Session session;
    private String congratulationsToast, loginError;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_login);

        session = new Session(this);
        constraintLayout_yes_or_not = findViewById(R.id.constraint_answer);
        linearLayout_notAnswer = findViewById(R.id.linearLayout_notAnswer);
        constraintLayout_angryAnswer = findViewById(R.id.constraintLayout_angryAnswer);
        exit_app = findViewById(R.id.exit_app);
        yes = findViewById(R.id.yes);
        no = findViewById(R.id.no);
        linearLayout_answer = findViewById(R.id.linearLayout_yesAnswer);
        linearLayout_login = findViewById(R.id.login_linearLayout) ;
        userName = findViewById(R.id.username);
        password = findViewById(R.id.password);
        showPassword = findViewById(R.id.showpassword_checkBox);
        login = findViewById(R.id.login);

        progressDialog = new ProgressDialog(LoginActivity.this);
        congratulationsToast = getResources().getString(R.string.loginActivity_congratulationsToast);
        loginError = getResources().getString(R.string.loginActivity_ErrorLogin);

        final Animation slideFromLeft = AnimationUtils.loadAnimation(this, R.anim.slide_from_left);
        final Animation slideFromRight = AnimationUtils.loadAnimation(this, R.anim.slide_from_right);
        final Animation slideUp = AnimationUtils.loadAnimation(this, R.anim.slide_up);

        no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                linearLayout_answer.setVisibility(View.GONE);
                linearLayout_login.setVisibility(View.GONE);
                constraintLayout_yes_or_not.setVisibility(View.GONE);
                constraintLayout_angryAnswer.setVisibility(View.VISIBLE);
                constraintLayout_angryAnswer.startAnimation(slideFromRight);
                linearLayout_notAnswer.setVisibility(View.VISIBLE);
                linearLayout_notAnswer.startAnimation(slideUp);
            }
        });

        exit_app.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                LoginActivity.super.onBackPressed();
            }
        });

        yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                linearLayout_answer.setVisibility(View.VISIBLE);
                linearLayout_answer.startAnimation(slideFromLeft);
                linearLayout_login.setVisibility(View.VISIBLE);
                linearLayout_login.startAnimation(slideFromRight);
                constraintLayout_yes_or_not.setVisibility(View.GONE);
            }
        });

        if(session.loggedin()){
            startActivity(new Intent(LoginActivity.this,MainActivity.class));
            finish();
        }

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginUser();
            }
        });

        showPassword.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    password.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                }else {
                    password.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                }
            }
        });
    }

    private int loginUser() {
        final String singingIn = getResources().getString(R.string.mainActivity_signingIn);

        progressDialog.setMessage(singingIn);
        progressDialog.show();

        if(userName.getText().toString().equals("baundule@developer.com") &&
                password.getText().toString().equals("baundule")) {

            session.setLoggedin(true);
            Toast.makeText(getApplicationContext(), congratulationsToast, Toast.LENGTH_SHORT).show();
            Intent i = new Intent(LoginActivity.this, MainActivity.class);
            progressDialog.hide();
            startActivity(i);
            finish();
            overridePendingTransition(R.anim.slide_from_right,R.anim.slideout_from_left);

        }else{
            progressDialog.hide();
            showSnackbar(loginError);
        }
        return 0;
    }

    private Boolean exit = false;
    @Override
    public void onBackPressed() {
        if (exit) {
            finish(); // finish activity
        } else {
            Toast.makeText(this, "Press Back again to Exit.",
                    Toast.LENGTH_SHORT).show();
            exit = true;
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    exit = false;
                }
            }, 3 * 1000);
        }
    }

    public void showSnackbar(String stringSnackbar){
        Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content), stringSnackbar,Snackbar.LENGTH_LONG);
        View view = snackbar.getView();
        view.setBackgroundColor(ContextCompat.getColor(this, R.color.colorPrimaryDark));
        snackbar.show();
    }

}

