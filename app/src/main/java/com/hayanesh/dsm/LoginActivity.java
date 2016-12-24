package com.hayanesh.dsm;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.facebook.stetho.Stetho;
import com.rey.material.widget.ProgressView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {
    private Button login;
    private TextView no_account;
    private EditText id,password;
    private String _id,_pass;
    private String URL = "http://192.168.1.6/DSM/Authentication.php";
    RequestQueue requestQueue;
    StringRequest request;
    PrefManager prefManager;
    public static final String JSON_ARRAY = "success";
    final static String KEY_ID  = "id";
    final static String KEY_NAME = "name";
    final static String KEY_EMAIL = "email";
    final static String KEY_PHONE = "phone";
    final static String KEY_ADDRESS = "address";
    final static String KEY_LOCALITY = "locality";
    final static String KEY_PINCODE = "pincode";
    final static String KEY_CATEGORY = "category";
    final static String KEY_TYPE = "type";
    final static String KEY_PASS = "password";
    DatabaseHelper db;
    Boolean isRetrived = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Stetho.initializeWithDefaults(this);
        db = new DatabaseHelper(getApplicationContext());
        prefManager = new PrefManager(this.getApplicationContext());
        if(prefManager.isFirstTimeLaunch())
        {
            Toast.makeText(this, "Welcome New User", Toast.LENGTH_SHORT).show();
        }
        else {
            Intent homeIntent = new Intent(LoginActivity.this,Home.class);
            startActivity(homeIntent);
        }

        initView();
        requestQueue = Volley.newRequestQueue(this);


        login = (Button)findViewById(R.id.login);
        no_account = (TextView)findViewById(R.id.noaccount);
        no_account.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

              //  Intent toreg =  new Intent(LoginActivity.this,RegistrationActivity.class);
              //  startActivity(toreg);
                Intent toregister = new Intent(LoginActivity.this,RegistrationActivity.class);
                startActivity(toregister);

            }
        });

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if(validate())
                {
                    if(isNetworkConnected())
                    {
                        new AuthenticateAsync().execute();
                    }
                    else
                    {
                        Snackbar.make((RelativeLayout)findViewById(R.id.activity_login),"Connect to network",Snackbar.LENGTH_LONG).show();
                    }
                  //  prefManager.setFirstTimeLaunch(false);

                }

            }
        });

    }
    public boolean validate()
    {   Boolean valid = true;
        _id = id.getText().toString();
        _pass = password.getText().toString();
        if(_id.isEmpty())
        {
            id.setError("Enter a valid id");
            valid = false;
        }
        else {
            id.setError(null);
        }
        if(_pass.isEmpty()||_pass.length()<3)
        {
            password.setError("enter a strong password");
            valid = false;
        }
        else {
            password.setError(null);
        }
        return valid;
    }
    public void initView()
    {
        id = (EditText)findViewById(R.id.con_id);
        password = (EditText)findViewById(R.id.pass);

    }

    class AuthenticateAsync extends AsyncTask<Void,Void,Void>
    {
        final ProgressDialog progressDialog = new ProgressDialog(LoginActivity.this,
                ProgressView.MODE_DETERMINATE);

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog.setMessage("Please wait...");
            progressDialog.show();
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            progressDialog.dismiss();
            if(isRetrived)
            {
                Intent toHome = new Intent(getApplication(),Home.class);
                startActivity(toHome);
            }else {
                Snackbar.make((RelativeLayout)findViewById(R.id.activity_login),"Invalid login credentials",Snackbar.LENGTH_LONG).show();
            }
        }

        @Override
        protected Void doInBackground(Void... params) {

            request = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    try {
                        Log.d("response",response);
                        JSONObject jsonObject = new JSONObject(response);
                        if(jsonObject.names().get(0).equals("success"))
                        {
                            JSONObject content = jsonObject.getJSONObject("success");
                            int id =  Integer.parseInt(content.getString(KEY_ID));
                            String name = content.getString(KEY_NAME);
                            String email = content.getString(KEY_EMAIL);
                            String phone = content.getString(KEY_PHONE);
                            String locality = content.getString(KEY_LOCALITY);
                            String pin = content.getString(KEY_PINCODE);
                            String cat =  content.getString(KEY_CATEGORY);
                            String type =  content.getString(KEY_TYPE);
                            Details detail = new Details(id,name,email,null,phone,locality,pin,cat,type);
                            db.createDetails(detail);
                            prefManager.setUserEmail(email);
                            prefManager.setUserName(name);
                            prefManager.setUserId(String.valueOf(id));
                            isRetrived = true;
                            Log.d("TAG","success"+String.valueOf(id));

                        }
                        else {
                            Log.d("TAG","unsuccessful");
                        }
                    }catch (Exception e)
                    {
                        Log.e("ERROR",e.toString());
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.d("TAG",error.toString());
                }
            }){
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {

                    HashMap<String,String> map = new HashMap<>();
                    map.put("id",_id);
                    map.put("password",_pass);
                    return map;
                }
            };
            requestQueue.add(request);

            try {
                Thread.sleep(1000);
            }catch (Exception e)
            {
                e.printStackTrace();
            }
            return null;



        }
    }
    private boolean isNetworkConnected(){
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}
