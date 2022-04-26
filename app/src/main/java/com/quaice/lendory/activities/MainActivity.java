package com.quaice.lendory.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.quaice.lendory.adapters.MyRecyclerViewAdapter;
import com.quaice.lendory.R;
import com.quaice.lendory.constants.Const;
import com.quaice.lendory.typeclass.Account;
import com.quaice.lendory.typeclass.Adv;
import com.quaice.lendory.typeclass.User;
import java.util.ArrayList;
import es.dmoral.toasty.Toasty;

public class MainActivity extends AppCompatActivity {
    private RecyclerView recyclerView, likerecycler;
    private RelativeLayout new_adv, rentcard;
    private CardView cancel, send, menu_show, menu_hide, menu, sender, logout, settingscard, filtercard;
    private EditText name_edit, desc_edit, lock_edit, area_edit, room_edit, help_edit, price_edit, floor_edit, search, search_lockation,
        search_min_price, search_max_price;
    private ImageView homepagebut, likedpagebut, searchbutton, filterShow;
    private ArrayList<Adv> downloaded;
    private ArrayList<Adv> sorted;
    private ArrayList<ImageView> photos;
    private DatabaseReference myRef, acc;
    private ArrayList<String> images;
    private TextView your_name, your_phone, currency, search_cancel, search_use;
    private int photoposition;
    public static Account yourAccount;
    private ArrayList<Adv> likedByYou;
    private RadioButton yesbut;
    private CheckBox search_yes, search_no;
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
        currency = findViewById(R.id.currency);
        rentcard = findViewById(R.id.rentcard);
        searchbutton = findViewById(R.id.lupsearch);
        settingscard = findViewById(R.id.settingscard);
        canceler = findViewById(R.id.canceler);
        filtercard = findViewById(R.id.filterpanel);
        filterShow = findViewById(R.id.sett);
        search_lockation = findViewById(R.id.searchlock);
        search_min_price = findViewById(R.id.searchmin);
        search_max_price = findViewById(R.id.searchmax);
        search_yes = findViewById(R.id.searchyes);
        search_no = findViewById(R.id.searchno);
        search_cancel = findViewById(R.id.seachcanel);
        search_use = findViewById(R.id.searchuse);
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
        //setTheme(R.style.Dark);
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

        photosOnClick(0);
        photosOnClick(1);
        photosOnClick(2);
        photosOnClick(3);

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
                    //Toasty.success(MainActivity.this, "Оголошння створено", Toast.LENGTH_SHORT, true).show();
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
                animateView(homepagebut, R.drawable.selectedhome);
                likedpagebut.setImageResource(R.drawable.heart);
                recyclerView.setVisibility(View.VISIBLE);
                likerecycler.setVisibility(View.INVISIBLE);
            }
        });

        likedpagebut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                animateView(likedpagebut, R.drawable.liked_heart);
                homepagebut.setImageResource(R.drawable.home);
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
                animateView(searchbutton);
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
                    if(filtercard.getVisibility() == View.VISIBLE)
                        filterview(0, -1000);
                }
                view.setVisibility(View.INVISIBLE);
            }
        });

        filterShow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                animateView(filterShow);
                if(filtercard.getVisibility() == View.INVISIBLE) {
                    //Show hide anim method
                    filterview(-1000, 0);
                    canceler.setVisibility(View.VISIBLE);
                }
                else {
                    filterview(0, -1000);
                    canceler.setVisibility(View.INVISIBLE);
                }
            }
        });

        search_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                search.setText("");
                search_lockation.setText("");
                search_min_price.setText("");
                search_max_price.setText("");
                search_yes.setChecked(true);
                search_no.setChecked(true);
                search();
                canceler.setVisibility(View.INVISIBLE);
                filterview(0, -1000);
                build_recycler(downloaded, recyclerView);
            }
        });

        search_use.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                search();
                canceler.setVisibility(View.INVISIBLE);
                filterview(0, -1000);
            }
        });

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
                    StorageReference ref = storage.getReference();
                    ref.child("images/" + photos.get(photoposition).hashCode()).putFile(selectedImageUri);
                    ref.child("images/" + photos.get(photoposition).hashCode()).getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                        @Override
                        public void onComplete(@NonNull Task<Uri> task) {
                            if (task.isSuccessful()) {
                                images.add("" + task.getResult());
                            }
                        }
                    });
                }
            }
        }
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

    private boolean areElementEmpty(EditText textView){
        if(textView.getText().toString().equals(""))
            return true;
        else
            return false;
    }

    private Adv createNewAdv(){
        int price = 0;
        boolean vol;
        boolean somethingNotFilled = false;
        try {
            price = Integer.parseInt(price_edit.getText().toString());
        }catch (Exception e){};
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
            cur.setTime(System.currentTimeMillis());
            return cur;
        }else{
            Toasty.error(this, "Заповніть усі поля!", Toast.LENGTH_SHORT, true).show();
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
                        try {
                            if (yourAccount.getLiked().get(i).equals(dataSnapshotchild.getValue(Adv.class).getHashnumber()))
                                likedByYou.add(dataSnapshotchild.getValue(Adv.class));
                        }catch (NullPointerException e){}
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

    private void filterview(int startY, int endY){
        TranslateAnimation animate = new TranslateAnimation(0, 0, startY, endY);
        animate.setDuration(500);
        animate.setFillAfter(false);
        animate.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                if(startY == 0){
                    filtercard.setEnabled(false);
                    filtercard.setVisibility(View.INVISIBLE);
                }else {
                    filtercard.setEnabled(true);
                    filtercard.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        filtercard.startAnimation(animate);
    }

    public static void animateView(View view){
        Animation click = new ScaleAnimation(1f, 0.7f, 1f, 0.7f,
                Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f);
        click.setDuration(200);
        view.startAnimation(click);
    }

    public static void animateView(ImageView view, int image){
        Animation click = new ScaleAnimation(1f, 0.7f, 1f, 0.7f,
                Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f);
        click.setDuration(200);
        click.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                view.setImageResource(image);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        view.startAnimation(click);
    }

    private void search(){
        search.setEnabled(false);
        search.setEnabled(true);
        sorted = new ArrayList<>();
        if(!areElementEmpty(search)) {
            for (int i = 0; i < downloaded.size(); i++) {
                if (downloaded.get(i).getName().contains(search.getText().toString()))
                    sorted.add(downloaded.get(i));
            }
        }else {
            sorted.addAll(downloaded);
        }
        sort(sorted);
    }

    private void sort(ArrayList<Adv> sorted){
        ArrayList<Adv> preSorted = new ArrayList<>();
        if(!search_yes.isChecked()){
            for (int i = 0; i < sorted.size(); i++) {
                if(sorted.get(i).isVolunteering())
                    sorted.remove(i);
            }
        }
        if(!search_no.isChecked()){
            for (int i = 0; i < sorted.size(); i++) {
                if(!sorted.get(i).isVolunteering())
                    sorted.remove(i);
            }
        }
        if(!areElementEmpty(search_lockation)) {
            for (int i = 0; i < sorted.size(); i++) {
                if (!sorted.get(i).getLocation().contains(search_lockation.getText().toString()))
                    sorted.remove(i);
            }
        }
        int min, max;
        for (int i = 0; i < sorted.size(); i++) {
            try {
                min = Integer.parseInt(search_min_price.getText().toString());
            }catch (Exception e){
                min = 0;
            }
            try {
                max = Integer.parseInt(search_max_price.getText().toString());
            }catch (Exception e){
                max = 1000000000;
            }
            if(sorted.get(i).getPrice()>=min &
                    sorted.get(i).getPrice()<=max)
                preSorted.add(sorted.get(i));
        }
        sorted = preSorted;
        build_recycler(sorted, recyclerView);
    }

    private void photosOnClick(int position){
        photos.get(position).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                imageChooser(position);
            }
        });
    }

}
