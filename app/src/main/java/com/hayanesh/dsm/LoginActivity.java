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
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static com.hayanesh.dsm.app.Config.serverIp;

public class LoginActivity extends AppCompatActivity {
    private Button login;
    private TextView no_account;
    private EditText id,password;
    private String _id,_pass;
    private String URL = "http://"+ serverIp +"/DSM/Authentication.php";
    RequestQueue requestQueue;
    StringRequest request;
    PrefManager prefManager;
    public static final String JSON_ARRAY = "success";
    final static String KEY_ID  = "id";
    final static String KEY_NAME = "name";
    final static String KEY_EMAIL = "email";
    final static String KEY_PHONE = "phone";
    final static String KEY_ALTPHONE = "alt_phone";
    final static String KEY_ADDRESS = "address";
    final static String KEY_LOCALITY = "locality";
    final static String KEY_PINCODE = "pincode";
    final static String KEY_CATEGORY = "category";
    final static String KEY_TYPE = "type";
    final static String KEY_PASS = "password";
    int cat_choice = 0;
    String[] selected_cat_appliances;
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
            finish();
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


                if(validate()) {
                  new AuthenticateAsync().execute();
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
                prefManager.setFirstTimeLaunch(false);
                finish();
                Intent toHome = new Intent(LoginActivity.this,Home.class);
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
                    Log.d("response",response+"response");
                    try {

                        JSONObject jsonObject = new JSONObject(response);
                        if(jsonObject.names().get(0).equals("success"))
                        {
                            Toast.makeText(LoginActivity.this, "Success", Toast.LENGTH_SHORT).show();
                            JSONArray complete_arr = jsonObject.getJSONArray("success");
                            JSONObject user_content = complete_arr.getJSONObject(0);
                            JSONObject content = user_content.getJSONObject("user");

                            String id =  content.getString(KEY_ID);
                            String name = content.getString(KEY_NAME);
                            String email = content.getString(KEY_EMAIL);
                            String password =content.getString(KEY_PASS);
                            String phone = content.getString(KEY_PHONE);
                            String alt_phone = content.getString(KEY_ALTPHONE);
                            String address = content.getString(KEY_ADDRESS);
                            String locality = content.getString(KEY_LOCALITY);
                            String pin = content.getString(KEY_PINCODE);
                            String cat =  content.getString(KEY_CATEGORY);
                            String type =  content.getString(KEY_TYPE);
                            Details detail = new Details(id,name,email,password,phone,alt_phone,address,locality,cat,type,pin);
                            db.createDetails(detail);
                            prefManager.setUserEmail(email);
                            prefManager.setUserName(name);
                            prefManager.setUserId(id);
                            prefManager.setUserCat(cat);
                            isRetrived = true;
                            CreateApp();
                            Log.d("TAG","success"+String.valueOf(id));
                            JSONObject app_obj = complete_arr.getJSONObject(1);
                            JSONArray app = app_obj.getJSONArray("app");
                            for(int i = 0;i<app.length();i++)
                            {
                                JSONObject app_i = app.getJSONObject(i);
                                String app_name = app_i.getString("appliance");
                                String app_rate = app_i.getString("rating");
                                int app_qty = app_i.getInt("qty");
                                int app_shiftable = app_i.getInt("shiftable");
                                Log.d("Appliance",app_name +" "+app_qty+" "+app_rate+" "+app_shiftable);
                                Appliances appliances = new Appliances(app_name,new String[]{app_rate},app_qty,app_shiftable);
                                db.updateAppobj(appliances);
                            }
                            prefManager.setMODE(true);
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
                    progressDialog.dismiss();
                    Log.e("Error",error.toString());
                    Snackbar.make((RelativeLayout)findViewById(R.id.activity_login),"Network error occurred. Please try again",Snackbar.LENGTH_LONG).show();
                }
            })
            {
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {

                    HashMap<String,String> map = new HashMap<>();
                    map.put("id",_id);
                    map.put("password",_pass);
                    return map;
                }
            };
            requestQueue.add(request);
            return null;
        }
    }
    private boolean isNetworkConnected(){
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public void CreateApp()//Initial insertion
    {

        String _category = prefManager.getUserCat();
        ArrayList<Appliances> appList = new ArrayList<Appliances>();
        String[] residential_app = {"Tubelight",
                "Ceiling Fan",
                "Table Fan",
                "Computer",
                "Dishwasher",
                "Water Heater",
                "Refrigerator",
                "Air Conditioner",
                "TV",
                "Laptop",
                "Microwave oven",
                "Toaster",
                "Vaccum Cleaner",
                "Washing Machine",
                "Pump Motor",
                "Grinder",
                "Mixer",
                "Electric Iron"};
        String[] commercial_app = {"Tubelight",
                "Fan",
                "Refrigerator",
                "Air Conditioner",
                "Computer",
                "Printer",
                "CCTV camera",
                "Microwave Oven",
                "Vaccum Cleaner",
                "Water heater",
                "Washing Machine",
                "Coffee Maker",
                "UPS",
                "Ice Maker",
                "CFL Bulb"};
        String[] industrial_app = {"Tublelight",
                "CFL bulb","Ceiling Fan","Table Fan","Air Conditioner","Refrigerator","CCTV camera","Coffee Maker","UPS","Vaccum Cleaner",
                "Machine 1","Machine 2","Machine 3","Machine 4","Machine 5","Machine 6","Machine 7","Machine 8"};

        if (_category.equals("Residential"))
            cat_choice = 0;
        else if (_category.equals("Commercial"))
            cat_choice = 1;
        else
            cat_choice = 2;
        switch (cat_choice)
        {
            case 0 : selected_cat_appliances = residential_app.clone();
                break;
            case 1 : selected_cat_appliances = commercial_app.clone();
                break;
            case 2 : selected_cat_appliances = industrial_app.clone();
                break;

        }
        for(int i=0;i<selected_cat_appliances.length;i++)
        {
            Appliances a=new Appliances(selected_cat_appliances[i],new String[]{null},0);
            appList.add(a);
        }
        db.CreateTableAppliances(appList);
    }
}
