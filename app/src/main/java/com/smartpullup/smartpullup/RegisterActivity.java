package com.smartpullup.smartpullup;

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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RegisterActivity extends AppCompatActivity {
    private static final String TAG = "RegisterActivity";

    private EditText txtPassword;
    private EditText txtRePassword;
    private EditText txtEmail;
    private EditText txtFirstName;
    private EditText txtLastName;
    private EditText txtHeight;
    private EditText txtWeight;
    private EditText txtDateBirth;

    private Button btnRegister;
    private FirebaseAuth mAuth;
    private FirebaseDatabase database;
    private DatabaseReference userDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        txtEmail = (EditText) findViewById(R.id.edit_Email_R);
        txtPassword = (EditText) findViewById(R.id.edit_Password_R);
        txtRePassword = (EditText) findViewById(R.id.input_reEnterPassword);
        txtFirstName = (EditText) findViewById(R.id.edit_FirstName);
        txtLastName = (EditText) findViewById(R.id.edit_LastName);
        txtDateBirth = (EditText)findViewById(R.id.edit_Date_of_Birth);
        txtHeight = (EditText)findViewById(R.id.edit_Height);
        txtWeight = (EditText) findViewById(R.id.edit_Weight);
        btnRegister = (Button) findViewById(R.id.btn_Register);

        //getting the link with the database
        database = FirebaseDatabase.getInstance();
        userDatabase = database.getReference("Users");
        mAuth = FirebaseAuth.getInstance();

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createAccount();
            }
        });
    }

    private void createAccount() {
        final String email = txtEmail.getText().toString();
        final String password = txtPassword.getText().toString();
        final String repassword = txtRePassword.getText().toString();
        final String firstName = txtFirstName.getText().toString();
        final String lastName = txtLastName.getText().toString();
        final String height = txtHeight.getText().toString();
        final String weight = txtWeight.getText().toString();
        final String dateBirth = txtDateBirth.getText().toString();

        if(email != null && !email.equals("")
                && password != null && !password.equals("")
                && repassword != null && !repassword.equals("")
                && firstName!= null && !firstName.equals("")
                && lastName!= null && !lastName.equals("")
                && height!= null && !height.equals("")
                && weight!= null && !weight.equals("")
                && dateBirth!= null && !dateBirth.equals("")){
            if(repassword.equals(password)){
                mAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    // Sign in success, update UI with the signed-in user's information
                                    Log.d(TAG, "createUserWithEmail:success");

                                    //create entry in database for user
                                    FirebaseUser user = mAuth.getCurrentUser();
                                    String id = user.getUid();
                                    User newuser = new User(id, firstName, lastName, email, height, weight, dateBirth);
                                    userDatabase.child(id).setValue(newuser);

                                    Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                                    startActivity(intent);

                                }
                                else {
                                    // If sign in fails, display a message to the user.
                                    Log.w(TAG, "createUserWithEmail:failure", task.getException());
                                    String errorMessage = task.getException().toString();
                                    Toast.makeText(RegisterActivity.this, errorMessage,
                                            Toast.LENGTH_LONG).show();
                                }
                            }
                        });
            } else{
                Toast.makeText(RegisterActivity.this, "Passwords don't match",
                        Toast.LENGTH_SHORT).show();
            }

        }

        else{
            Toast.makeText(RegisterActivity.this, "Empty field",
                    Toast.LENGTH_SHORT).show();
        }
    }
}
