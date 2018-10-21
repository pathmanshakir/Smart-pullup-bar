package com.smartpullup.smartpullup;

import android.app.Activity;
import android.app.Dialog;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import static android.content.Context.MODE_PRIVATE;

public class DialogEditProfileActivity extends Dialog implements
        android.view.View.OnClickListener{

    public DialogEditProfileActivity(@NonNull Context context) {
        super(context);
        try{
            host=(MainActivity)context;
        }
        catch (ClassCastException a){
            a.printStackTrace();
        }

    }

    private EditText Height;
    private EditText Weight;
    private EditText DateBirth;
    private Button Save;
    private Button Cancel;
    private MainActivity host;
    private String userId;
    private FirebaseAuth mAuth;
    private FirebaseDatabase database;
    FirebaseUser currentUser;
    private DatabaseReference userDatabase;
    String weight;
    String height;
    String dateOfBirth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dialog_edit_profile);
        DateBirth = (EditText)findViewById(R.id.edit_Date_of_Birth);
        Height = (EditText)findViewById(R.id.height_edt);
        Weight = (EditText) findViewById(R.id.weight_edt);
        Save=(Button)findViewById(R.id.btn_Save);
        Cancel=(Button)findViewById(R.id.btn_cancel);

        database = FirebaseDatabase.getInstance();
        userDatabase = database.getReference("Users");
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        userId = currentUser.getUid();

        userDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                Log.d("User", userId);
                Log.d("User", dataSnapshot.getValue().toString());



            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

                Toast.makeText(getContext(), "Hello",
                        Toast.LENGTH_SHORT).show();
            }
        });


        Save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                weight = Weight.getText().toString();
                height = Height.getText().toString();
                dateOfBirth = DateBirth.getText().toString();

                if(weight !=null && !weight.equals("") && height!=null && !height.equals("") && dateOfBirth !=null &&!dateOfBirth.equals("")) {
                    userDatabase.child(userId).child("weight").setValue(weight);
                    userDatabase.child(userId).child("height").setValue(height);
                    userDatabase.child(userId).child("dateBirth").setValue(dateOfBirth);
                    host.refreshProfile();
                    dismiss();
                }
                else {
                    Toast.makeText(getContext(), "Please fill all fields", Toast.LENGTH_LONG).show();
                }



            }
        });

        Cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
    }


    @Override
    public void onClick(View v) {

    }
}
