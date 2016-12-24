package com.hayanesh.dsm;

import android.animation.Animator;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.opengl.Visibility;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.provider.ContactsContract;
import android.provider.Settings;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.hayanesh.dsm.RadarExtra.RandomTextView;

import java.util.List;

public class DeviceConnection extends AppCompatActivity {
    String networkSSID = "sha";
    String networkPass = "12345678";
    WifiConfiguration conf;
    WifiManager wifiManager;
    FloatingActionButton fab;
    RandomTextView randomTextView;
    TextView con_status;
    Handler handler;
    CoordinatorLayout device_coordinator;
    ImageButton smartmeter;
    int id;
    View myview;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_connection);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        smartmeter = (ImageButton)findViewById(R.id.image_smartmeter);
        fab = (FloatingActionButton) findViewById(R.id.fab_refresh);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                con_status.setError("Searching for smart meter...");
                smartmeter.setVisibility(View.VISIBLE);
                myview.setVisibility(View.VISIBLE);
                fab.setVisibility(View.INVISIBLE);
                TimeOut();
                StartScanning();
            }
        });
        con_status = (TextView)findViewById(R.id.con_status);
        device_coordinator = (CoordinatorLayout)findViewById(R.id.device_coordinatorLayout);
        wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        //getWifiID();
        //Initially off the wifi to disconnect it from other networks
        if(wifiManager.isWifiEnabled() == true)
        {
            wifiManager.setWifiEnabled(false);
        }
        showRadar();
        TimeOut();
        StartScanning();
    }


    public void showRadar()
    {
        randomTextView = (RandomTextView) findViewById(
                R.id.random_textview);
        randomTextView.setOnRippleViewClickListener(
                new RandomTextView.OnRippleViewClickListener()
                {
                    @Override
                    public void onRippleViewClicked(View view)
                    {
                        Toast.makeText(DeviceConnection.this, "Connected", Toast.LENGTH_SHORT).show();
                    }
                });

    }
    public boolean getWifiID()
    {
        if(wifiManager.isWifiEnabled() == false)
        {
            wifiManager.setWifiEnabled(true);
        }
        conf = new WifiConfiguration();
        conf.SSID = String.format("\"%s\"", networkSSID);
        conf.preSharedKey =String.format("\"%s\"", networkPass);
        conf.priority = 40;
        conf.status = WifiConfiguration.Status.ENABLED;
        wifiManager.addNetwork(conf);
        List<WifiConfiguration> list = wifiManager.getConfiguredNetworks();
        for( WifiConfiguration i : list ) {
            if(i.SSID != null && i.SSID.equals("\"" + networkSSID + "\"")) {
                Log.d("Connected",""+i.networkId);
                wifiManager.disconnect();
                wifiManager.enableNetwork(i.networkId, true);
                id = i.networkId;
                wifiManager.reconnect();
                con_status.setText("C O N N E C T E D ...");
                break;
            }
        }



        return true;
    }

   public void StartScanning()
   {
       wifiManager.setWifiEnabled(true);
       System.out.println("Inside start scanning");
       registerReceiver(wifiScanReceiver,
               new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
       wifiManager.startScan();
   }
    private final BroadcastReceiver wifiScanReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context c, Intent intent) {
            if (intent.getAction().equals(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION)) {
                List<ScanResult> scanResults = wifiManager.getScanResults();
                int j = 0;
                for(ScanResult i : scanResults)
                {
                    Log.d("INSIDE",i.toString());
                    if(i.SSID!=null && i.SSID.equals(networkSSID))
                    {

                        Log.d("Found","found");

                        showSnack();
                        //  wifiManager.setWifiEnabled(false);
                    }
                    j++;
                }

            }
        }
    };
    public void showSnack()
    {
        Snackbar snackbar = Snackbar
                .make(device_coordinator,"Device found", Snackbar.LENGTH_LONG);
        snackbar.show();
        con_status.setText("Connecting to smart meter...");
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                randomTextView.addKeyWord("");
                randomTextView.show();
            }
        },100);
        getWifiID();
    }

    public void TimeOut()
    {
        Log.d("TIM","timer");
        new CountDownTimer(15000,1000)
        {
            @Override
            public void onTick(long millisUntilFinished) {

            }

            @Override
            public void onFinish() {
                Log.d("TAG","ontick");
                WifiInfo info =  wifiManager.getConnectionInfo();
                if(!info.getSSID().equals(networkSSID))
                {
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            con_status.setText("Problem in connecting to device");
                            smartmeter.setVisibility(View.INVISIBLE);
                            myview = (View)findViewById(R.id.radar);
                            myview.setVisibility(View.INVISIBLE);
                            fab.setVisibility(View.VISIBLE);
                        }
                    },10);
                }
            }
        }.start();
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        wifiManager.removeNetwork(id);
    }
}

