package np.edu.thapathalicampus.acwir;

/**
 * Created by ThapaKAZZI on 10/29/2015.
 */import java.io.IOException;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.Handler;

public class ConnectThread extends Thread {
    private BluetoothSocket mBluetoothSocket;
    private final BluetoothDevice mDevice;
    private final BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    private final Handler mHandler;

    public ConnectThread(String deviceID, Handler handler) {
        mDevice = mBluetoothAdapter.getRemoteDevice(deviceID);
        mHandler = handler;
        try {
            mBluetoothSocket = mDevice.createRfcommSocketToServiceRecord(MainActivity.APP_UUID);
        } catch (IOException e) {
            try {
                mBluetoothSocket = mDevice.createRfcommSocketToServiceRecord(MainActivity.APP_UUIDmodule);
            } catch (Exception ex) {
                e.printStackTrace();
            }
            e.printStackTrace();
        }
    }

    public void run() {
        mBluetoothAdapter.cancelDiscovery();
        try {
            if (mBluetoothSocket == null)
            // This is a blocking call and will only return on a
            // successful connection or an exception
            MainActivity.tv.setText("Connecting...");
            mBluetoothSocket.connect(); //prob here of HC-05
            manageConnectedSocket();//instead of creating new thread

        } catch (IOException connectException) {
            try {
                mBluetoothSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void manageConnectedSocket() {
        ConnectionThread conn = new ConnectionThread(mBluetoothSocket, mHandler);
        mHandler.obtainMessage(MainActivity.SOCKET_CONNECTED, conn).sendToTarget();
        conn.start();
    };
    public void cancel() {
        try {
            mBluetoothSocket.close();
        } catch (IOException e) {
        }
    }

}
