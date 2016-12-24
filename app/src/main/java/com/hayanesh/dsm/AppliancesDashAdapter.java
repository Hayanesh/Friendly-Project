package com.hayanesh.dsm;

import android.content.Context;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

/**
 * Created by Hayanesh on 16-Dec-16.
 */

public class AppliancesDashAdapter extends RecyclerView.Adapter<AppliancesDashAdapter.MyViewHolder> {

    private Context mContext;
    private List<Appliances> appList;
    int w = 0,q = 0;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView name,quantity,rating;
        public MyViewHolder(View view) {
            super(view);
            name = (TextView)view.findViewById(R.id.appliance);
            quantity =(TextView)view.findViewById(R.id.Qty);
            rating = (TextView)view.findViewById(R.id.rating);
        }
    }


    public AppliancesDashAdapter(Context mContext, List<Appliances> appList) {
        this.mContext = mContext;
        this.appList = appList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.appliance_display_info, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {

        Appliances appliances = appList.get(position);
        holder.name.setText(appliances.getName());
        holder.quantity.setText("x"+ String.valueOf(appliances.getQty()));
        String[] rate = appliances.getRating().clone();
        holder.rating.setText(rate[0]);
    }


    @Override
    public int getItemCount() {
        return appList.size();
    }
}

