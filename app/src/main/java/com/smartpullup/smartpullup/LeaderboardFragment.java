package com.smartpullup.smartpullup;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;


/**
 * Created by bjorn on 1/03/2018.
 */

public class LeaderboardFragment extends Fragment {
    private static final String TAG = "FragmentLeaderboard";

    private MainActivity host;
    private FirebaseDatabase database;
    private DatabaseReference databaseReference;

    private List<Entry> pullups;
    private List<Entry> maxSpeeds;
    private List<Entry> avgSpeeds;
    private List<Entry> exerciseLengths;
    //private List<Exercise> exercises; !!LATEN STAAN, IS VOOR DUMMY DATA TOE TE VOEGEN AAN FIREBASE

    LineChart lineTotalPullups;
    LineChart lineMaxSpeed;
    LineChart lineAvgSpeed;
    LineChart lineExerciseLength;
    List<LineChart> timeLineCharts;
    IAxisValueFormatter xAxisValueFormatter;
    IAxisValueFormatter yAxisValueFormatter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_leaderboard, container, false);

        final SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM");
        final SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");
        pullups = new ArrayList<>();
        maxSpeeds = new ArrayList<>();
        avgSpeeds = new ArrayList<>();
        exerciseLengths = new ArrayList<>();
        //exercises = new ArrayList<>();

        timeLineCharts = new ArrayList<>();
        lineTotalPullups = (LineChart)view.findViewById(R.id.lineTotalPullups);
        lineMaxSpeed = (LineChart)view.findViewById(R.id.lineMaxSpeed);
        lineAvgSpeed = (LineChart)view.findViewById(R.id.lineAvgSpeed);
        lineExerciseLength = (LineChart)view.findViewById(R.id.lineExerciseLength);
        timeLineCharts.add(lineMaxSpeed);
        timeLineCharts.add(lineAvgSpeed);
        timeLineCharts.add(lineExerciseLength);

        database = FirebaseDatabase.getInstance();
        databaseReference = database.getReference("Users/"+host.currentUser.getId()+"/exercises");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.getValue() != null){
                    for(DataSnapshot d : dataSnapshot.getChildren()){
                        pullups.add(new Entry(d.getValue(Exercise.class).getDate().getTime(), d.getValue(Exercise.class).getTotalPullups()));
                        maxSpeeds.add(new Entry(d.getValue(Exercise.class).getDate().getTime(), ((float) d.getValue(Exercise.class).getMaxSpeed())));
                        avgSpeeds.add(new Entry(d.getValue(Exercise.class).getDate().getTime(), ((float) d.getValue(Exercise.class).getAvgSpeed())));
                        exerciseLengths.add(new Entry(d.getValue(Exercise.class).getDate().getTime(), ((float) d.getValue(Exercise.class).getTotalTime())));
                    }
                    setLineChart(pullups, lineTotalPullups);
                    setLineChart(maxSpeeds, lineMaxSpeed);
                    setLineChart(avgSpeeds, lineAvgSpeed);
                    setLineChart(exerciseLengths, lineExerciseLength);
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
/*
!!LATEN STAAN, IS VOOR DUMMY DATA TOE TE VOEGEN AAN FIREBASE
        Calendar c = Calendar.getInstance();
        c.set(2018, Calendar.APRIL, 4, 12, 23);
        exercises.add(new Exercise(c.getTime(), 10.2, 14.2, 102.0, 10));
        c.set(2018, Calendar.APRIL, 5, 15, 50);
        exercises.add(new Exercise(c.getTime(), 9.8, 13.5, 110.0, 11));
        c.set(2018, Calendar.APRIL, 6, 10, 1);
        exercises.add(new Exercise(c.getTime(), 11, 14.5, 130.0, 12));
        c.set(2018, Calendar.APRIL, 7, 16, 36);
        exercises.add(new Exercise(c.getTime(), 9.5, 13.3, 126.0, 12));
        c.set(2018, Calendar.APRIL, 8, 12, 23);
        exercises.add(new Exercise(c.getTime(), 9.7, 15.3, 142.0, 13));


        DatabaseReference databaseReference2 = database.getReference("Users/" + host.currentUser.getId());
        databaseReference2.child("exercises").setValue(exercises);
        Log.i(TAG, "onCreateView: added demo exercises to db");
*/        //exercises = host.currentUser.getExercises();
/*        Collections.sort(exercises, new Comparator<Exercise>() {
            @Override
            public int compare(Exercise e1, Exercise e2) {
                if(e1.getDate() == null || e2.getDate() == null)
                    return 0;
                return e1.getDate().compareTo(e2.getDate());
            }
        });
*/

        xAxisValueFormatter = new IAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                return dateFormat.format(new Date((long)value)) + "\n" + timeFormat.format(new java.util.Date((long)value));
            }
        };
        yAxisValueFormatter = new IAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                return converTime(value);
            }

            private String converTime(float time){
                if(time < 60)
                    return Float.toString(time) + "s";
                else
                    return Integer.toString((int)time / 60) + "m" + converTime(time % 60);
            }
        };
        
        for(LineChart lc : timeLineCharts){
            createLineChart(lc);
            MyMarkerView mv = new MyMarkerView(getContext(), R.layout.markerview_time);
            lc.setMarker(mv);
            lc.getAxisLeft().setValueFormatter(yAxisValueFormatter);
        }
        createLineChart(lineTotalPullups);
        MyMarkerView mv = new MyMarkerView(getContext(), R.layout.markerview_pullup);
        lineTotalPullups.setMarker(mv);
        return view;
    }

    private float millisecondsInADay() {
        Calendar c = Calendar.getInstance();
        c.set(2018, Calendar.APRIL, 4, 0, 0);
        long begin = c.getTime().getTime();
        c.set(2018, Calendar.APRIL, 5, 0, 0);
        long end = c.getTime().getTime();
        return end - begin;
    }

    private void setLineChart(List<Entry> entries, LineChart chart) {
        LineDataSet lineDataSet = new LineDataSet(entries,"line");
        lineDataSet.setDrawValues(false);
        lineDataSet.setDrawHighlightIndicators(false);
        lineDataSet.setLineWidth(3f);
        lineDataSet.setColor(getResources().getColor(R.color.Red));
        lineDataSet.setCircleColor(getResources().getColor(R.color.Red));
        chart.setData(new LineData(lineDataSet));
        calculateMinMaxYAxis(chart, chart.getAxisLeft().getLabelCount());
        calculateMinMaxXAxis(chart);
        chart.notifyDataSetChanged();
        chart.invalidate();
    }

    private void createLineChart(LineChart lc){
        XAxis xAxis = lc.getXAxis();
        xAxis.setValueFormatter(xAxisValueFormatter);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setGranularity(millisecondsInADay());
        xAxis.setGranularityEnabled(true);
        xAxis.setLabelCount(4, true);
        YAxis yAxis = lc.getAxisLeft();
        yAxis.setGranularity(1.0f);
        yAxis.setGranularityEnabled(true);
        yAxis.setAxisMinimum(0f);
        lc.getAxisRight().setDrawGridLines(false);
        lc.getAxisRight().setDrawAxisLine(false);
        lc.getAxisRight().setDrawLabels(false);
        lc.setXAxisRenderer(new MyXAxisRenderer(lc.getViewPortHandler(), xAxis, lc.getTransformer(YAxis.AxisDependency.LEFT)));
        lc.getLegend().setEnabled(false);
        lc.getDescription().setText("");
        lc.setNoDataText("No data found");
        lc.setExtraOffsets(5f,35f,10f,20f);
    }

    private void calculateMinMaxXAxis(LineChart chart) {
        Calendar c = Calendar.getInstance();
        float maxValue = chart.getData().getXMax();
        c.setTimeInMillis((long)maxValue);
        c.set(Calendar.DATE, c.get(Calendar.DATE) + 1);
        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        chart.getXAxis().setAxisMaximum(c.getTimeInMillis());

        float minValue = chart.getData().getXMin();
        c.setTimeInMillis((long)minValue);
        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        chart.getXAxis().setAxisMinimum(c.getTimeInMillis());
    }

    private void calculateMinMaxYAxis(LineChart chart, int labelCount) {
        //found on https://stackoverflow.com/a/41788628
        float maxValue = chart.getData().getYMax();
        float minValue = chart.getData().getYMin();

        if ((maxValue - minValue) < labelCount) {
            float diff = labelCount - (maxValue - minValue);
            maxValue += diff;
            minValue -= diff;
            chart.getAxisLeft().setAxisMaximum(maxValue);
            if(minValue > 0)
                chart.getAxisLeft().setAxisMinimum(minValue);
            else
                chart.getAxisLeft().setAxisMinimum(0f);
        }
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
