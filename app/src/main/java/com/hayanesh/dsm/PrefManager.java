package com.hayanesh.dsm;
import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by Hayanesh on 16-Jun-16.
 */
public class PrefManager {
    SharedPreferences pref;
    SharedPreferences.Editor editor;
    Context _context;

    //shared pref mode
    int PRIVATE_MODE = 0;

    //Shared prefereces file name
    private static final String PREF_NAME = "android-welcome";
    private static final String IS_FIRST_TIME_LAUNCH = "IsFirstTimeLaunch";
    private static final String USER_EMAIL ="UserEmail";
    private static final String USER_NAME = "UserName";
    private static final String USER_PIC = "UserPic";
    private static final String USER_ID = "UserId";
    private static final String USER_CAT = "UserCat";
    private static final String MODE = "Mode";
    private static final String RegID = "regId";
    private static final String MIN = "min";
    private static final String MAX = "max";
    //Constructor
    public PrefManager(Context context){
        this._context = context;
        pref = _context.getSharedPreferences(PREF_NAME,PRIVATE_MODE);
        editor = pref.edit();
    }

    public void setFirstTimeLaunch(boolean isFirstTime){
        editor.putBoolean(IS_FIRST_TIME_LAUNCH,isFirstTime);
        editor.commit();
    }
    public void setUserEmail(String email)
    {
        editor.putString(USER_EMAIL,email);
        editor.commit();
    }
    public void setUserPic(String pic)
    {
        editor.putString(USER_PIC,pic);
        editor.commit();
    }
    public void setUserName(String name)
    {
        editor.putString(USER_NAME,name);
        editor.commit();
    }
    public boolean isFirstTimeLaunch(){
        return pref.getBoolean(IS_FIRST_TIME_LAUNCH,true);
    }

    public String getUserEmail()
    {
        return pref.getString(USER_EMAIL,"test@mail.com");
    }

    public String getUserName(){
        return pref.getString(USER_NAME,"test");
    }
    public String getUserPic()
    {
        return pref.getString(USER_PIC,null);
    }

    public void setUserId(String userId) {
        editor.putString(USER_ID,userId);
        editor.commit();
    }

    public String getUserId() {
        return pref.getString(USER_ID,"0000");
    }

    public String getUserCat() {
        return pref.getString(USER_CAT,"Residential");
    }

    public void setUserCat(String userCat) {
        editor.putString(USER_CAT,userCat);
        editor.commit();
    }

    public void setMODE(Boolean mode) {
        editor.putBoolean(MODE,mode);
        editor.commit();
    }

    public void setRegID(String regID)
    {
        editor.putString(RegID,regID);
        editor.commit();
    }
    public String getRegID()
    {
        return pref.getString(RegID,"0000");
    }
    public Boolean getMODE() {
        return pref.getBoolean(MODE,false);
    }

    public int getMin()
    {
        return pref.getInt(MIN,0);
    }
    public void setMin(int min)
    {
        editor.putInt(MIN,min);
        editor.commit();
    }
    public int getMax()
    {
        return pref.getInt(MAX,0);
    }
    public void setMax(int max)
    {
        editor.putInt(MAX,max);
        editor.commit();
    }
}
