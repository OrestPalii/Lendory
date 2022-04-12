package com.quaice.lendory.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.viewpager.widget.ViewPager;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.quaice.lendory.adapters.MyRecyclerViewAdapter;
import com.quaice.lendory.R;
import com.quaice.lendory.adapters.ViewPagerAdapters;
import com.quaice.lendory.constants.Const;
import com.quaice.lendory.typeclass.Adv;

import java.util.ArrayList;

public class AdvReview extends AppCompatActivity {
    private Adv cur;
    private ImageView mainImage;
    private TextView name, description, location, floor, sellername, sellerphone, price;
    private CardView sellercard, backcard, imagecard, infocard, profilecard;
    private ViewPager viewPager;
    private ViewPagerAdapters adapter;
    private StorageReference mImageStorage, ref;
    private ArrayList<String> images;
    private static final int MY_PERMISSION_REQUEST_CODE_CALL_PHONE = 555;

    private void init(){
        cur = MyRecyclerViewAdapter.current;
        MyRecyclerViewAdapter.current = null;
        name = findViewById(R.id.name);
        name.setText(cur.getName());
        description = findViewById(R.id.description);
        description.setText(cur.getDescription());
        location = findViewById(R.id.location);
        location.setText(cur.getLocation());
        floor = findViewById(R.id.floor);
        floor.setText(cur.getFloor() + " Поверх");
        sellername = findViewById(R.id.profilename);
        sellername.setText(cur.getCreator().getName());
        sellerphone = findViewById(R.id.profilephone);
        sellerphone.setText(cur.getCreator().getPhoneNumber());
        sellercard = findViewById(R.id.profilecard);
        backcard = findViewById(R.id.backcard);
        imagecard = findViewById(R.id.imagecard);
        infocard = findViewById(R.id.infocard);
        viewPager = findViewById(R.id.view_pager);
        mainImage = findViewById(R.id.main_image);
        price = findViewById(R.id.price);
        profilecard = findViewById(R.id.profilecard);
        price.setText(cur.getPrice() + " " + cur.getCurrency());
        //price.setTextSize(name.getTextSizeUnit(), name.getTextSize());
        mImageStorage = FirebaseStorage.getInstance(Const.STORAGE_URL).getReference();
        ref = mImageStorage.child("images/");
        ref.child(cur.getImages().get(0) + "/").getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                if (task.isSuccessful()) {
                    Glide.with(AdvReview.this).load(task.getResult()).into(mainImage);
                }
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_adv_review);
        init();
        backcard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (imagecard.getVisibility() == View.VISIBLE) {
                    finish();
                }else{
                    imagecard.setVisibility(View.VISIBLE);
                    infocard.setVisibility(View.VISIBLE);
                    viewPager.setVisibility(View.INVISIBLE);
                }
            }
        });
        imagecard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(adapter == null) {
                    showpager();
                }else{
                    imagecard.setVisibility(View.INVISIBLE);
                    infocard.setVisibility(View.INVISIBLE);
                    viewPager.setVisibility(View.VISIBLE);
                }
            }
        });
        profilecard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                askPermissionAndCall();
            }
        });
    }

    private void show_image(int n){
        if (n < cur.getImages().size()) {
            ref.child(cur.getImages().get(n) + "/").getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()) {
                        Uri downUri = task.getResult();
                        images.add(downUri.toString());
                        int ni = n + 1;
                        show_image(ni);
                    }
                }
            });
        }else{
            adapter = new ViewPagerAdapters(AdvReview.this, images);
            viewPager.setAdapter(adapter);
            imagecard.setVisibility(View.INVISIBLE);
            infocard.setVisibility(View.INVISIBLE);
            viewPager.setVisibility(View.VISIBLE);
        }
    }
    private void showpager(){
        images = new ArrayList<>();
        show_image(0);
    }

    private void askPermissionAndCall() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) { // 23
            int sendSmsPermisson = ActivityCompat.checkSelfPermission(this,
                    Manifest.permission.CALL_PHONE);
            if (sendSmsPermisson != PackageManager.PERMISSION_GRANTED) {
                this.requestPermissions(
                        new String[]{Manifest.permission.CALL_PHONE},
                        MY_PERMISSION_REQUEST_CODE_CALL_PHONE
                );
                return;
            }
        }
        this.callNow();
    }

    @SuppressLint("MissingPermission")
    private void callNow() {
        String phoneNumber = this.cur.getCreator().getPhoneNumber();
        Intent callIntent = new Intent(Intent.ACTION_CALL);
        callIntent.setData(Uri.parse("tel:" + phoneNumber));
        try {
            this.startActivity(callIntent);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case MY_PERMISSION_REQUEST_CODE_CALL_PHONE: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    this.callNow();
                }
                else {}
                break;
            }
        }
    }

}
