package com.quaice.lendory.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.InputType;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
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
import com.quaice.lendory.adapters.MyRecyclerViewAdapter;
import com.quaice.lendory.R;
import com.quaice.lendory.Registration;
import com.quaice.lendory.constants.Const;
import com.quaice.lendory.typeclass.Account;
import com.quaice.lendory.typeclass.Adv;
import com.quaice.lendory.typeclass.User;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private RecyclerView recyclerView, likerecycler;
    private RelativeLayout new_adv, rentcard;
    private CardView cancel, send, menu_show, menu_hide, menu, sender, logout, settingscard;
    private EditText name_edit, desc_edit, lock_edit, area_edit, room_edit, help_edit, price_edit, floor_edit, search;
    private ImageView homepagebut, likedpagebut, searchbutton;
    private ArrayList<Adv> downloaded;
    private ArrayList<Adv> sorted;
    private ArrayList<ImageView> photos;
    private DatabaseReference myRef, acc;
    private ArrayList<String> images;
    private TextView your_name, your_phone, currency;
    private int photoposition;
    public static Account yourAccount;
    private ArrayList<Adv> likedByYou;
    private RadioButton yesbut, nobut;
    private Button canceler;
    public static boolean canrefresh = true;

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
        //help_edit = findViewById(R.id.elp_edit);
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
        yesbut = findViewById(R.id.yesrad);
        nobut = findViewById(R.id.norad);
        currency = findViewById(R.id.currency);
        rentcard = findViewById(R.id.rentcard);
        searchbutton = findViewById(R.id.lupsearch);
        settingscard = findViewById(R.id.settingscard);
        canceler = findViewById(R.id.canceler);
        your_phone.setText(Registration.phone_str);
        homepagebut.setImageResource(R.drawable.selectedhome);
        likedpagebut.setImageResource(R.drawable.heart);
        name_edit.setHeight(0);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        FirebaseDatabase database = FirebaseDatabase.getInstance(Const.DATABASE_URL);
        myRef = database.getReference("advertisement");
        init();
        acc = database.getReference("profiles/"+your_phone.getText().toString());
        acc.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                yourAccount = dataSnapshot.getValue(Account.class);
                your_name.setText(yourAccount.getName());
                myRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if(canrefresh) {
                            downloaded = new ArrayList<>();
                            for (DataSnapshot dataSnapshotchild : dataSnapshot.getChildren()) {
                                downloaded.add(dataSnapshotchild.getValue(Adv.class));
                            }
                            build_recycler(downloaded, recyclerView);
                            canrefresh = false;
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError error) {}
                });
            }

            @Override
            public void onCancelled(DatabaseError error) {}

        });

        search.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if(canceler.getVisibility() == View.INVISIBLE)
                    canceler.setVisibility(View.VISIBLE);
                if ((event.getAction() == KeyEvent.ACTION_DOWN) &&
                        (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    search();
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
                    yourAccount.addnewAdv(hn);
                    acc.setValue(yourAccount);
                    new_adv.setVisibility(View.INVISIBLE);
                    images = new ArrayList<>();
                    canrefresh = true;
                }
            }
        });


        menu_show.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showpanel();
            }
        });
        menu_hide.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hidepanel();
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

        currency.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean can = true;
                if(currency.getText().toString().equals("₴") && can) {
                    currency.setText("$");
                    can = false;
                }
                if(currency.getText().toString().equals("$") && can) {
                    currency.setText("€");
                    can = false;
                }
                if(currency.getText().toString().equals("€") && can) {
                    currency.setText("₴");
                    can = false;
                }
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

        settingscard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hidepanel();
                Intent intent = new Intent(MainActivity.this, YourAdverts.class);
                startActivity(intent);
            }
        });

        searchbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                search();
            }
        });

        canceler.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(menu.getVisibility() == View.VISIBLE)
                    hidepanel();
                else {
                    search.setEnabled(false);
                    search.setEnabled(true);
                }
                view.setVisibility(View.INVISIBLE);
            }
        });
    }

    void build_recycler(ArrayList<Adv> list, RecyclerView recyclerView){
        recyclerView.setLayoutManager(new GridLayoutManager(this, 1));
        MyRecyclerViewAdapter adapter = new MyRecyclerViewAdapter(this, list, false);
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
            }
            if (images.size() == 0){
                images.add("NoImages");
            }
            //змінні
            Adv cur = new Adv(name_edit.getText().toString(), desc_edit.getText().toString(),
                    lock_edit.getText().toString(), currency.getText().toString(), price,
                    Integer.parseInt(area_edit.getText().toString()), Integer.parseInt(room_edit.getText().toString()),
                    Integer.parseInt(floor_edit.getText().toString()), vol, images,
                    new User(yourAccount.getName(), yourAccount.getPhonenumber()));
            return cur;
        }else{
            Toast.makeText(this, "Заповніть усі поля!", Toast.LENGTH_SHORT).show();
            return  null;
        }
    }

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

    private void hidepanel(){
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

    private void showpanel(){
        canceler.setVisibility(View.VISIBLE);
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

    private void search(){
        search.setEnabled(false);
        search.setEnabled(true);
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
    }
}
