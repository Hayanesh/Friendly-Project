package com.hayanesh.dsm;

import android.content.Context;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.github.zagum.expandicon.ExpandIconView;

import java.util.List;

/**
 * Created by Hayanesh on 16-Dec-16.
 */

public class AppliancesDashAdapter extends RecyclerView.Adapter<AppliancesDashAdapter.MyViewHolder> {

    private Context mContext;
    private List<Appliances> appList;
    public int w = 0,q = 0,s=0;
    DatabaseHelper db;
    String selected_watt,selected_qty;
    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView name,watt;
        public EditText quantity,rating;
        public LinearLayout expandable_linear;
        public LinearLayout top_linear;
        public Button save;
        public ExpandIconView expandIconView;
        public MyViewHolder(View view) {
            super(view);
            name = (TextView)view.findViewById(R.id.app_name);
            watt = (TextView)view.findViewById(R.id.wattage);
            quantity =(EditText)view.findViewById(R.id.qty);
            rating = (EditText)view.findViewById(R.id.rate);
            expandable_linear = (LinearLayout)view.findViewById(R.id.app_below);
            save = (Button)view.findViewById(R.id.save);
            expandIconView =(ExpandIconView)view.findViewById(R.id.expand_icon);
            top_linear =(LinearLayout)view.findViewById(R.id.linearTop);

        }
    }


    public AppliancesDashAdapter(Context mContext, List<Appliances> appList) {
        this.mContext = mContext;
        this.appList = appList;
        db = new DatabaseHelper(mContext.getApplicationContext());
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.appliance_info, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder,final int position) {

        final Appliances appliances = appList.get(position);
        s = appliances.getShiftable();
        String ret_rating = appliances.getRating()[0];
        String ret_quantity = String.valueOf(appliances.getQty());
        holder.name.setText(appliances.getName());
        holder.rating.setText(ret_rating);
        holder.quantity.setText(ret_quantity);
        int wt = Integer.parseInt(appliances.getRating()[0])*appliances.getQty();
        holder.watt.setText(String.valueOf(wt)+ "W");


        holder.top_linear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.expandIconView.switchState();
                if(holder.expandable_linear.getVisibility() == LinearLayout.GONE)
                {
                    holder.expandable_linear.setVisibility(LinearLayout.VISIBLE);
                    holder.expandable_linear.animate().alpha(1.0f).setDuration(400).start();
                }
                else {
                    holder.expandable_linear.animate().alpha(0.0f).setDuration(400).start();
                    holder.expandable_linear.setVisibility(LinearLayout.GONE);
                }

            }
        });

        holder.save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selected_qty = holder.quantity.getText().toString();
                selected_watt = holder.rating.getText().toString();
                String name = holder.name.getText().toString();
                if(selected_qty.equals("") || selected_watt.equals(""))
                {
                    Toast.makeText(mContext, "Some fields are not set", Toast.LENGTH_SHORT).show();
                }
                else {
                    Appliances app = new Appliances(name,new String[]{selected_watt},Integer.parseInt(selected_qty),s);
                    db.updateAppobj(app);
                    appliances.setQty(Integer.parseInt(selected_qty));
                    appliances.setRating(new String[]{selected_watt});
                    notifyDataSetChanged();
                }

            }
        });
    }


    @Override
    public int getItemCount() {
        return appList.size();
    }
}

