package com.example.sensor;


import android.Manifest;
import android.app.Dialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothProfile;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.example.sensor.bluetooth.BTDeviceAdapter;
import com.example.sensor.bluetooth.BTDeviceInfor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import static android.bluetooth.BluetoothGattCharacteristic.FORMAT_UINT8;


public class ShowBlueConnectActivity extends AppCompatActivity  {

    public static final String CLIENT_CHARACTERISTIC_CONFIG = "00002902-0000-1000-8000-00805f9b34fb";
    //需要根据条件修改的参数
    private static String MyMacDevice = " ";  //微雪蓝牙版MAC地址，这样赋值防止空指针异常
    private String SERVICESUUID = "00001523-1212-efde-1523-785feabcd123";   //服务的UUID
    private String WRITEUUID = "0000ff02-0000-1000-8000-00805f9b34fb";      //写入特征的UUID
    private String TEM_NOTIFY_UUID = "00001526-1212-efde-1523-785feabcd123";     //监听特征的UUID,温度湿度数据
    private String HUM_NOTIFY_UUID = "00001527-1212-efde-1523-785feabcd123";

    public static String temValue;//接收到的温度值
    public static String humValue;//接收到的湿度值

    private BluetoothAdapter bluetoothAdapter;
    private BluetoothDevice mBluetoothDevice;
    private BluetoothGatt bluetoothGatt;
    private BluetoothGattService bluetoothGattService;
    private BluetoothGattCharacteristic writeCharacteristic;
    private BluetoothGattCharacteristic tmp_NotifyCharacteristic;
    private BluetoothGattCharacteristic hum_NotifyCharacteristic;

    private ListView lvDevice;
    private Button btnCheckPermission;
    private Button btnSearchBLE;
    private TextView tvMsg;
    private TextView tvMAC;

    private HashMap<String, BTDeviceInfor> mBTDevicesHashMap = new HashMap<>();
    private ArrayList<BTDeviceInfor> mBTDevicesArrayList = new ArrayList<>();
    private BTDeviceAdapter btAdapter;
    private Dialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_blue_connect);

        //蓝牙设备列表,用于显示搜索到的蓝牙设备
        btnCheckPermission = findViewById(R.id.btnCheckPermission);
        tvMsg = findViewById(R.id.tvMsg);
        btnSearchBLE = findViewById(R.id.btnSearchBLE);
        tvMAC = findViewById(R.id.tvMAC);
        lvDevice = findViewById(R.id.lv_device);

        initDialog();       //初始化弹窗
        bluetoothInit();    //蓝牙初始化
        widgetListener();   //控件监听

        btAdapter = new BTDeviceAdapter(this,R.layout.blue_device_item,mBTDevicesArrayList);
        lvDevice.setAdapter(btAdapter);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        bluetoothAdapter.stopLeScan(mBLEScanCallback);//停止搜索
        MyMacDevice = " ";
    }

    //用于设置连接设备的地址
    public static void setDeviceMac(String address){
        MyMacDevice = address;
    }

    //搜索到设备时，添加device到集合mBTDevicesArrayList中
    public void addDevice(BluetoothDevice device, int rssi) {
        String address = device.getAddress();
        if (!mBTDevicesHashMap.containsKey(address)) {
            BTDeviceInfor bleDevice = new BTDeviceInfor(device);
            bleDevice.setRSSI(rssi);

            mBTDevicesHashMap.put(address, bleDevice);
            mBTDevicesArrayList.add(bleDevice);
            btAdapter.notifyDataSetChanged();
        }
        else {
            mBTDevicesHashMap.get(address).setRSSI(rssi);
        }
    }

    //获取初略位置权限
    private void getPermission() {
        //动态申请权限
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M &&
                checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED )
        {
            requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION ,
                    Manifest.permission.CAMERA,}, 10);
        }
    }

    //初始化弹窗
    private void initDialog(){
        dialog = new Dialog(ShowBlueConnectActivity.this,R.style.Theme_AppCompat_Dialog);
        dialog.setContentView(LayoutInflater.from(ShowBlueConnectActivity.this).inflate(R.layout.bt_service_dialog, null));
        Window dialogWindow = dialog.getWindow();
        dialogWindow.getDecorView().setPadding(0, 0, 0, 0);// 边距设为0
        dialogWindow.setBackgroundDrawableResource(android.R.color.transparent);//背景透明，不然会有个白色的东东
        dialogWindow.setWindowAnimations(R.style.dialogWindowAnim); //设置窗口弹出动画
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        lp.width = WindowManager.LayoutParams.MATCH_PARENT; // 宽度
        lp.height = WindowManager.LayoutParams.FLAG_FULLSCREEN; // 高度
        dialogWindow.setAttributes(lp);
        dialogWindow.setGravity(Gravity.BOTTOM);
    }

    //蓝牙初始化，检查有无蓝牙功能，并开启蓝牙
    private void bluetoothInit() {
        //如果不支持蓝牙
        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE))
        {
            //提示不支持蓝牙
            Toast.makeText(this, "本设备不支持BLE", Toast.LENGTH_SHORT).show();
            //退出程序
            finish();
        }
        //创建蓝牙适配器原型是BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        //如果蓝牙适配器为空
        if (bluetoothAdapter == null)
        {
            //显示设备无蓝牙
            Toast.makeText(this, "设备无蓝牙", Toast.LENGTH_SHORT).show();
            //退出
            finish();
        }
        //如果蓝牙未开启
        if (!bluetoothAdapter.isEnabled())
        {
            //不提示,直接开启蓝牙
            bluetoothAdapter.enable();
            //提示开启蓝牙中
            Toast.makeText(this, "开启蓝牙中,如果未开启,请检查应用权限", Toast.LENGTH_SHORT).show();
        }

    }

    //控件监听
    private void widgetListener() {
        //测试位置权限按钮监听，如果没有则开启
        btnCheckPermission.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int permissionCheck = ContextCompat.checkSelfPermission
                        (ShowBlueConnectActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION);
                if (permissionCheck == PackageManager.PERMISSION_GRANTED) {//如果有权限
                    Toast.makeText(ShowBlueConnectActivity.this, "access position permission", Toast.LENGTH_SHORT).show();//toast信息
                    Log.d("权限：", "有定位权限");//在logcat上打印信息
                    tvMsg.setText("有定位权限");
                } else {
                    getPermission();//获取权限
                    Toast.makeText(ShowBlueConnectActivity.this, "no position permission", Toast.LENGTH_SHORT).show();//toast信息
                    Log.d("权限：", "无定位权限");//在logcat上打印信息
                    tvMsg.setText("无定位权限");
                }
            }
        });

        //搜索蓝牙按钮监听
        btnSearchBLE.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    //开始搜索蓝牙
                    tvMsg.setText("搜索蓝牙中。。。");
                    //清除蓝牙设备信息
                    mBTDevicesHashMap.clear();
                    mBTDevicesArrayList.clear();
                    bluetoothAdapter.startLeScan(mBLEScanCallback); //开始搜索
            }
        });

        //点击弹出device的characteristic列表
        lvDevice.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                BTDeviceInfor bleDevice = mBTDevicesArrayList.get(position);
                Toast.makeText(ShowBlueConnectActivity.this,bleDevice.getAddress(),Toast.LENGTH_LONG).show();

                // 弹出dialog
                dialog.show();

            }
        });
    }

    //mBLEScanCallback回调函数
    private BluetoothAdapter.LeScanCallback mBLEScanCallback = new BluetoothAdapter.LeScanCallback() {
        @Override
        public void onLeScan(BluetoothDevice device, int rssi, byte[] scanRecord) {

            tvMsg.setText("搜索到BLE设备");
            addDevice(device,rssi);//搜索到设备，便执行添加操作

            //打印蓝牙mac地址
            Log.d("BleMAC", device.getAddress());
            //判断设备地址是否与我们指定设备地址相等，相等则连接
            if(device.toString().indexOf(MyMacDevice) == 0)
            {
                Log.d("匹配的蓝牙地址", device.getAddress());
                //停止搜索蓝牙，降低功耗
                bluetoothAdapter.stopLeScan(mBLEScanCallback);
                //获取远程设备（连接蓝牙）原型是BluetoothDevice mBluetoothDevice = bluetoothAdapter.getRemoteDevice(des);请自行提升全局
                mBluetoothDevice = bluetoothAdapter.getRemoteDevice(device.toString());
                //显示连接设备的MAC
                tvMAC.setText("设备MAC："+device.getAddress());

                //连接bluetoothGatt 到这一步时，蓝牙已经连接上了
                //原型是BluetoothGatt bluetoothGatt = device.connectGatt(MainActivity.this, false, bluetoothGattCallback); 请自行提升全局
                //bluetoothGattCallback是蓝牙gatt回调函数，接下来会跳到bluetoothGattCallback函数
                bluetoothGatt = mBluetoothDevice.connectGatt(ShowBlueConnectActivity.this, false, bluetoothGattCallback);
            }
        }
    };

    //蓝牙回调函数
    private final BluetoothGattCallback bluetoothGattCallback = new BluetoothGattCallback() {
        //连接状态改变时回调
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            bluetoothGatt.discoverServices();
            if (newState == BluetoothProfile.STATE_CONNECTED) {
                tvMsg.setText("指定设备已连接");//显示连接状态
                Log.d("onConnectionStateChange","连接成功");
            }
            else {
                tvMsg.setText("连接已断开");
                Log.d("onConnectionStateChange","连接断开");
            }
        }

        //UUID搜索成功回调
        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            Log.d("onCharacteristicWrite", "写入成功");

            if (status == BluetoothGatt.GATT_SUCCESS) {
                //定义一个名为supporGattaservices的list，储存RemoteDevice设备的所有支持的service
                List<BluetoothGattService> supportedGattServices = bluetoothGatt.getServices();

                //在logcat上打印每一个serviceUUID对应的的characteristicUUID
                for (int i = 0; i < supportedGattServices.size(); i++) {
                    Log.i("success", i+": BluetoothGattService UUID=:" + supportedGattServices.get(i).getUuid());
                    List<BluetoothGattCharacteristic> listGattCharacteristic = supportedGattServices.get(i).getCharacteristics();
                    for (int j = 0; j < listGattCharacteristic.size(); j++) {
                        Log.i("success", "    BluetoothGattCharacteristic UUID=:" + listGattCharacteristic.get(j).getUuid());
                    }
                }

                //设置serviceUUID,原型是：BluetoothGattService bluetoothGattService = bluetoothGatt.getService(UUID.fromString(SERVICESUUID));
                bluetoothGattService = bluetoothGatt.getService(UUID.fromString(SERVICESUUID));
                //设置写入特征UUID,原型是：BluetoothGattCharacteristic writeCharacteristic = bluetoothGattService.getCharacteristic(UUID.fromString(WRITEUUID));
                writeCharacteristic = bluetoothGattService.getCharacteristic(UUID.fromString(WRITEUUID));
                //设置监听特征UUID,原型是：BluetoothGattCharacteristic notifyCharacteristic = bluetoothGattService.getCharacteristic(UUID.fromString(NOTIFYUUID));
                tmp_NotifyCharacteristic = bluetoothGattService.getCharacteristic(UUID.fromString(TEM_NOTIFY_UUID));
                hum_NotifyCharacteristic = bluetoothGattService.getCharacteristic(UUID.fromString(HUM_NOTIFY_UUID));
                //开启监听
                boolean tempNoteEnable = gatt.setCharacteristicNotification(tmp_NotifyCharacteristic,true);
                boolean humidityNoteEnable = gatt.setCharacteristicNotification(hum_NotifyCharacteristic,true);
                if (tempNoteEnable && humidityNoteEnable) {

                    BluetoothGattDescriptor temConfig = tmp_NotifyCharacteristic.getDescriptor(UUID.fromString(CLIENT_CHARACTERISTIC_CONFIG)); //通过系统默认获取
                    BluetoothGattDescriptor humConfig = hum_NotifyCharacteristic.getDescriptor(UUID.fromString(CLIENT_CHARACTERISTIC_CONFIG));

                    temConfig.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
                    boolean isSuccess = bluetoothGatt.writeDescriptor(temConfig);
                    Log.d("ReceiveSuccess", "temConfig notify UUID :" + temConfig.getUuid().toString());

                    Log.d("ReceiveSuccess", "startRead: " + "监听接收数据开始");

                }

            } else {
                Log.e("fail", "onservicesdiscovered收到: " + status);
            }
            Log.d("uuidconnectsuccess", "uuid连接成功");
        }

        @Override
        public void onDescriptorWrite(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status){
            super.onDescriptorWrite(gatt, descriptor, status);
            Log.d("ReceiveSuccess", "onDescriptorWrite: " + "设置成功");

        }

        //写入成功回调函数
        @Override
        public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            super.onCharacteristicWrite(gatt, characteristic, status);
            Log.d("onCharacteristicWrite", "写入成功");
        }

        //接受数据回调
        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
            Log.i("ReceiveSuccess", "接收数据的UUID！" + characteristic.getUuid().toString());

            if (characteristic.getUuid().toString().equals(TEM_NOTIFY_UUID)) {
                tvMsg.setText("接受到Characteristic的数据");
                temValue = String.valueOf(characteristic.getIntValue(FORMAT_UINT8,3))
                        +'.'+ String.valueOf(characteristic.getIntValue(FORMAT_UINT8,2));
                humValue = String.valueOf(characteristic.getIntValue(FORMAT_UINT8,1))
                        +'.'+ String.valueOf(characteristic.getIntValue(FORMAT_UINT8,0));
                Log.i("湿度数据为："+humValue, "温度数据为：" +temValue);

            }
        }
    };


}