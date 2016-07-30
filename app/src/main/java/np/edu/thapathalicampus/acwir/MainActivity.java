package np.edu.thapathalicampus.acwir;

import android.app.AlertDialog;
import android.bluetooth.BluetoothSocket;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.widget.ToggleButton;
import android.app.Activity;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;

import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

import android.os.Bundle;
import android.util.Log;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;
import android.widget.CompoundButton;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Set;
import java.util.UUID;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Set;

import static android.util.Log.w;

public class MainActivity extends Activity implements SensorEventListener{
    private static String TAG = "MainActivity:Bluetooth";

    private static final int REQUEST_ENABLE_BT = 0;
    private static final int SELECT_SERVER = 1;
    public static final int DATA_RECEIVED = 3;
    public static final int SOCKET_CONNECTED = 4;

    public static int CONNECTIONSTATUS=0;

    public static final UUID APP_UUID = UUID.fromString("aeb9f938-a1a3-4947-ace2-9ebd0c67adf1"); //AnotherAPP
    public static final UUID APP_UUIDmodule = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");//module

    private Button serverButton, clientButton,aboutButton;
    public static TextView tv = null;

    private String data;

    private BluetoothAdapter mBluetoothAdapter = null;
    private ConnectionThread mBluetoothConnection = null;
    private AcceptThread mAcceptThread;
    private ConnectThread mConnectThread;


    private boolean mServerMode,mClientMode;

    private BluetoothDevice zDevice;

    String stringForHandler="Bluetooth: Connected";

    SensorManager sm;
    TextView xText,yText,zText,directionText;
    ToggleButton toggleBtn,imageToggle;
    String strx,stry,strz,strng;
    private float x,y;
    float temp;
    String  oldData="z",newData;
    int sendCommand=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        checkFirstRun();


        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter == null) {
            Log.i(TAG, "Bluetooth not supported");
            finish();
        }
        if (!mBluetoothAdapter.isEnabled()) {
            Intent enableBluetoothIntent = new Intent(
                    BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBluetoothIntent, REQUEST_ENABLE_BT);
        } else {
            //  setButtonsEnabled(true);
        }

        tv = (TextView) findViewById(R.id.text_window);
        serverButton = (Button) findViewById(R.id.server_button);
        serverButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!mBluetoothAdapter.isEnabled()) {
                    Intent enableBluetoothIntent = new Intent(
                            BluetoothAdapter.ACTION_REQUEST_ENABLE);
                    startActivityForResult(enableBluetoothIntent, REQUEST_ENABLE_BT);
                } else {
                    //  setButtonsEnabled(true);
                }
                serverButton.setText("SERVER MODE ON");
                startAsServer();
                mServerMode = true;
            }
        });
/*resetBtn= (Button)findViewById(R.id.iresetBtn);
        resetBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent enableBluetoothIntent = new Intent(
                        BluetoothAdapter.);

                startActivityForResult(enableBluetoothIntent, REQUEST_ENABLE_BT);//disable bluetooth
            }
        });*/
        clientButton = (Button) findViewById(R.id.client_button);
        clientButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mClientMode=true;

                clientButton.setText("CLIENT MODE ON");
                selectServer();
            }
        });



        aboutButton = (Button) findViewById(R.id.iAbout);
        aboutButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this,"Developers Revealed..",Toast.LENGTH_SHORT).show();
                startActivity(new Intent(MainActivity.this, About.class));
            }
        });

        toggleBtn=(ToggleButton)findViewById(R.id.iToggleBtn);
        directionText=(TextView)findViewById(R.id.iDirectionText);

        /*textUsart = (TextView)findViewById(R.id.itextUsart);
        toggleUsart = (ToggleButton)findViewById(R.id.iToggleUsart);
        toggleUsart.setBackgroundColor(Color.RED);*/
        imageToggle=(ToggleButton)findViewById(R.id.iToggleImage);

        xText = (TextView)findViewById(R.id.iXText);
        yText= (TextView)findViewById(R.id.iYText);
        zText= (TextView)findViewById(R.id.iZText);

        sm = (SensorManager) getSystemService(SENSOR_SERVICE);

        toggleBtn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (buttonView.isChecked()) {
                    /*mClientMode=true;
                    if (!mBluetoothAdapter.isEnabled()) {
                        Intent enableBluetoothIntent = new Intent(
                                BluetoothAdapter.ACTION_REQUEST_ENABLE);
                        startActivityForResult(enableBluetoothIntent, REQUEST_ENABLE_BT);  //CHECKLIST,CHeCK IF THREADS DONOT POSES PROBLEM//add condition if connected
                    } else {
                        //  setButtonsEnabled(true);
                    }
                    clientButton.setText("CLIENT MODE ON");
                    selectServer();*/
                    directionText.setText("Accelerometer : Enabled");

                    stringForHandler = "Accelerometer : Enabled";
                        toggleBtn.setBackgroundResource(R.drawable.acceooon);
                        sm = (SensorManager) getSystemService(SENSOR_SERVICE);
                        sm.registerListener(MainActivity.this, sm.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), sm.SENSOR_DELAY_NORMAL); //sm.SENSOR_DELAY_NORMAL for fast data exchange
                        imageToggle.setEnabled(true);//enabled ,not checked
                }
                else {
                    toggleBtn.setBackgroundResource(R.drawable.accelooof);
                    zText.setText("Z Axis : NULL");
                    xText.setText("X Axis : NULL");
                    yText.setText("Y Axis : NULL");
                    directionText.setText("Accelerometer : Disabled");
                    // toggleUsart.setEnabled(false);
                    imageToggle.setEnabled(false);
                    sm.unregisterListener(MainActivity.this);
                    stringForHandler="Accelerometer : Disabled";
                }
                if(mBluetoothAdapter.isEnabled() && CONNECTIONSTATUS==1) { //TODO checkif connect instead
                    mHandler.obtainMessage(MainActivity.SOCKET_CONNECTED, mBluetoothConnection).sendToTarget();
                }
            }
        });

        imageToggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {//NOT NEEDED now
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (buttonView.isChecked()) {
                    Toast.makeText(MainActivity.this,"Connect Bluetooth to send the Commmands",Toast.LENGTH_SHORT).show();
                    //now send directions
                    // TODOno need just send the direction
                    // Toast.makeText(MainActivity.this,"IF TRUE,Run this to RESUME",Toast.LENGTH_SHORT).show();
                    //   Toast.makeText(MainActivity.this,"Image Toggle setChecked",Toast.LENGTH_SHORT).show();
                    //send
                } else {

                    //  Toast.makeText(MainActivity.this, "IF UnTRUE,Run this to PAUSE", Toast.LENGTH_SHORT).show();
                    //   Toast.makeText(MainActivity.this,"Image Toggle NOT NOT NOT setChecked",Toast.LENGTH_SHORT).show();
                }
            }
        });

    } //END ONCREATE


    private void checkFirstRun() {

        final String PREFS_NAME = "MyPrefsFile";
        final String PREF_VERSION_CODE_KEY = "version_code";
        final int DOESNT_EXIST = -1;


        // Get current version code
        int currentVersionCode = 0;
        try {
            currentVersionCode = getPackageManager().getPackageInfo(getPackageName(), 0).versionCode;
        } catch (android.content.pm.PackageManager.NameNotFoundException e) {
            // handle exception
            e.printStackTrace();
            return;
        }

        // Get saved version code
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        int savedVersionCode = prefs.getInt(PREF_VERSION_CODE_KEY, DOESNT_EXIST);

        // Check for first run or upgrade
        if (currentVersionCode == savedVersionCode) {
            Toast.makeText(MainActivity.this,"Thankyou for Returning",Toast.LENGTH_SHORT).show();
            // This is just a normal run
            return;

        } else if (savedVersionCode == DOESNT_EXIST) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("Select a mode to Communicate...\n SERVER MODE = Receive Data \n CLIENT MODE = Send Commands")
                    .setCancelable(false)
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            //do things
                        }
                    });
            AlertDialog alert = builder.create();
            alert.setTitle("Welcome To My App Nerds..");
            alert.setIcon(R.mipmap.logo);
            alert.show();
            // TODO This is a new install (or the user cleared the shared preferences)

        } else if (currentVersionCode > savedVersionCode) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("1.Explore the Accelerometer without connection \n  2. Detect Bluetooth : Disconnected status \n 3. Send Stop command \"z\" only once")
                    .setCancelable(false)
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            //do things
                        }
                    });
            AlertDialog alert = builder.create();
            alert.setTitle("Whats New : Version 1.1");
            alert.setIcon(R.mipmap.logo);
            alert.show();
            // TODO This is an upgrade

        }

        // Update the shared preferences with the current version code
        prefs.edit().putInt(PREF_VERSION_CODE_KEY, currentVersionCode).commit();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_ENABLE_BT && resultCode == RESULT_OK) {
            setButtonsEnabled(true);
        }
        else if (requestCode == SELECT_SERVER
                && resultCode == RESULT_OK) {
            BluetoothDevice device = data.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
            connectToBluetoothServer(device.getAddress());
        }
       /* else if(requestCode==REQUEST_DISABLE_BT && resultCode== RESULT_OK){
            setButtonsEnabled(false);
        }*/
    }

    private void startAsServer() {
        setButtonsEnabled(false);
        if (mConnectThread != null) {mConnectThread.cancel(); mConnectThread = null;}

        // Cancel any thread currently running a connection
        if (mBluetoothConnection != null) {mBluetoothConnection.cancel(); mBluetoothConnection = null;}
        if (mAcceptThread == null) {
            mAcceptThread = new AcceptThread(mHandler);
            mAcceptThread.start();
        }
    }

    private void selectServer() {
        if (!mBluetoothAdapter.isEnabled()) {
            Intent enableBluetoothIntent = new Intent(
                    BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBluetoothIntent, REQUEST_ENABLE_BT);
        }
        setButtonsEnabled(false);
        Set<BluetoothDevice> pairedDevices = mBluetoothAdapter
                .getBondedDevices();
        ArrayList<String> pairedDeviceStrings = new ArrayList<String>();
        if (pairedDevices.size() > 0) {
            for (BluetoothDevice device : pairedDevices) {
                pairedDeviceStrings.add(device.getName() + "\n" + device.getAddress());
            }
        }
        Intent showDevicesIntent = new Intent(this, ShowDevices.class);
        showDevicesIntent.putStringArrayListExtra("devices", pairedDeviceStrings);
        startActivityForResult(showDevicesIntent, SELECT_SERVER);
    }

    private void connectToBluetoothServer(String id) {
        zDevice = mBluetoothAdapter.getRemoteDevice(id);
        tv.setText("Connecting to Server...:" + zDevice.getName());
        new ConnectThread(id, mHandler).start();
    }

    public Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case SOCKET_CONNECTED: {
                    CONNECTIONSTATUS=1;
                    tv.setText("Connected :"+ zDevice.getName());
                    mBluetoothConnection = (ConnectionThread) msg.obj;
                    if (!mServerMode) { //clientMode
                        mBluetoothConnection.write(stringForHandler.getBytes());
                    }
                    break;
                }
                case DATA_RECEIVED: {
                    data = (String) msg.obj;
                    tv.setText(data);
                    if (false)// mServerMode)
                        mBluetoothConnection.write(data.getBytes());
                }
                default:
                    break;
            }
        }
    };

    private void setButtonsEnabled(boolean state) {
        serverButton.setEnabled(state);
        clientButton.setEnabled(state);
    }
    @Override
    public void onSensorChanged(SensorEvent event) {
        if(event.sensor.getType()==Sensor.TYPE_ACCELEROMETER){
            float x= event.values[1];
            float y=event.values[0];
            float z= event.values[2];

            if(x<0) strx = "Forward"; //tala janxa ni ta
            else   strx = "Backward";



            if(y<0) stry = "Right";
            else   stry = "Left";

            if(z>0) strz = "Up";
            else strz = "Down";

            if(toggleBtn.isChecked()){
                xText.setText("Direction:"+strx+"! X axis Data:" + x);
                yText.setText("Direction:"+stry+"! Y axis Data:" + y);
                zText.setText("Direction:"+strz+"! Z axis Data:"+z);

                if(x<0) {                                   //front
                    if (y < 0) {                                               //both neg
                        if (y<x) {                                                           //right
                            strng="r";
                            temp=x-y;
                            if(temp>3){
                                strng="s";
                                if(temp>6)  strng="t";
                            }
                        }
                        if (y>x) {
                            strng="f";                                                        //forward
                            temp=y-x;
                            if(temp>3) {
                                strng="g";
                                if(temp>6)  strng="h";
                            }

                        }
                    }
                    else {                                                     //x neg only
                        if (y > Math.abs(x)) {                                                               //left
                            strng="l";
                            temp=y-x;
                            if(temp>3){
                                strng="m";
                                if(temp>6)  strng="n";
                            }

                        }
                        else {                                                                       //forward
                            strng="f";
                            temp=x-y;
                            if(temp>3) {
                                strng="g";
                                if(temp>6)  strng="h";
                            }

                        }
                    }
                }

                if(x>0){                                             //back
                    if (y < 0) {                                               //y neg only
                        y=Math.abs(y);
                        if (y>x) {                                                           //  right
                            strng="r";
                            temp=y-x;
                            if(temp>3) {
                                strng="s";
                                if(temp>6)  strng="t";
                            }
                        }
                        if(y<x) {                                                              //bckwards
                            strng="b";
                            temp=x-y;
                            if(temp>4)  {
                                strng="c";
                                if(temp>7)  strng="d";
                            }
                        }
                    }
                    else {                                                    // both positv
                        if (y > x) {
                            strng="l";                                                             //left
                            temp=y-x;
                            if(temp>3) {
                                strng = "m";
                                if (temp > 6) strng = "n";
                            }
                        }
                        else {                                                                      //backward
                            strng="b";
                            temp=x-y;
                            if(temp>5) {
                                strng="c";
                                if(temp>7)  strng="d";
                            }
                        }
                    }
                }
                if((x<1&&x>-1 && y<1&&y>-1) || (!imageToggle.isChecked()) ){
                    strng="z";
                }
                stringForHandler=strng;

                if ((!mBluetoothAdapter.isEnabled() || (CONNECTIONSTATUS!=1))) { //TODO check conneccted Instead
                        directionText.setText("Command Generated: " + strng);
                }
                else{

                  //  if(sendCommand==1 && (mServerMode || mClientMode)){
                    newData = strng;
                    if((strng.equals("z") || strng=="z")){ //DONE send z once only
                        if(newData!=oldData) {
                            mHandler.obtainMessage(MainActivity.SOCKET_CONNECTED, mBluetoothConnection).sendToTarget();
                            oldData=newData;
                       /* if(imageToggle.isChecked()) {
                            sendCommand = 1;
                        }*/
                        }
                    }
                    else {
                        mHandler.obtainMessage(MainActivity.SOCKET_CONNECTED, mBluetoothConnection).sendToTarget();
                        //      }
                    }
                    directionText.setText("Sending Data: " + strng);
                }
            }

        } //ends if accelerometer
    }//endon sensor changed
    @Override
    public void onDestroy(){
        super.onDestroy();
        if (mConnectThread != null) {mConnectThread.cancel(); mConnectThread = null;}
    if (mBluetoothConnection != null) {mBluetoothConnection.cancel(); mBluetoothConnection = null;}
    if (mAcceptThread != null) {mAcceptThread.cancel(); mAcceptThread = null;}
    }
    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }


}