package com.example.sensor;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.github.mikephil.charting.charts.LineChart;


public class ShowBodyfatActivity extends AppCompatActivity {
    private DynamicLineChartManager dynamicLineChartManager3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_bodyfat);
        LineChart mChart = (LineChart) findViewById(R.id.chart_bodyfat);

        dynamicLineChartManager3 = new DynamicLineChartManager(mChart, "体脂率：%", Color.CYAN);
        dynamicLineChartManager3.setYAxis(30, 0, 15);
        dynamicLineChartManager3.setHightLimitLine(20, "20体脂线", Color.rgb(255, 80, 10));
        dynamicLineChartManager3.setLowLimitLine(10, "10体脂线", Color.rgb(255, 80, 10));

    }

    //按钮点击添加数据
    public void onAddEntry (View view){
        dynamicLineChartManager3.addEntry((float) (Math.random() * 30));//随机生成0-30的数
    }
}
