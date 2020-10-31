package com.example.sensor;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.widget.EditText;

//实现SensorEventListener接口
public class PhoneSensorActivity extends Activity implements SensorEventListener {
    private EditText textLight,textMagnetic,textStep;//传感器值输出信息编辑框
    private SensorManager sensorManager;//传感器管理器对象
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone_sensor);
        //获取传感器输出信息编辑框
        textLight=findViewById(R.id.textLight);
        textMagnetic=findViewById(R.id.textMagnetic);
        textStep=findViewById(R.id.textStep);
        sensorManager= (SensorManager) getSystemService(Context.SENSOR_SERVICE);//获取传感器管理器
    }

    @Override
    protected void onResume() {
        super.onResume();
        //磁场传感器注册传感器监听器
        sensorManager.registerListener(this, sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD), SensorManager.SENSOR_DELAY_UI);
        //为光线传感器注册传感器监听器
        sensorManager.registerListener(this,sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT),SensorManager.SENSOR_DELAY_NORMAL);
        //为计步器传感器注册传感器监听器
        sensorManager.registerListener(this,sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER),SensorManager.SENSOR_DELAY_NORMAL);


    }

    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);//取消注册的监听器
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        //显示光的强度
        if(event.sensor.getType()==Sensor.TYPE_LIGHT){
            textLight.setText("当前光的强度值为： " + event.values[0] +" lux");
        }
        //显示磁场强度
        if(event.sensor.getType()==Sensor.TYPE_MAGNETIC_FIELD){
            int x = (int) event.values[0];
            int y = (int) event.values[1];
            int z = (int) event.values[2];
            textMagnetic.setText("当前磁场强度为："+x+"uT,"+y+"uT,"+z+"uT");
        }
        //显示步数
        if(event.sensor.getType()==Sensor.TYPE_STEP_COUNTER){
            textStep.setText("当前步数为：" + event.values[0] +"步");
        }



    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}