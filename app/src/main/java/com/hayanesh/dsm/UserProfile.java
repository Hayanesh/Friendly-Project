package com.hayanesh.dsm;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.BitmapImageViewTarget;

import java.io.File;
import java.util.List;

public class UserProfile extends AppCompatActivity {
    ImageButton user_pic;
    final static int RESULT_LOAD_IMAGE = 222;
    DatabaseHelper db;
    TextView id,name,phone,locality,type,category,shift,nshift,user_email;
    PrefManager prefManager;
    int shiftable =0,nshiftable =0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);
        prefManager = new PrefManager(this.getApplicationContext());
        user_pic = (ImageButton)findViewById(R.id.user_profile_photo);
        shift = (TextView)findViewById(R.id.shift_id);
        nshift = (TextView)findViewById(R.id.non_shift_id);
        initView();
        List<Appliances> app_shift = db.getAppliances();
        for(int i = 0;i<app_shift.size();i++)
        {

            Appliances a = app_shift.get(i);
            Log.d("RETRIVED",a.getName());
            if(a.getShiftable() == 0)
                nshiftable++;
            else
                shiftable++;
        }
      //  Toast.makeText(this, ""+shiftable+" "+nshiftable, Toast.LENGTH_SHORT).show();
        shift.setText(String.valueOf(shiftable));
        nshift.setText(String.valueOf(nshiftable));

        user_pic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(galleryIntent,RESULT_LOAD_IMAGE);
            }
        });




    }
    public void initView()
    {
        Glide.with(this).load(R.drawable.profile_bg).asBitmap().centerCrop().into((ImageView) findViewById(R.id.header_cover_image));
        String email = prefManager.getUserEmail();
        Log.d("EMAIL",email);
        try {
            if(prefManager.getUserPic() == null)
            {
             //   Glide.with(this).load(R.drawable.profile_bg).asBitmap().centerCrop().into((ImageView) findViewById(R.id.user_profile_photo));
            }
            else {
                String imgDecodableString = prefManager.getUserPic();
                prefManager.setUserPic(imgDecodableString);
                Glide.with(this).load(new File(imgDecodableString)).asBitmap().centerCrop().into(new BitmapImageViewTarget((ImageView) findViewById(R.id.user_profile_photo)){
                    @Override
                    protected void setResource(Bitmap resource) {
                        RoundedBitmapDrawable circularBitmapDrawable =
                                RoundedBitmapDrawableFactory.create(getApplication().getResources(), resource);
                        circularBitmapDrawable.setCircular(true);
                        ((ImageView) findViewById(R.id.user_profile_photo)).setImageDrawable(circularBitmapDrawable);
                    }
                });
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        db = new DatabaseHelper(getApplicationContext());
        Details d1 = new Details();
        d1 = db.getDetails(email);
        System.out.println(d1.getId()+" "+d1.getName()+" "+d1.getEmail()+" "+d1.getPass()+" "+d1.getPhone()+" "+d1.getLocality()+" "+d1.getCategory()+" "+d1.getType()  );
        name = (TextView)findViewById(R.id.user_name_display);
        id = (TextView)findViewById(R.id.cname);
        phone = (TextView)findViewById(R.id.cno);
        type = (TextView)findViewById(R.id.ctype);
        category = (TextView)findViewById(R.id.c_cat);
        locality = (TextView)findViewById(R.id.c_locality);
        user_email = (TextView)findViewById(R.id.email);
        String _name = "Hello, "+d1.getName();
        try {
            user_email.setText(email);
            name.setText(_name);
            id.setText(String.valueOf(d1.getId()));
            phone.setText(d1.getPhone());
            type.setText(d1.getType());
            category.setText(d1.getCategory());
            locality.setText(d1.getLocality());
        }catch (NullPointerException e)
        {
            e.printStackTrace();
        }

    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        try {
            if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && data != null) {
                Uri selectedImage = data.getData();
                String[] filepath = {MediaStore.Images.Media.DATA};
                Cursor cursor = getContentResolver().query(selectedImage, filepath, null, null, null);
                cursor.moveToFirst();
                int columnIndex = cursor.getColumnIndex(filepath[0]);
                String imgDecodableString = cursor.getString(columnIndex);
                Glide.with(this).load(new File(imgDecodableString)).asBitmap().centerCrop().into(new BitmapImageViewTarget(user_pic){
                    @Override
                    protected void setResource(Bitmap resource) {
                        RoundedBitmapDrawable circularBitmapDrawable =
                                RoundedBitmapDrawableFactory.create(getApplication().getResources(), resource);
                        circularBitmapDrawable.setCircular(true);
                        user_pic.setImageDrawable(circularBitmapDrawable);
                    }
                });
            }
        }catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}
