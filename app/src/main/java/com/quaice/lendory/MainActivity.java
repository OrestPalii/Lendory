package com.quaice.lendory;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.quaice.lendory.typeclass.Adv;
import com.quaice.lendory.typeclass.User;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private RelativeLayout sender, new_adv;
    private CardView cancel, send;
    private EditText name_edit, desc_edit, search;
    private ArrayList<Adv> downloaded;
    private ArrayList<Adv> sorted;
    private ArrayList<ImageView> photos;
    private DatabaseReference myRef;
    private ArrayList<String> images;

    private void init(){
        images = new ArrayList<>();
        photos = new ArrayList<>();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        FirebaseDatabase database = FirebaseDatabase.getInstance("https://lendory-b5d8b-default-rtdb.firebaseio.com/");
        myRef = database.getReference("advertisement");
        init();
        photos.add(findViewById(R.id.first_image));
        photos.add(findViewById(R.id.second_image));
        photos.add(findViewById(R.id.third_image));
        photos.add(findViewById(R.id.forth_image));
        search = findViewById(R.id.search);
        search.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if ((event.getAction() == KeyEvent.ACTION_DOWN) &&
                        (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    if(search.getText().toString().equals("")){
                        build_recycler(downloaded);
                    }else {
                        sorted = new ArrayList<>();
                        for (int i = 0; i < downloaded.size(); i++) {
                            if (downloaded.get(i).getName().contains(search.getText().toString()))
                                sorted.add(downloaded.get(i));
                        }
                        build_recycler(sorted);
                    }
                    return true;
                }
                return false;
            }
        });
        send = findViewById(R.id.send_button);
        name_edit = findViewById(R.id.name_edit);
        desc_edit = findViewById(R.id.description_edit);
        cancel = findViewById(R.id.cancel);
        new_adv = findViewById(R.id.new_adw);
        sender = findViewById(R.id.add);

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
                imageChooser();
            }
        });

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Adv cur = new Adv(name_edit.getText().toString(),desc_edit.getText().toString(),"Львів","$",1000,50,2,10,
                        false,images,new User("Антон", "+380976547419"));

                myRef.child("" + cur.hashCode()).setValue(cur);
                new_adv.setVisibility(View.INVISIBLE);
                images = new ArrayList<>();
            }
        });

        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                downloaded = new ArrayList<>();
                for (DataSnapshot dataSnapshotchild : dataSnapshot.getChildren()) {
                    downloaded.add(dataSnapshotchild.getValue(Adv.class));
                }
                build_recycler(downloaded);
            }

            @Override
            public void onCancelled(DatabaseError error) {}
        });
    }

    void build_recycler(ArrayList<Adv> list){
        RecyclerView recyclerView = findViewById(R.id.recycler);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 1));
        MyRecyclerViewAdapter  adapter = new MyRecyclerViewAdapter(this, list);
        recyclerView.setAdapter(adapter);
    }

    void imageChooser() {
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
                    photos.get(0).clearColorFilter();
                    photos.get(0).setImageResource(0);
                    photos.get(0).setImageURI(selectedImageUri);

                    FirebaseStorage storage = FirebaseStorage.getInstance("gs://lendory-b5d8b.appspot.com/");;
                    StorageReference ref = storage.getReference().child("images/" + photos.get(0).hashCode());
                    ref.putFile(selectedImageUri);

                    images.add("" + photos.get(0).hashCode());
                }
            }
        }
    }
}
