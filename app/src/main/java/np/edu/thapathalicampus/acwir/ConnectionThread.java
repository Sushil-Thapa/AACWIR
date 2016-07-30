package np.edu.thapathalicampus.acwir;

/**
 * Created by ThapaKAZZI on 11/4/2015.
 */
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android.bluetooth.BluetoothSocket;
import android.os.Handler;

public class ConnectionThread extends Thread {
    BluetoothSocket mBluetoothSocket;
    private final Handler mHandler;
    private InputStream mInStream;
    private OutputStream mOutStream;

    ConnectionThread(BluetoothSocket socket, Handler handler){
        super();
        mBluetoothSocket = socket;
        mHandler = handler;

        try {
            mInStream = mBluetoothSocket.getInputStream();
            mOutStream = mBluetoothSocket.getOutputStream();
        } catch (IOException e) {
        }
    }

    @Override
    public void run() {
        byte[] buffer = new byte[1024];
        int bytes;
        while (true) {
            try {
                bytes = mInStream.read(buffer);
                String data = new String(buffer, 0, bytes);
                mHandler.obtainMessage(MainActivity.DATA_RECEIVED,data).sendToTarget();
            } catch (IOException e) {
                MainActivity.CONNECTIONSTATUS=2;
                break;
            }
        }
    }

    public void write(byte[] bytes) {
        try {
            if(MainActivity.CONNECTIONSTATUS!=1){
                MainActivity.tv.setText("**ERROR : DISCONNECTED**");
            }
            mOutStream.write(bytes);
        } catch (IOException e) {
            MainActivity.CONNECTIONSTATUS=2;
        }
    }
    public void cancel() {
        try {
            mBluetoothSocket.close();
        } catch (IOException e) {
            MainActivity.CONNECTIONSTATUS=2;
        }
    }
}
