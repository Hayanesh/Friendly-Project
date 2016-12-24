package com.hayanesh.dsm;

import java.lang.reflect.Array;

/**
 * Created by Hayanesh on 18-Nov-16.
 */

public class Appliances {
    String name = null;
    String[] rating = null;
    int qty = 0;
    int shiftable =0;
    //boolean selected = false;
    public Appliances()
    {

    }
    public Appliances(String name,String[] rating,int qty,int shiftable)
    {
        super();
        this.name = name;
        this.rating = rating.clone();
        this.qty = qty;
        this.shiftable = shiftable;
    }
    public Appliances(String name,String[] rating,int qty)
    {
        super();
        this.name = name;
        this.rating = rating.clone();
        this.qty = qty;
        this.shiftable = 0;
    }


    public void setName(String name)
    {
        this.name = name;
    }

    public String getName()
    {
        return name;
    }

    public void setQty(boolean selected)
    {
        this.qty = qty;
    }
    public void setQty(int qty)
    {
        this.qty = qty;
    }
    public int getQty()
    {
        return qty;
    }

    public void setRating(String[] rating)
    {
        this.rating = rating.clone();

    }
    public String[] getRating()
    {
        return rating;
    }

    public void setShiftable(int shiftable) {
        this.shiftable = shiftable;
    }

    public int getShiftable() {
        return shiftable;
    }
}
