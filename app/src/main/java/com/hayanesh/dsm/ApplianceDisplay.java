package com.hayanesh.dsm;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.gitonway.lee.niftymodaldialogeffects.lib.Effectstype;
import com.gitonway.lee.niftymodaldialogeffects.lib.NiftyDialogBuilder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import static com.hayanesh.dsm.app.Config.serverIp;

public class ApplianceDisplay extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    TextView Name;
    Handler progressHandler = new Handler();
    int i = 0;
    JSONArray jsonArray;
    RequestQueue requestQueue;
    JsonObjectRequest request;
    String URL = "http://"+ serverIp +"/DSM/ApplianceStore.php";
    ArrayList<Appliances> appliances;
    RecyclerView recyclerView2;
    AppliancesDashAdapter appliancesDashAdapter;
    DatabaseHelper db;
    PrefManager prefManager;
    DrawerLayout drawer;
    private TextView user_name,user_email;
    private ImageView user_pic;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_appliance);
        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Name = (TextView)findViewById(R.id.display_name);
        db = new DatabaseHelper(getApplicationContext());
        prefManager = new PrefManager(getApplicationContext());
        requestQueue = Volley.newRequestQueue(this);
        recyclerView2 = (RecyclerView) findViewById(R.id.recycler_view2);

        appliances = new ArrayList<>();
        appliances = db.getAppliances();
        appliancesDashAdapter = new AppliancesDashAdapter(this, appliances);

        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(this, 1);
        recyclerView2.setLayoutManager(mLayoutManager);
        recyclerView2.setItemAnimator(new DefaultItemAnimator());
        recyclerView2.setAdapter(appliancesDashAdapter);
        Name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new ApplianceAsync().execute();
            }
        });

        FloatingActionButton floatingActionButton = (FloatingActionButton)findViewById(R.id.add_fab);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent toEdit = new Intent(ApplianceDisplay.this,DSM.class);
                startActivity(toEdit);
            }
        });
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
    }
    class ApplianceAsync extends AsyncTask<Void,Void,Void>
    {
        ProgressDialog progressDialog = new ProgressDialog(ApplianceDisplay.this);
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog.setMessage("Storing in Database...");
            progressDialog.show();
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            progressDialog.dismiss();

        }

        @Override
        protected Void doInBackground(Void... params) {
            networkStore();
            try {
                Thread.sleep(1000);
            }catch (Exception e)
            {
                e.printStackTrace();
            }
            return null;
        }
    }


    public void networkStore()
    {
        String id = prefManager.getUserId();
        List<Appliances> retrived_app = new ArrayList<Appliances>();
        retrived_app = db.getAppliances();
        jsonArray = new JSONArray();
        for(int i =0;i<retrived_app.size();i++)
        {
            Appliances appliances = retrived_app.get(i);
            try {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("connection_id",id);
                jsonObject.put("appliance",appliances.getName());
                jsonObject.put("rating",appliances.getRating()[0]);
                jsonObject.put("qty",appliances.getQty());
                jsonObject.put("shiftable",appliances.getShiftable());
                jsonArray.put(jsonObject);
            }catch (JSONException e)
            {
                Log.e("JSON",e.toString());
            }
        }
        JSONObject app_json = new JSONObject();
        try {
            app_json.put("AppList",jsonArray);
            Log.d("JSON LIST",app_json.toString());
        }catch (Exception e)
        {
            Log.e("JSON",e.toString());
        }
        request = new JsonObjectRequest(Request.Method.POST, URL, app_json, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try
                {
                    if(response.names().get(0).equals("success"))
                    {
                        Toast.makeText(ApplianceDisplay.this, "Success", Toast.LENGTH_SHORT).show();
                    }
                    else
                    {
                        Toast.makeText(ApplianceDisplay.this, "Cant reach", Toast.LENGTH_SHORT).show();
                    }
                }catch (JSONException e)
                {
                    Log.e("Json",e.toString());
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("JSON",error.toString());
            }
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Content-Type", "application/json");
                headers.put( "charset", "utf-8");
                return headers;
            }
        };
        requestQueue.add(request);

    }

    public class GridSpacingItemDecoration extends RecyclerView.ItemDecoration {

        private int spanCount;
        private int spacing;
        private boolean includeEdge;

        public GridSpacingItemDecoration(int spanCount, int spacing, boolean includeEdge) {
            this.spanCount = spanCount;
            this.spacing = spacing;
            this.includeEdge = includeEdge;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            int position = parent.getChildAdapterPosition(view); // item position
            int column = position % spanCount; // item column

            if (includeEdge) {
                outRect.left = spacing - column * spacing / spanCount; // spacing - column * ((1f / spanCount) * spacing)
                outRect.right = (column + 1) * spacing / spanCount; // (column + 1) * ((1f / spanCount) * spacing)

                if (position < spanCount) { // top edge
                    outRect.top = spacing;
                }
                outRect.bottom = spacing; // item bottom
            } else {
                outRect.left = column * spacing / spanCount; // column * ((1f / spanCount) * spacing)
                outRect.right = spacing - (column + 1) * spacing / spanCount; // spacing - (column + 1) * ((1f /    spanCount) * spacing)
                if (position >= spanCount) {
                    outRect.top = spacing; // item top
                }
            }
        }
    }

    /**
     * Converting dp to pixel
     */
    private int dpToPx(int dp) {
        Resources r = getResources();
        return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics()));
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);
        MenuItem menuItem = menu.findItem(R.id.action_settings);
        String User_id = prefManager.getUserEmail();
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
                    Intent i = new Intent(ApplianceDisplay.this,LoginActivity.class);
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
            Intent todevice = new Intent(ApplianceDisplay.this,DeviceConnection.class);
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
            finish();
            Intent toHome = new Intent(ApplianceDisplay.this,Home.class);
            startActivity(toHome);
        } else if (id == R.id.nav_gallery) {
            Toast.makeText(this, "Already there..", Toast.LENGTH_SHORT).show();
        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
