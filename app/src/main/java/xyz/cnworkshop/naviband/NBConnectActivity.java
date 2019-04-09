package xyz.cnworkshop.naviband;

import app.akexorcist.bluetotohspp.library.BluetoothSPP;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

public class NBConnectActivity extends AppCompatActivity {

    BluetoothSPP bt = new BluetoothSPP(this);


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nbconnect);
        if(!bt.isBluetoothAvailable()) {
            Log.d("BTHConnector","Blue Tooth UnAvailable");
        }
    }
}

