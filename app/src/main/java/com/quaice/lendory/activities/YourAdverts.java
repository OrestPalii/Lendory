package com.quaice.lendory.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.quaice.lendory.R;
import com.quaice.lendory.adapters.MyRecyclerViewAdapter;
import com.quaice.lendory.adapters.ViewPagerAdapters;
import com.quaice.lendory.constants.Const;
import com.quaice.lendory.typeclass.Adv;
import com.quaice.lendory.typeclass.User;

import java.util.ArrayList;

public class YourAdverts extends AppCompatActivity {
    private RecyclerView recyclerView;
    private CardView backcard;
    public static DatabaseReference myRef;
    private ArrayList<Adv> downloaded;
    private boolean canupdate = true;
    public static EditText name_edit, desc_edit, lock_edit, area_edit, room_edit, price_edit, floor_edit;
    private CardView cancel, send;
    public static RelativeLayout new_adv;
    private RelativeLayout rentcard;
    public static RadioButton yesbut;
    public static TextView currency;
    public static ArrayList<ImageView> photos;
    public static ArrayList<String> images;
    private int photoposition;
    public static String hashNumber;
    public static StorageReference mImageStorage, ref;

    public static void showEditDialog(Adv curedit, Context context){
        boolean vol = curedit.isVolunteering();
        name_edit.setText(curedit.getName());
        desc_edit.setText(curedit.getDescription());
        lock_edit.setText(curedit.getLocation());
        area_edit.setText("" + curedit.getArea());
        room_edit.setText("" + curedit.getNumberOfRooms());
        floor_edit.setText("" + curedit.getFloor());
        price_edit.setText("" + curedit.getPrice());
        if (vol)
            yesbut.setChecked(true);
        else
            yesbut.setChecked(false);
        show_image(curedit,0, context);
        new_adv.setVisibility(View.VISIBLE);
    }

    public static void show_image(Adv cur, int n, Context context){
        if (n < cur.getImages().size()) {
            ref.child(cur.getImages().get(n) + "/").getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()) {
                        Uri downUri = task.getResult();
                        if(!downUri.toString().equals("NoImages")) {
                            Glide.with(context).load(downUri.toString()).into(photos.get(n));
                            int ni = n + 1;
                            show_image(cur, ni, context);
                        }
                    }
                }
            });
        }else{

        }
    }

    private Adv createNewAdv(){
        int price = 0;
        boolean vol = false;
        boolean somethingNotFilled = false;
        try {
            price = Integer.parseInt(price_edit.getText().toString());
        }catch (Exception e){};
        //}
        //Перевірка заповнення полів
        if(areElementEmpty(name_edit))
            somethingNotFilled = true;
        if(areElementEmpty(desc_edit))
            somethingNotFilled = true;
        if(areElementEmpty(lock_edit))
            somethingNotFilled = true;
        if(areElementEmpty(area_edit))
            somethingNotFilled = true;
        if(areElementEmpty(room_edit))
            somethingNotFilled = true;
        if(areElementEmpty(floor_edit))
            somethingNotFilled = true;
        if(!somethingNotFilled) {
            if(yesbut.isChecked()) {
                vol = true;
                try {
                    price = Integer.parseInt(price_edit.getText().toString());
                }catch (Exception e){};
            }
            else {
                vol = false;
                price = 0;
            }
            if (images.size() == 0){
                images.add("NoImages");
            }
            //змінні
            Adv cur = new Adv(name_edit.getText().toString(), desc_edit.getText().toString(),
                    lock_edit.getText().toString(), currency.getText().toString(), price,
                    Integer.parseInt(area_edit.getText().toString()), Integer.parseInt(room_edit.getText().toString()),
                    Integer.parseInt(floor_edit.getText().toString()), vol, images,
                    new User(MainActivity.yourAccount.getName(), MainActivity.yourAccount.getPhonenumber()));
            return cur;
        }else{
            Toast.makeText(this, "Заповніть усі поля!", Toast.LENGTH_SHORT).show();
            return  null;
        }
    }

    private boolean areElementEmpty(TextView textView){
        if(textView.getText().toString().equals(""))
            return true;
        else
            return false;
    }

    public static void deleteAdv(){
        myRef.child("" + hashNumber).removeValue();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_your_adverts);
        recyclerView = findViewById(R.id.recycler);
        backcard = findViewById(R.id.backcard);
        name_edit = findViewById(R.id.name_edit);
        desc_edit = findViewById(R.id.description_edit);
        lock_edit = findViewById(R.id.location_edit);
        area_edit = findViewById(R.id.area_edit);
        room_edit = findViewById(R.id.roomcount_edit);
        price_edit = findViewById(R.id.price_edit);
        floor_edit = findViewById(R.id.floor_edit);
        cancel = findViewById(R.id.cancel);
        new_adv = findViewById(R.id.new_adw);
        yesbut = findViewById(R.id.yesrad);
        rentcard = findViewById(R.id.rentcard);
        currency = findViewById(R.id.currency);
        send = findViewById(R.id.send_button);
        images = new ArrayList<>();
        photos = new ArrayList<>();
        photos.add(findViewById(R.id.first_image));
        photos.add(findViewById(R.id.second_image));
        photos.add(findViewById(R.id.third_image));
        photos.add(findViewById(R.id.forth_image));

        mImageStorage = FirebaseStorage.getInstance(Const.STORAGE_URL).getReference();
        ref = mImageStorage.child("images/");
        FirebaseDatabase database = FirebaseDatabase.getInstance(Const.DATABASE_URL);
        myRef = database.getReference("advertisement");
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (canupdate){
                    downloaded = new ArrayList<>();
                    for (DataSnapshot dataSnapshotchild : dataSnapshot.getChildren()) {
                        for (int i = 0; i < MainActivity.yourAccount.getCreated().size(); i++) {
                            if (MainActivity.yourAccount.getCreated().get(i).equals(dataSnapshotchild.getValue(Adv.class).getHashnumber()))
                                downloaded.add(dataSnapshotchild.getValue(Adv.class));
                        }
                    }
                    build_recycler(downloaded, recyclerView);
                    canupdate = false;
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {}
        });
        backcard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        yesbut.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b){
                    Animation anim = new ScaleAnimation(
                            1f, 1f,
                            1f, 0f,
                            Animation.RELATIVE_TO_SELF, 0.5f,
                            Animation.RELATIVE_TO_SELF, 0f);
                    anim.setFillAfter(true);
                    anim.setDuration(250);
                    rentcard.startAnimation(anim);
                }else {
                    Animation anim = new ScaleAnimation(
                            1f, 1f,
                            0f, 1f,
                            Animation.RELATIVE_TO_SELF, 0.5f,
                            Animation.RELATIVE_TO_SELF, 0f);
                    anim.setFillAfter(true);
                    anim.setDuration(250);
                    rentcard.startAnimation(anim);
                }
            }
        });
        photos.get(0).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                imageChooser(0);
            }
        });

        photos.get(1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                imageChooser(1);
            }
        });

        photos.get(2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                imageChooser(2);
            }
        });

        photos.get(3).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                imageChooser(3);
            }
        });

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Adv cur = createNewAdv();
                if(cur != null) {
                    cur.setHashnumber(hashNumber);
                    myRef.child("" + hashNumber).setValue(cur);
                    new_adv.setVisibility(View.INVISIBLE);
                    images = new ArrayList<>();
                    canupdate = true;
                }
            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new_adv.setVisibility(View.INVISIBLE);
            }
        });
    }
    void build_recycler(ArrayList<Adv> list, RecyclerView recyclerView){
        recyclerView.setLayoutManager(new GridLayoutManager(this, 1));
        MyRecyclerViewAdapter adapter = new MyRecyclerViewAdapter(this, list, true);
        recyclerView.setAdapter(adapter);
    }
    void imageChooser(int pos) {
        photoposition = pos;
        Intent i = new Intent();
        i.setType("image/*");
        i.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(i, "Select Picture"), 1);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == 1) {
                Uri selectedImageUri = data.getData();
                if (null != selectedImageUri) {
                    photos.get(photoposition).clearColorFilter();
                    photos.get(photoposition).setImageResource(0);
                    photos.get(photoposition).setImageURI(selectedImageUri);
                    //const
                    FirebaseStorage storage = FirebaseStorage.getInstance(Const.STORAGE_URL);;
                    StorageReference ref = storage.getReference().child("images/" + photos.get(photoposition).hashCode());
                    ref.putFile(selectedImageUri);
                    images.add("" + photos.get(photoposition).hashCode());
                }
            }
        }
    }
}