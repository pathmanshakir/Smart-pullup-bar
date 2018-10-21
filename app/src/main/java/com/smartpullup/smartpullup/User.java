package com.smartpullup.smartpullup;

import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Jorren on 1/03/2018.
 */

public class User {
    private static final String TAG = "User.class";

    private FirebaseDatabase database;
    private DatabaseReference userRef;

    private String id;
    private String firstName;
    private String lastName;
    private String email;
    private String weight;
    private String height;
    private String dateBirth;
    private List<Exercise> exercises;

    public User(){
        this.exercises = new ArrayList<>();
    }

    public User(String id) {
        this.id = id;
        this.exercises = new ArrayList<>();
        database = FirebaseDatabase.getInstance();
        userRef = database.getReference("Users/"+id);
        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User u = dataSnapshot.getValue(User.class);
                firstName = u.getFirstName();
                lastName = u.getLastName();
                email = u.getEmail();
                if(u.getExercises() != null)
                    exercises = u.getExercises();
                Log.i(TAG, "onDataChange: user filled in");
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e(TAG, "getting userdata from firebase was cancelled");
            }
        });
    }

    public User(String id, String firstName, String lastName, String email, String height, String weight, String dateBirth) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.weight = weight;
        this.height = height;
        this.dateBirth = dateBirth;
        this.exercises = new ArrayList<>();
    }


    public String getId() {
        return id;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getEmail() {
        return email;
    }

    public String getHeight() {
        return height;
    }

    public String getWeght() {
        return weight;
    }

    public String getDateBirth() {
        return dateBirth;
    }

    public List<Exercise> getExercises() {
        return exercises;
    }

}
