package com.quaice.lendory.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.quaice.lendory.MyRecyclerViewAdapter;
import com.quaice.lendory.R;
import com.quaice.lendory.typeclass.Adv;

public class AdvReview extends AppCompatActivity {
    private Adv cur;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_adv_review);
        cur = MyRecyclerViewAdapter.current;
        MyRecyclerViewAdapter.current = null;
    }
}