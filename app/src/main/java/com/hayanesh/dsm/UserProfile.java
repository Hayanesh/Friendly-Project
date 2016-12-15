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

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.BitmapImageViewTarget;

import java.io.File;

public class UserProfile extends AppCompatActivity {
    ImageButton user_pic;
    final static int RESULT_LOAD_IMAGE = 222;
    DatabaseHelper db;
    TextView name,phone,locality,type,category;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        user_pic = (ImageButton)findViewById(R.id.user_profile_photo);
        initView();



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
        String email = getIntent().getStringExtra("email");
        try {
            Glide.with(this).load(R.drawable.profile_bg).asBitmap().centerCrop().into((ImageView) findViewById(R.id.header_cover_image));
        } catch (Exception e) {
            e.printStackTrace();
        }
        db = new DatabaseHelper(getApplicationContext());
        Details d1 = new Details();
        d1 = db.getDetails(email);
        System.out.println(d1.getId()+" "+d1.getName()+" "+d1.getEmail()+" "+d1.getPass()+" "+d1.getPhone()+" "+d1.getLocality()+" "+d1.getCategory()+" "+d1.getType()  );
        name = (TextView)findViewById(R.id.cname);
        phone = (TextView)findViewById(R.id.cno);
        type = (TextView)findViewById(R.id.ctype);
        category = (TextView)findViewById(R.id.c_cat);
        locality = (TextView)findViewById(R.id.c_locality);
        try {
            name.setText(d1.getName());
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
