package xyz.cnworkshop.naviband;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Icon;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.maps2d.AMap;
import com.amap.api.maps2d.MapView;
import com.amap.api.navi.AMapNaviView;
import com.amap.api.navi.enums.IconType;
import com.amap.api.navi.enums.NaviType;
import com.amap.api.navi.model.AMapTrafficStatus;
import com.amap.api.navi.model.NaviInfo;
import com.amap.api.navi.model.NaviLatLng;


import java.util.ArrayList;
import java.util.List;

import app.akexorcist.bluetotohspp.library.BluetoothSPP;
import app.akexorcist.bluetotohspp.library.BluetoothState;
import app.akexorcist.bluetotohspp.library.DeviceList;


public class MainActivity extends BaseActivity{
    TextView textView;
    BluetoothSPP bt;


    Toast mToastNoBTH;
    Toast mToastBTHEnable;
    Toast mToastDeviceConnected;
    Toast mToastDeviceDisconnected;
    Button mBtnLeftV;
    Button mBtnRightV;
    Button mBtnNaviControl;


    protected NaviLatLng mEndLatlng = new NaviLatLng(31.020147,121.439583);
    protected NaviLatLng mStartLatlng = new NaviLatLng(31.295178,121.507843);
    protected final List<NaviLatLng> sList = new ArrayList<NaviLatLng>();
    protected final List<NaviLatLng> eList = new ArrayList<NaviLatLng>();


    private int lastDist;
    private int currDist;
    private boolean isAlarmed;
    private int innerFSM;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        isAlarmed = false;
        innerFSM=0;

        lastDist=1;
        currDist=0;

        mToastNoBTH = Toast.makeText(this, "蓝牙不可用!", Toast.LENGTH_LONG);
        mToastBTHEnable = Toast.makeText(this, "蓝牙已激活", Toast.LENGTH_LONG);
        mToastDeviceConnected = Toast.makeText(this, "设备已连接", Toast.LENGTH_LONG);
        mToastDeviceDisconnected = Toast.makeText(this, "设备已断开", Toast.LENGTH_LONG);


        mBtnLeftV = findViewById(R.id.BtnLeftV);
        mBtnRightV= findViewById(R.id.BtnRightV);
        mBtnNaviControl = findViewById(R.id.BtnNaviControl);

        mBtnLeftV.setEnabled(false);
        mBtnRightV.setEnabled(false);

        mAMapNaviView =  findViewById(R.id.navi_view);
        mAMapNaviView.onCreate(savedInstanceState);
        mAMapNaviView.setAMapNaviViewListener(this);

        sList.add(mStartLatlng);
        eList.add(mEndLatlng);


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

    @Override
    public void onInitNaviSuccess() {
        super.onInitNaviSuccess();
        int strategy = 0;
        try {
            //再次强调，最后一个参数为true时代表多路径，否则代表单路径
            strategy = mAMapNavi.strategyConvert(true, false, false, false, false);
        } catch (Exception e) {
            e.printStackTrace();
        }
        mAMapNavi.calculateDriveRoute(sList, eList, mWayPointList, strategy);
    }

    @Override
    public void onCalculateRouteSuccess(int[] ids) {
        super.onCalculateRouteSuccess(ids);
//        mAMapNavi.startNavi(NaviType.EMULATOR);
    }

    public  void naviControlFSM(View view){
        if(innerFSM ==0) {
            mAMapNavi.startNavi(NaviType.EMULATOR);
            mBtnNaviControl.setText("暂停导航");
            innerFSM=1;
        }
        else if(innerFSM==1)
        {
            mAMapNavi.pauseNavi();
            mBtnNaviControl.setText("恢复导航");
            innerFSM=2;
        }
        else{
            mAMapNavi.resumeNavi();
            mBtnNaviControl.setText("暂停导航");
            innerFSM=1;
        }
    }





    @Override
    public void onNaviInfoUpdate(NaviInfo naviinfo) {
        super.onNaviInfoUpdate(naviinfo);
        int direct=naviinfo.getIconType();
        currDist=naviinfo.getCurStepRetainDistance();
        if(currDist>lastDist){
            isAlarmed=false;
        }

        if(currDist<50){
            if(!isAlarmed){
                if(direct == IconType.LEFT)this.leftVibrator(null);
                else if(direct == IconType.RIGHT) this.rightVibrator(null);
                isAlarmed=true;
            }
        }
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

    public void leftVibrator(View view){
        bt.send("NB__head__\r\nL1NB__end__\r\n",true);
    }
    public void rightVibrator(View view){
        bt.send("NB__head__\r\nR1NB__end__\r\n",true);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        mAMapNaviView.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mAMapNaviView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mAMapNaviView.onPause();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mAMapNaviView.onSaveInstanceState(outState);
    }
}
