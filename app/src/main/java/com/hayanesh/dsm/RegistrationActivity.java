package com.hayanesh.dsm;

import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;

public class RegistrationActivity extends AppCompatActivity {
    private Spinner category;
    private RadioButton rb;
    private EditText cid,cname,email,phone,aphone,address,locality,pincode,password;

    String _id,_name, _email,_phone,_aphone,_address,_locality,_category, _type,_pass,_pin;
    DatabaseHelper db;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        db = new DatabaseHelper(getApplicationContext());
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(validate())
                {
                    Details details = new Details(Integer.parseInt(_id),_name,_email,_pass,_phone,_locality,_pin,_category,_type);
                    db.createDetails(details);

                    Snackbar.make(view, "Registration Successfull", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();


                    try {
                        this.wait(100);
                    }catch (Exception e)
                    {
                        e.printStackTrace();
                    }

                    
                    Intent toload = new Intent(RegistrationActivity.this,UserProfile.class);
                    toload.putExtra("email",_email);
                    startActivity(toload);
                }

            }
        });
        initView();
    }
    public void initView()
    {
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
        RadioGroup rg = (RadioGroup)findViewById(R.id.phase);
        rb = (RadioButton)findViewById(rg.getCheckedRadioButtonId());
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
}
