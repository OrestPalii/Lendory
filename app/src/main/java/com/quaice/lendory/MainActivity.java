package com.quaice.lendory;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.net.URL;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    Button sender;
    Context context;
    ArrayList<Adv> downloaded;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context = this;
        FirebaseDatabase database = FirebaseDatabase.getInstance("https://lendory-b5d8b-default-rtdb.firebaseio.com/");
        DatabaseReference myRef = database.getReference("advertisement");

        sender = findViewById(R.id.sender);
        sender.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ArrayList<String> arrayList = new ArrayList<>();
                arrayList.add("dsfsafd");arrayList.add("dsfsafd1");arrayList.add("dsfsafd2");
                Adv cur = new Adv("Оголошення","Опис","Львів","$",1000,50,2,10,
                        false,arrayList,new User("Антон", "+380976547419"));
                myRef.child("" + cur.hashCode()).setValue(cur);

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
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        MyRecyclerViewAdapter  adapter = new MyRecyclerViewAdapter(this, list);
        recyclerView.setAdapter(adapter);
    }
}
