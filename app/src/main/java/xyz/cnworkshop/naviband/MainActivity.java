package xyz.cnworkshop.naviband;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.maps2d.AMap;
import com.amap.api.maps2d.MapView;

import app.akexorcist.bluetotohspp.library.BluetoothSPP;
import app.akexorcist.bluetotohspp.library.BluetoothState;
import app.akexorcist.bluetotohspp.library.DeviceList;


public class MainActivity extends AppCompatActivity {
    TextView textView;
    private AMap aMap;
    private MapView mMapView = null;
    BluetoothSPP bt;
    Toast mToastNoBTH;
    Toast mToastBTHEnable;
    Toast mToastDeviceConnected;
    Toast mToastDeviceDisconnected;
    Button mBtnLeftV;
    Button mBtnRightV;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mToastNoBTH = Toast.makeText(this, "蓝牙不可用!", Toast.LENGTH_LONG);
        mToastBTHEnable = Toast.makeText(this, "蓝牙已激活", Toast.LENGTH_LONG);
        mToastDeviceConnected = Toast.makeText(this, "设备已连接", Toast.LENGTH_LONG);
        mToastDeviceDisconnected = Toast.makeText(this, "设备已断开", Toast.LENGTH_LONG);

        mBtnLeftV = findViewById(R.id.BtnLeftV);
        mBtnRightV= findViewById(R.id.BtnRightV);
        mMapView = findViewById(R.id.map);
        mMapView.onCreate(savedInstanceState);

        mBtnLeftV.setEnabled(false);
        mBtnRightV.setEnabled(false);

        if (aMap == null) aMap = mMapView.getMap();
        aMap.setMapLanguage(AMap.CHINESE);
        aMap.setMapType(AMap.MAP_TYPE_NORMAL);


        bt = new BluetoothSPP(getApplicationContext());
        if (!bt.isBluetoothAvailable()) {
            mToastNoBTH.show();
            bt.enable();
        } else {
            bt.setupService();
            bt.startService(BluetoothState.DEVICE_OTHER);
            mToastBTHEnable.show();
        }

        bt.setBluetoothConnectionListener(new BluetoothSPP.BluetoothConnectionListener() {
            public void onDeviceConnected(String name, String address) {
                mToastDeviceConnected.show();
                mBtnLeftV.setEnabled(true);
                mBtnRightV.setEnabled(true);
            }
            public void onDeviceDisconnected() {
                mToastDeviceDisconnected.show();
                mBtnLeftV.setEnabled(false);
                mBtnRightV.setEnabled(false);
            }
            public void onDeviceConnectionFailed() {
                mToastDeviceDisconnected.show();
                mBtnLeftV.setEnabled(false);
                mBtnRightV.setEnabled(false);
            }
        });


    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == BluetoothState.REQUEST_CONNECT_DEVICE) {
                Log.i("BT",Integer.toString(resultCode));
                Log.i("BT","OK Code"  + Activity.RESULT_OK);
            if (resultCode == Activity.RESULT_OK) {
                Log.i("BT", data.toString());
                bt.connect(data);
            }
        } else if (requestCode == BluetoothState.REQUEST_ENABLE_BT) {
            if (resultCode == Activity.RESULT_OK) {
                bt.setupService();
                bt.startService(BluetoothState.DEVICE_OTHER);
            } else {
                // Do something if user doesn't choose any device (Pressed back)
            }
        }
    }

    public void chooseDevice(View view) {
        Intent intent = new Intent(getApplicationContext(), DeviceList.class);
        startActivityForResult(intent, BluetoothState.REQUEST_CONNECT_DEVICE);
    }

    public void connectBTH(View view) {
        Intent intent = new Intent(this, NBConnectActivity.class);
        startActivity(intent);
    }
    public void leftVibrator(View view){
        bt.send("NB__head__\r\nL1NB__end__\r\n",true);
    }
    public void rightVibrator(View view){
        bt.send("NB__head__\r\nR1NB__end__\r\n",true);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mMapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mMapView.onPause();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mMapView.onSaveInstanceState(outState);
    }
}
