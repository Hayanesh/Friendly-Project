package com.hayanesh.dsm;

import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v4.view.ScrollingView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.rey.material.widget.ProgressView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class RegistrationActivity extends AppCompatActivity {
    private Spinner category;
    private RadioButton rb;
    RadioGroup rg;
    private EditText cid,cname,email,phone,aphone,address,locality,pincode,password;
    ImageButton profile_pic;
    String _id,_name, _email,_phone,_aphone,_address,_locality,_category, _type,_pass,_pin;
    DatabaseHelper db;
    final String URL = "http://192.168.1.6/DSM/UserCreation.php";
    RequestQueue requestQueue;
    StringRequest request;
    final static int RESULT_LOAD_IMAGE = 222;
    Snackbar snacbar;
    boolean isRegistered = false;
    PrefManager prefManager;
    String[] selected_cat_appliances;
    String str_image = null;
    int cat_choice = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        initView();
        prefManager = new PrefManager(this.getApplicationContext());
        requestQueue = Volley.newRequestQueue(this);
        db = new DatabaseHelper(getApplicationContext());
        snacbar = Snackbar.make((ScrollView)findViewById(R.id.activity_registration), "Registration UnSuccessful", Snackbar.LENGTH_LONG)
                .setAction("Action", null);
        profile_pic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(galleryIntent,RESULT_LOAD_IMAGE);
            }
        });


        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(str_image != null)
                {
                    prefManager.setUserPic(str_image);
                }
                if(validate())
                {

                    new RegistrationAsync().execute();
                }
                else
                {
                    snacbar.setText("Some fields are incomplete").show();
                }

            }
        });

    }
    public void initView()
    {
        profile_pic = (ImageButton)findViewById(R.id.user_photo);
        cid = (EditText)findViewById(R.id.cid);
        cname = (EditText)findViewById(R.id.cname);
        email = (EditText)findViewById(R.id.email);
        phone = (EditText)findViewById(R.id.phone_number);
        aphone = (EditText)findViewById(R.id.alt_number);
        address = (EditText)findViewById(R.id.address);
        locality = (EditText)findViewById(R.id.c_locality);
        pincode = (EditText)findViewById(R.id.pincode);
        password = (EditText)findViewById(R.id.pass);
        category = (Spinner)findViewById(R.id.service);
        rg = (RadioGroup)findViewById(R.id.phase);

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        try {
            if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && data != null) {
                Uri selectedImage = data.getData();
                String[] filepath = {MediaStore.Images.Media.DATA};
                Cursor cursor = getContentResolver().query(selectedImage, filepath, null, null, null);
                cursor.moveToFirst();
                int columnIndex = cursor.getColumnIndex(filepath[0]);
                String imgDecodableString = cursor.getString(columnIndex);
                str_image = imgDecodableString;
                Glide.with(this).load(new File(imgDecodableString)).asBitmap().centerCrop().into(new BitmapImageViewTarget(profile_pic){
                    @Override
                    protected void setResource(Bitmap resource) {
                        RoundedBitmapDrawable circularBitmapDrawable =
                                RoundedBitmapDrawableFactory.create(getApplication().getResources(), resource);
                        circularBitmapDrawable.setCircular(true);
                        profile_pic.setImageDrawable(circularBitmapDrawable);
                    }
                });
            }
        }catch (Exception e)
        {
            e.printStackTrace();
        }
    }
    public boolean validate()
    {
        boolean valid = true;
        _id = cid.getText().toString();
        _name = cname.getText().toString();
        _email = email.getText().toString();
        _pass = password.getText().toString();
        _phone = phone.getText().toString();
        _aphone = aphone.getText().toString();
        _address = address.getText().toString();
        _locality = locality.getText().toString();
        _pin = pincode.getText().toString();
        _category = category.getSelectedItem().toString();
        rb = (RadioButton)findViewById(rg.getCheckedRadioButtonId());
        _type = rb.getText().toString();
        if(_id.isEmpty())
        {
            cid.setError("Enter a valid id");
            valid = false;
        }
        else {
            cid.setError(null);
        }
        if(_name.isEmpty())
        {
            cname.setError("Enter a valid user name");
            valid = false;
        }else {
            cname.setError(null);
        }

        if (_email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(_email).matches()) {
            email.setError("enter a valid email address");
            valid = false;
        } else {
            email.setError(null);
        }
        if(_pass.isEmpty()||_pass.length()<3)
        {
            password.setError("enter a strong password");
            valid = false;
        }
        else {
            password.setError(null);
        }
        if (_phone.isEmpty()) {
            phone.setError("enter a valid phone number");
            valid = false;
        } else {
            phone.setError(null);
        }
        if (_aphone.isEmpty()) {

            aphone.setError("enter a valid phone number");
            valid = false;
        } else {
            aphone.setError(null);
        }
        if (_address.isEmpty()) {
            address.setError("enter a valid address");
            valid = false;
        } else {
            address.setError(null);
        }
        if(_locality.isEmpty())
        {
            locality.setError("enter a valid locality");
            valid = false;
        }else {
            locality.setError(null);
        }
        return valid;

    }
    public void getdata()
    {
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

    class RegistrationAsync extends AsyncTask<Void,Void,Void>
    {
        final ProgressDialog progressDialog = new ProgressDialog(RegistrationActivity.this,
                                            ProgressView.MODE_DETERMINATE);
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog.setMessage("Registering New user...");
            progressDialog.show();
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            progressDialog.dismiss();
            if(isRegistered)
            {
                getdata();
                //prefManager.setFirstTimeLaunch(false);
                prefManager.setUserEmail(_email);
                prefManager.setUserName(_name);
                prefManager.setUserId(_id);
                Intent toload = new Intent(RegistrationActivity.this,DSM.class);
                toload.putExtra("Category",_category);
                startActivity(toload);
            }

        }

        @Override
        protected Void doInBackground(Void... params) {
                request = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try{
                            JSONObject jsonObject = new JSONObject(response);
                            if(jsonObject.names().get(0).equals("success"))
                            {
                               // Toast.makeText(RegistrationActivity.this, "Registration Successful", Toast.LENGTH_SHORT).show();

                                //Local Database Storing
                                Details details = new Details(Integer.parseInt(_id),_name,_email,_pass,_phone,_locality,_pin,_category,_type);
                                db.createDetails(details);
                                isRegistered = true;

                            }
                            else {
                                progressDialog.dismiss();
                                snacbar.show();

                            }
                        }catch (JSONException e)
                        {
                            progressDialog.dismiss();
                            snacbar.show();
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("TAG",error.toString());
                        progressDialog.dismiss();
                        snacbar.show();
                    }
                })
                {
                    @Override
                    protected Map<String, String> getParams() throws AuthFailureError {
                        HashMap<String,String> map = new HashMap<String, String>();
                        map.put("id",_id);
                        map.put("name",_name);
                        map.put("email",_email);
                        map.put("password",_pass);
                        map.put("phone",_phone);
                        map.put("alt_phone",_aphone);
                        map.put("address",_address);
                        map.put("locality",_locality);
                        map.put("category",_category);
                        map.put("type",_type);
                        map.put("pincode",_pin);
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
}
