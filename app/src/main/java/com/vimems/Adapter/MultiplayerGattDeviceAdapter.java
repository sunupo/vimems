package com.vimems.Adapter;

import android.bluetooth.BluetoothDevice;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.vimems.R;
import com.vimems.mainactivity.MultiplayerModeTrainingMainActivity;

import java.util.ArrayList;

import static com.vimems.mainactivity.MultiplayerModeTrainingMainActivity.mScanning;
import static com.vimems.mainactivity.MultiplayerModeTrainingMainActivity.selectedBluetoothDeviceList;
import static com.vimems.mainactivity.MultiplayerModeTrainingMainActivity.selectedBluetoothDeviceSize;


public class MultiplayerGattDeviceAdapter extends RecyclerView.Adapter<MultiplayerGattDeviceAdapter.ViewHolder> {
    private ArrayList<BluetoothDevice> bluetoothDeviceArrayList;
    private final String TAG="MultiplayerGattDeviceAdapter";

    static class ViewHolder extends RecyclerView.ViewHolder{
        View bluetoothDeviceView;
        TextView addDeviceFlag;
        TextView bluetoothDeviceName;
        TextView bluetoothDeviceAddress;
        Button addDeviceButton;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            bluetoothDeviceView=itemView;
            addDeviceFlag=itemView.findViewById(R.id.add_device_flag);
            bluetoothDeviceName=itemView.findViewById(R.id.item_bluetoothdevice_name);
            bluetoothDeviceAddress=itemView.findViewById(R.id.item_bluetoothdevice_address);
            addDeviceButton=itemView.findViewById(R.id.gatt_device_bind_member);

            ((MultiplayerModeTrainingMainActivity)itemView.getContext()).setDeviceRecyclerViewControllerItem(new MultiplayerModeTrainingMainActivity.DeviceRecyclerViewController() {
                @Override
                public void setDeviceRecyclerViewOnClickAble(boolean onClickAble) {
                    addDeviceButton.setClickable(onClickAble);
                }
                @Override
                public void setDeviceRecyclerViewAlpha(float alpha) {
                    addDeviceButton.setAlpha(alpha);
                }
            });
        }
    }

    public MultiplayerGattDeviceAdapter(ArrayList<BluetoothDevice> bluetoothDeviceArrayList) {
        this.bluetoothDeviceArrayList = bluetoothDeviceArrayList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        final ViewHolder viewHolder=new ViewHolder(LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.item_bluetoothdevice_multiplayer,viewGroup,false));
        /*viewHolder.bluetoothDeviceView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position=viewHolder.getAdapterPosition();
                BluetoothDevice bluetoothDevice=bluetoothDeviceArrayList.get(position);

                Intent intent=new Intent(v.getContext(),GattPeripheralDetailActivity.class);
                intent.putExtra(EXTRAS_DEVICE_NAME, bluetoothDevice.getName());
                intent.putExtra(EXTRAS_DEVICE_ADDRESS, bluetoothDevice.getAddress());
                if(MultiplayerModeTrainingMainActivity.mScanning){
                    // TODO: 2/19/2019 发出广播到 MultiplayerModeTrainingMainActivity，停止扫描
                }
                v.getContext().startActivity(intent);
            }
        });*/

        viewHolder.addDeviceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position=viewHolder.getAdapterPosition();
                BluetoothDevice bluetoothDevice=bluetoothDeviceArrayList.get(position);
                if(mScanning){
                    Toast.makeText(v.getContext(),"正在扫描",Toast.LENGTH_SHORT).show();
                    return;
                }
                if(viewHolder.addDeviceButton.getText().toString()=="添加"){
                    selectedBluetoothDeviceList.add(bluetoothDevice);
                    MultiplayerModeTrainingMainActivity.selectedBluetoothDeviceSize++;

                    viewAlphaItem.setConnectDeviceButtonAlpha(1f);//当添加了设备，设置不透明度为1
                    viewAlphaItem.setConnectDeviceButtonClickable(true);//当添加了设备，设置为可以点击

                    Log.d(TAG, "onClick: setConnectDeviceButtonAlpha=1f");
                    viewHolder.addDeviceButton.setText("删除");
                    viewHolder.addDeviceFlag.setVisibility(View.VISIBLE);
                    Log.d(TAG, "onClick: +"+"设备数目为="+selectedBluetoothDeviceList.size()+"地址为"+getAddress());

                }else{
                    selectedBluetoothDeviceList.remove(bluetoothDevice);
                    MultiplayerModeTrainingMainActivity.selectedBluetoothDeviceSize--;
                    if(selectedBluetoothDeviceSize==0){
                        viewAlphaItem.setConnectDeviceButtonAlpha(0.5f);//当全部删除了设备，设置不透明度为0.5
                        viewAlphaItem.setConnectDeviceButtonClickable(false);//当删除了设备，设置为可以点击
                        Log.d(TAG, "onClick: setConnectDeviceButtonAlpha=0.5f");
                    }
                    viewHolder.addDeviceButton.setText("添加");
                    viewHolder.addDeviceFlag.setVisibility(View.INVISIBLE);
                    Log.d(TAG, "onClick: -"+"设备数目为="+selectedBluetoothDeviceList.size()+"地址为"+getAddress());

                }
            }
        });
        return viewHolder;
    }
    private String getAddress(){
        StringBuffer stringBuffer=new StringBuffer();
        for (int j = 0; j < selectedBluetoothDeviceList.size(); j++) {
            stringBuffer.append(selectedBluetoothDeviceList.get(j).getAddress()+"\n");
        }
        return stringBuffer.toString();
    }
    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        BluetoothDevice bluetoothDevice=bluetoothDeviceArrayList.get(i);
        viewHolder.addDeviceFlag.setVisibility(View.INVISIBLE);
        viewHolder.bluetoothDeviceName.setText(bluetoothDevice.getName());
        viewHolder.bluetoothDeviceAddress.setText(bluetoothDevice.getAddress());
        viewHolder.addDeviceButton.setText("添加");
    }
    @Override
    public int getItemCount() {
        return bluetoothDeviceArrayList.size();
    }

//    适配器中定义的接口，在活动中实现
    public interface ViewAlpha{
        void setConnectDeviceButtonAlpha(float alpha);
        void setConnectDeviceButtonClickable(boolean clickable);
    }
    public void setViewAlphaItem(ViewAlpha viewAlphaItem){
        this.viewAlphaItem=viewAlphaItem;
    }
    private ViewAlpha viewAlphaItem;

}
