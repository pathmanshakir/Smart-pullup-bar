package com.smartpullup.smartpullup;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;


public class WorkoutFragment extends Fragment {

    View view;

    ListView workout_listview;
    WorkoutListAdapter list_adapter;

    String[] workout_titel;
    String[] workout_content;


    public static int [] workout_images ={
            R.drawable.workout_img_1,
            R.drawable.workout_img_2,
            R.drawable.workout_img_3,
            R.drawable.workout_img_4,
            R.drawable.workout_img_5,
            R.drawable.workout_img_6,
            R.drawable.workout_img_7,
            R.drawable.workout_img_8,
            R.drawable.workout_img_9,
            R.drawable.workout_img_10,
    };


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_workout, container, false);

        workout_titel = new String[] {

                getString(R.string.card_title_workout_1),
                getString(R.string.card_title_workout_2),
                getString(R.string.card_title_workout_3),
                getString(R.string.card_title_workout_4),
                getString(R.string.card_title_workout_5),
                getString(R.string.card_title_workout_6),
                getString(R.string.card_title_workout_7),
                getString(R.string.card_title_workout_8),
                getString(R.string.card_title_workout_9),
                getString(R.string.card_title_workout_10),

        };

        workout_content = new  String[] {
                getString(R.string.card_content_workout_1),
                getString(R.string.card_content_workout_2),
                getString(R.string.card_content_workout_3),
                getString(R.string.card_content_workout_4),
                getString(R.string.card_content_workout_5),
                getString(R.string.card_content_workout_6),
                getString(R.string.card_content_workout_7),
                getString(R.string.card_content_workout_8),
                getString(R.string.card_content_workout_9),
                getString(R.string.card_content_workout_10),
        };

        init();
        workout_listview.setAdapter(list_adapter);

        return view;
    }

    private void init() {

        list_adapter = new WorkoutListAdapter(this, workout_titel,workout_content, workout_images);
        workout_listview = (ListView) view.findViewById(R.id.workout_listView);
    }

}



