package com.example.sensor.bluetooth;

import android.bluetooth.BluetoothDevice;

public class BTDeviceInfor {

    private BluetoothDevice bluetoothDevice;
    private int rssi;

    //用于测试搜索bte设备
    private String name;
    private String address;
    public BTDeviceInfor(String name,int rssi,String address){
        this.name = name;
        this.rssi= rssi;
        this.address = address;
    }

    public BTDeviceInfor(BluetoothDevice bluetoothDevice) {
        this.bluetoothDevice = bluetoothDevice;
    }

    public String getAddress(){
        return bluetoothDevice.getAddress();
    }
    public String getName() {
        return bluetoothDevice.getName();
    }

    public void setRSSI(int rssi) {
        this.rssi = rssi;
    }
    public int getRSSI() {
        return rssi;
    }

}

