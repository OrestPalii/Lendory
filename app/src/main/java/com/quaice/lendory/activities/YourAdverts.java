package com.quaice.lendory.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.quaice.lendory.R;
import com.quaice.lendory.adapters.MyRecyclerViewAdapter;
import com.quaice.lendory.constants.Const;
import com.quaice.lendory.typeclass.Adv;

import java.util.ArrayList;

public class YourAdverts extends AppCompatActivity {
    private RecyclerView recyclerView;
    private CardView backcard;
    private DatabaseReference myRef;
    private ArrayList<Adv> downloaded;
    private boolean canupdate = true;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_your_adverts);
        recyclerView = findViewById(R.id.recycler);
        backcard = findViewById(R.id.backcard);
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
    }
    void build_recycler(ArrayList<Adv> list, RecyclerView recyclerView){
        recyclerView.setLayoutManager(new GridLayoutManager(this, 1));
        MyRecyclerViewAdapter adapter = new MyRecyclerViewAdapter(this, list, true);
        recyclerView.setAdapter(adapter);
    }
}