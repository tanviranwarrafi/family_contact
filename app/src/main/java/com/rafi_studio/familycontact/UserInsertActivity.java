package com.rafi_studio.familycontact;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Base64;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class UserInsertActivity extends AppCompatActivity {

    private EditText englishName;
    private EditText banglaName;
    private EditText imageName;
    private EditText englishMobileNo;
    private EditText banglaMobileNo;
    private EditText designation;
    private EditText email;
    private Button selectImage;
    private ImageView imageViewer;
    private ProgressBar progressBar;
    private Button insertMemberBtn;

    private ActionBar actionBar;
    private String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+.[a-z]+";

    private final int IMG_REQUEST = 1;
    Bitmap bitmap;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_insert);

        actionBar = getSupportActionBar();
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        englishName = findViewById(R.id.userInsert_englishName);
        banglaName = findViewById(R.id.userInsert_banglaName);
        imageName = findViewById(R.id.userInsert_image_name);
        englishMobileNo = findViewById(R.id.userInsert_english_mobile_no);
        banglaMobileNo = findViewById(R.id.userInsert_bangla_mobile_no);
        designation = findViewById(R.id.userInsert_designation);
        email = findViewById(R.id.userInsert_email);
        selectImage = findViewById(R.id.userSelect_image);
        imageViewer = findViewById(R.id.userInsert_imageView);
        progressBar = findViewById(R.id.userInsert_progressbar);
        insertMemberBtn = findViewById(R.id.userInsert_member_btn);

        englishName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                checkInputs();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        banglaName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                checkInputs();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


        englishMobileNo.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                checkInputs();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        banglaMobileNo.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                checkInputs();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        selectImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImageFromDevice();
            }
        });

        insertMemberBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                insertData();
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home){
            Intent intent = new Intent(UserInsertActivity.this, MainActivity.class);
            startActivity(intent);
            this.finish();
            overridePendingTransition(R.anim.slide_from_right,R.anim.slideout_from_left);
        }

        return super.onOptionsItemSelected(item);
    }

    private void checkInputs() {
        if (!TextUtils.isEmpty(englishName.getText())) {
            if (!TextUtils.isEmpty(banglaName.getText())) {
                    if (!TextUtils.isEmpty(englishMobileNo.getText())) {
                        if (!TextUtils.isEmpty(englishMobileNo.getText())) {
                                   insertMemberBtn.setEnabled(true);
                                   insertMemberBtn.setTextColor(Color.rgb(255, 255,255));
                                } else {
                                    insertMemberBtn.setEnabled(false);
                                }
                            } else {
                                insertMemberBtn.setEnabled(false);
                            }
                        }  else {
                insertMemberBtn.setEnabled(false);
            }
        }
    }

    public void onBackPressed() {
        Intent intent = new Intent(UserInsertActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
        super.onBackPressed();
    }

    private void selectImageFromDevice() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, IMG_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == IMG_REQUEST && resultCode == RESULT_OK && data != null) {
            Uri path = data.getData();
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), path);
                imageViewer.setImageBitmap(bitmap);
                imageViewer.setVisibility(View.VISIBLE);
                imageName.setVisibility(View.VISIBLE);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private String imageToString(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
        byte[] imageBytes = byteArrayOutputStream.toByteArray();
        return Base64.encodeToString(imageBytes, Base64.DEFAULT);
    }

    private void insertData() {

        String mobileNoMustBeInElevenDigit = getResources().getString(R.string.userInsertActivity_mobileNoMustBeInElevenDigit);
        String invalidEmailAddress = getResources().getString(R.string.userInsertActivity_invalidEmailAddress);

        Drawable customErrorIcon = getResources().getDrawable(R.mipmap.alert);
        customErrorIcon.setBounds(0,0,customErrorIcon.getIntrinsicWidth(),customErrorIcon.getIntrinsicHeight());

        if (email.getText().toString().matches(emailPattern)){
            if(englishMobileNo.length()>10 && englishMobileNo.length()<12){
                if (banglaMobileNo.length()>10 && banglaMobileNo.length()<12){

                    progressBar.setVisibility(View.VISIBLE);
                    StringRequest stringRequest = new StringRequest(Request.Method.POST, Config.uploadURL,
                            new Response.Listener<String>() {
                                @Override
                                public void onResponse(String response) {
                                    try {
                                        progressBar.setVisibility(View.INVISIBLE);
                                        JSONObject jsonObject = new JSONObject(response);
                                        String Response = jsonObject.getString("response");

                                        Toast.makeText(UserInsertActivity.this, Response, Toast.LENGTH_SHORT).show();
                                        Intent intent = new Intent(UserInsertActivity.this, MainActivity.class);
                                        startActivity(intent);
                                        finish();
                                        overridePendingTransition(R.anim.slide_from_right,R.anim.slideout_from_left);

                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }

                                }
                            }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            progressBar.setVisibility(View.INVISIBLE);
                        }
                    }) {
                        @Override
                        protected Map<String, String> getParams() throws AuthFailureError {
                            Map<String, String> params = new HashMap<>();

                            params.put("image", imageName.getText().toString());
                            params.put("image_select", imageToString(bitmap));

                            params.put("english_name", englishName.getText().toString());
                            params.put("bangla_name", banglaName.getText().toString());
                            params.put("english_mobile_no", englishMobileNo.getText().toString());
                            params.put("bangla_mobile_no", banglaMobileNo.getText().toString());
                            params.put("designation", designation.getText().toString());
                            params.put("email", email.getText().toString());

                            return params;
                        }
                    };

                    MySingleton.getInstance(UserInsertActivity.this).addToRequestQueue(stringRequest);

                } else {
                    banglaMobileNo.setError(mobileNoMustBeInElevenDigit, customErrorIcon);
                }
            } else {
                englishMobileNo.setError(mobileNoMustBeInElevenDigit, customErrorIcon);
            }
        } else {
            email.setError(invalidEmailAddress, customErrorIcon);
        }
    }
}