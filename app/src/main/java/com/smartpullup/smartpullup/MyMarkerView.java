package com.smartpullup.smartpullup;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.widget.TextView;

import com.github.mikephil.charting.components.MarkerView;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.utils.MPPointF;

import java.text.SimpleDateFormat;

/**
 * Created by Jorren on 21/04/2018.
 */

public class MyMarkerView extends MarkerView {

    private TextView txt_date;
    private TextView txt_time;
    private TextView txt_pullup;
    private SimpleDateFormat sdf;

    public MyMarkerView(Context context, int layoutResource) {
        super(context,  layoutResource);
        sdf = new SimpleDateFormat("dd/MM HH:mm");

        txt_date = findViewById(R.id.txt_date);
        txt_time = findViewById(R.id.txt_time);
        txt_pullup = findViewById(R.id.txt_pullup);
    }

    @Override
    public void refreshContent(Entry e, Highlight highlight) {
        super.refreshContent(e, highlight);
        txt_date.setText((sdf.format(e.getX())));
        if(txt_time != null)
            txt_time.setText(converTime(e.getY()));
        else if(txt_pullup != null)
            txt_pullup.setText((int)e.getY() + " pullups");

    }

    private String converTime(float time){
        if(time < 60)
            return Float.toString(time) + "s";
        else
            return Integer.toString((int)time / 60) + "m" + converTime((int)time % 60);
    }

    @Override
    public MPPointF getOffsetForDrawingAtPoint(float posX, float posY) {
        return new MPPointF(-(getWidth() / 2), -getHeight());
    }
}
