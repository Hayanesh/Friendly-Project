package com.hayanesh.dsm;

import android.content.Context;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.ListViewAutoScrollHelper;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.MultiAutoCompleteTextView;
import android.widget.PopupWindow;
import android.widget.TextView;


import com.reginald.editspinner.EditSpinner;
import com.weiwangcn.betterspinner.library.BetterSpinner;

import java.lang.reflect.Array;

public class UpdateApp extends AppCompatActivity {
    TextView name;
    LinearLayout expandable_linear;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.appliance_info);

       name = (TextView)findViewById(R.id.app_name);
       expandable_linear = (LinearLayout)findViewById(R.id.app_below);
        name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    if(expandable_linear.getVisibility() == LinearLayout.GONE)
                    {
                        expandable_linear.setVisibility(LinearLayout.VISIBLE);
                    }
                else {
                        expandable_linear.setVisibility(LinearLayout.GONE);
                    }

            }
        });
    }





}

