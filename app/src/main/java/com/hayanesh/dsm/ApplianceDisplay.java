package com.hayanesh.dsm;

import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class ApplianceDisplay extends AppCompatActivity {
    ProgressBar myprogressBar;
    TextView progressingTextView;
    Handler progressHandler = new Handler();
    int i = 0;

    ArrayList<Appliances> appliances;
    RecyclerView recyclerView2;
    AppliancesDashAdapter appliancesDashAdapter;
    DatabaseHelper db;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_appliance_display);

        db = new DatabaseHelper(getApplicationContext());
        appliances = db.getAppliances();


        appliancesDashAdapter = new AppliancesDashAdapter(this,appliances);
        appliancesDashAdapter.getItemCount();
        Toast.makeText(this, "Count"+appliancesDashAdapter.getItemCount(), Toast.LENGTH_SHORT).show();
        recyclerView2 = (RecyclerView)findViewById(R.id.recycler_view2);

        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(this,1);
        recyclerView2.setLayoutManager(mLayoutManager);
        recyclerView2.setAdapter(appliancesDashAdapter);


        /*myprogressBar = (ProgressBar) findViewById(R.id.progressBar);
        progressingTextView = (TextView) findViewById(R.id.progress_circle_text);

        new Thread(new Runnable() {
            public void run() {
                while (i < 24) {
                    i += 1;
                    progressHandler.post(new Runnable() {
                        public void run() {
                            myprogressBar.setProgress(i);
                            progressingTextView.setText("" + i + " %");
                        }
                    });
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();*/
    }
    }
