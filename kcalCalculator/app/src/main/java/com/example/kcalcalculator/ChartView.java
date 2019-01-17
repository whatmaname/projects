package com.example.kcalcalculator;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import org.achartengine.chart.PieChart;
import org.achartengine.model.CategorySeries;
import org.achartengine.renderer.DefaultRenderer;
import org.achartengine.renderer.SimpleSeriesRenderer;

public class ChartView extends View {
    private PieChart mPieChart = null;
    private ChartActivity parent;
    public ChartView(Context context) {
        super(context);
        setFocusable(true);
        parent = (ChartActivity) context;

    }
    public ChartView(Context context, AttributeSet attrs){

        super(context,attrs);
        setFocusable(true);
        parent = (ChartActivity) context;
    }
    public ChartView(Context context, AttributeSet attrs, int defaultStyle)
    {
        super(context, attrs, defaultStyle);
        setFocusable(true);
        parent = (ChartActivity) context;
    }
    public void makeChart(double[] value, String[] text){

        //double[] values = new double[] {10,20,30,40}; //각 계열(Series)의 값
        int[] colors = new int[]{Color.CYAN, Color.MAGENTA}; //각 계열(Series) 색깔,  Color.YELLOW, Color.GREEN
        //String[] texts = new String[] {"SAMPLE1", "SAMPEL2", "SAMPLE3", "SAMPLE4" }; //각 계열(Series) 명

        //차트에서 사용될 데이터(아이템의 값, 색깔, 텍스트)를 차트를 만들 때 필요한
        //DefaultRenderer객체와 CategorySeries객체에 세팅한다.

        DefaultRenderer renderer = new DefaultRenderer();
        for(int color:colors){
            SimpleSeriesRenderer ssr = new SimpleSeriesRenderer();
            ssr.setColor(color);
            renderer.addSeriesRenderer(ssr);
        }

        //CategorySeries(java.lang.String title)
        CategorySeries series = new CategorySeries("계열 타이틀");
        int count = 0;
        for(double values: value){
            //add(java.lang.String category, double value)
            series.add(text[count++],values);
        }
        //PieChart 생성자 -> PieChart(CategorySeries dataset, DefaultRenderer renderer)
        mPieChart = new PieChart(series, renderer);

        //뷰에 다시 그리기
        invalidate();
    }
    @Override
    protected void onDraw(Canvas canvas)
    {
        int width = getMeasuredWidth();
        int height = getMeasuredHeight();
        Log.i("ChartView","onDraw->Width:"+width+"/height:"+height);

        Paint paint= new Paint();
        paint.setColor(Color.BLACK);
        if (mPieChart != null) //mPieChart가 널이 아니면 차트가 가득차도록 그림
            mPieChart.draw(canvas, 0, 0, width-10, height-10,paint);
    }
}
