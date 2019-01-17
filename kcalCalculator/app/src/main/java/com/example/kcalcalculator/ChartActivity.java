package com.example.kcalcalculator;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.LinearLayout;

public class ChartActivity extends AppCompatActivity {
private ChartView mChartView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chart);
        LinearLayout dynamicLayout = (LinearLayout) findViewById(R.id.layout);
        LinearLayout.LayoutParams layoutParams=new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.MATCH_PARENT
        );
        //차트를 출력하는 뷰객체(ChartView) 생성
        mChartView = new ChartView(this);

        //리니어 레이아웃에 차트뷰 추가( 폭, 높이 가득차게 )
        dynamicLayout.addView(mChartView, layoutParams);

        Intent intent=getIntent();
        double[] value=intent.getDoubleArrayExtra("chart");


        String[] text={"일일 섭취량","권장 섭취량"};
        //차트 그리기
        mChartView.makeChart(value,text);
    }
}
