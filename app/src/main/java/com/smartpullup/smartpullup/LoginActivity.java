package com.smartpullup.smartpullup;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.Profile;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class LoginActivity extends AppCompatActivity {
    private static final String TAG = "LoginActivity";

    private FirebaseAuth mAuth;
    FirebaseUser currentUser;
    private FirebaseDatabase database;
    private DatabaseReference userDatabase;

    private CallbackManager mCallbackManager;

    private RelativeLayout overlay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        overlay = (RelativeLayout)findViewById(R.id.LoginProgressOverlay);

        //FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        userDatabase = database.getReference("Users");

        //Login with email and password
        Button loginButton = (Button)findViewById(R.id.btn_Login);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final EditText txtUsername = (EditText)findViewById(R.id.edit_Email_L);
                final EditText txtPassword = (EditText)findViewById(R.id.edit_Password_L);
                String email = txtUsername.getText().toString();
                String password = txtPassword.getText().toString();

                if(email != null && !email.equals("") && password != null && !password.equals(""))
                {
                    mAuth.signInWithEmailAndPassword(email, password)
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        Log.d(TAG, "signInWithEmail:complete");
                                        //FirebaseUser user = mAuth.getCurrentUser();
                                        goToMainActivity();
                                        
                                        //Notification to watch Demo video on first loging
                                        final NotificationCompat.Builder mBuilder = (NotificationCompat.Builder) new NotificationCompat.Builder(LoginActivity.this).setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.pull_up)).setSmallIcon(R.drawable.pull_up).setContentTitle("Demo Video").setContentText("Watch how Smart pull-up works").setPriority(Notification.PRIORITY_MAX).setVibrate(new long[]{Notification.DEFAULT_VIBRATE});
                                        Intent resultIntent = new Intent(Intent.ACTION_VIEW);
                                        resultIntent.setData(Uri.parse("https://www.youtube.com/watch?v=slYcC98yrVI&feature=youtu.be"));
                                        PendingIntent resultPendingIntent = PendingIntent.getActivity(LoginActivity.this, 0, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                                        mBuilder.setContentIntent(resultPendingIntent);
                                        mBuilder.setAutoCancel(true);
                                        final int mNotificationID =001;
                                        NotificationManager mNotifyMgr= (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
                                        mNotifyMgr.notify(mNotificationID,mBuilder.build());

                                    } else {
                                        Log.w(TAG, "signInWithEmail:failure", task.getException());
                                        Toast.makeText(LoginActivity.this, "Authentication failed. \n Wrong username or password.",
                                                Toast.LENGTH_LONG).show();
                                        txtUsername.setText("");
                                        txtPassword.setText("");
                                    }
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(LoginActivity.this, "Failed to authenticate. try again later", Toast.LENGTH_LONG).show();
                                }
                            });
                }
                else Toast.makeText(LoginActivity.this, "Empty field",
                        Toast.LENGTH_SHORT).show();
            }
        });

//        TextView registerTextView=(TextView)findViewById(R.id.btn_Register);
//        registerTextView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent register = new Intent(LoginActivity.this,RegisterActivity.class);
//                startActivity(register);
//            }
//        });

        // Initialize Facebook Login
        mCallbackManager = CallbackManager.Factory.create();
        LoginButton fbLoginButton = findViewById(R.id.login_button);
        fbLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                overlay.setVisibility(View.VISIBLE);
            }
        });
        fbLoginButton.setReadPermissions("email", "public_profile");
        fbLoginButton.registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.d(TAG, "facebook:onSuccess:" + loginResult);
                handleFacebookAccessToken(loginResult.getAccessToken());
            }

            @Override
            public void onCancel() {
                Log.d(TAG, "facebook:onCancel");
                overlay.setVisibility(View.GONE);
            }

            @Override
            public void onError(FacebookException error) {
                Log.d(TAG, "facebook:onError", error);
                overlay.setVisibility(View.GONE);
                Toast.makeText(LoginActivity.this, "Something went wrong, please check your facebook account and internet settings and try again", Toast.LENGTH_LONG);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        currentUser = mAuth.getCurrentUser();
        if(currentUser != null){
            goToMainActivity();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Pass the activity result back to the Facebook SDK
        mCallbackManager.onActivityResult(requestCode, resultCode, data);
    }

    private void handleFacebookAccessToken(AccessToken token) {
        Log.d(TAG, "handleFacebookAccessToken:" + token);

        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");
                            //FirebaseUser user = mAuth.getCurrentUser();
                            goToMainActivity();
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            Toast.makeText(LoginActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    public void goToMainActivity(){
        currentUser = mAuth.getCurrentUser();
        checkIfFirstFacebookLogin();
        Intent login = new Intent(LoginActivity.this,MainActivity.class);
        startActivity(login);
        overlay.setVisibility(View.GONE);
        finish();
    }

    private void checkIfFirstFacebookLogin() {
        userDatabase.child(currentUser.getUid()).equalTo(currentUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(!dataSnapshot.exists()){
                  /*  Log.d(TAG + " fbLogin", Profile.getCurrentProfile().getFirstName());
                    Log.d(TAG + " fbLogin", Profile.getCurrentProfile().getLastName());
                    Log.d(TAG + " fbLogin", currentUser.getUid());
                    Log.d(TAG + " fbLogin", currentUser.getEmail());
                  */

                    Profile p = Profile.getCurrentProfile();
                    if(p != null){
                        User newuser = new User(currentUser.getUid(), p.getFirstName(), p.getLastName(), currentUser.getEmail(), "Not found","Not found","Not found");
                        userDatabase.child(currentUser.getUid()).setValue(newuser);
                    }

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d(TAG, databaseError.getMessage());
            }
        });

    }
}
