package com.smartpullup.smartpullup;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

import pl.pawelkleczkowski.customgauge.CustomGauge;

/**
 * Created by Jorren on 22/02/2018.
 */

public class ExerciseFragment extends Fragment {
    private static final String TAG = "FragmentExcercise";
    double elapsedMillis;
    private FirebaseDatabase database;
    private DatabaseReference databaseReference;

    private MainActivity host;
    private Chronometer chrono;
    private  boolean running;

    private static final String MY_PREFS_NAME = "DataFromPullUpBar";
    private SharedPreferences prefs;

    public static final String PREFS_GOAL_EXERCISE = "inputGoal";
    private SharedPreferences prefsGoalExercise;
    private SharedPreferences.OnSharedPreferenceChangeListener goalListener;

    private TextView counterUpTextView;
    private TextView weightTextView;
    private TextView type_TextView;

    private TextView txt_PullupSpeed;
    private TextView txt_PullupAverageSpeed;

    private Button startExercise_Button;
    private Button stopExercise_Button;

    ProgressBar pbCounterUp;
    //ProgressBar pbCounterDown;

    private double pullupSpeed;

    private List<Double> pullupSpeeds;

    private View view;

    private String typeInput;
    private double upInput;
    private double startInput;
    private double weightInput;

    private int counterUp = 0;
    private double previousValueUp;
    private double startTime;

    String inputGoalExercises;
    int goalExercises;

    Boolean isStarting = false;

    private int i=-1, second = 3;

    MediaPlayer beepSound;
    MediaPlayer startSound;
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        prefs = getContext().getSharedPreferences(MY_PREFS_NAME, Context.MODE_MULTI_PROCESS);
        prefsGoalExercise = getContext().getSharedPreferences(PREFS_GOAL_EXERCISE, Context.MODE_PRIVATE);

        database = FirebaseDatabase.getInstance();
        databaseReference = database.getReference();

        pullupSpeeds = new ArrayList<>();
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_exercise, container, false);
        txt_PullupSpeed = (TextView)view.findViewById(R.id.txt_PullupSpeed);
        txt_PullupAverageSpeed = (TextView)view.findViewById(R.id.txt_PullupAverageSpeed);
        startExercise_Button = (Button) view.findViewById(R.id.startExercise_Button);
        stopExercise_Button = (Button) view.findViewById(R.id.stopExercise_Button);
        chrono= (Chronometer) view.findViewById(R.id.chronoTimer);
        counterUpTextView = (TextView) view.findViewById(R.id.pullUpCounter_textView);
        weightTextView = (TextView) view.findViewById(R.id.weight_textView);
        type_TextView = (TextView) view.findViewById(R.id.TypeMesurament_textView);

        pbCounterUp = (ProgressBar) view.findViewById(R.id.progress_pullups);
        //pbCounterDown = (ProgressBar) view.findViewById(R.id.progress_calories);

        MeasurementOfExercise();

        startExercise_Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogGoalActivity dg = new DialogGoalActivity(getActivity());
                dg.show();
            }
        });

        stopExercise_Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                resetValues();
                counterUpTextView.setText("0");
                pbCounterUp.setProgress(0);
                isStarting = false;
                startExercise_Button.setVisibility(view.VISIBLE);
                stopExercise_Button.setVisibility(view.GONE);
                updateUI();
                //PushExercise();
            }
        });
        StartExercise();

        return view;
    }

    private void MeasurementOfExercise(){
        prefs.registerOnSharedPreferenceChangeListener(
                new SharedPreferences.OnSharedPreferenceChangeListener() {
                    public void onSharedPreferenceChanged(SharedPreferences prefs, String key) {

                        InputData(prefs);
                        if(isStarting) {
                            CounterUp();

                            startExercise_Button.setVisibility(view.GONE);
                            stopExercise_Button.setVisibility(view.VISIBLE);

                            if (pbCounterUp.getProgress() >= goalExercises) {
                                elapsedMillis = SystemClock.elapsedRealtime() - chrono.getBase();
                                PushExercise();
                                resetValues();
                                isStarting = false;
                            }
                        }else {
                            startExercise_Button.setVisibility(view.VISIBLE);
                            stopExercise_Button.setVisibility(view.GONE);
                            resetValues();
                        }
                        updateUI();
                    }
                });
    }


    private void StartExercise(){
        goalListener = new SharedPreferences.OnSharedPreferenceChangeListener() {
            public void onSharedPreferenceChanged(SharedPreferences prefs, String key) {
                inputGoalExercises = prefsGoalExercise.getString("goal", "");
                if(!inputGoalExercises.equals("")){
                    goalExercises = Integer.parseInt(inputGoalExercises);
                    pbCounterUp.setMax(goalExercises+goalExercises/4);
                    pbCounterUp.invalidate();
                    startSound =MediaPlayer.create(getActivity(),R.raw.startsound);
                    startSound.start();
                    CountDown();

                    //chronometer start


                }
                else
                    Toast.makeText(getContext(), "Please enter a valid number", Toast.LENGTH_LONG).show();

            }
        };
        prefsGoalExercise.registerOnSharedPreferenceChangeListener(goalListener);


    }

    private void CountDown(){
        new Thread( new Runnable() {
            public void run() {
                while( i != second ) {
                    try {
                        handle.sendMessage( handle.obtainMessage());
                        Thread.sleep(1000);
                    } catch( Throwable t) {
                        t.printStackTrace();
                    }
                }
            }

            Handler handle = new Handler() {
                public void handleMessage( Message msg) {
                    counterUpTextView.setText(String.valueOf(second));
                    second--;
                    if (second == -1){
                        isStarting = true;
                        Log.i(TAG, isStarting.toString());
                        counterUpTextView.setText("START");
                        startTime = System.currentTimeMillis();
                        if(!running) {
                            chrono.setBase(SystemClock.elapsedRealtime());
                            chrono.start();
                            running=true;

                        }
                    }
                }
            };
        }).start();
        second = 3;
    }


    @Override
    public void onResume() {
        super.onResume();
        MeasurementOfExercise();
    }

    private void resetValues(){
        counterUp = 0;
        upInput = 0;
        previousValueUp = 0;
        chrono.stop();
        running=false;
        startTime = System.currentTimeMillis();
        pullupSpeeds = new ArrayList<>();
        pullupSpeed = 0;
        startExercise_Button.setTextColor(Color.WHITE);
        startExercise_Button.setText("START");
    }

    private void CounterUp(){
        if(upInput != previousValueUp)
        {
            Log.i(TAG, "CounterUp: " + counterUp);
            counterUp++;
           beepSound =MediaPlayer.create(getActivity(),R.raw.beep);
           beepSound.start();
            calculateSpeed();
            previousValueUp = upInput;
        }else if (upInput == 0){
            counterUp = 0;
        }
    }

    public void setPullupSpeed(double pullupSpeed) {
        this.pullupSpeed = pullupSpeed;
        pullupSpeeds.add(pullupSpeed);
    }

    private void InputData(SharedPreferences prefs) {
        typeInput = prefs.getString("type", "");
        upInput = prefs.getInt("up", 0);
        startInput = prefs.getInt("down", 0);
        Log.i(TAG, String.valueOf("Up: " + upInput + "Down: " + startInput));
        weightInput = prefs.getInt("weight", 0);
    }

    private void updateUI() {
        txt_PullupSpeed.setText("Speed: " + String.format("%.2f", pullupSpeed) + " s");
        txt_PullupAverageSpeed.setText("Average Speed: " + String.format("%.2f", calculateAverage()) + " s");
        counterUpTextView.setText(Integer.toString(counterUp));
        weightTextView.setText(Double.toString(weightInput) + " kg");
        type_TextView.setText(typeInput);
        pbCounterUp.setProgress(counterUp);
        Log.i(TAG, "updateUI: ");
    }

    private double calculateAverage() {
        if(pullupSpeeds.size() == 0)
            return 0;
        double sum = 0;
        for (double i:pullupSpeeds) {
            sum += i;
        }
        return sum / pullupSpeeds.size();
    }

    private void calculateSpeed() {
        double speed = upInput - startInput;
        if(speed > 0)
            setPullupSpeed(speed);
    }

    private void PushExercise(){
        double maxSpeed = 100;
        for (double speed : pullupSpeeds){
            if(speed < maxSpeed)
                maxSpeed = speed;
        }

        Exercise e = new Exercise(maxSpeed, calculateAverage(), elapsedMillis/1000, counterUp);
        host.currentUser.getExercises().add(e);
        databaseReference.child("Users").child(host.currentUser.getId()).child("exercises").setValue(host.currentUser.getExercises());
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try{
            host = (MainActivity) context;
        }catch (ClassCastException e){
            throw new ClassCastException(context.toString() + "is not MainActivity");
        }
    }
}
