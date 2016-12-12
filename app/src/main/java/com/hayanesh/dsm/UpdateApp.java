package com.hayanesh.dsm;

import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.ListViewAutoScrollHelper;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.lang.reflect.Array;

public class UpdateApp extends AppCompatActivity {
    DrawerLayout drawerLayout;
    ListView listView;
    ActionBarDrawerToggle drawerToggle;
    String title = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.appliance_info);
//        listView = (ListView)findViewById(R.id.rating);
  //      String[] dataArray = getResources().getStringArray(R.array.watt);
    //    listView.setAdapter(new ArrayAdapter<String>(this,
      //          android.R.layout.simple_list_item_1,
        //        dataArray));
    }
}

