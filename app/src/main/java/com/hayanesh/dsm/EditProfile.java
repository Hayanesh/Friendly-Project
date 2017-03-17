package com.hayanesh.dsm;

import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.Spinner;

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
import java.util.HashMap;
import java.util.Map;

import static com.hayanesh.dsm.app.Config.serverIp;

public class EditProfile extends AppCompatActivity {
    private RadioButton rb;
    RadioGroup rg;
    private EditText cname,email,phone,aphone,address,locality,pincode,password;
    ImageView profile_pic;
    String _name, _email,_phone,_aphone,_address,_locality, _type,_pass,_pin;
    DatabaseHelper db;
    final String URL = "http://"+serverIp+"/DSM/UserCreation.php";
    RequestQueue requestQueue;
    StringRequest request;
    final static int RESULT_LOAD_IMAGE = 224;
    boolean isRegistered = false;
    PrefManager prefManager;
    String str_image = null;
    Snackbar snackbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        prefManager = new PrefManager(this.getApplicationContext());
        db = new DatabaseHelper(getApplicationContext());
        requestQueue = Volley.newRequestQueue(this);
        snackbar = Snackbar.make((CoordinatorLayout)findViewById(R.id.activity_edit_profile), "Something went wrong.Please try again", Snackbar.LENGTH_LONG)
                .setAction("Action", null);
        initView();
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(galleryIntent,RESULT_LOAD_IMAGE);
            }
        });
    }
    public void initView()
    {
        cname = (EditText)findViewById(R.id.cname);
        email = (EditText)findViewById(R.id.email);
        phone = (EditText)findViewById(R.id.phone_number);
        aphone = (EditText)findViewById(R.id.alt_number);
        address = (EditText)findViewById(R.id.address);
        locality = (EditText)findViewById(R.id.c_locality);
        pincode = (EditText)findViewById(R.id.pincode);
        password = (EditText)findViewById(R.id.pass);
        rg = (RadioGroup)findViewById(R.id.phase);
        Details rd = db.getDetails(prefManager.getUserId());
        cname.setText(rd.getName());
        email.setText(rd.getEmail());
        phone.setText(rd.getPhone());
        aphone.setText(rd.getAlt_phone());
        address.setText(rd.getAddress());
        locality.setText(rd.getLocality());
        pincode.setText(rd.getPin());
        password.setText(rd.getPass());
        if(rd.getType().equals("Single Phase"))
        {
           RadioButton tr = (RadioButton)findViewById(R.id.radioButton4);
            tr.setChecked(true);
        }
        else {
            RadioButton tr = (RadioButton)findViewById(R.id.radioButton5);
            tr.setChecked(true);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        profile_pic = (ImageView) findViewById(R.id.user_photo);
        try {
            if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && data != null) {
                Uri selectedImage = data.getData();
                String[] filepath = {MediaStore.Images.Media.DATA};
                Cursor cursor = getContentResolver().query(selectedImage, filepath, null, null, null);
                cursor.moveToFirst();
                int columnIndex = cursor.getColumnIndex(filepath[0]);
                String imgDecodableString = cursor.getString(columnIndex);
                str_image = imgDecodableString;
                Glide.with(this).load(new File(str_image)).asBitmap().centerCrop().into(new BitmapImageViewTarget(profile_pic){
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
        _name = cname.getText().toString();
        _email = email.getText().toString();
        _pass = password.getText().toString();
        _phone = phone.getText().toString();
        _aphone = aphone.getText().toString();
        _address = address.getText().toString();
        _locality = locality.getText().toString();
        _pin = pincode.getText().toString();
        rb = (RadioButton)findViewById(rg.getCheckedRadioButtonId());
        _type = rb.getText().toString();
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
        return true;

    }
    class RegistrationAsync extends AsyncTask<Void,Void,Void>
    {
        final ProgressDialog progressDialog = new ProgressDialog(EditProfile.this,
                ProgressView.MODE_DETERMINATE);
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog.setMessage("Updating details...");
            progressDialog.show();
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            progressDialog.dismiss();
            if(isRegistered)
            {
                //prefManager.setFirstTimeLaunch(false);
                prefManager.setUserEmail(_email);
                prefManager.setUserName(_name);
                if(str_image != null)
                {
                    prefManager.setUserPic(str_image);
                }

            }

        }

        @Override
        protected Void doInBackground(Void... params) {
            request = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    Log.d("Response",response);
                    try{
                        JSONObject jsonObject = new JSONObject(response);
                        if(jsonObject.names().get(0).equals("success"))
                        {
                            // Toast.makeText(RegistrationActivity.this, "Registration Successful", Toast.LENGTH_SHORT).show();

                            //Local Database Storing
                            Details details = new Details(prefManager.getUserId(),_name,_email,_pass,_phone,_aphone,_address,_locality,prefManager.getUserCat(),_type,_pin);
                            db.DeleteDetails();
                            db.createDetails(details);
                            isRegistered = true;
                        }
                        else {
                            progressDialog.dismiss();
                            snackbar.show();

                        }
                    }catch (JSONException e)
                    {
                        progressDialog.dismiss();
                        snackbar.show();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.d("TAG",error.toString());
                    progressDialog.dismiss();
                    snackbar.show();
                }
            })
            {
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    HashMap<String,String> map = new HashMap<String, String>();
                    map.put("id",prefManager.getUserId());
                    map.put("name",_name);
                    map.put("email",_email);
                    map.put("password",_pass);
                    map.put("phone",_phone);
                    map.put("alt_phone",_aphone);
                    map.put("address",_address);
                    map.put("locality",_locality);
                    map.put("category",prefManager.getUserCat());
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
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.edit_profile,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if(item.getItemId() == R.id.action_store)
        {
            if(validate())
            {
                Log.d("Validated","valid");
                new RegistrationAsync().execute();

            }
            else {
               snackbar.show();
            }
            if(isRegistered == true)
            {
                Intent toProfile = new Intent(EditProfile.this,UserProfile.class);
                startActivity(toProfile);
            }
        }
        return super.onOptionsItemSelected(item);
    }
}
