package com.vimems.Adapter;

import android.bluetooth.BluetoothDevice;
import android.content.Intent;
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
import static com.vimems.coach.MemberDetailActivity.gattDeviceRecyclerView;
import static com.vimems.coach.MemberDetailActivity.memberID;
import static com.vimems.mainactivity.SingleModeTrainingMainActivity.defaultDeviceAddress;
import static com.vimems.mainactivity.SingleModeTrainingMainActivity.mDeviceAddress;
import static com.vimems.mainactivity.SingleModeTrainingMainActivity.scanResultDeviceRecyclerView;
import static com.vimems.mainactivity.SingleModeTrainingMainActivity.trainingPageDeviceName;
import static util.Constants.EXTRAS_DEVICE_ADDRESS;
import static util.Constants.EXTRAS_DEVICE_NAME;
import static com.vimems.coach.CustomTrainingItemFragment.memberIDDeviceMap;
import static com.vimems.coach.CustomTrainingItemFragment.deviceDefaultCustomNameMap;



public class TrainingMainGattDeviceAdapter extends RecyclerView.Adapter<TrainingMainGattDeviceAdapter.ViewHolder> {
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

    public TrainingMainGattDeviceAdapter(ArrayList<BluetoothDevice> bluetoothDeviceArrayList) {
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
                intent.putExtra(EXTRAS_DEVICE_NAME, bluetoothDevice.getName());
                intent.putExtra(EXTRAS_DEVICE_ADDRESS, bluetoothDevice.getAddress());
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

                trainingPageDeviceName.setText(bluetoothDevice.getName());
                defaultDeviceAddress.setText(bluetoothDevice.getAddress());
                mDeviceAddress=bluetoothDevice.getAddress();//这个参数是BlurtoothLeService.connect(mDeviceAddress)用到的
                scanResultDeviceRecyclerView.setVisibility(View.GONE);


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
