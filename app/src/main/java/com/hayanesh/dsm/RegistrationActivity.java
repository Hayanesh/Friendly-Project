package com.hayanesh.dsm;

import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Spinner;

public class RegistrationActivity extends AppCompatActivity {
    private Spinner category;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        category = (Spinner)findViewById(R.id.service);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                String cat = category.getSelectedItem().toString();
                Intent toload = new Intent(RegistrationActivity.this,DSM.class);
                toload.putExtra("Category",cat);
                startActivity(toload);
            }
        });
    }
}
