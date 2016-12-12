package com.hayanesh.dsm;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class LoginActivity extends AppCompatActivity {
    private Button login;
    private TextView no_account;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        login = (Button)findViewById(R.id.login);
        no_account = (TextView)findViewById(R.id.noaccount);
        no_account.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent toreg =  new Intent(LoginActivity.this,RegistrationActivity.class);
                startActivity(toreg);

            }
        });

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent toload = new Intent(LoginActivity.this,DSM.class);
                startActivity(toload);
            }
        });

    }
}
