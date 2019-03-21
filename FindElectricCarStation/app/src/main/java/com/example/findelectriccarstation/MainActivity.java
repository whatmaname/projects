package com.example.findelectriccarstation;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.simplexml.SimpleXmlConverterFactory;

public class MainActivity extends AppCompatActivity {
    //검색용 edittext
    EditText edtSearch;
    ListView list_region;
    //Retrofit2에서 response.body 받을 클래스
    StationInfo stationInfo;
    //Adapter에 장착할 배열
    ArrayList<StationInfos> stationInfos = new ArrayList<>();
    StationAdapter stationAdapter;
    // 리스트 아이템 클릭시 해당 아이템 위,경도 저장할 LatLng
    LatLng latLng;
    GPSinfo gps;
    // 전기차 주유소 정보 API key
    static String key = "a5LQB3DrXgIE5J%2Bwaz4HpLmUqyroCcRREqjVXvIGqceLLrS52Ob2nNQLxQNjXngBm19%2FMnFCQ%2BjNdSI2LqCe0A%3D%3D";
    ArrayList<LatLng> latLngs = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent intent = new Intent(this,SplashActivity.class);
        startActivity(intent);

        edtSearch = (EditText) findViewById(R.id.edtSearch);
        list_region = (ListView) findViewById(R.id.list_region);


        //xml로 받는 retrofit
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://open.ev.or.kr:8080/")
                .addConverterFactory(SimpleXmlConverterFactory.createNonStrict())
                .build();
        FindStationService service = retrofit.create(FindStationService.class);
        Call<StationInfo> call = service.getStationInfo(key);
        call.enqueue(new Callback<StationInfo>() {
            @Override
            public void onResponse(Call<StationInfo> call, Response<StationInfo> response) {
                if (response.isSuccessful()) {
                    stationInfo = response.body();
                    gps = new GPSinfo(MainActivity.this);
                    double latitude = 0;
                    double longitude = 0;
                    if (gps.isGetLocation()) {
                        latitude = gps.getLatitude();
                        longitude = gps.getLongitude();
                    } else {
                        // GPS 를 사용할수 없으므로
                        gps.showSettingsAlert();
                    }
                    LatLng latLng1 = new LatLng(latitude,longitude);
                    int i = 0;

                    while (i < stationInfo.body.items.itemlist.size()) {
                        StationInfo.Item item = stationInfo.body.items.itemlist.get(i);

                        latLngs.add(new LatLng(item.getLat(),item.getLng()));
                        stationInfos.add(new StationInfos(
                                item.getStationName(),
                                item.getAddr(),
                                CalculationByDistance(latLng1, latLngs.get(i)),
                                latLngs.get(i)
                        ));

                        i++;
                    }

                    Collections.sort(stationInfos, new Comparator<StationInfos>() {
                        @Override
                        public int compare(StationInfos o1, StationInfos o2) {
                            return Double.compare(o1.getDistance(),o2.getDistance());
                        }
                    });



                    stationAdapter = new StationAdapter(MainActivity.this, stationInfos);
                    list_region.setAdapter(stationAdapter);


                }
            }

            @Override
            public void onFailure(Call<StationInfo> call, Throwable t) {
                try{Log.d("연결 실패", t.getMessage());}
                catch (NullPointerException ne){
                    ne.printStackTrace();
                    Toast.makeText(MainActivity.this, "인터넷 연결을 확인해주세요", Toast.LENGTH_SHORT).show();
                }
            }
        });


        list_region.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                for (int i = 0; i<stationInfos.size() ; i++) {
                    if (stationInfos.get(i).equals(parent.getItemAtPosition(position))) {
                        latLng = stationInfos.get(i).getLatLng();
                        break;
                    }
                }

                Intent intent = new Intent(getApplicationContext(), MapsActivity.class);
                Bundle bundle = new Bundle();
                bundle.putParcelable("position",latLng);
                intent.putExtras(bundle);
                startActivity(intent);


            }
        });


        edtSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                stationAdapter.getFilter().filter(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


    }

    public class StationAdapter extends BaseAdapter implements Filterable {
        private ArrayList<StationInfos> originalData;
        private ArrayList<StationInfos> filteredData;
        private LayoutInflater mInflater;
        private ItemFilter mFilter = new ItemFilter();

        StationInfos stationInfos1;

        public StationAdapter(Context context, ArrayList<StationInfos> stationInfos) {
            filteredData = stationInfos;
            originalData = stationInfos;
            mInflater = LayoutInflater.from(context);
        }

        @Override
        public int getCount() {
            return filteredData.size();
        }

        @Override
        public Object getItem(int position) {
            return filteredData.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;

            if (convertView == null) {
                convertView = mInflater.inflate(R.layout.list_item, null);
                holder = new ViewHolder();

                holder.stationName = convertView.findViewById(R.id.txtStationName);
                holder.stationRegion = convertView.findViewById(R.id.txtStationReg);
                holder.img1 = convertView.findViewById(R.id.img1);
                holder.txtDistance = convertView.findViewById(R.id.txtDistance);
                holder.txtUnit = convertView.findViewById(R.id.txtUnit);

                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            stationInfos1 = filteredData.get(position);

            holder.stationName.setText(stationInfos1.getStationName());
            holder.stationRegion.setText(filteredData.get(position).getStationRegion());
            holder.img1.setImageResource(R.drawable.plug1);
            holder.txtUnit.setText(" km");
            holder.txtDistance.setText(String.valueOf(filteredData.get(position).getDistance()));

            return convertView;
        }

        class ViewHolder {
            TextView stationName;
            TextView stationRegion;
            ImageView img1;
            TextView txtDistance;
            TextView txtUnit;
        }

        @Override
        public Filter getFilter() {
            return mFilter;
        }

        private class ItemFilter extends Filter {


            public ItemFilter() {
                super();
            }

            @Override
            public CharSequence convertResultToString(Object resultValue) {


                return super.convertResultToString(resultValue);
            }

            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                String filterString = constraint.toString().toLowerCase();
                FilterResults results = new FilterResults();

                final ArrayList<StationInfos> list = originalData;

                int count = list.size();
                final ArrayList<StationInfos> nlist = new ArrayList<StationInfos>(count);

                StationInfos filterableString;

                for (int i = 0; i < count; i++) {
                    filterableString = list.get(i);
                    if (filterableString.getStationRegion().toLowerCase().contains(filterString)) {
                        nlist.add(filterableString);
                    }
                }

                results.values = nlist;
                results.count = nlist.size();

                return results;

            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                filteredData = (ArrayList<StationInfos>) results.values;
                notifyDataSetChanged();
            }
        }
    }

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
}
