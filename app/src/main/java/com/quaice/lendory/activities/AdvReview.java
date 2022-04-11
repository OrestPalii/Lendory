package com.quaice.lendory.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import com.quaice.lendory.MyRecyclerViewAdapter;
import com.quaice.lendory.R;
import com.quaice.lendory.typeclass.Adv;

import java.util.ArrayList;
import java.util.List;

public class AdvReview extends AppCompatActivity {
    private Adv cur;
    private TextView name, description, location, sellername, sellerphone;
    private CardView sellercard, backcard, imagecard;

    private void init(){
        cur = MyRecyclerViewAdapter.current;
        MyRecyclerViewAdapter.current = null;
        name = findViewById(R.id.name);
        name.setText(cur.getName());
        description = findViewById(R.id.description);
        description.setText(cur.getDescription());
        location = findViewById(R.id.location);
        location.setText(cur.getLocation());
        sellername = findViewById(R.id.profilename);
        sellername.setText(cur.getCreator().getName());
        sellerphone = findViewById(R.id.profilephone);
        sellerphone.setText(cur.getCreator().getPhoneNumber());
        sellercard = findViewById(R.id.profilecard);
        backcard = findViewById(R.id.backcard);
        imagecard = findViewById(R.id.imagecard);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_adv_review);
        init();
        backcard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

    }
}