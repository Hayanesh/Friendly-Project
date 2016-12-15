package com.hayanesh.dsm;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Hayanesh on 18-Nov-16.
 */

public class AppliancesAdapter extends RecyclerView.Adapter<AppliancesAdapter.MyViewHolder> {

    private Context mContext;
    private List<Appliances> appList;
    int w = 0,q = 0;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView title;
        public ImageView overflow;
        public Spinner spinner;
        public EditText editText;
        public TextView watt;
        public MyViewHolder(View view) {
            super(view);
            title = (TextView) view.findViewById(R.id.app_name);
            editText = (EditText) view.findViewById(R.id.qty);
            spinner = (Spinner)view.findViewById(R.id.rating);
            watt = (TextView)view.findViewById(R.id.wattage);
            overflow = (ImageView) view.findViewById(R.id.overflow);
        }
    }


    public AppliancesAdapter(Context mContext, List<Appliances> appList) {
        this.mContext = mContext;
        this.appList = appList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.appliance_info, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {

        Appliances appliances = appList.get(position);
        holder.title.setText(appliances.getName());
        holder.spinner.setAdapter(new ArrayAdapter<String>(mContext,android.R.layout.simple_list_item_activated_1,appliances.getRating()));
        holder.editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String selected_watt = holder.spinner.getSelectedItem().toString();
                String selected_qty = holder.editText.getText().toString();
                w = Integer.parseInt(selected_watt);
                if(selected_qty.equals(""))
                {
                    q = 0;
                }
                else
                {
                    q = Integer.parseInt(selected_qty);
                }
                if(w*q == 0)
                {
                    holder.watt.setText("");
                }
                else
                {
                    holder.watt.setText(w*q+" W");
                }

            }
        });

        holder.spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selected_watt = parent.getItemAtPosition(position).toString();
                String selected_qty = holder.editText.getText().toString();
                w = Integer.parseInt(selected_watt);
                if(selected_qty.equals(""))
                {
                    q = 0;
                }
                else
                {
                    q = Integer.parseInt(selected_qty);
                }
                if(w*q == 0)
                {
                    holder.watt.setText("");
                }
                else
                {
                    holder.watt.setText(w*q+" W");
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        holder.overflow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    showPopupMenu(holder.overflow);
                }
            });
    }

    /**
     * Showing popup menu when tapping on 3 dots
     */
    private void showPopupMenu(View view) {
        // inflate menu
        PopupMenu popup = new PopupMenu(mContext, view);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.menu, popup.getMenu());
        popup.setOnMenuItemClickListener(new MyMenuItemClickListener());
        popup.show();
    }

    /**
     * Click listener for popup menu items
     */
    class MyMenuItemClickListener implements PopupMenu.OnMenuItemClickListener {

        public MyMenuItemClickListener() {
        }

        @Override
        public boolean onMenuItemClick(MenuItem menuItem) {
            switch (menuItem.getItemId()) {
                case R.id.action_openRight:
                    Toast.makeText(mContext, "Add to favourite", Toast.LENGTH_SHORT).show();
                    return true;
                default:
            }
            return false;
        }
    }

    @Override
    public int getItemCount() {
        return appList.size();
    }
}
