package com.quaice.lendory.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.quaice.lendory.MyRecyclerViewAdapter;
import com.quaice.lendory.R;
import com.quaice.lendory.Registration;
import com.quaice.lendory.typeclass.Account;
import com.quaice.lendory.typeclass.Adv;
import com.quaice.lendory.typeclass.User;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private RecyclerView recyclerView, likerecycler;
    private RelativeLayout new_adv;
    private CardView cancel, send, menu_show, menu_hide, menu, sender, logout;
    private EditText name_edit, desc_edit, lock_edit, area_edit, room_edit, help_edit, price_edit, floor_edit, search;
    private ImageView homepagebut, likedpagebut;
    private ArrayList<Adv> downloaded;
    private ArrayList<Adv> sorted;
    private ArrayList<ImageView> photos;
    private DatabaseReference myRef, acc;
    private ArrayList<String> images;
    private TextView your_name, your_phone;
    private int photoposition;
    public static Account yourAccount;
    private ArrayList<Adv> likedByYou;

    private void showliked(){
        likedByYou = new ArrayList<>();
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                downloaded = new ArrayList<>();
                for (DataSnapshot dataSnapshotchild : dataSnapshot.getChildren()) {
                    for(int i = 0; i < yourAccount.getLiked().size(); i++){
                        if(yourAccount.getLiked().get(i).equals(dataSnapshotchild.getValue(Adv.class).getHashnumber()))
                            likedByYou.add(dataSnapshotchild.getValue(Adv.class));
                    }
                }
                build_recycler(likedByYou, likerecycler);
            }

            @Override
            public void onCancelled(DatabaseError error) {}
        });
        recyclerView.setVisibility(View.INVISIBLE);
        likerecycler.setVisibility(View.VISIBLE);
    }

    private void init(){
        images = new ArrayList<>();
        photos = new ArrayList<>();
        recyclerView = findViewById(R.id.recycler);
        likerecycler = findViewById(R.id.likedrecycler);
        photos.add(findViewById(R.id.first_image));
        photos.add(findViewById(R.id.second_image));
        photos.add(findViewById(R.id.third_image));
        photos.add(findViewById(R.id.forth_image));
        search = findViewById(R.id.search);
        send = findViewById(R.id.send_button);
        name_edit = findViewById(R.id.name_edit);
        desc_edit = findViewById(R.id.description_edit);
        lock_edit = findViewById(R.id.location_edit);
        area_edit = findViewById(R.id.area_edit);
        room_edit = findViewById(R.id.roomcount_edit);
        help_edit = findViewById(R.id.elp_edit);
        price_edit = findViewById(R.id.price_edit);
        floor_edit = findViewById(R.id.floor_edit);
        cancel = findViewById(R.id.cancel);
        new_adv = findViewById(R.id.new_adw);
        sender = findViewById(R.id.add);
        menu_show = findViewById(R.id.menu_show);
        menu_hide = findViewById(R.id.menu_hide);
        menu = findViewById(R.id.menu);
        your_name = findViewById(R.id.ur_name);
        your_phone = findViewById(R.id.ur_phone);
        homepagebut = findViewById(R.id.mainimage);
        likedpagebut = findViewById(R.id.likedimage);
        logout = findViewById(R.id.logout_card);
        your_phone.setText(Registration.phone_str);
        homepagebut.setImageResource(R.drawable.selectedhome);
        likedpagebut.setImageResource(R.drawable.heart);
    }

    private boolean areElementEmpty(TextView textView){
       if(textView.getText().toString().equals(""))
           return true;
       else
           return false;
    }

    private Adv createNewAdv(){
        int price = 0;
        boolean vol = false;
        boolean somethingNotFilled = false;
        if(help_edit.getText().toString().contains("Так")) {
            price = 0;
            vol = true;
        }
        else {
            try {
                price = Integer.parseInt(price_edit.getText().toString());
            }catch (Exception e){};
        }
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
            //змінні
            Adv cur = new Adv(name_edit.getText().toString(), desc_edit.getText().toString(),
                    lock_edit.getText().toString(), "$", price,
                    Integer.parseInt(area_edit.getText().toString()), Integer.parseInt(room_edit.getText().toString()),
                    Integer.parseInt(floor_edit.getText().toString()), vol, images,
                    new User(yourAccount.getName(), yourAccount.getPhonenumber()));
            return cur;
        }else{
            Toast.makeText(this, "Заповніть усі поля!", Toast.LENGTH_SHORT).show();
            return  null;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        FirebaseDatabase database = FirebaseDatabase.getInstance("https://lendory-b5d8b-default-rtdb.firebaseio.com/");
        myRef = database.getReference("advertisement");
        init();
        acc = database.getReference("profiles/"+your_phone.getText().toString());
        acc.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                yourAccount = dataSnapshot.getValue(Account.class);
                your_name.setText(yourAccount.getName());
            }

            @Override
            public void onCancelled(DatabaseError error) {}

        });

        search.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if ((event.getAction() == KeyEvent.ACTION_DOWN) &&
                        (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    if(areElementEmpty(search)){
                        build_recycler(downloaded, recyclerView);
                    }else {
                        sorted = new ArrayList<>();
                        for (int i = 0; i < downloaded.size(); i++) {
                            if (downloaded.get(i).getName().contains(search.getText().toString()))
                                sorted.add(downloaded.get(i));
                        }
                        build_recycler(sorted, recyclerView);
                    }
                    return true;
                }
                return false;
            }
        });

        sender.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new_adv.setVisibility(View.VISIBLE);
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new_adv.setVisibility(View.INVISIBLE);
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
                    String hn = "" + cur.hashCode();
                    cur.setHashnumber(hn);
                    myRef.child("" + cur.hashCode()).setValue(cur);
                    new_adv.setVisibility(View.INVISIBLE);
                    images = new ArrayList<>();
                }
            }
        });

        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                downloaded = new ArrayList<>();
                for (DataSnapshot dataSnapshotchild : dataSnapshot.getChildren()) {
                    downloaded.add(dataSnapshotchild.getValue(Adv.class));
                }
                build_recycler(downloaded, recyclerView);
            }

            @Override
            public void onCancelled(DatabaseError error) {}
        });

        menu_show.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                menu.setVisibility(View.VISIBLE);
                TranslateAnimation animate = new TranslateAnimation(-1000, 0, 0, 0);
                animate.setDuration(500);
                animate.setFillAfter(true);
                animate.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {
                        menu_hide.setEnabled(false);
                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        menu_hide.setEnabled(true);
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });
                menu.startAnimation(animate);
            }
        });
        menu_hide.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TranslateAnimation animate = new TranslateAnimation(0, -1000, 0, 0);
                animate.setDuration(500);
                animate.setFillAfter(false);
                animate.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {
                        menu_show.setEnabled(false);
                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        menu.setVisibility(View.INVISIBLE);
                        menu_show.setEnabled(true);
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });
                menu.startAnimation(animate);
            }
        });

        homepagebut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                homepagebut.setImageResource(R.drawable.selectedhome);
                likedpagebut.setImageResource(R.drawable.heart);
                recyclerView.setVisibility(View.VISIBLE);
                likerecycler.setVisibility(View.INVISIBLE);
            }
        });
        likedpagebut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                homepagebut.setImageResource(R.drawable.home);
                likedpagebut.setImageResource(R.drawable.liked_heart);
                showliked();
            }
        });

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Registration.editor.putBoolean("loggin", false);
                Registration.editor.putString("user_name", "");
                Registration.editor.putString("phone_number", "");
                Registration.editor.commit();
                finish();
            }
        });
    }

    void build_recycler(ArrayList<Adv> list, RecyclerView recyclerView){
        recyclerView.setLayoutManager(new GridLayoutManager(this, 1));
        MyRecyclerViewAdapter adapter = new MyRecyclerViewAdapter(this, list);
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
                    FirebaseStorage storage = FirebaseStorage.getInstance("gs://lendory-b5d8b.appspot.com/");;
                    StorageReference ref = storage.getReference().child("images/" + photos.get(photoposition).hashCode());
                    ref.putFile(selectedImageUri);
                    images.add("" + photos.get(photoposition).hashCode());
                }
            }
        }
    }
}
