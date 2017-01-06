package com.hayanesh.dsm;

import android.app.Activity;
import android.app.ProgressDialog;
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
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewSwitcher;

import com.reginald.editspinner.EditSpinner;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by Hayanesh on 18-Nov-16.
 */

public class AppliancesAdapter extends RecyclerView.Adapter<AppliancesAdapter.MyViewHolder> {

    private Context mContext;
    private List<Appliances> appList;
    List<Integer> qt;
    List<String> rating;
    int w = 0,q = 0;
    String selected_watt;
    String selected_qty;
    String selected_name;
    String selected_shift;
    String shiftable[] = new String[]{"(Non - Shiftable Appliance )","( Shiftable Appliance )"};
    DatabaseHelper db;
    PrefManager prefManager;
    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView title;
        public ImageButton remove;
        public EditSpinner rating;
        public EditSpinner editText;
        public TextView watt;
        public Button save;
        public TextView shift;
        public MyViewHolder(View view) {
            super(view);
            db = new DatabaseHelper(view.getContext().getApplicationContext());
            title = (TextView) view.findViewById(R.id.app_name);
            editText = (EditSpinner) view.findViewById(R.id.qty);
            rating = (EditSpinner) view.findViewById(R.id.rating);
            watt = (TextView)view.findViewById(R.id.wattage);
            remove = (ImageButton) view.findViewById(R.id.overflow);
            shift = (TextView)view.findViewById(R.id.shift_textView);


           // save = (Button)view.findViewById(R.id.save);
        }
    }


    public AppliancesAdapter(Context mContext, List<Appliances> appList) {
        this.mContext = mContext;
        this.appList = appList;
        qt = new LinkedList<>();
        rating = new LinkedList<>();
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.appliance_info2, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {

        final Appliances appliances = appList.get(position);
        Log.d("IN Adapter",""+appliances.getShiftable());
        holder.title.setText(appliances.getName());
        holder.shift.setText(shiftable[appliances.getShiftable()]);
        holder.rating.setAdapter(new ArrayAdapter<String>(mContext, android.R.layout.simple_dropdown_item_1line, appliances.getRating()));
        holder.editText.setAdapter(new ArrayAdapter<String>(mContext,android.R.layout.simple_dropdown_item_1line,mContext.getResources().getStringArray(R.array.qty_array)));

        holder.remove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selected_name = holder.title.getText().toString();
                appList.remove(holder.getAdapterPosition());
                //this.notifyAll();
                notifyItemRemoved(holder.getAdapterPosition());
                // v.setTag(String.valueOf(position));
                Appliances a=new Appliances(selected_name,new String[]{null},0);
                db.updateAppobj(a);

                if(mContext instanceof DSM){
                    ((DSM)mContext).removeItem(selected_name);
                    ((DSM)mContext).changeUnit();
                }

            }
        });
        holder.editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                int si;
                selected_watt = holder.rating.getText().toString();
                selected_qty = holder.editText.getText().toString();
                selected_name = holder.title.getText().toString();
                selected_shift = holder.shift.getText().toString();
                if(selected_shift.equals(shiftable[0]))
                {
                  si = 0 ;
                }
                else {
                    si =1;
                }
                try {
                    q = Integer.parseInt(selected_qty);
                }catch (NumberFormatException e)
                {

                    Log.e("Q value",""+q);
                    q = 0;
                }

                if (selected_watt.equals("")) {
                    w = 0;
                } else {
                    w = Integer.parseInt(selected_watt);
                }
                if (w * q == 0) {
                    holder.watt.setText("");
                } else {
                    holder.watt.setText(w * q + " W");
                }
                // qt.add(position,Integer.parseInt(selected_qty));
                Appliances a=new Appliances(selected_name,new String[]{selected_watt},q,si);
                db.updateAppobj(a);
            }
        });

        holder.rating.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                int si;
                selected_watt = holder.rating.getText().toString();
                selected_qty = holder.editText.getText().toString();
                selected_name = holder.title.getText().toString();
                selected_shift = holder.shift.getText().toString();
                if(selected_shift.equals(shiftable[0]))
                {
                    si = 0 ;
                }
                else {
                    si =1;
                }
                try{
                    w = Integer.parseInt(selected_watt);
                }catch (NumberFormatException e)
                {

                    Log.d("W value"," "+w);
                    w = 0;
                }
                if (selected_qty.equals("")) {
                    q = 0;
                } else {
                    q = Integer.parseInt(selected_qty);
                }
                if (w * q == 0) {
                    holder.watt.setText("");
                } else {
                    holder.watt.setText(w * q + " W");
                }
                Appliances a=new Appliances(selected_name,new String[]{selected_watt},q,si);
                db.updateAppobj(a);

            }
        });
    }
       /* holder.spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selected_watt = parent.getItemAtPosition(position).toString();
                selected_qty = holder.editText.getText().toString();
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
              //  rating.add(position,selected_watt);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });*/

      /*  holder.overflow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    showPopupMenu(holder.overflow);
                }
            });}*/


    /**
     * Showing popup menu when tapping on 3 dots
     */
   /* private void showPopupMenu(View view) {
        // inflate menu
        PopupMenu popup = new PopupMenu(mContext, view);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.menu, popup.getMenu());
        popup.setOnMenuItemClickListener(new MyMenuItemClickListener());
        popup.show();
    }*/

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
