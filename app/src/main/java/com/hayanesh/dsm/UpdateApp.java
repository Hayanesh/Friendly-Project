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
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.MultiAutoCompleteTextView;
import android.widget.PopupWindow;


import com.reginald.editspinner.EditSpinner;
import com.weiwangcn.betterspinner.library.BetterSpinner;

import java.lang.reflect.Array;

public class UpdateApp extends AppCompatActivity {
    DrawerLayout drawerLayout;
    EditSpinner mEditSpinner1;
    ListView listView;
    ActionBarDrawerToggle drawerToggle;
    String title = "";
    AutoCompleteTextView text;
    String[] languages = {"Android ", "java", "IOS", "SQL", "JDBC", "Web services"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.appliance_info2);
        mEditSpinner1 = (EditSpinner) findViewById(R.id.rating);
        ListAdapter adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item,
                getResources().getStringArray(R.array.edits_array_1));
        mEditSpinner1.setAdapter(adapter);

        // triggered when dropdown popup window dismissed
        mEditSpinner1.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                Log.d("TAG", "mEditSpinner1 popup window dismissed!");
            }
        });

        // triggered when dropdown popup window shown
        mEditSpinner1.setOnShowListener(new EditSpinner.OnShowListener() {
            @Override
            public void onShow() {
                // maybe you want to hide the soft input panel when the popup window is showing.
                hideSoftInputPanel();
            }
        });
    }

    private static final String[] COUNTRIES = new String[]{
            "Belgium", "France", "Italy", "Germany", "Spain"
    };
//        listView = (ListView)findViewById(R.id.rating);
    //      String[] dataArray = getResources().getStringArray(R.array.watt);
    //    listView.setAdapter(new ArrayAdapter<String>(this,
    //          android.R.layout.simple_list_item_1,
    //        dataArray));

    private void hideSoftInputPanel() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(mEditSpinner1.getWindowToken(), 0);
        }
    }

    private void showSoftInputPanel(View view) {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.showSoftInput(view, 0);
        }
    }


}

