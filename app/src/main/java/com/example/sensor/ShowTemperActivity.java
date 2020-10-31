package com.example.sensor;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.github.mikephil.charting.charts.LineChart;

import java.util.Timer;
import java.util.TimerTask;


public class ShowTemperActivity extends AppCompatActivity {

    private static final String TAG = "存储";
    private DynamicLineChartManager dynamicLineChartManager1;
    private DynamicLineChartManager dynamicLineChartManager2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_temper);
        initLineChart();

        //每隔1s添加一个数据
        new Timer().schedule(new TimerTask() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void run() {
                getData();
            }
        }, 0, 1000);
    }


    private void initLineChart(){
        //温度图
        LineChart mChart1 = (LineChart) findViewById(R.id.dynamic_chart_tem);
        dynamicLineChartManager1 = new DynamicLineChartManager(mChart1, "温度:℃", Color.CYAN);
        dynamicLineChartManager1.setYAxis(40, 10, 20);
        dynamicLineChartManager1.setHightLimitLine(37, "体温警戒线", Color.rgb(255, 80, 10));
        dynamicLineChartManager1.setFillColor(Color.rgb(255,105,180));
        //湿度图
        LineChart mChart2 = (LineChart) findViewById(R.id.dynamic_chart_hum);
        dynamicLineChartManager2 = new DynamicLineChartManager(mChart2, "湿度",getResources().getColor(R.color.DeepPink));
        dynamicLineChartManager2.setYAxis(100, 0, 20);
        dynamicLineChartManager2.setHightLimitLine(90, "湿度警戒线", Color.rgb(255, 80, 10));
        dynamicLineChartManager2.setFillColor(Color.rgb(0,255,127));
    }

    //获取characteristic的数据
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void getData(){
        if(ShowBlueConnectActivity.temValue!=null){
            dynamicLineChartManager1.addEntry(Float.valueOf(ShowBlueConnectActivity.temValue));
            //saveText(ShowBlueConnectActivity.temValue,"temData");
        }
        if(ShowBlueConnectActivity.humValue!=null){
            dynamicLineChartManager2.addEntry(Float.valueOf(ShowBlueConnectActivity.humValue));
            //saveText(ShowBlueConnectActivity.humValue,"humData");
        }
    }

//    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
//    private boolean saveText(String text, String name) {
//
//        if(text == null) return false;
//
//        byte[] data = text.getBytes(StandardCharsets.UTF_8);
//        if(null == data || data.length == 0){
//            return false;
//        }
//        FileOutputStream fout = null;
//        try {
//            String downloadDirectoryString = null;
//
//            // Depending on the location, there may be an extension already on the name or not
//            String dir = Environment.getExternalStorageDirectory() + "/"+ downloadDirectoryString + "/";
//            String extension = ".txt";
//            File file = new File(dir + name + extension);
//
//            // make sure the path is valid and directories created for this file.
//            File parentFile = file.getParentFile();
//            if (!parentFile.exists() && !parentFile.mkdirs()) {
//                return false;
//            }
//
//            fout = new FileOutputStream(file);
//            int size = data.length;
//            //UTF8 TEXT HEAD
//            byte[] heads = new byte[3];
//            heads[0] = (byte)(-17);
//            heads[1] = (byte)(-69);
//            heads[2] = (byte)(-65);
//            fout.write(heads, 0, heads.length);
//            fout.write(data, 0, size);
//
//            sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(file)));
//        } catch (IOException e) {
//            // Ignore
//            Log.e(TAG, "IOException caught while opening or reading stream", e);
//            return false;
//        } finally {
//            if (null != fout) {
//                try {
//                    fout.close();
//                } catch (IOException e) {
//                    // Ignore
//                    Log.e(TAG, "IOException caught while closing stream", e);
//                    return false;
//                }
//            }
//        }
//        return true;
//    }

}


