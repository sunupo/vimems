package com.vimems.Adapter;

import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.vimems.Ble.AppBluetoothGatt.DeviceControlActivity;
import com.vimems.Ble.AppBluetoothGatt.GattPeripheralDetailActivity;
import com.vimems.R;

import java.util.ArrayList;

public class GattDeviceAdapter extends RecyclerView.Adapter<GattDeviceAdapter.ViewHolder> {
    private ArrayList<BluetoothDevice> bluetoothDeviceArrayList;

    static class ViewHolder extends RecyclerView.ViewHolder{
        View bluetoothDeviceView;
        TextView bluetoothDeviceName;
        TextView bluetoothDeviceAddress;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            bluetoothDeviceView=itemView;
            bluetoothDeviceName=itemView.findViewById(R.id.item_bluetoothdevice_name);
            bluetoothDeviceAddress=itemView.findViewById(R.id.item_bluetoothdevice_address);
        }
    }

    public GattDeviceAdapter(ArrayList<BluetoothDevice> bluetoothDeviceArrayList) {
        this.bluetoothDeviceArrayList = bluetoothDeviceArrayList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        final ViewHolder viewHolder=new ViewHolder(LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.item_bluetoothdevice,viewGroup,false));
        viewHolder.bluetoothDeviceView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position=viewHolder.getAdapterPosition();
                BluetoothDevice bluetoothDevice=bluetoothDeviceArrayList.get(position);

                Intent intent=new Intent(v.getContext(),GattPeripheralDetailActivity.class);
                intent.putExtra(DeviceControlActivity.EXTRAS_DEVICE_NAME, bluetoothDevice.getName());
                intent.putExtra(DeviceControlActivity.EXTRAS_DEVICE_ADDRESS, bluetoothDevice.getAddress());
               /* if(GattMainActivity.mScanning){
                    GattMainActivity.mBluetoothAdapter.stopLeScan();
                }*/
                v.getContext().startActivity(intent);
            }
        });

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {

        BluetoothDevice bluetoothDevice=bluetoothDeviceArrayList.get(i);
        viewHolder.bluetoothDeviceName.setText(bluetoothDevice.getName());
        viewHolder.bluetoothDeviceAddress.setText(bluetoothDevice.getAddress());
    }

    @Override
    public int getItemCount() {
        return bluetoothDeviceArrayList.size();
    }


}
