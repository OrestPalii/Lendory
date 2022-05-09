package com.quaice.lendory.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
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
import com.quaice.lendory.R;
import com.quaice.lendory.constants.Const;
import com.quaice.lendory.typeclass.Account;
import java.util.ArrayList;

import cn.pedant.SweetAlert.SweetAlertDialog;
import es.dmoral.toasty.Toasty;

public class Registration extends AppCompatActivity {
    private EditText login_phonenumber, login_password, reg_phonenumber, reg_password, reg_name;
    private RelativeLayout reg, login;
    private CardView reg_but, log_but;
    private TextView reg_text, log_text;
    private DatabaseReference myRef;
    private Account you;
    private FirebaseDatabase database;
    public static SharedPreferences.Editor editor;
    //public static String name_str, phone_str;

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
        database = FirebaseDatabase.getInstance(Const.DATABASE_URL);
        myRef = database.getReference("profiles");
//        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        init();
        SharedPreferences activityPreferences = getPreferences(Activity.MODE_PRIVATE);
        editor = activityPreferences.edit();
//        editor.putBoolean("loggin", false);
//        editor.commit();

        if(activityPreferences.getBoolean("loggin", false)){
//            name_str = activityPreferences.getString("user_name", "");
//            phone_str = activityPreferences.getString("phone_number", "");
            myRef = null;
            Intent intent = new Intent(Registration.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
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
                ArrayList<String> likedEmpty = new ArrayList<>();
                likedEmpty.add("ThisHashCodeWillNeverBeUsed");
                ArrayList<String> createdEmpty = new ArrayList<>();
                createdEmpty.add("ThisHashCodeWillNeverBeUsedToo");
                if(!reg_name.getText().toString().equals("") && !reg_phonenumber.getText().toString().equals("") &&
                        !reg_password.getText().toString().equals("")) {

                    myRef.child(reg_phonenumber.getText().toString()).setValue(new Account(
                            reg_name.getText().toString(), reg_phonenumber.getText().toString(),
                            reg_password.getText().toString(), likedEmpty, createdEmpty));
                }
                reg.setVisibility(View.INVISIBLE);
                login.setVisibility(View.VISIBLE);
            }
        });

        log_but.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                database.getReference("profiles").child(""+login_phonenumber.getText().toString()).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DataSnapshot> task) {
                        you = task.getResult().getValue(Account.class);
                        try {
                            if (login_phonenumber.getText().toString().equals(you.getPhonenumber())
                                    && login_password.getText().toString().equals(you.getPassword())) {
                                //Local save
                                editor.putBoolean("loggin", true);
                                editor.putString("user_name", reg_name.getText().toString());
                                editor.putString("phone_number", login_phonenumber.getText().toString());
                                editor.commit();
//                                name_str = activityPreferences.getString("user_name", "");
//                                phone_str = activityPreferences.getString("phone_number", "");
                                myRef = null;
                                Intent intent = new Intent(Registration.this, MainActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                                finish();
                            }else{
                                new SweetAlertDialog(Registration.this, SweetAlertDialog.ERROR_TYPE)
                                        .setTitleText("Увага!")
                                        .setContentText("Хибний номер телефону чи пароль")
                                        .show();
                            }
                        }catch (NullPointerException e){
                            new SweetAlertDialog(Registration.this, SweetAlertDialog.ERROR_TYPE)
                                    .setTitleText("Увага!")
                                    .setContentText("Користувача з таким номером телефону не існує")
                                    .show();
                            //Toasty.error(Registration.this, "Хибний номер телефону чи пароль", Toast.LENGTH_SHORT, true).show();
                        }
                    }
                });
            }
        });
    }
}

