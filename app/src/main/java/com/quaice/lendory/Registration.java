package com.quaice.lendory;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.quaice.lendory.activities.MainActivity;
import com.quaice.lendory.typeclass.Account;

import java.util.ArrayList;

public class Registration extends AppCompatActivity {
    private EditText login_phonenumber, login_password, reg_phonenumber, reg_password, reg_name;
    private CardView reg, login;
    private CardView reg_but, log_but;
    private TextView reg_text, log_text;
    private DatabaseReference myRef;
    private Account you;
    private FirebaseDatabase database;
    public static SharedPreferences.Editor editor;
    public static String name_str, phone_str;
    
    void init(){
        login_phonenumber = findViewById(R.id.loginphonenumber);
        login_password = findViewById(R.id.loginpassword);
        reg_phonenumber = findViewById(R.id.regphonenumber);
        reg_password = findViewById(R.id.regpassword);
        reg_name = findViewById(R.id.reg_name);
        reg = findViewById(R.id.register);
        login = findViewById(R.id.login);
        reg_but = findViewById(R.id.reg_but);
        log_but = findViewById(R.id.logn_but);
        reg_text = findViewById(R.id.regtext);
        log_text = findViewById(R.id.logtext);
        database = FirebaseDatabase.getInstance("https://lendory-b5d8b-default-rtdb.firebaseio.com/");
        myRef = database.getReference("profiles");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        init();
        SharedPreferences activityPreferences = getPreferences(Activity.MODE_PRIVATE);
        editor = activityPreferences.edit();

        if(activityPreferences.getBoolean("loggin", false)){
            name_str = activityPreferences.getString("user_name", "");
            phone_str = activityPreferences.getString("phone_number", "");
            Intent intent = new Intent(Registration.this, MainActivity.class);
            startActivity(intent);
        }

        reg_text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                reg.setVisibility(View.VISIBLE);
                login.setVisibility(View.INVISIBLE);
            }
        });

        log_text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                reg.setVisibility(View.INVISIBLE);
                login.setVisibility(View.VISIBLE);
            }
        });

        reg_but.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ArrayList<String> arr = new ArrayList<>();
                arr.add("ThisHashCodeWillNeverBeUsed");
                ArrayList<String> arrer = new ArrayList<>();
                arrer.add("ThisHashCodeWillNeverBeUsedToo");
                myRef = database.getReference("profiles");
                if(!reg_name.getText().toString().equals("") && !reg_phonenumber.getText().toString().equals("") &&
                        !reg_password.getText().toString().equals("")) {

                    myRef.child(reg_phonenumber.getText().toString()).setValue(new Account(
                            reg_name.getText().toString(), reg_phonenumber.getText().toString(),
                            reg_password.getText().toString(), arr, arrer));
                }
                reg.setVisibility(View.INVISIBLE);login.setVisibility(View.VISIBLE);
            }
        });

        log_but.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                myRef = database.getReference("profiles/"+login_phonenumber.getText().toString());
                myRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        you = dataSnapshot.getValue(Account.class);
                        if(login_phonenumber.getText().toString().equals(you.getPhonenumber())
                            && login_password.getText().toString().equals(you.getPassword())){
                            Intent intent = new Intent(Registration.this, MainActivity.class);
                            startActivity(intent);
                            //Локал сейв
                            editor.putBoolean("loggin", true);
                            editor.putString("user_name", reg_name.getText().toString());
                            editor.putString("phone_number", login_phonenumber.getText().toString());
                            editor.commit();
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError error) {}
                });
            }
        });
    }
}

