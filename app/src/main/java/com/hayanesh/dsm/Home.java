package com.hayanesh.dsm;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v7.widget.CardView;
import android.text.Layout;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import com.hayanesh.dsm.app.Config;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.gitonway.lee.niftymodaldialogeffects.lib.Effectstype;
import com.gitonway.lee.niftymodaldialogeffects.lib.NiftyDialogBuilder;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessaging;
import com.hayanesh.dsm.util.NotificationUtils;
import com.rey.material.widget.ProgressView;
import com.tapadoo.alerter.Alerter;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.hayanesh.dsm.app.Config.serverIp;

public class Home extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    PrefManager prefManager;
    private DatabaseReference mDatabase;
    private String User_id = "test@mail.com";
    private TextView user_name,user_email;
    private ImageView user_pic;
    private TextView minLoad,maxLoad,load_status;
    public String URL = "http://"+ serverIp +"/DSM/MinMaxStore.php";
    RequestQueue requestQueue;
    private BroadcastReceiver mRegistrationBroadcastReceiver;
    Menu menu;
    DatabaseHelper db;
    DrawerLayout drawer;
    List<Appliances> appl;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        prefManager = new PrefManager(this.getApplicationContext());
        requestQueue = Volley.newRequestQueue(this);
        db = new DatabaseHelper(getApplicationContext());
        mDatabase = FirebaseDatabase.getInstance().getReference();
        minLoad = (TextView)findViewById(R.id.min);
        maxLoad = (TextView)findViewById(R.id.max);
        if(prefManager.getMin()!=0 && prefManager.getMax()!=0)
        {
            minLoad.setText(prefManager.getMin()+" W");
            maxLoad.setText(prefManager.getMax()+" W");
        }
        load_status = (TextView)findViewById(R.id.load_status);
        load_status.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                appl = db.getAppliances();
                if(appl.size()>0)
                {
                    if(getMinMax(appl))
                    {
                        new MinMaxAsync().execute();
                    }

                }
            }
        });


                //Handle network store
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        View headerView = navigationView.getHeaderView(0);
        user_name = (TextView)headerView.findViewById(R.id.nav_user);
        user_name.setText(prefManager.getUserName());
        user_email =(TextView)headerView.findViewById(R.id.nav_email);
        user_email.setText(prefManager.getUserEmail());
        user_pic = (ImageView)headerView.findViewById(R.id.nav_pic);
        if(prefManager.getUserPic()!=null)
        {
            Glide.with(this).load(new File(prefManager.getUserPic())).asBitmap().centerCrop().into(new BitmapImageViewTarget(user_pic){
                @Override
                protected void setResource(Bitmap resource) {
                    RoundedBitmapDrawable circularBitmapDrawable =
                            RoundedBitmapDrawableFactory.create(getApplication().getResources(), resource);
                    circularBitmapDrawable.setCircular(true);
                    user_pic.setImageDrawable(circularBitmapDrawable);
                }
            });
        }
        CardView app = (CardView)findViewById(R.id.app_icon);
        app.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                    Intent toView = new Intent(Home.this,ApplianceDisplay.class);
                    startActivity(toView);
            }
        });
        //Alert();
        mRegistrationBroadcastReceiver = new BroadcastReceiver(){
            @Override
            public void onReceive(Context context, Intent intent) {
                // checking for type intent filter
                if (intent.getAction().equals(Config.REGISTRATION_COMPLETE)) {
                    // gcm successfully registered
                    // now subscribe to `global` topic to receive app wide notifications
                    FirebaseMessaging.getInstance().subscribeToTopic(Config.TOPIC_GLOBAL);


                } else if (intent.getAction().equals(Config.PUSH_NOTIFICATION)) {
                    // new push notification is received

                    String message = intent.getStringExtra("message");
                    //Toast.makeText(context, "Message "+message, Toast.LENGTH_SHORT).show();
                    Alert(message);


                    // txtMessage.setText(message);
                }
            }
        };
    }
    public void Alert(String message)
    {
        Alerter.create(this)
                .setTitle("Usage limit exceeded")
                .setText("Current load : "+message)
                .setBackgroundColor(R.color.colourAccent)
                .setDuration(10000)
                .show();
    }
    public boolean getMinMax(List<Appliances> appliances)
    {
        int min = 0,max =0;
        for(int i=0;i<appliances.size();i++)
        {
            Appliances app = appliances.get(i);
            if(app.getShiftable()==0)
            {
                min += CalculateUnit(app.getRating()[0],app.getQty());
            }
            else
            {
                max += CalculateUnit(app.getRating()[0],app.getQty());
            }
        }
        max += min;
        Log.d("MAX",""+max);
        Log.d("MIN",""+min);
        minLoad.setText(String.valueOf(min)+" W");
        maxLoad.setText(String .valueOf(max)+" W");
        if(prefManager.getMin()!=min || prefManager.getMax()!=max)
        {
            prefManager.setMax(max);
            prefManager.setMin(min);
            return true;
        }
        else
        {
            prefManager.setMax(max);
            prefManager.setMin(min);
            return false;
        }
    }
    public int CalculateUnit(String rat,int qty)
    {
        return (Integer.parseInt(rat)*qty);
    }
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);
        this.menu = menu;
        MenuItem menuItem = menu.findItem(R.id.action_settings);
        User_id = prefManager.getUserEmail();
        Toast.makeText(this, "ID"+User_id, Toast.LENGTH_SHORT).show();
        menuItem.setTitle(User_id);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent i = new Intent(this,UserProfile.class);
            startActivity(i);
            return true;
        }
        if(id == R.id.action_logout)
        {
            final NiftyDialogBuilder materialDesignAnimatedDialog = NiftyDialogBuilder.getInstance(this);

            materialDesignAnimatedDialog
                    .withTitle("Confirm Logout")
                    .withMessage("Are you sure you want to logout ?")
                    .withDialogColor("#1c90ec")
                    .withButton1Text("Yes")
                    .withButton2Text("No")
                    .withDuration(700)
                    .withEffect(Effectstype.Fall)
                    .show();
            materialDesignAnimatedDialog.setButton1Click(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    materialDesignAnimatedDialog.dismiss();
                    db.DeleteAppliances();
                    db.DeleteDetails();
                    prefManager.editor.clear();
                    prefManager.editor.commit();
                    finish();
                    Intent i = new Intent(Home.this,LoginActivity.class);
                    startActivity(i);
                }
            });
            materialDesignAnimatedDialog.setButton2Click(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    materialDesignAnimatedDialog.dismiss();
                }
            });
            //clear shared preference
           /* AlertDialog.Builder alertDialog = new AlertDialog.Builder(Home.this);
            alertDialog.setTitle("Confirm Logout");
            alertDialog.setMessage("Are you sure you want to logout ?");
            alertDialog.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Intent i = new Intent(getApplicationContext(),LoginActivity.class);
                    startActivity(i);
                }
            });
            alertDialog.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });
            alertDialog.show();*/
            return true;
        }
        if(id==R.id.action_device)
        {
            Intent todevice = new Intent(Home.this,DeviceConnection.class);
            startActivity(todevice);
        }
        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {

        } else if (id == R.id.nav_gallery) {
            Intent todisplay = new Intent(Home.this,ApplianceDisplay.class);
            startActivity(todisplay);
        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    class MinMaxAsync extends AsyncTask<Void,Void,Void>
    {    final ProgressDialog progressDialog = new ProgressDialog(Home.this,
            ProgressView.MODE_DETERMINATE);
        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            progressDialog.setMessage("Synchronizing...");
            progressDialog.show();
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(final Void... params) {

            //Firebase Db
            mDatabase.child("Consumers").child(prefManager.getUserId()).setValue(db.getDetails(prefManager.getUserId()));
            mDatabase.child("Consumers").child(prefManager.getUserId()).child("id").removeValue();

            StringRequest request = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    try {
                        Log.d("Response",response + " ");
                        JSONObject jsonObject = new JSONObject(response);
                        if(jsonObject.names().get(0).equals("success"))
                        {
                            Toast.makeText(Home.this, "Data fetched successfully", Toast.LENGTH_SHORT).show();
                            progressDialog.dismiss();
                        }
                        else {
                            Toast.makeText(Home.this, "Error while retrieving details..", Toast.LENGTH_SHORT).show();
                        }
                    }catch (JSONException e)
                    {
                        Log.e("JSON",e.toString());
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.d("Volley", error.toString());
                }
            }
            ){
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    HashMap<String,String> map = new HashMap<>();
                    map.put("min",minLoad.getText().toString());
                    map.put("max",maxLoad.getText().toString());
                    map.put("id",prefManager.getUserId());
                    map.put("regId",prefManager.getRegID());

                    //Firebase DB
                    mDatabase.child("Consumers").child(prefManager.getUserId()).child("Load Status").setValue(map);
                    mDatabase.child("Consumers").child(prefManager.getUserId()).child("Load Status").child("id").removeValue();

                    return map;
                }
            };
            requestQueue.add(request);
            return null;
        }
    }

    @Override
    protected void onResume() {

        // register GCM registration complete receiver
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(Config.REGISTRATION_COMPLETE));

        // register new push message receiver
        // by doing this, the activity will be notified each time a new message arrives
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(Config.PUSH_NOTIFICATION));

        // clear the notification area when the app is opened
        NotificationUtils.clearNotifications(getApplicationContext());
        super.onResume();
    }

    @Override
    protected void onPause() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mRegistrationBroadcastReceiver);
        super.onPause();
    }
}
