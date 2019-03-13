package com.example.weatherforecast;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.gson.Gson;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.simplexml.SimpleXmlConverterFactory;

public class MainActivity extends AppCompatActivity {
    final static String weatherService = "http://newsky2.kma.go.kr/",
            airKoreaService = "http://openapi.airkorea.or.kr/",
            serviceKey = "a5LQB3DrXgIE5J%2Bwaz4HpLmUqyroCcRREqjVXvIGqceLLrS52Ob2nNQLxQNjXngBm19%2FMnFCQ%2BjNdSI2LqCe0A%3D%3D";
    AirKorea airKorea;
    Weather weather;
    TextView txtCity, txtDust, txtPm10, txtPm2_5,txtTemp;
    LinearLayout layoutToday;
    Spinner spnRegion;
    //스피너 배열에서 인덱스 찾기용 array
    ArrayList<String> regions = new ArrayList<String>();
    ImageView ivWeather, ivFace;
    //날씨 API 오퍼레이션 차례대로 기상정보,기온정보
    String[] operation = {"getMiddleLandWeather", "getMiddleTemperature"};
    //기상조회코드
    String[] regId = {
            "11B00000", "11H20000", "11H10000", "11B00000", "11F20000", "11C20000", "11H20000", "11B00000",
            "11D20000", "11C10000", "11C20000", "11F10000", "11F20000", "11H10000", "11H20000", "11G00000", "11C20000"
    };
    //에어코리아 API의 지역과 기상청 API지역이 차이가 많이나서 대표지역만 정보를 얻어야 할것같음.
    /*String[] regId2 = {"11B10101", "11B20201", "11B20601", "11B20605", "11B20602", "11B10103", "11B10102", "11B20606", "11B20603", "11B20609", "11B20612", "11B20610",
            "11B20611", "11B20604", "11B20503", "11B20501", "11B20502", "11B20504", "11B20701", "11B20703", "11B20702", "11B20301", "11B20302", "11B20305", "11B20304",
            "11B20401", "11B20402", "11B20403", "11B20404", "11B20101", "11B20102", "11B20202", "11B20204", "11B20203", "11A00101", "11H20201", "11H20101", "11H20304",
            "11H20102", "11H20301", "11H20601", "11H20603", "11H20604", "11H20602",
            "11H20701", "11H20704", "11H20402", "11H20502", "11H20503", "11H20703", "11H20501", "11H20401", "11H20403", "11H20404", "11H20405", "11H10701", "11H10702",
            "11H10703", "11H10704", "11H10705", "11H10601", "11H10602", "11H10603", "11H10604", "11H10605", "11H10501", "11H10502", "11H10503", "11H10302", "11H10301",
            "11H10303", "11H10401", "11H10402", "11H10403", "11H10101", "11H10102", "11H10201", "11H10202", "11E00101", "11E00102", "11F20501", "11F20503", "11F20502",
            "11F20504", "11F20505", "21F20102", "21F20101", "21F20801", "21F20804", "21F20802", "21F20201", "21F20803", "11F20701", "11F20603", "11F20405", "11F20402",
            "11F20601", "11F20602", "11F20301", "11F20303", "11F20304", "11F20302", "11F20401", "11F20403", "11F20404", "11F10201", "11F10202", "21F10501", "11F10203",
            "21F10502", "11F10401", "21F10601", "11F10302", "21F10602", "11F10403", "11F10204", "11F10402", "11F10301", "11F10303", "11C20401", "11C20404", "11C20402",
            "11C20602", "11C20403", "11C20601", "11C20301", "11C20302", "11C20303", "11C20101", "11C20102", "11C20103", "11C20104", "11C20201", "11C20202", "11C20502",
            "11C20501", "11C10301", "11C10304", "11C10303", "11C10102", "11C10101", "11C10103", "11C10201", "11C10202", "11C10302", "11C10403", "11C10402", "11C10401",
            "11D10101", "11D10102", "11D10201", "11D10202", "11D10301", "11D10302", "11D10401", "11D10402", "11D10501", "11D10502", "11D10503", "11D20201", "11D20401",
            "11D20402", "11D20403", "11D20501", "11D20601", "11D20602", "11D20301", "11G00201", "11G00401", "11G00101", "11G00501", "11G00302", "11G00601", "11G00800"};*/
    //기온조회코드
    String[] regId2 = {"11B10101", "11H20201", "11H10701", "11B20201", "11B20702", "11C20401", "11H20101",
            "11B20605", "11D20501", "11C10101", "11C20301", "11F10201", "11F20503", "11H10201", "11H20304", "11G00201", "11C20404"};
    //에어코리아 요구파람
    String regionNames[] = {"서울", "부산", "대구", "인천", "광주", "대전", "울산", "경기", "강원", "충북", "충남", "전북", "전남", "경북", "경남", "제주", "세종"};
    //stationName받을 array
    ArrayList<String> dataArr = new ArrayList<String>();
    //미세먼지 값 받을 array
    ArrayList<String> pm10Values = new ArrayList<String>(), pm25Values = new ArrayList<String>();
    //미세먼지 등급 받을 array
    ArrayList<String> pm10Grades = new ArrayList<String>(), pm25Grades = new ArrayList<String>();
    //기온 받을 array
    ArrayList<Integer> taMins = new ArrayList<>(), taMaxs = new ArrayList<>();
    String time, yTime;
    //1:좋음, 2 : 보통 , 3: 나쁨 , 4: 매우나쁨
    int pm10Grade = 0, pm25Grade = 0;
    ArrayAdapter<String> newsAdapter;
    SearchView.SearchAutoComplete searchAutoComplete;
    //차트뷰 첫번째 데이터 담을 array
    ArrayList<Entry> entries = new ArrayList<>();
    //차트뷰 두번째 데이터 담을 array
    ArrayList<Entry> entries1 = new ArrayList<>();
    LineData data;
    LineChart lineChart;
    ProgressBar pbMise, pbChoMise;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Intent intent = new Intent(this , LoadingActivity.class);
        startActivity(intent);

        spnRegion = (Spinner) findViewById(R.id.spnRegion);
        txtCity = (TextView) findViewById(R.id.txtCity);
        txtDust = (TextView) findViewById(R.id.txtDust);
        txtPm10 = (TextView) findViewById(R.id.txtPm10);
        txtPm2_5 = (TextView) findViewById(R.id.txtPm2_5);
        txtTemp = (TextView)findViewById(R.id.txtTemp);
        layoutToday = (LinearLayout) findViewById(R.id.tabToday);
        ivWeather = (ImageView) findViewById(R.id.ivWeather);
        ivFace = (ImageView) findViewById(R.id.ivFace);
        lineChart = (LineChart) findViewById(R.id.chart);
        pbMise = (ProgressBar) findViewById(R.id.pbMise);
        pbChoMise = (ProgressBar) findViewById(R.id.pbChoMise);

        TabHost tabHost = (TabHost) findViewById(R.id.tabHost);
        tabHost.setup();

        TabHost.TabSpec ts1 = tabHost.newTabSpec("Tab Spec 1");
        ts1.setContent(R.id.tabToday);
        ts1.setIndicator("오늘날씨");
        tabHost.addTab(ts1);

        TabHost.TabSpec ts2 = tabHost.newTabSpec("Tab Spec 2");
        ts2.setContent(R.id.tabWeek);
        ts2.setIndicator("예상기온");
        tabHost.addTab(ts2);

        tabHost.setOnTabChangedListener(new TabHost.OnTabChangeListener() {
            @Override
            public void onTabChanged(String tabId) {

                entries.add(new Entry(0, taMaxs.get(0)));
                entries.add(new Entry(1, taMaxs.get(1)));
                entries.add(new Entry(2, taMaxs.get(2)));
                entries.add(new Entry(3, taMaxs.get(3)));
                entries.add(new Entry(4, taMaxs.get(4)));


                entries1.add(new Entry(0, taMins.get(0)));
                entries1.add(new Entry(1, taMins.get(1)));
                entries1.add(new Entry(2, taMins.get(2)));
                entries1.add(new Entry(3, taMins.get(3)));
                entries1.add(new Entry(4, taMins.get(4)));

                LineDataSet dataSet = new LineDataSet(entries, "# 최고 기온");
                LineDataSet dataSet1 = new LineDataSet(entries1, "# 최저 기온");

                data = new LineData(dataSet, dataSet1);
                dataSet.setColors(ColorTemplate.COLORFUL_COLORS);
                dataSet.setFillDrawable(new BitmapDrawable(String.valueOf(R.drawable.toolbar_find)));
                dataSet1.setColors(ColorTemplate.PASTEL_COLORS);
                lineChart.setData(data);
                lineChart.notifyDataSetChanged();
            }
        });


        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmm");
        time = sdf.format(calendar.getTime());
        calendar.add(Calendar.DATE, -1);
        yTime = sdf.format(calendar.getTime());
        Log.d("시간 :", time);

        regions.addAll(Arrays.asList(regionNames));


        ArrayAdapter<String> spnAdapter = new ArrayAdapter<String>(this, R.layout.spinner, regionNames);
        spnAdapter.setDropDownViewResource(R.layout.spinnerstyle);
        spnRegion.setAdapter(spnAdapter);
        spnRegion.setGravity(Gravity.CENTER);
        spnRegion.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String queryStr = (String) parent.getItemAtPosition(position);
                Log.d("스피너 스트링 : ", queryStr);
                response(queryStr);

                int i = regions.indexOf((String) parent.getItemAtPosition(position));
                responseAtTime(regId[i], regId2[i], time, yTime);

                newsAdapter = new ArrayAdapter<String>(MainActivity.this, android.R.layout.simple_dropdown_item_1line, dataArr);
                searchAutoComplete.setAdapter(newsAdapter);

                entries.clear();
                entries1.clear();
                taMaxs.clear();
                taMins.clear();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                response("서울");
            }
        });


        pbMise.setMax(300);
        pbChoMise.setMax(200);


        /*dataset.setDrawCubic(true); //선 둥글게 만들기
        dataset.setDrawFilled(true); //그래프 밑부분 색칠*/

        XAxis xAxis = lineChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.TOP);
        xAxis.setLabelCount(5);
        xAxis.setTextColor(Color.BLACK);
        xAxis.enableGridDashedLine(8, 24, 0);
        YAxis yLAxis = lineChart.getAxisLeft();
        yLAxis.setTextColor(Color.BLACK);

        YAxis yRAxis = lineChart.getAxisRight();
        yRAxis.setDrawLabels(false);
        yRAxis.setDrawAxisLine(false);
        yRAxis.setDrawGridLines(false);

        lineChart.animateY(5000);
        lineChart.setBackgroundColor(Color.rgb(246, 230, 203));

    }

    public void responseAtTime(String regId, String regId2, String time, String yTime) {
        if (Integer.parseInt(time.substring(8, time.length())) > 1800) {
            response(operation, regId, regId2, time.substring(0, 8) + "1800");
        } else if (Integer.parseInt(time.substring(8, time.length())) >= 600 && Integer.parseInt(time.substring(8, time.length())) <= 1800) {
            response(operation, regId, regId2, time.substring(0, 8) + "0600");
        } else if (Integer.parseInt(yTime.substring(8, time.length())) < 600) {
            response(operation, regId, regId2, yTime.substring(0, 8) + "1800");
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.actionbar_actions, menu);

        SearchView searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
        searchView.setMaxWidth(Integer.MAX_VALUE);
        searchView.setQueryHint("지역명을 검색하세요.");

        searchAutoComplete = (SearchView.SearchAutoComplete) searchView.findViewById(android.support.v7.appcompat.R.id.search_src_text);

        searchAutoComplete.setTextColor(Color.WHITE);
        searchAutoComplete.setDropDownBackgroundResource(R.drawable.sunnyday);

        // 새 ArrayAdapter를 만들고 데이터를 추가하여 자동 완료 객체를 검색합니다.

        /*newsAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, dataArr);
        searchAutoComplete.setAdapter(newsAdapter);*/

        searchAutoComplete.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String queryString = (String) adapterView.getItemAtPosition(i);
                txtPm10.setText("미세먼지(pm10) 농도 : " + pm10Values.get(dataArr.indexOf(queryString)) + " ㎍/㎥");
                txtPm2_5.setText("초미세먼지(pm25) 농도 : " + pm25Values.get(dataArr.indexOf(queryString)) + " ㎍/㎥");

                try {
                    if (pm10Grades.get(dataArr.indexOf(queryString)).equals("-")) {
                        pm10Grade = 0;
                    } else {
                        pm10Grade = Integer.parseInt(pm10Grades.get(dataArr.indexOf(queryString)));
                    }
                    if (pm25Grades.get(dataArr.indexOf(queryString)).equals("-")) {
                        pm25Grade = 0;
                    } else {
                        pm25Grade = Integer.parseInt(pm25Grades.get(dataArr.indexOf(queryString)));
                    }
                } catch (NullPointerException nE) {
                    nE.printStackTrace();
                    pm25Grade = 0;
                    pm10Grade = 0;
                }
                switchGrade(pm10Grade, pm25Grade);
                txtCity.setText(queryString.substring(2));
                String pm10 = pm10Values.get(dataArr.indexOf(queryString));
                String pm2_5 = pm25Values.get(dataArr.indexOf(queryString));
                if (pm10.equals("-") || pm10.equals("")) {
                    pbMise.setProgress(0);
                } else {
                    pbMise.setProgress(Integer.parseInt(pm10Values.get(dataArr.indexOf(queryString))));
                }
                if (pm2_5.equals("-") || pm2_5.equals("")) {
                    pbChoMise.setProgress(0);
                } else {
                    pbChoMise.setProgress(Integer.parseInt(pm25Values.get(dataArr.indexOf(queryString))));
                }

                searchAutoComplete.clearFocus();
            }
        });

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return true;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                //오토컴플리먼트 됐으니까 어댑터만 손봐주면될것같음

                return true;
            }
        });
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        if (null != searchManager) {
            searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        }
        searchView.setIconifiedByDefault(true);


        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_search:

                return true;
            default:
                return onOptionsItemSelected(item);
        }

    }

    public static Retrofit retroitBuild(String uri) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(uri)
                .addConverterFactory(SimpleXmlConverterFactory.createNonStrict())
                .build();
        return retrofit;
    }

    public void response(String[] operation, String regId, String regId2, final String date) {

        try {
            WeatherForecastService service1 = retroitBuild(weatherService).create(WeatherForecastService.class);


            Call<Weather> call1 = service1.getWeatherForcast(operation[0], serviceKey, regId, date);
            Call<Weather> call2 = service1.getWeatherForcast(operation[1], serviceKey, regId2, date);


            call1.enqueue(new Callback<Weather>() {
                @Override
                public void onResponse(Call<Weather> call, Response<Weather> response) {
                    if (response.isSuccessful()) {
                        weather = response.body();
                        Log.d("3차 확인 :", weather.body.items.itemlist.get(0).getWf3Am());
                        String dayWt;
                        if (Integer.parseInt(date.substring(8)) > 1200) {
                            dayWt = weather.body.items.itemlist.get(0).getWf3Pm();

                            if (dayWt.indexOf("맑음") != -1) {
                                ivWeather.setImageResource(R.drawable.sun1);
                            } else if (dayWt.indexOf("구름") != -1) {
                                ivWeather.setImageResource(R.drawable.cloudy2);
                            } else if (dayWt.indexOf("비") != -1) {
                                ivWeather.setImageResource(R.drawable.rain);
                            } else if (dayWt.indexOf("눈") != -1) {
                                ivWeather.setImageResource(R.drawable.snow);
                            } else if (dayWt.indexOf("흐") != -1) {
                                ivWeather.setImageResource(R.drawable.cloudy2);
                            }
                        } else {
                            dayWt = weather.body.items.itemlist.get(0).getWf3Am();

                            if (dayWt.indexOf("맑음") != -1) {
                                ivWeather.setImageResource(R.drawable.sun1);
                            } else if (dayWt.indexOf("구름") != -1) {
                                ivWeather.setImageResource(R.drawable.cloudy2);
                            } else if (dayWt.indexOf("비") != -1) {
                                ivWeather.setImageResource(R.drawable.rain);
                            } else if (dayWt.indexOf("눈") != -1) {
                                ivWeather.setImageResource(R.drawable.snow);
                            } else if (dayWt.indexOf("흐") != -1) {
                                ivWeather.setImageResource(R.drawable.cloudy2);
                            }
                        }


                    } else {
                        Toast.makeText(MainActivity.this, "no", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<Weather> call, Throwable t) {
                    Log.d("response Fail 2", t.toString());
                }
            });
            call2.enqueue(new Callback<Weather>() {
                @Override
                public void onResponse(Call<Weather> call, Response<Weather> response) {
                    if (response.isSuccessful()) {
                        weather = response.body();
                        Log.d("4차 확인 :", String.valueOf(weather.body.items.itemlist.get(0).getTaMax3()));

                        taMaxs.add(weather.body.items.itemlist.get(0).getTaMax3());
                        taMaxs.add(weather.body.items.itemlist.get(0).getTaMax4());
                        taMaxs.add(weather.body.items.itemlist.get(0).getTaMax5());
                        taMaxs.add(weather.body.items.itemlist.get(0).getTaMax6());
                        taMaxs.add(weather.body.items.itemlist.get(0).getTaMax7());

                        taMins.add(weather.body.items.itemlist.get(0).getTaMin3());
                        taMins.add(weather.body.items.itemlist.get(0).getTaMin4());
                        taMins.add(weather.body.items.itemlist.get(0).getTaMin5());
                        taMins.add(weather.body.items.itemlist.get(0).getTaMin6());
                        taMins.add(weather.body.items.itemlist.get(0).getTaMin7());

                        txtTemp.setText(weather.body.items.itemlist.get(0).getTaMax3()+" ˚C \n / \n"+weather.body.items.itemlist.get(0).getTaMin3()+" ˚C");
                    } else {

                    }
                }

                @Override
                public void onFailure(Call<Weather> call, Throwable t) {
                    Log.d("response Fail 3", t.toString());
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    public void response(final String sidoName) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                dataArr.clear();
                pm10Grades.clear();
                pm10Values.clear();
                pm25Grades.clear();
                pm25Values.clear();
                WeatherForecastService service = retroitBuild(airKoreaService).create(WeatherForecastService.class);
                Log.d("시도 이름 : ", sidoName);
                Call<AirKorea> call = service.getDustInfo(serviceKey, sidoName, 100, 1.3f);
                call.enqueue(new Callback<AirKorea>() {
                    @Override
                    public void onResponse(Call<AirKorea> call, Response<AirKorea> response) {
                        if (response.isSuccessful()) {
                            airKorea = response.body();
                            Log.d("json 로그 : ", new Gson().toJson(response.body()));
                            AirKorea.item airk = airKorea.body.items.itemlist.get(0);
                            String pm10 = airk.pm10Value;
                            String pm2_5 = airk.pm25Value;
                            String grade10 = airk.pm10Grade, grade2_5 = airk.pm25Grade;
                            txtCity.setText(airk.getStationName());
                            txtPm10.setText("미세먼지(pm10) 농도 : " + pm10 + " ㎍/㎥");
                            txtPm2_5.setText("초미세먼지(pm25) 농도 : " + pm2_5 + " ㎍/㎥");
                            if (grade10 == null && grade2_5 == null) {
                                switchGrade(0, 0);
                            } else if (grade10 == null) {
                                switchGrade(0, Integer.parseInt(grade2_5));
                            } else if (grade2_5 == null) {
                                switchGrade(Integer.parseInt(grade10), 0);
                            } else {
                                switchGrade(Integer.parseInt(grade10), Integer.parseInt(grade2_5));
                            }

                            if (pm10.equals("-") || pm10.equals("")) {
                                pbMise.setProgress(0);
                            } else {
                                pbMise.setProgress(Integer.parseInt(pm10));
                            }
                            if (pm2_5.equals("-") || pm2_5.equals("") || airKorea.body.items.itemlist.get(0).getPm2_5Value() == null) {
                                pbChoMise.setProgress(0);
                            } else {
                                pbChoMise.setProgress(Integer.parseInt(pm2_5));
                            }
                            int i = 0;
                            while (i < airKorea.body.items.itemlist.size()) {
                                if (airKorea.body.items.itemlist.get(i).getStationName().equals("") || airKorea.body.items.itemlist.get(i).getStationName().equals(null)) {
                                    return;
                                }
                                dataArr.add(sidoName + " " + airKorea.body.items.itemlist.get(i).getStationName());
                                pm10Grades.add(airKorea.body.items.itemlist.get(i).getPm10Grade());
                                pm10Values.add(airKorea.body.items.itemlist.get(i).getPm10Value());
                                pm25Grades.add(airKorea.body.items.itemlist.get(i).getPm2_5Grade());
                                pm25Values.add(airKorea.body.items.itemlist.get(i).getPm2_5Value());
                                Log.d("데이터 확인", dataArr.get(i));
                                i++;
                            }


                        } else {
                            Toast.makeText(MainActivity.this, "연결오류", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<AirKorea> call, Throwable t) {
                        Log.d("response Fail 1 : ", t.getMessage());
                        Toast.makeText(MainActivity.this, "연결실패", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

    }

    public void switchGrade(final int grade10, final int grade2_5) {
        Log.d("addadad", grade10 + " " + grade2_5);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (grade10 < 2 && grade2_5 < 2) {
                    ivFace.setImageResource(R.drawable.smileface);
                    layoutToday.setBackgroundColor(Color.rgb(0, 87, 75));
                    spnRegion.setBackgroundColor(Color.rgb(0, 87, 75));
                    getSupportActionBar().setBackgroundDrawable(new ColorDrawable(0xFF00574B));
                    txtDust.setText("좋음");
                } else if ((grade10 < 4 && grade2_5 < 4) && (grade10 > 1 || grade2_5 > 1)) {
                    if (grade10 == 3 || grade2_5 == 3) {
                        ivFace.setImageResource(R.drawable.sadface);
                        layoutToday.setBackgroundColor(Color.rgb(253, 69, 28));
                        spnRegion.setBackgroundColor(Color.rgb(253, 69, 28));
                        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(0xFFFD451C));
                        txtDust.setText("나쁨");
                    } else {
                        ivFace.setImageResource(R.drawable.soullessface);
                        layoutToday.setBackgroundColor(Color.rgb(248, 183, 24));
                        spnRegion.setBackgroundColor(Color.rgb(248, 183, 24));
                        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(0xFFF8B718));
                        txtDust.setText("보통");
                    }
                } else {
                    ivFace.setImageResource(R.drawable.maskedface);
                    layoutToday.setBackgroundColor(Color.GRAY);
                    spnRegion.setBackgroundColor(Color.GRAY);
                    getSupportActionBar().setBackgroundDrawable(new ColorDrawable(0xFF6E6E6E));
                    txtDust.setText("매우 나쁨");
                }

            }
        });

    }

}
