package com.example.findelectriccarstation;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.ArrayList;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback {
    //API로부터 값을 받을 때 태그저장용
    boolean isStatId = false, isStatNm = false, isChgerId = false, isChgerType = false, isStat = false, isLat = false,
            isLng = false, isAddrDoro = false, isUseTime = false;
    //값저장용
    String statId = null, statNm = null, chgerId = null, chgerType = null, stat = null, lat = null, lng = null,
            addrDoro = null, useTime = null;
    //API키
    String key = "a5LQB3DrXgIE5J%2Bwaz4HpLmUqyroCcRREqjVXvIGqceLLrS52Ob2nNQLxQNjXngBm19%2FMnFCQ%2BjNdSI2LqCe0A%3D%3D";

    GPSinfo gps;
    boolean isAccessFineLocation = false;
    boolean isAccessCoarseLocation = false;
    boolean isPermission = false;
    final int PERMISSIONS_ACCESS_FINE_LOCATION = 1000;
    final int PERMISSIONS_ACCESS_COARSE_LOCATION = 1001;

    LatLng place;
    private GoogleMap mMap;

    XmlPullParserFactory parserFactory;
    XmlPullParser parser;
    URL url;
    //    Button btnMyPosition,btnShow;
    /*Spinner spnArea,spnDetail;*/
    String[] area = {"서울특별시", "부산광역시", "대구광역시", "인천광역시",
            "광주광역시", "대전광역시", "울산광역시", "세종특별자치시", "경기도", "강원도",
            "충청북도", "충청남도", "전라북도", "전라남도", "경상북도", "경상남도", "제주특별자치도"};
    ArrayList<String> detailArea = new ArrayList<>();
    //    ArrayList<String> detailArea2 = new ArrayList<>();
    ArrayList<String> names = new ArrayList<>();
    ArrayList<String> stats = new ArrayList<>();
    ArrayList<String> useTimes = new ArrayList<>();
    ArrayList<String> chgerTypes = new ArrayList<>();
    ArrayList<LatLng> places = new ArrayList<>();
//    ArrayList<LatLng> places2 = new ArrayList<>();

    LatLng position;
    AlertDialog.Builder dialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
//        btnMyPosition = (Button)findViewById(R.id.btnMyPosition);
       /* btnShow = (Button)findViewById(R.id.btnShow);
        spnArea = (Spinner)findViewById(R.id.spnArea);
        spnDetail = (Spinner)findViewById(R.id.spnDetail);*/

        Log.d("사이즈 : ", "" + detailArea.size());


        position = getIntent().getParcelableExtra("position");
        dialog = new AlertDialog.Builder(this);
        dialog.setIcon(R.drawable.plug1);
        /*//spnArea에 장착할 어댑터
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item
        ,area);
        spnArea.setAdapter(adapter);
        //spnArea의 아이템을 선택하면 spnDetail의 아이템이 변경되도록
        spnArea.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                detailArea2.clear();
                places2.clear();
                for(int y=0; y<detailArea.size(); y++){
                    if (detailArea.get(y).indexOf(area[i])!=-1){
                        detailArea2.add(detailArea.get(y));
                        places2.add(places.get(y));
                    }
                }
                //spnDetail에 장착할 어댑터
                ArrayAdapter<String> adapter1 = new ArrayAdapter<String>(MapsActivity.this,android.R.layout.simple_spinner_item
                        ,detailArea2);
                spnDetail.setAdapter(adapter1);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                detailArea2.clear();
                places2.clear();
                for(int y=0; y<detailArea.size(); y++){
                    if (detailArea.get(y).indexOf(area[0])!=-1){
                        detailArea2.add(detailArea.get(y));
                        places2.add(places.get(y));
                    }
                }
                //spnDetail에 장착할 어댑터
                ArrayAdapter<String> adapter1 = new ArrayAdapter<String>(MapsActivity.this,android.R.layout.simple_spinner_item
                        ,detailArea2);
                spnDetail.setAdapter(adapter1);
            }
        });
        //스피너에 지정된 장소로 카메라이동
        btnShow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String search = spnDetail.getSelectedItem().toString();
                if(detailArea2.get(spnDetail.getSelectedItemPosition()).equals(search)){
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(places2.get(spnDetail.getSelectedItemPosition()),15));
                }
            }
        });*/


        //메인 쓰레드에서 네트워크작업하면 에러떠서 쓰레드생성
        new Thread() {

            public void run() {
                int i = 0;
                try {
                    url = new URL("http://open.ev.or.kr:8080/openapi/services/rest/EvChargerService?serviceKey=" + key);
                    parserFactory = XmlPullParserFactory.newInstance();
                    parser = parserFactory.newPullParser();
                    parser.setInput(url.openStream(), null);

                    int parserEvent = parser.getEventType();

                    while (parserEvent != XmlPullParser.END_DOCUMENT) {
                        switch (parserEvent) {
                            case XmlPullParser.START_TAG:// 시작태그 만나면 실행
                                if (parser.getName().equals("statId")) {
                                    isStatId = true;
                                }
                                if (parser.getName().equals("statNm")) {
                                    isStatNm = true;
                                }
                                if (parser.getName().equals("chgerId")) {
                                    isChgerId = true;
                                }
                                if (parser.getName().equals("chgerType")) {
                                    isChgerType = true;
                                }
                                if (parser.getName().equals("stat")) {
                                    isStat = true;
                                }
                                if (parser.getName().equals("lat")) {
                                    isLat = true;
                                }
                                if (parser.getName().equals("lng")) {
                                    isLng = true;
                                }
                                if (parser.getName().equals("addrDoro")) {
                                    isAddrDoro = true;
                                }
                                if (parser.getName().equals("useTime")) {
                                    isUseTime = true;
                                }
                                if (parser.getName().equals("message")) {
                                    //Toast.makeText(this, "error", Toast.LENGTH_SHORT).show();
                                }
                                break;
                            case XmlPullParser.TEXT:
                                if (isStatId) {
                                    statId = parser.getText();
                                    isStatId = false;
                                }
                                if (isStatNm) {
                                    statNm = parser.getText();
                                    isStatNm = false;
                                }
                                if (isChgerId) {
                                    chgerId = parser.getText();
                                    isChgerId = false;
                                }
                                if (isChgerType) {
                                    chgerType = parser.getText();
                                    isChgerType = false;
                                }
                                if (isStat) {
                                    stat = parser.getText();
                                    isStat = false;
                                }
                                if (isLat) {
                                    lat = parser.getText();
                                    isLat = false;
                                }
                                if (isLng) {
                                    lng = parser.getText();
                                    isLng = false;
                                }
                                if (isAddrDoro) {
                                    addrDoro = parser.getText();
                                    isAddrDoro = false;
                                }
                                if (isUseTime) {
                                    useTime = parser.getText();
                                    isUseTime = false;
                                }
                                break;
                            case XmlPullParser.END_TAG:
                                if (parser.getName().equals("item")) {


                                    Bundle bun = new Bundle();

                                    bun.putString("markers", lat);
                                    bun.putString("markers2", lng);
                                    bun.putString("name", statNm);
                                    bun.putString("area", addrDoro);
                                    bun.putString("chgerType", chgerType);
                                    bun.putString("useTime", useTime);
                                    bun.putString("stat", stat);

                                    Message msg = handler.obtainMessage();
                                    msg.setData(bun);
                                    handler.sendMessage(msg);

                                }
                                break;
                        }
                        parserEvent = parser.next();
                    }
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (XmlPullParserException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }.start();


    }

    //핸들러로 받아서 메인으로 보냄
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            Bundle bun = msg.getData();
            String lat = bun.getString("markers");
            String lng = bun.getString("markers2");
            String name = bun.getString("name");
            names.add(bun.getString("name"));
            useTimes.add(bun.getString("useTime"));
            detailArea.add(bun.getString("area"));
            switch (bun.getString("chgerType")) {
                case "01":
                    chgerTypes.add("DC차데모");
                    break;
                case "03":
                    chgerTypes.add("DC차데모 + AC3상");
                    break;
                case "06":
                    chgerTypes.add("DC차데모+AC3상+DC콤보");
                    break;
                default:
                    chgerTypes.add("");
                    break;
            }
            switch (bun.getString("stat")) {
                case "1":
                    stats.add("통신이상");
                    break;
                case "2":
                    stats.add("충전대기");
                    break;
                case "3":
                    stats.add("충전중");
                    break;
                case "4":
                    stats.add("운영중지");
                    break;
                case "5":
                    stats.add("점검중");
                    break;
                default:
                    stats.add("");
                    break;

            }
            //Log.d("주소 :",addrDoro);
            place = new LatLng(Double.parseDouble(lat), Double.parseDouble(lng));
            //Log.d("마커1", " " + place);
            places.add(place);
            MarkerOptions markerOptions = new MarkerOptions();
            markerOptions.position(place).title(name);
            markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.plug1));
            mMap.addMarker(markerOptions);
        }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.actionbar_actions, menu);

        return true;
    }

    //현위치로 이동
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_location:
                gps = new GPSinfo(MapsActivity.this);
                double latitude = 0;
                double longitude = 0;
                if (gps.isGetLocation()) {
                    latitude = gps.getLatitude();
                    longitude = gps.getLongitude();
                } else {
                    // GPS 를 사용할수 없으므로
                    gps.showSettingsAlert();
                }
                Log.d("gps :", "위도 :" + latitude + "경도 :" + longitude);
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(latitude, longitude), 15));

                return true;

            default:

                return super.onOptionsItemSelected(item);
        }

    }

    //현위치와 목표위치 계산용메서드
    public static double CalculationByDistance(LatLng StartP, LatLng EndP) {
        int Radius = 6371;// radius of earth in Km
        double lat1 = StartP.latitude;
        double lat2 = EndP.latitude;
        double lon1 = StartP.longitude;
        double lon2 = EndP.longitude;
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat1))
                * Math.cos(Math.toRadians(lat2)) * Math.sin(dLon / 2)
                * Math.sin(dLon / 2);
        double c = 2 * Math.asin(Math.sqrt(a));
        double valueResult = Radius * c;
        double km = valueResult / 1;
        DecimalFormat newFormat = new DecimalFormat("###0.##");
        double kmInDec = Double.parseDouble(newFormat.format(km));


        return kmInDec;
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        gps = new GPSinfo(MapsActivity.this);
        double latitude = 0;
        double longitude = 0;
        if (gps.isGetLocation()) {
            latitude = gps.getLatitude();
            longitude = gps.getLongitude();
        } else {
            // GPS 를 사용할수 없으므로
            gps.showSettingsAlert();
        }
        Log.d("gps :", "위도 :" + latitude + "경도 :" + longitude);
        // mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(latitude, longitude), 15));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(position, 15));
        //마커클릭시 오차작은범위로 정보뜨워줌
        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                int i = 0;
                while (true) {
                    if (CalculationByDistance(marker.getPosition(), places.get(i)) < 0.01) {
                        dialog.setTitle(names.get(i));
                        dialog.setMessage("주소 : " + detailArea.get(i)
                                + "\n 이용시간 : " + useTimes.get(i) +
                                "\n 충전기 타입 : " + chgerTypes.get(i) +
                                "\n 충전기 상태 : " + stats.get(i));
                        dialog.setPositiveButton("확인", null);
                        dialog.show();

                        break;
                    } else {
                        i++;
                    }
                }
                return true;
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == PERMISSIONS_ACCESS_FINE_LOCATION
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

            isAccessFineLocation = true;

        } else if (requestCode == PERMISSIONS_ACCESS_COARSE_LOCATION
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

            isAccessCoarseLocation = true;
        }

        if (isAccessFineLocation && isAccessCoarseLocation) {
            isPermission = true;
        }

    }
}
