package com.hayanesh.dsm;

import java.lang.reflect.Array;

/**
 * Created by Hayanesh on 18-Nov-16.
 */

public class Appliances {
    String name = null;
    String[] rating = null;
    String qty = null;
    //boolean selected = false;
    public Appliances(String name,String[] rating,String qty)
    {
        super();
        this.name = name;
        this.rating = rating.clone();
        this.qty = qty;
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

    public String getQty()
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

}
