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
import com.google.firebase.storage.UploadTask;
import com.quaice.lendory.R;
import com.quaice.lendory.adapters.MyRecyclerViewAdapter;
import com.quaice.lendory.constants.Const;
import com.quaice.lendory.typeclass.Adv;
import com.quaice.lendory.typeclass.User;
import java.util.ArrayList;

import cn.pedant.SweetAlert.SweetAlertDialog;
import es.dmoral.toasty.Toasty;

public class YourAdverts extends AppCompatActivity {
    private RecyclerView recyclerView;
    private CardView backcard, cancel, send;
    private ArrayList<Adv> downloaded;
    private RelativeLayout rentcard, noADVLayout;
    private int photoposition;
    public static DatabaseReference myRef;
    public static EditText name_edit, desc_edit, lock_edit, area_edit, room_edit, price_edit, floor_edit;
    public static RelativeLayout new_adv;
    public static RadioButton yesbut;
    public static TextView currency;
    public static ArrayList<ImageView> photos;
    public static ArrayList<String> images;
    public static String hashNumber;
    public static StorageReference mImageStorage, ref;
    public static Adv curentedit;
    public static boolean canupdate = true;

    private void init(){
        canupdate = true;
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
        noADVLayout = findViewById(R.id.noadvlayout);
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
        canupdate = true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_your_adverts);
        init();

        //Download all created by you adverts
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (canupdate){
                    //Toast.makeText(YourAdverts.this, "dsfdsffds" + dataSnapshot.getChildrenCount(), Toast.LENGTH_SHORT).show();
                    downloaded = new ArrayList<>();
                    for (DataSnapshot dataSnapshotchild : dataSnapshot.getChildren()) {
                        try {
                            for (int i = 0; i < MainActivity.yourAccount.getCreated().size(); i++) {
                                if (MainActivity.yourAccount.getCreated().get(i).
                                        equals(dataSnapshotchild.getValue(Adv.class).getHashnumber()))
                                    downloaded.add(dataSnapshotchild.getValue(Adv.class));
                            }
                        }catch (Exception e){break;}
                        //Toast.makeText(YourAdverts.this, "" + dataSnapshotchild.getValue(Adv.class).getName(), Toast.LENGTH_SHORT).show();
                    }
                    if(downloaded.size() != 0)
                        build_recycler(downloaded, recyclerView);
                    else
                        noADVLayout.setVisibility(View.VISIBLE);
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

        photosOnClick(0);
        photosOnClick(1);
        photosOnClick(2);
        photosOnClick(3);

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Adv cur = createNewAdv(curentedit);
                if(cur != null) {
                    cur.setHashnumber(hashNumber);
                    myRef.child("" + hashNumber).setValue(cur);
                    new_adv.setVisibility(View.INVISIBLE);
                    images = new ArrayList<>();
                    canupdate = true;
                    refresh();
                    MainActivity.canrefresh = true;
                    advCreatroCleaner();
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
                    ref.putFile(selectedImageUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                            ref.child("images/" + photos.get(photoposition).hashCode()).getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                                @Override
                                public void onComplete(@NonNull Task<Uri> task) {
                                    if (task.isSuccessful()) {
                                        if (images.size()>photoposition)
                                            images.set(photoposition,"" + task.getResult());
                                        else
                                            images.add("" + task.getResult());
                                    }
                                }
                            });
                        }
                    });
                }
            }
        }
    }

    public void refresh(){
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (canupdate){
                    downloaded = new ArrayList<>();
                    for (DataSnapshot dataSnapshotchild : dataSnapshot.getChildren()) {
                        try {
                            for (int i = 0; i < MainActivity.yourAccount.getCreated().size(); i++) {
                                if (MainActivity.yourAccount.getCreated().get(i)
                                        .equals(dataSnapshotchild.getValue(Adv.class).getHashnumber()))
                                    downloaded.add(dataSnapshotchild.getValue(Adv.class));
                            }
                        }catch (Exception e){break;}
                    }
                    build_recycler(downloaded, recyclerView);
                    canupdate = false;
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {}
        });
    }

    public static void showEditDialog(Adv curedit, Context context){
        curentedit = curedit;
        images = curedit.getImages();
        name_edit.setText(curedit.getName());
        desc_edit.setText(curedit.getDescription());
        lock_edit.setText(curedit.getLocation());
        area_edit.setText("" + curedit.getArea());
        room_edit.setText("" + curedit.getNumberOfRooms());
        floor_edit.setText("" + curedit.getFloor());
        price_edit.setText("" + curedit.getPrice());
        if (curedit.isVolunteering())
            yesbut.setChecked(true);
        else
            yesbut.setChecked(false);
        show_image(curedit,0, context);
        new_adv.setVisibility(View.VISIBLE);
    }

    public static void show_image(Adv cur, int n, Context context){
        if (n < cur.getImages().size()) {
            Glide.with(context).load(cur.getImages().get(n)).into(photos.get(n));
            int ni = n + 1;
            show_image(cur, ni, context);
        }
    }

    private Adv createNewAdv(Adv redcur){
        int price = 0;
        boolean vol;
        boolean somethingNotFilled = false;
        try {
            price = Integer.parseInt(price_edit.getText().toString());
        }catch (Exception e){};
        //Filds review
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
            Adv cur = new Adv(name_edit.getText().toString(), desc_edit.getText().toString(),
                    lock_edit.getText().toString(), currency.getText().toString(), price,
                    Integer.parseInt(area_edit.getText().toString()), Integer.parseInt(room_edit.getText().toString()),
                    Integer.parseInt(floor_edit.getText().toString()), vol, images,
                    new User(MainActivity.yourAccount.getName(), MainActivity.yourAccount.getPhonenumber()), true);
            return cur;
        }else{
            new SweetAlertDialog(YourAdverts.this, SweetAlertDialog.ERROR_TYPE)
                    .setTitleText("Увага!")
                    .setContentText("Заповніть усі поля")
                    .show();
            //Toasty.error(this, "Заповніть усі поля!", Toast.LENGTH_SHORT, true).show();
            return  null;
        }
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

    private boolean areElementEmpty(TextView textView){
        if(textView.getText().toString().equals(""))
            return true;
        else
            return false;
    }

    public static void deleteAdv(Context context){
        canupdate = true;
        MainActivity.canrefresh = true;
        myRef.child("" + hashNumber).removeValue();
        new SweetAlertDialog(context, SweetAlertDialog.SUCCESS_TYPE)
                .setTitleText("")
                .setContentText("Оголошення успішно видалене")
                .show();
    }

    private void photosOnClick(int position){
        photos.get(position).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                imageChooser(position);
            }
        });
    }
    public void advCreatroCleaner(){
        for(int i = 0; i < photos.size(); i++) {
            photos.get(photoposition).clearColorFilter();
            photos.get(photoposition).setImageResource(R.drawable.plus_img);
            photos.get(photoposition).setImageURI(null);
        }
        name_edit.setText("");
        desc_edit.setText("");
        lock_edit.setText("");
        currency.setText("");
        price_edit.setText("");
        area_edit.setText("");
        room_edit.setText("");
        floor_edit.setText("");
        yesbut.setChecked(false);
        images = new ArrayList<>();
    }
}