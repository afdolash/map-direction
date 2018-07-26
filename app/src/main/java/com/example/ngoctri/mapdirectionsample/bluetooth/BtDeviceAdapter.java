package com.example.ngoctri.mapdirectionsample.bluetooth;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ngoctri.mapdirectionsample.map.MapsActivity;
import com.example.ngoctri.mapdirectionsample.R;

import java.util.List;

/**
 * Created by afdol on 7/5/2018.
 */

public class BtDeviceAdapter extends RecyclerView.Adapter<BtDeviceAdapter.MyViewHolder> {
    public static final String EXTRA_DEVICE_ADDRESS = "address";

    private Context context;
    private List<BluetoothDevice> devices;

    public BtDeviceAdapter(Context context, List<BluetoothDevice> devices) {
        this.context = context;
        this.devices = devices;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_bt_device, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        final BluetoothDevice device = devices.get(position);

        holder.tvName.setText(device.getName());
        holder.tvAddress.setText(device.getAddress());
        holder.cardDevice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (device.getBondState() == BluetoothDevice.BOND_BONDED) {
                    try {
                        Intent intent = new Intent(context, MapsActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.putExtra(EXTRA_DEVICE_ADDRESS, device.getAddress());
                        context.startActivity(intent);
                    } catch (Exception e) {
                        Toast.makeText(context, "Bluetooth connection failed!", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    device.createBond();
                    Toast.makeText(context, "Create bond!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return devices.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView tvName, tvAddress;
        public RelativeLayout cardDevice;

        public MyViewHolder(View itemView) {
            super(itemView);
            tvName = (TextView) itemView.findViewById(R.id.tv_name);
            tvAddress = (TextView) itemView.findViewById(R.id.tv_mac);
            cardDevice = (RelativeLayout) itemView.findViewById(R.id.card_device);
        }
    }
}
