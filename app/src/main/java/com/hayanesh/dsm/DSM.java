package com.hayanesh.dsm;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Rect;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.rey.material.widget.ProgressView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DSM extends AppCompatActivity {
    ArrayList<String> checkedValue;
    ArrayList<Integer> checkedPosition;
    int cat_choice = 0;
    LinearLayout title;
    String[] selected_cat_appliances;
    String[][] selected_cat_rating;
    String selectedAppliances;
    PrefManager prefManager;
    RequestQueue requestQueue;
    JSONArray jsonArray;
    String URL = "http://192.168.1.4/DSM/ApplianceStore.php";
    JsonObjectRequest request;
    String res_rating[][]  =  new String[][]{{"0","0"},{"100", "60"}, {"25", "50", "75"}, {"10", "25"},{"100","200","450"},
            {"1200","1300","1500"},{"2000","2500","3000"},{"150","200","400"},{"1000","2000","4000"},{"150","200"},
            {"50","100"},{"600","1000","1700"},{"800","1000","1800"},{"200","500","700"},{"500","700","600"},
            {"700","800","900"},{"200","400","600"},{"200","300","400"},{"750","1000","1500"}};
    String com_rating[][]  =  new String[][]{{"0","0"},{"100", "60"}, {"25", "50", "75"},
            {"150", "200","400"},{"1000","2000","4000"},{"100","200","450"},{"50","70","100"},
            {"12","24"},{"600","1000","1700"},{"200","500","700"},{"2000","2500","3000"},
            {"500","600","700"},{"800","900","1000"},{"2000","4000","8000"},{"800","900","1000"},{"40","70","100"}};
    String ind_rating[][]  =  new String[][]{{"0","0"},{"100", "60"}, {"40", "70", "100"}, {"25", "50","75"},
            {"100","200","450"},{"1000","2000","3000"},{"150","200","400"},{"12","24"},
            {"800","900","1000"},{"2000","4000","8000"},{"200","500","700"},{"6000","7000","8000"},
            {"6000","7000","8000"},{"6000","7000","8000"},{"6000","7000","8000"},{"5000","7000","8000"},
            {"6000","7000","8000"},{"7000","8000","9000"},{"6000","7000","8000"}};
    int [] res_shift = {0,0,0, 0, 0, 1, 1, 0, 1, 0, 0, 1, 0, 1, 1, 1, 1, 0, 1};
    int[] com_shift = {0,0,0,0,1,0,0,0,0,1,1,1,0,0,1,0};
    int[] ind_shift = {0,0,0,0,0,1,1,0,0,0,1,1,1,1,1,1,1,1,1};
    int [] shiftable = {};
    String rating[][];
    ListView navigationView;//This is appliance listview
    RecyclerView recyclerView;
    ArrayList<Appliances> appList;
    AppliancesAdapter adapter;
    DrawerLayout drawer;
    DatabaseHelper db;
    TextView units;
    Button save;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dsm);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        prefManager = new PrefManager(this.getApplicationContext());
        title = (LinearLayout)findViewById(R.id.list_title);
        units = (TextView)findViewById(R.id.units);
        save = (Button)findViewById(R.id.save_app);
        db = new DatabaseHelper(getApplicationContext());
        requestQueue = Volley.newRequestQueue(this);
        try
        {
            String cat = prefManager.getUserCat();
            if (cat.equals("Residential"))
                cat_choice = 0;
            else if (cat.equals("Commercial"))
                cat_choice = 1;
            else
                cat_choice = 2;
        }catch (Exception e)
        {
            Toast.makeText(this, "category not selected", Toast.LENGTH_SHORT).show();
        }
        Toolbar tb = new Toolbar(this);


        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);

        appList = new ArrayList<>();

        adapter = new AppliancesAdapter(this, appList);

        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(this, 1);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.addItemDecoration(new GridSpacingItemDecoration(1, dpToPx(2), true));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);

        final FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clearAdapter();
                navigationView.clearChoices();
                db.removeAll();

            }
        });
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Log.d("TAG","inside onclick");

                //store the above to server...
                new AppliancAsync().execute();
                finish();
                Intent toViewApp = new Intent(DSM.this,ApplianceDisplay.class);
                startActivity(toViewApp);

                //Intent toHome = new Intent(DSM.this,Home.class);
                // startActivity(toHome);
            }
        });
        ActionBarDrawerToggle toggle;
            toggle = new ActionBarDrawerToggle(
                    this, drawer,tb, R.string.app_name, R.string.app_name) {

                @Override
                public void onDrawerOpened(View drawerView) {
                    super.onDrawerOpened(drawerView);
                    // checkedValue.clear();
                    // checkedPosition.clear();
                    // clearAdapter();
                }

                @Override
                public void onDrawerClosed(View drawerView) {
                    super.onDrawerClosed(drawerView);
                    doClick(navigationView);
                    Log.d("TAG", checkedValue.toString());
                    changeUnit();
                    // prepareAppliances();

                }
            };
            drawer.addDrawerListener(toggle);
            toggle.syncState();
            displayListView();



    }
    public void changeUnit()
    {
        units.setText(String.valueOf(appList.size()));
    }
    public void displayListView() {
        ArrayList<Appliances> appList = new ArrayList<Appliances>();

        String[] residential_app = {"Tubelight",
                "Ceiling Fan",
                "Table Fan",
                "Computer",
                "Dishwasher",
                "Water Heater",
                "Refrigerator",
                "Air Conditioner",
                "TV",
                "Laptop",
                "Microwave oven",
                "Toaster",
                "Vaccum Cleaner",
                "Washing Machine",
                "Pump Motor",
                "Grinder",
                "Mixer",
                "Electric Iron"};
        String[] commercial_app = {"Tubelight",
                "Fan",
                "Refrigerator",
                "Air Conditioner",
                "Computer",
                "Printer",
                "CCTV camera",
                "Microwave Oven",
                "Vaccum Cleaner",
                "Water heater",
                "Washing Machine",
                "Coffee Maker",
                "UPS",
                "Ice Maker",
                "CFL Bulb"};
        String[] industrial_app = {"Tublelight",
        "CFL bulb","Ceiling Fan","Table Fan","Air Conditioner","Refrigerator","CCTV camera","Coffee Maker","UPS","Vaccum Cleaner",
        "Machine 1","Machine 2","Machine 3","Machine 4","Machine 5","Machine 6","Machine 7","Machine 8"};

        switch (cat_choice)
        {
            case 0: selected_cat_appliances = residential_app.clone();
                    rating = res_rating.clone();
                    shiftable = res_shift.clone();
                    break;
            case 1: selected_cat_appliances = commercial_app.clone();
                    rating = com_rating.clone();
                    shiftable = com_shift.clone();
                    break;
            case 2: selected_cat_appliances = industrial_app.clone();
                    rating = ind_rating.clone();
                    shiftable = ind_shift.clone();
                    break;
        }

      /*  try {
            appList.clear();
            for (int i = 0; i < residential_app.length; i++) {
                Appliances appliances = new Appliances(residential_app[i],false);
                appList.add(appliances);
            }
        }catch (Exception e)
        {
            e.printStackTrace();
        }*/
        //MyCustomAdapter dataAdapter = new MyCustomAdapter(this,R.layout.appliance_info,appList);
        ArrayAdapter arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_multiple_choice, Arrays.asList(selected_cat_appliances));
        navigationView = (ListView) findViewById(R.id.nav_view);
        navigationView.setAdapter(arrayAdapter);
        navigationView.setChoiceMode(AbsListView.CHOICE_MODE_MULTIPLE);
        LayoutInflater inflater = getLayoutInflater();
        ViewGroup header = (ViewGroup)inflater.inflate(R.layout.snippet_header,navigationView, false);
      //  ViewGroup footer = (ViewGroup)inflater.inflate(R.layout.snippet_footer,navigationView, false);
        navigationView.addHeaderView(header, null, false);
        LinearLayout footer = (LinearLayout)findViewById(R.id.footer_below);
      //  navigationView.addFooterView(footer,null,false);
        checkedValue = new ArrayList<String>();
        checkedPosition = new ArrayList<Integer>();
        navigationView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String itemValue = (String) navigationView.getItemAtPosition(position);
                // Toast.makeText(getApplicationContext(), itemValue, Toast.LENGTH_LONG).show();


            }
        });

    }

    public void doClick(View view) {

        SparseBooleanArray viewItems = navigationView.getCheckedItemPositions();
        ArrayList<String> temp_value = new ArrayList<String>();
        ArrayList<Integer> temp_pos = new ArrayList<Integer>();

        Log.d("NAV_COUNT"," "+navigationView.getCount());

        //NV creation
        ArrayList<String> NV = new ArrayList<String>();
        for (int i =1;i<navigationView.getCount();i++)
        {
            if(viewItems.get(i))
            {
                NV.add(navigationView.getItemAtPosition(i).toString());
            }
        }

        //cv contains NV
        for (int i = 0; i < navigationView.getCount(); i++) {
            if (viewItems.get(i)) {
                Log.d("POS",""+i);//Listview positioning starts from 1 **
                if(checkedValue.contains((String)navigationView.getItemAtPosition(i)))
                {
                    continue;
                }
                else
                {
                    checkedValue.add((String) navigationView.getItemAtPosition(i));
                    checkedPosition.add(i);
                    temp_value.add(navigationView.getItemAtPosition(i).toString());
                    temp_pos.add(i);

                }

            }
        }
        if(appList.size() == 0)
        {
            Log.d("TAG","inside prepare");
            prepareAppliances();
        }
        else {
            addItemtoAdapter(temp_value,temp_pos);
            temp_pos.clear();
            temp_value.clear();
        }

     /*   for(int i =0 ;i<=navigationView.getCount();i++)
        {
            if(viewItems.get(i))
            {
                if(checkedValue.contains(navigationView.getItemAtPosition(i).toString()))
                {
                    continue;
                }
                else
                {
                    checkedValue.add(navigationView.getItemAtPosition(i).toString());
                    checkedPosition.add(i);
                    if(appList.isEmpty())
                    {
                        //Creation
                        prepareAppliances();
                    }
                    else
                    {
                        //Insertion
                        Appliances a = new Appliances(checkedValue.get(i).toString(),rating[checkedPosition.get(i)],null);
                        appList.add(a);
                        adapter.notifyItemInserted(i);
                    }
                }
            }
        }*/

        //NV contains CV
        ArrayList<Integer> temp = new ArrayList<>();
        Log.d("checked value size"," "+checkedValue.size());
        for (int i=0;i<checkedValue.size();i++)
        {
            Log.d("CONTENTS",checkedValue.get(i));
            if (NV.contains(checkedValue.get(i))) {
                continue;
            } else {
                System.out.println("Removed value : " + checkedValue.get(i).toString() + " " + checkedPosition.get(i).toString());
                Appliances ap_remove = new Appliances(checkedValue.get(i).toString(),new String[]{null},0);
                db.updateAppobj(ap_remove);
                adapter.notifyItemRemoved(i);
                appList.remove(i);
                checkedValue.remove(i);
                checkedPosition.remove(i);
                temp.add(i);
                //decrementor++;
            }
        }



    }

    public void addItemtoAdapter(ArrayList<String> value,ArrayList<Integer> pos)
    {
        Appliances a;
        for (int i = 0; i <value.size(); i++) {
            Log.d("POS",pos.get(i).toString());
            Log.d("SHOW",value.get(i).toString()+" "+String.valueOf(rating[pos.get(i)][0])+" "+String.valueOf(shiftable[pos.get(i)]));
            a = new Appliances(value.get(i).toString(), rating[pos.get(i)],0, shiftable[pos.get(i)]);
            appList.add(a);
            adapter.notifyItemInserted(pos.get(i));
        }

    }
    @Override
    public void onBackPressed() {
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.END)) {
            drawer.closeDrawer(GravityCompat.END);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.dsm, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        switch (id) {
            case R.id.action_cancel :
                drawer.openDrawer(Gravity.RIGHT);
                break;
           //     return true;
        }


        return super.onOptionsItemSelected(item);
    }

    public void applocalStore()
    {
        if(getAppDetails())
        {
            Toast.makeText(this, "Data Stored", Toast.LENGTH_SHORT).show();
            clearAdapter();
            Intent showActivity = new Intent(this,ApplianceDisplay.class);
            startActivity(showActivity);
        }
        else
        {
            Toast.makeText(this, "Problem in storing data", Toast.LENGTH_SHORT).show();
        }
    }

    public boolean getAppDetails()
    {
        List<Appliances> appliances = new ArrayList<>();
        ArrayList<Integer> qty = new ArrayList<>(adapter.qt);
        ArrayList<String> rating = new ArrayList<>(adapter.rating);
        ArrayList<String> app = new ArrayList<>();
        app = checkedValue;
        //Log.d("COUNT",app.size()+" "+qty.size()+" "+rating.size());
        /*RecyclerView rv = (RecyclerView)findViewById(R.id.recycler_view);
        for(int i = 0;i<rv.getChildCount();i++)
        {
            View v = rv.getChildAt(i);
            EditText editText = (EditText)v.findViewById(R.id.qty);
            TextView ap_name = (TextView) v.findViewById(R.id.app_name);
            Spinner spinner = (Spinner)v.findViewById(R.id.rating);
            try{
                app.add(ap_name.getText().toString());
                qty.add(Integer.parseInt(editText.getText().toString()));
                rating.add(spinner.getSelectedItem().toString());
                Log.e("TAG",editText.getText().toString()+ " "+ spinner.getSelectedItem().toString());
            }
            catch (NumberFormatException e)
            {
                e.printStackTrace();
                editText.setError("Specify quantity");
            }

        }*/
        for (int i =0;i<qty.size();i++)
        {
            Appliances ap = new Appliances();
            ap.setRating(new String[]{rating.get(i)});
            ap.setName(app.get(i));
            ap.setQty(qty.get(i));
            appliances.add(ap);
        }
        StoreData(appliances);
        return true;
    }
    public void StoreData(List<Appliances> appliances)
    {
        DatabaseHelper db = new DatabaseHelper(getApplicationContext());
        db.CreateTableAppliances(appliances);
        db.close();
    }
    public void prepareAppliances() {

        Appliances a;
        for (int i = 0; i < checkedValue.size(); i++) {
            Log.d("IN prepare",""+shiftable[checkedPosition.get(i)]);
            a = new Appliances(checkedValue.get(i).toString(), rating[checkedPosition.get(i).intValue()],0, shiftable[checkedPosition.get(i)]);
            appList.add(a);
        }
        adapter.notifyDataSetChanged();
    }

    public void clearAdapter()
    {
        checkedValue.clear();
        checkedPosition.clear();
        appList.clear();
        adapter = new AppliancesAdapter(this,appList);
        recyclerView.setAdapter(adapter);
    }

    public void removeItem(String value)
    {
       for(int i = 1;i<navigationView.getCount();i++)
       {
           if(navigationView.getItemAtPosition(i).toString().equals(value))
           {
               Log.d("POSITION",""+i);
               navigationView.setItemChecked(i,false);
               break;
           }
       }
        for (int i = 0;i<checkedValue.size();i++)
        {
            if(checkedValue.get(i).equals(value))
            {
                checkedValue.remove(i);
                checkedPosition.remove(i);
                break;
            }
        }

    }

    class AppliancAsync extends AsyncTask<Void,Void,Void>
    {
        final ProgressDialog progressDialog = new ProgressDialog(DSM.this,
                ProgressView.MODE_DETERMINATE);
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
           progressDialog.setMessage("Storing in Database...");
//           progressDialog.show();
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            prefManager.setMODE(true);
           // progressDialog.dismiss();

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
                        Toast.makeText(DSM.this, "Success", Toast.LENGTH_SHORT).show();
                    }
                    else
                    {
                        Toast.makeText(DSM.this, "Cant reach", Toast.LENGTH_SHORT).show();
                    }
                }catch (JSONException e)
                {
                    Log.e("Json",e.toString());
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("JSON","error");
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

   /* @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        CheckBox cb = (CheckBox)view.findViewById(R.id.checkBox1);
        TextView tv = (TextView)view.findViewById(R.id.name);
        cb.performClick();
        if(cb.isChecked()){
            checkedValue.add(tv.getText().toString());

        }else if(!cb.isChecked())
        {
            checkedValue.remove(tv.getText().toString());
        }
    }*/

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

}
