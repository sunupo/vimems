package com.vimems.Adapter;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.vimems.Ble.AppBluetoothGatt.DeviceControlActivity;
import com.vimems.Ble.AppBluetoothGatt.GattPeripheralDetailActivity;
import com.vimems.R;

import java.util.ArrayList;

import static com.vimems.coach.CustomTrainingItemFragment.customDeviceName;
import static com.vimems.coach.CustomTrainingItemFragment.deviceAddress;
import static com.vimems.coach.MemberDetailActivity.deviceDefaultCustomNameMap;
import static com.vimems.coach.MemberDetailActivity.gattDeviceRecyclerView;
import static com.vimems.coach.MemberDetailActivity.memberID;
import static com.vimems.coach.MemberDetailActivity.memberIDDeviceMap;

public class GattDeviceAdapter extends RecyclerView.Adapter<GattDeviceAdapter.ViewHolder> {
    private ArrayList<BluetoothDevice> bluetoothDeviceArrayList;

    static class ViewHolder extends RecyclerView.ViewHolder{
        View bluetoothDeviceView;
        TextView bluetoothDeviceName;
        TextView bluetoothDeviceAddress;
        Button bindButton;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            bluetoothDeviceView=itemView;
            bluetoothDeviceName=itemView.findViewById(R.id.item_bluetoothdevice_name);
            bluetoothDeviceAddress=itemView.findViewById(R.id.item_bluetoothdevice_address);
            bindButton=itemView.findViewById(R.id.gatt_device_bind_member);
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
        viewHolder.bindButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position=viewHolder.getAdapterPosition();
                BluetoothDevice bluetoothDevice=bluetoothDeviceArrayList.get(position);

                //memberID来自MemberDetailActivity
                //绑定用户设备
                memberIDDeviceMap.put(memberID,bluetoothDevice);
                Log.d("memberIDDeviceMap.put(memberID,device)", "<"+memberID+","+bluetoothDevice.getName()+">");

                //更新设备自定义名称列表
                deviceDefaultCustomNameMap.put(bluetoothDevice,bluetoothDevice.getName());
                Log.d("deviceDefaultCustomNameMap.put(device,device.getName())", "<"+bluetoothDevice.getName()+","+deviceDefaultCustomNameMap.get(bluetoothDevice)+">");

                customDeviceName.setText(bluetoothDevice.getName());
                deviceAddress.setText(bluetoothDevice.getAddress());
                gattDeviceRecyclerView.setVisibility(View.GONE);
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
