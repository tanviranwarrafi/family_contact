package com.rafi_studio.familycontact;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private Session session;

    private TextView menutext;
    private ImageView menuImage;
    private Button selectSearchOrText;
    private SearchView searchView;
    private ProgressBar progressBar;
    private FloatingActionButton floatingActionButton;
    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView recyclerView;
    private List<ContactModel> contactModelList = new ArrayList<>();

    private ContactAdapter contactAdapter;
    private RequestQueue mRequestQueue;
    private JSONArray jsonArray = null;
    public static final String JSON_ARRAY = "result";

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Window window = this.getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(this,R.color.statusBar));

        menutext = findViewById(R.id.menu_text);
        menuImage = findViewById(R.id.answerImage);
        selectSearchOrText = findViewById(R.id.select_search_btn);
        searchView = findViewById(R.id.searchView);
        recyclerView = findViewById(R.id.recyclerView);
        progressBar = findViewById(R.id.progressBar);
        floatingActionButton = findViewById(R.id.floatingActionButton);
        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);

        swipeRefreshLayout.setColorSchemeResources(R.color.yellow);

        final Animation slideFadeIn = AnimationUtils.loadAnimation(this, R.anim.fade_in);
        final Animation slidefromLeft = AnimationUtils.loadAnimation(this, R.anim.slide_from_left);

        ConnectivityManager connectivityManager = (ConnectivityManager)
                getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if (networkInfo == null || !networkInfo.isConnected() || !networkInfo.isAvailable()){
            noInternetConnectionDialog();

        } else {
            // When Internet is Active
            selectSearchOrText.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (searchView.getVisibility() == View.GONE) {
                        searchView.setVisibility(View.VISIBLE);
                        searchView.startAnimation(slidefromLeft);
                        menutext.setVisibility(View.GONE);
                        menuImage.setVisibility(View.GONE);
                    } else {
                        menuImage.setVisibility(View.VISIBLE);
                        menuImage.startAnimation(slideFadeIn);
                        menutext.setVisibility(View.VISIBLE);
                        menutext.startAnimation(slideFadeIn);
                        searchView.setVisibility(View.GONE);
                    }
                }
            });

            session = new Session(this);
            if(!session.loggedin()){
                logout();
            }

            LinearLayoutManager layoutManager = new LinearLayoutManager(this);
            layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
            recyclerView.setLayoutManager(layoutManager);

            retriveContacts();

            searchView.setActivated(true);
            searchView.setIconified(true);
            searchView.clearFocus();

            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {
                    return true;
                }

                @Override
                public boolean onQueryTextChange(String newText) {
                    if (contactAdapter != null){
                        contactAdapter.getFilter().filter(newText);
                    }
                    return true;
                }
            });

            swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    swipeToRefreshData();
                }
            });

            floatingActionButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    floatingDialog();
                }
            });
        }
    }

    @Override
    public void onBackPressed() {
        finish();
        super.onBackPressed();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    private void noInternetConnectionDialog() {

        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_network);
        dialog.setCanceledOnTouchOutside(false);

        dialog.getWindow().setLayout(WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().getAttributes().windowAnimations = android.R.style.Animation_Dialog;

        Button tryAgain = dialog.findViewById(R.id.dialogNetwork_tryAgainbtn);
        tryAgain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recreate();
            }
        });

        dialog.show();
    }

    private void logout(){
        session.setLoggedin(false);
        finish();
        startActivity(new Intent(MainActivity.this,LoginActivity.class));
    }

    private void retriveContacts() {

        progressBar.setVisibility(View.VISIBLE);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, Config.retriveURL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        /*Log.d("response","Responsed Data" +response);*/

                        progressBar.setVisibility(View.INVISIBLE);
                        JSONObject jsonObject = null;
                        contactModelList.clear();

                        try {
                            jsonObject = new JSONObject(response);
                            jsonArray = jsonObject.getJSONArray(JSON_ARRAY);

                            if (jsonArray.length() == 0) {
                                Toast.makeText(MainActivity.this, "No Data Recorded...", Toast.LENGTH_SHORT).show();
                            } else {
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject object = jsonArray.getJSONObject(i);

                                    ContactModel contactModel = new ContactModel();

                                    contactModel.setId(object.getInt("id"));
                                    contactModel.setEnglishName(object.getString("english_name"));
                                    contactModel.setBanglaName(object.getString("bangla_name"));
                                    contactModel.setEnglish_mobile_no(object.getString("english_mobile_no"));
                                    contactModel.setBangla_mobile_no(object.getString("bangla_mobile_no"));
                                    contactModel.setDesignation(object.getString("designation"));
                                    contactModel.setEmail(object.getString("email"));
                                    contactModel.setImage(object.getString("image"));

                                    contactModelList.add(contactModel);
                                }
                                contactAdapter = new ContactAdapter(contactModelList, MainActivity.this);
                                recyclerView.setAdapter(contactAdapter);
                                contactAdapter.notifyDataSetChanged();
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progressBar.setVisibility(View.INVISIBLE);
                        String noInternet = getResources().getString(R.string.mainActivity_snackbar_noInternet);
                        noInternetSnackbar(noInternet);
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();

                return params;
            }
        };

        mRequestQueue = Volley.newRequestQueue(MainActivity.this);
        mRequestQueue.add(stringRequest);
    }

    private void floatingDialog() {

        final Dialog floatingDialog = new Dialog(MainActivity.this);
        floatingDialog.setContentView(R.layout.dialog_floating);
        floatingDialog.setCancelable(true);

        floatingDialog.getWindow().getAttributes().windowAnimations = R.style.TopToBottom;
        floatingDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        final LinearLayout text_and_buttonLayout = floatingDialog.findViewById(R.id.floatingDialog_text_and_buttonLayout);
        LinearLayout call_admin = floatingDialog.findViewById(R.id.floatingDialog_callAdminLayout);
        final LinearLayout goto_login = floatingDialog.findViewById(R.id.floatingDialog_gotoLoginLayout);

        final LinearLayout loginLayout = floatingDialog.findViewById(R.id.floating_loginLayout);
        final EditText username = floatingDialog.findViewById(R.id.floating_username);
        final EditText password = floatingDialog.findViewById(R.id.floating_password);
        LinearLayout loginBtn = floatingDialog.findViewById(R.id.floating_loginBtn);
        LinearLayout cancelbtn = floatingDialog.findViewById(R.id.floating_cancelBtn);
        final ProgressDialog progressDialog = new ProgressDialog(MainActivity.this);
        final CheckBox rememberMe = floatingDialog.findViewById(R.id.rememberme);

        final SharedPreferences sharedPreferences;
        String PREFS_NAME = "PrefsFile";
        sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);

        SharedPreferences sp = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        if (sp.contains("pref_name")) {
            String user = sp.getString("pref_name", "not found");
            username.setText(user.toString());
        }
        if (sp.contains("pref_pass")){
            String pass = sp.getString("pref_pass", "not found");
            password.setText(pass.toString());
        }
        if (sp.contains("pref_check")){
            Boolean bool = sp.getBoolean("pref_check", false);
            rememberMe.setChecked(bool);
        }

        final Animation slideFromLeft = AnimationUtils.loadAnimation(this, R.anim.slide_from_left);
        final Animation slideFromRight = AnimationUtils.loadAnimation(this, R.anim.slide_from_right);
        final String admin_contact_number = getResources().getString(R.string.mainActivity_adminContactNo);

        call_admin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent callIntent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + admin_contact_number));
                startActivity(callIntent);
                floatingDialog.dismiss();
            }
        });

        goto_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (loginLayout.getVisibility() == View.GONE){
                    loginLayout.setVisibility(View.VISIBLE);
                    loginLayout.startAnimation(slideFromRight);
                    text_and_buttonLayout.setVisibility(View.GONE);
                } else {
                    loginLayout.setVisibility(View.GONE);
                    text_and_buttonLayout.setVisibility(View.VISIBLE);
                }
            }
        });

        cancelbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (loginLayout.getVisibility() == View.VISIBLE){
                    loginLayout.setVisibility(View.GONE);
                    text_and_buttonLayout.setVisibility(View.VISIBLE);
                    text_and_buttonLayout.startAnimation(slideFromLeft);
                }else {
                    loginLayout.setVisibility(View.VISIBLE);
                    text_and_buttonLayout.setVisibility(View.GONE);
                }
            }
        });

        final String loginSuccessFull = getResources().getString(R.string.mainActivity_floatingDialog_LoginSuccessful);
        final String pleaseEnterValidUserAndPass = getResources().getString(R.string.mainActivity_floatingDialog_pleaseentervalidusernameandpassword);
        final String singingIn = getResources().getString(R.string.mainActivity_signingIn);

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressDialog.setMessage(singingIn);
                progressDialog.show();

                if(username.getText().toString().equals("baundule@developer.com") &&
                        password.getText().toString().equals("baundule")) {

                    if (rememberMe.isChecked()){

                        Boolean boolIsChecked = rememberMe.isChecked();
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString("pref_name", username.getText().toString());
                        editor.putString("pref_pass", password.getText().toString());
                        editor.putBoolean("pref_check", boolIsChecked);
                        editor.apply();
//
                    } else {
                        sharedPreferences.edit().clear().apply();
                    }

                    Toast.makeText(getApplicationContext(), loginSuccessFull, Toast.LENGTH_SHORT).show();
                    Intent i = new Intent(MainActivity.this, UserInsertActivity.class);
                    progressDialog.hide();
                    startActivity(i);

                    username.getText().clear();
                    password.getText().clear();

                    finish();
                    overridePendingTransition(R.anim.slide_up,R.anim.no_animation);
                }else{
                    progressDialog.hide();
                    Toast.makeText(MainActivity.this, pleaseEnterValidUserAndPass, Toast.LENGTH_SHORT).show();
                }
            }
        });

        floatingDialog.show();
    }

    private void swipeToRefreshData() {
        swipeRefreshLayout.setRefreshing(true);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Config.retriveURL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        JSONObject jsonObject = null;
                        contactModelList.clear();
                        swipeRefreshLayout.setRefreshing(false);
                        try {
                            jsonObject = new JSONObject(response);
                            jsonArray = jsonObject.getJSONArray(JSON_ARRAY);

                            if (jsonArray.length() == 0) {
                                Toast.makeText(MainActivity.this, "No Data Recorded...", Toast.LENGTH_SHORT).show();
                            } else {
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject object = jsonArray.getJSONObject(i);

                                    ContactModel contactModel = new ContactModel();

                                    contactModel.setId(object.getInt("id"));
                                    contactModel.setEnglishName(object.getString("english_name"));
                                    contactModel.setBanglaName(object.getString("bangla_name"));
                                    contactModel.setEnglish_mobile_no(object.getString("english_mobile_no"));
                                    contactModel.setBangla_mobile_no(object.getString("bangla_mobile_no"));
                                    contactModel.setDesignation(object.getString("designation"));
                                    contactModel.setEmail(object.getString("email"));
                                    contactModel.setImage(object.getString("image"));

                                    contactModelList.add(contactModel);
                                }
                                contactAdapter = new ContactAdapter(contactModelList, MainActivity.this);
                                recyclerView.setAdapter(contactAdapter);
                                contactAdapter.notifyDataSetChanged();
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        swipeRefreshLayout.setRefreshing(false);
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();

                return params;
            }
        };

        mRequestQueue = Volley.newRequestQueue(MainActivity.this);
        mRequestQueue.add(stringRequest);
    }

    public void noInternetSnackbar(String stringSnackbar){
        Snackbar noInternet_snackber = Snackbar.make(findViewById(android.R.id.content), stringSnackbar,Snackbar.LENGTH_LONG);
        View view = noInternet_snackber.getView();
        view.setBackgroundColor(ContextCompat.getColor(this, R.color.colorPrimaryDark));
        noInternet_snackber.show();
    }

}
