package com.smartpullup.smartpullup;

import android.app.IntentService;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;


public class BTReceiverService extends IntentService {

    public static final String ACTION_MyIntentService = "com.smartpullup.smartpullup.UPDATE";
    public static final String EXTRA_KEY_OUT = "EXTRA_OUT";

    //private static final UUID BTMODULEUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    private static final String TAG = "DataTransmissionService";

    Handler bluetoothIn;
    final int handlerState = 0;         //used to identify handler message

    public BluetoothAdapter btAdapter = null;
    public BluetoothSocket btSocket = null;
    public InputStream inputStream = null;
    public BluetoothDevice device = null;
    private StringBuilder recDataString = new StringBuilder();

    static BTReceiverService self;

    private static String address;      // String for MAC address
    private String inputBTString = null;


    JSONObject jsonObj = null;
    String jsonData = null;

    public BTReceiverService() {

        super("com.smartpullup.smartpullup.BTReceiverService");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        HandleInputData();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Toast.makeText(this, "BT service started", Toast.LENGTH_SHORT).show();
        return super.onStartCommand(intent,flags,startId);
    }

    @Override
    protected void onHandleIntent(Intent intent) {


        if(intent != null){
            btAdapter = BluetoothAdapter.getDefaultAdapter();
            address = intent.getStringExtra("address");

            try {
                Log.d(TAG, address);
                device = btAdapter.getRemoteDevice(address);
                Log.d(TAG, "Device bond state : " + device.getBondState());

            } catch (Exception e) {
                Log.e(TAG, "Invalid address: " + e.getMessage());
                return;
            }

            try {
                btSocket = createBluetoothSocket(device);
            } catch (IOException e) {
                Log.e(TAG, "Socket creation failed: " + e.getMessage());
                return;
            }

            try {

                if (!btSocket.isConnected()) {
                    btSocket.connect();
                    Log.d(TAG, "Connected");
                } else {
                    Log.d(TAG, "Already Connected");  //flow never reaches here for any use case
                }

            } catch (IOException e) {
                Log.e(TAG, "btSocket.connect() failed : " + e.getMessage());
                return;
            }

            try {
                inputStream = btSocket.getInputStream();

            } catch (IOException e) {
                Log.e(TAG, "Failed to get input stream:" + e.getMessage());
                return;
            }

            Receiver();



        }
    }

    private BluetoothSocket createBluetoothSocket(BluetoothDevice bluetoothDevice) throws IOException {

        try {
            Method m = bluetoothDevice.getClass().getMethod(
                    "createRfcommSocket", new Class[] { int.class });
            btSocket = (BluetoothSocket) m.invoke(bluetoothDevice, 1);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return btSocket;
    }

    private void Receiver() {

        byte[] buffer = new byte[256];  // buffer store for the stream
        int bytes; // bytes returned from read()

        while (true) {
            try {
                bytes = inputStream.read(buffer);            //read bytes from input buffer
                inputBTString = new String(buffer, 0, bytes);
                bluetoothIn.obtainMessage(handlerState, bytes, -1, inputBTString).sendToTarget();

            } catch (IOException e) {
                Log.e(TAG, "failed to reade " + inputBTString);
                break;
            }
        }
    }

    private void HandleInputData(){
        bluetoothIn = new Handler() {
            public void handleMessage(android.os.Message msg) {
                if (msg.what == handlerState) {              //if string is what we want
                    String readMessage = (String)msg.obj;                                 // msg.arg1 = bytes from connect thread
                    recDataString.append(readMessage);                                      //keep appending to string until }
                    int endOfLineIndex = recDataString.indexOf("}")+1;                    // determine the end-of-line and add the last }
                    if (endOfLineIndex > 0) {                                           // make sure there data before }
                        String dataInPrint = recDataString.substring(0, endOfLineIndex);    // extract string
                        int dataLength = dataInPrint.length();                          //get length of data received
                        //Log.i(TAG, "String Length = " + String.valueOf(dataLength));

                        if (recDataString.charAt(0) == '{')                             //if it starts with { we know it is what we are looking for
                        {
                            jsonData = dataInPrint;             //get sensor value from string
                            SendJsonStringBroadcast(jsonData);

                        }
                        recDataString.delete(0, recDataString.length());                   //clear all string data

                        dataInPrint = " ";
                    }
                }
            }
        };
    }

    private void SendJsonStringBroadcast(String input){
        Intent intentResponse = new Intent();
        intentResponse.setAction(ACTION_MyIntentService);
        intentResponse.addCategory(Intent.CATEGORY_DEFAULT);
        intentResponse.putExtra(EXTRA_KEY_OUT, input);
        sendBroadcast(intentResponse);
    }

    @Override
    public void onStart(@Nullable Intent intent, int startId) {
        super.onStart(intent, startId);
        self = this;
    }

    static public BTReceiverService getBTObject(){

        return self;
    }



}