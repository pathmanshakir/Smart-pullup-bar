package com.smartpullup.smartpullup;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by moham on 18/03/2018.
 */

public class JSONBroadcastReceiver extends BroadcastReceiver {

    private static final String TAG = "DataTransmissionService";

    private String JSONStructureInput = "";
    JSONObject JSONInputData = null;

    String typeJsonData;
    int upJsonData;
    int startJsonData;
    double weightJsonData;

    double actualWeight;

    public static final String MY_PREFS_NAME = "DataFromPullUpBar";

    @Override
    public void onReceive(Context context, Intent intent) {
        JSONStructureInput = intent.getStringExtra(BTReceiverService.EXTRA_KEY_OUT);

        Log.i(TAG, "JSONStructureInput= " + JSONStructureInput);

        //Making JSON obj from input string
        try {
            JSONInputData = new JSONObject(JSONStructureInput);

            typeJsonData = JSONInputData.getString("Type");

            if (typeJsonData.equals("Initial")){
                weightJsonData = JSONInputData.getDouble("Weight");
                actualWeight = weightJsonData;
                upJsonData = 0;
                startJsonData = 0;
            }else{
                weightJsonData = weightJsonData;
                upJsonData = JSONInputData.getInt("Up");
                startJsonData = JSONInputData.getInt("Start");
            }


            Log.i(TAG, "type= " + typeJsonData);
            Log.i(TAG, "weight= " + weightJsonData);
            Log.i(TAG, "up= " + upJsonData);
            Log.i(TAG, "down= " + startJsonData);


            SharedPreferences.Editor editor = context.getSharedPreferences(MY_PREFS_NAME, context.MODE_PRIVATE).edit();
            editor.putString("type", typeJsonData);
            //editor.putInt("machine_ID", machine_ID_JsonData);
            editor.putInt("up", upJsonData);
            editor.putInt("down", startJsonData);
            editor.putInt("weight", (int) actualWeight);

            editor.clear();
            editor.commit();


        } catch (JSONException e) {
            e.printStackTrace();
        }


    }


}
