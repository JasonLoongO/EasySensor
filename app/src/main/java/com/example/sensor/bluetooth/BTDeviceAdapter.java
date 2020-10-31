package com.example.sensor.bluetooth;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.sensor.R;
import com.example.sensor.ShowBlueConnectActivity;

import java.util.ArrayList;

public  class BTDeviceAdapter extends ArrayAdapter<BTDeviceInfor> {

    Activity activity;
    ArrayList<BTDeviceInfor> devices;
    private int resID;
    private TextView blueDeviceName;
    private TextView blueDeviceRssi;
    private TextView blueDeviceAddress;
    private Button btnConnect;

    //初始化，把布局资源文件ID给
    public BTDeviceAdapter(Activity activity, int textViewResourceID, ArrayList<BTDeviceInfor> btDeviceInfors){
        super(activity, textViewResourceID, btDeviceInfors);
        this.activity = activity;
        resID = textViewResourceID;
        devices = btDeviceInfors;
    }

    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        //获取传入BTDeviceAdapter的实体类BTDeviceInfor
        final BTDeviceInfor device = devices.get(position);
        //从LayoutInflater获取自定义的布局文件
        if (convertView == null) {
            LayoutInflater inflater =
                    (LayoutInflater) activity.getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(resID, parent, false);

            blueDeviceName = convertView.findViewById(R.id.blueDevice_name);
            blueDeviceRssi = convertView.findViewById(R.id.blueDevice_rssi);
            blueDeviceAddress = convertView.findViewById(R.id.blueDevice_address);
            btnConnect = convertView.findViewById(R.id.btn_connect);
        }

        if(device.getName()==null){
            blueDeviceName.setText("noName");
        }else {
            blueDeviceName.setText(device.getName());
        }
        blueDeviceRssi.setText(Integer.toString(device.getRSSI())+" dBm");
        blueDeviceAddress.setText("Mac："+device.getAddress());

        //点击列表中的某一设备，设置MyMacDevice为该设备的address
        btnConnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShowBlueConnectActivity.setDeviceMac(device.getAddress());
            }
        });

        return convertView;
    }

}


