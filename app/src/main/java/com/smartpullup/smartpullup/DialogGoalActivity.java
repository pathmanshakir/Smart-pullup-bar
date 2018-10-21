package com.smartpullup.smartpullup;

import android.app.Activity;
import android.app.Dialog;
import android.content.SharedPreferences;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;

import static android.content.Context.MODE_PRIVATE;

public class DialogGoalActivity extends Dialog implements
        android.view.View.OnClickListener {

    String TAG = "DialogGoalActivity";

    private Activity c;
    private Dialog d;
    private Button goBtnn, cancelButton;
    private EditText goalPullUp_edit;
    public String goal;

    String input;

    public static final String PREFS_GOAL_EXERCISE = "inputGoal";

    public DialogGoalActivity(Activity a){
        super(a);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_dialog_goal);

        goBtnn = (Button) findViewById(R.id.btn_go);
        cancelButton = (Button) findViewById(R.id.btn_cancel);
        goalPullUp_edit = (EditText) findViewById(R.id.edit_goalPullUps);

        cancelButton.setOnClickListener(this);

        goBtnn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                input = goalPullUp_edit.getText().toString();
                Log.i(TAG, input);

                SharedPreferences.Editor editor = getContext().getSharedPreferences(PREFS_GOAL_EXERCISE, MODE_PRIVATE).edit();
                editor.putString("goal", input);
                editor.clear();
                editor.commit();

                dismiss();

            }
        });



    }

    @Override
    public void onClick(View v) {
        dismiss();
    }
}
