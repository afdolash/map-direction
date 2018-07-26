package com.example.ngoctri.mapdirectionsample.bluetooth;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ngoctri.mapdirectionsample.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class BluetoothActivity extends AppCompatActivity {

    public static final String BLUETOOTH_TAG = BluetoothActivity.class.getSimpleName();

    private TextView tvDiscover;
    private RecyclerView recyclerView;
    private BluetoothAdapter bluetoothAdapter;
    private List<BluetoothDevice> bluetoothDevices = new ArrayList<>();
    private BtDeviceAdapter deviceAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetooth);

        recyclerView = (RecyclerView) findViewById(R.id.rc_devices);
        tvDiscover = (TextView) findViewById(R.id.tv_discover);

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        deviceAdapter = new BtDeviceAdapter(this, bluetoothDevices);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(deviceAdapter);

        Set<BluetoothDevice> pairedDevices = bluetoothAdapter.getBondedDevices();

        if (pairedDevices.size() > 0) {
            for (BluetoothDevice device : pairedDevices) {
                bluetoothDevices.add(device);
                deviceAdapter.notifyDataSetChanged();
            }
        }

        tvDiscover.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bluetoothDevices.clear();
                Set<BluetoothDevice> pairedDevices = bluetoothAdapter.getBondedDevices();

                if (pairedDevices.size() > 0) {
                    for (BluetoothDevice device : pairedDevices) {
                        bluetoothDevices.add(device);
                        deviceAdapter.notifyDataSetChanged();
                    }
                }
                Toast.makeText(BluetoothActivity.this, "Discovering...", Toast.LENGTH_SHORT).show();
            }
        });

        checkBTPermission();
    }

    @Override
    protected void onResume() {
        super.onResume();
        enableBluetooth();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private void enableBluetooth() {
        if (bluetoothAdapter == null) {
            Log.d(BLUETOOTH_TAG, "enableBluetooth: Does not have BT capabilities.");
        }

        if(!bluetoothAdapter.isEnabled()){
            //Prompt user to turn on Bluetooth
            checkBTPermission();

            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, 1);
        }
    }

    private void checkBTPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M ) {
            int permissionCheck = this.checkSelfPermission("Manifest.permission.ACCESS_FINE_LOCATION");
            permissionCheck += this.checkSelfPermission("Manifest.permission.ACCESS_COARSE_LOCATION");
            if (permissionCheck != 0) {
                this.requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 1001); //Any number
            }
        }else{
            Log.d(BLUETOOTH_TAG, "checkBTPermissions: No need to check permissions. SDK version < LOLLIPOP.");
        }
    }
}
