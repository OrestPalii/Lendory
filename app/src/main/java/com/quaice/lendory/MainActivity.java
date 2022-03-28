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

        ArrayList<Adv> downloaded = new ArrayList<>();
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot dataSnapshotchild : dataSnapshot.getChildren())
                    downloaded.add(dataSnapshot.getValue(Adv.class));
                Toast.makeText(context, "" + downloaded.size(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelled(DatabaseError error) {}
        });
        String[] data = {"1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23", "24", "25", "26", "27", "28", "29", "30", "31", "32", "33", "34", "35", "36", "37", "38", "39", "40", "41", "42", "43", "44", "45", "46", "47", "48"};

        // set up the RecyclerView
        RecyclerView recyclerView = findViewById(R.id.recycler);
        int numberOfColumns = 2;
        recyclerView.setLayoutManager(new GridLayoutManager(this, numberOfColumns));
        MyRecyclerViewAdapter  adapter = new MyRecyclerViewAdapter(this, data);
        //adapter.setClickListener(this);
        recyclerView.setAdapter(adapter);

    }
}
