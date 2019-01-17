package com.example.pc.mypet;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class LivestockActivity extends AppCompatActivity {
    ListView listAnimal;
    TextView tvMoney, tvLevel;
    Button btnUpgrade,btnPrev;
    int Lv, milk, wool, egg, meat, pos , wich;
    MyListAdapter myListAdapter;
    myDBhelper myDBhelper;
    SQLiteDatabase sqLite;
    String money;
    List<Animal> animals;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_livestock);
        listAnimal = (ListView) findViewById(R.id.listAnimal);
        tvMoney = (TextView) findViewById(R.id.tvMoney);
        tvLevel = (TextView) findViewById(R.id.tvLevel);
        btnUpgrade = (Button) findViewById(R.id.btnUpgrade);
        btnPrev = (Button)findViewById(R.id.btnPrev);

        registerForContextMenu(listAnimal);

        myDBhelper = new myDBhelper(this);
        sqLite = myDBhelper.getReadableDatabase();
        Cursor cursor1 = sqLite.rawQuery("SELECT * FROM myWallet ", null);
        cursor1.moveToFirst();
        money = cursor1.getString(1);
        Lv = cursor1.getInt(2) * 10;
        cursor1.close();
        sqLite.close();
        tvMoney.setText(money);
       // tvLevel.setText("목장 Lv." + Lv / 10);

       /* btnUpgrade.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sqLite = myDBhelper.getWritableDatabase();
                int sum = Integer.parseInt(money) - 30000000;
                if (sum > 0) {
                    if (Lv < 40) {
                        sqLite.execSQL("UPDATE myWallet SET lv = " + ((Lv / 10) + 1) + "");
                        sqLite.execSQL("UPDATE myWallet SET money = " + sum + "");
                    } else {
                        Toast.makeText(LivestockActivity.this, "목장레벨이 최대입니다.", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(LivestockActivity.this, "잔액이 부족합니다.", Toast.LENGTH_SHORT).show();
                }
                sqLite.close();
                sqLite = myDBhelper.getReadableDatabase();
                Cursor cursor2 = sqLite.rawQuery("SELECT money FROM myWallet ", null);
                cursor2.moveToFirst();
                money = cursor2.getString(0);
                cursor2.close();
                sqLite.close();
                //tvLevel.setText("목장 Lv." + Lv / 10);
                tvMoney.setText(money);
            }
        });*/

        animals = new ArrayList<>();
        Intent intent = getIntent();
        pos = intent.getIntExtra("chk", 0);

        sqLite = myDBhelper.getReadableDatabase();

        Cursor cursor;
        switch (pos) {
            case 0:
                try {
                    cursor = sqLite.rawQuery("SELECT * FROM animal WHERE kind = '소';", null);
                    cursor.moveToFirst();
                    animals.add(new Animal(values(cursor.getDouble(2), cursor.getDouble(3)),
                            cursor.getString(1), cursor.getInt(0)));
                    while (cursor.moveToNext()) {
                        animals.add(new Animal(values(cursor.getDouble(2), cursor.getDouble(3)),
                                cursor.getString(1), cursor.getInt(0)));
                    }
                    cursor.close();
                } catch (Exception e) {
                    Toast.makeText(this, "자료가 없습니다.", Toast.LENGTH_SHORT).show();
                }
                break;

            case 1://양
                try {

                    cursor = sqLite.rawQuery("SELECT * FROM animal WHERE kind = '양';", null);
                    cursor.moveToFirst();
                    animals.add(new Animal(values(cursor.getDouble(2), cursor.getDouble(3)),
                            cursor.getString(1), cursor.getInt(0)));
                    while (cursor.moveToNext()) {
                        animals.add(new Animal(values(cursor.getDouble(2), cursor.getDouble(3)),
                                cursor.getString(1), cursor.getInt(0)));
                        }
                    cursor.close();
                } catch (Exception e) {
                    Toast.makeText(this, "자료가 없습니다.", Toast.LENGTH_SHORT).show();
                }
                break;
            case 2:
                try {

                    cursor = sqLite.rawQuery("SELECT * FROM animal WHERE kind = '돼지';", null);
                    cursor.moveToFirst();
                    animals.add(new Animal(values(cursor.getDouble(2), cursor.getDouble(3)),
                            cursor.getString(1), cursor.getInt(0)));
                    while (cursor.moveToNext()) {
                        animals.add(new Animal(values(cursor.getDouble(2), cursor.getDouble(3)),
                                cursor.getString(1), cursor.getInt(0)));
                        }
                    cursor.close();
                } catch (Exception e) {
                    Toast.makeText(this, "자료가 없습니다.", Toast.LENGTH_SHORT).show();
                }
                break;
            case 3:
                try {

                    cursor = sqLite.rawQuery("SELECT * FROM animal WHERE kind = '닭';", null);
                    cursor.moveToFirst();
                    animals.add(new Animal(values(cursor.getDouble(2), cursor.getDouble(3)),
                            cursor.getString(1), cursor.getInt(0)));
                    while (cursor.moveToNext()) {
                        animals.add(new Animal(values(cursor.getDouble(2), cursor.getDouble(3)),
                                cursor.getString(1), cursor.getInt(0)));

                    }
                    cursor.close();
                } catch (Exception e) {
                    Toast.makeText(this, "자료가 없습니다.", Toast.LENGTH_SHORT).show();
                }
                break;
        }
        sqLite.close();
        myListAdapter = new MyListAdapter(this, animals);
        listAnimal.setAdapter(myListAdapter);
        String kind = pos==0?"소":pos==1?"양":pos==2?"돼지":"닭";
        tvLevel.setText("동물 : "+kind+ myListAdapter.getCount()+" 마리");


        btnPrev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent1= new Intent(getApplicationContext(),MainActivity.class);
                setResult(RESULT_OK,intent1);
                finish();
            }
        });

    }


    public class MyListAdapter extends BaseAdapter {
        private Context context;
        private List<Animal> itemDataList;
        private LayoutInflater minflater;
        Animal itemData;
        TextView tvPersonalInfo,tvPersonalStatus;

        public MyListAdapter(Context c, List<Animal> itemDataList) {
            context = c;
            this.minflater = LayoutInflater.from(c);
            this.itemDataList = itemDataList;
        }

        @Override
        public int getCount() {
            return itemDataList.size();
        }

        @Override
        public Object getItem(int position) {
            return itemDataList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            View itemView = convertView;

            if (itemView == null) {
                itemView = minflater.inflate(R.layout.listview2, null);
            }
            ImageView ivPersonal = (ImageView) itemView.findViewById(R.id.ivPersonal);
            tvPersonalInfo = (TextView) itemView.findViewById(R.id.tvPersonalInfo);
            tvPersonalStatus = (TextView) itemView.findViewById(R.id.tvPersonalStatus);
            ImageButton btnFeed = (ImageButton) itemView.findViewById(R.id.btnFeed);
            ImageButton btnSell = (ImageButton) itemView.findViewById(R.id.btnSell);

            itemData = itemDataList.get(position);
            ivPersonal.setImageResource(itemData.getImgID());
            tvPersonalInfo.setText(itemData.getInfos());
            tvPersonalStatus.setText(itemData.getStatus());

            wich = position;
            btnFeed.setTag(position);
            btnFeed.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                        animals.get(position).feed();
                        sqLite = myDBhelper.getWritableDatabase();
                        sqLite.execSQL("UPDATE animal SET kgs = " + animals.get(position).kgs
                                + " WHERE num = " + animals.get(position).num);
                        sqLite.execSQL("UPDATE animal SET price = " + animals.get(position).price
                                + " WHERE num = " + animals.get(position).num);
                        Log.d("num", animals.get(position).num + "num");
                        sqLite.close();
                        animals.clear();

                        sqLite = myDBhelper.getReadableDatabase();

                        Cursor cursor;
                        switch (pos) {
                            case 0:
                                try {
                                    cursor = sqLite.rawQuery("SELECT * FROM animal WHERE kind = '소';", null);
                                    cursor.moveToFirst();
                                    animals.add(new Animal(values(cursor.getDouble(2), cursor.getDouble(3)),
                                            cursor.getString(1), cursor.getInt(0)));
                                    while (cursor.moveToNext()) {
                                        animals.add(new Animal(values(cursor.getDouble(2), cursor.getDouble(3)),
                                                cursor.getString(1), cursor.getInt(0)));
                                    }
                                    cursor.close();
                                } catch (Exception e) {
                                    Toast.makeText(LivestockActivity.this, "자료가 없습니다.", Toast.LENGTH_SHORT).show();
                                }
                                break;

                            case 1://양
                                try {

                                    cursor = sqLite.rawQuery("SELECT * FROM animal WHERE kind = '양';", null);
                                    cursor.moveToFirst();
                                    animals.add(new Animal(values(cursor.getDouble(2), cursor.getDouble(3)),
                                            cursor.getString(1), cursor.getInt(0)));
                                    while (cursor.moveToNext()) {
                                        animals.add(new Animal(values(cursor.getDouble(2), cursor.getDouble(3)),
                                                cursor.getString(1), cursor.getInt(0)));
                                    }
                                    cursor.close();
                                } catch (Exception e) {
                                    Toast.makeText(LivestockActivity.this, "자료가 없습니다.", Toast.LENGTH_SHORT).show();
                                }
                                break;
                            case 2:
                                try {

                                    cursor = sqLite.rawQuery("SELECT * FROM animal WHERE kind = '돼지';", null);
                                    cursor.moveToFirst();
                                    animals.add(new Animal(values(cursor.getDouble(2), cursor.getDouble(3)),
                                            cursor.getString(1), cursor.getInt(0)));
                                    while (cursor.moveToNext()) {
                                        animals.add(new Animal(values(cursor.getDouble(2), cursor.getDouble(3)),
                                                cursor.getString(1), cursor.getInt(0)));
                                    }
                                    cursor.close();
                                } catch (Exception e) {
                                    Toast.makeText(LivestockActivity.this, "자료가 없습니다.", Toast.LENGTH_SHORT).show();
                                }
                                break;
                            case 3:
                                try {

                                    cursor = sqLite.rawQuery("SELECT * FROM animal WHERE kind = '닭';", null);
                                    cursor.moveToFirst();
                                    animals.add(new Animal(values(cursor.getDouble(2), cursor.getDouble(3)),
                                            cursor.getString(1), cursor.getInt(0)));
                                    while (cursor.moveToNext()) {
                                        animals.add(new Animal(values(cursor.getDouble(2), cursor.getDouble(3)),
                                                cursor.getString(1), cursor.getInt(0)));

                                    }
                                    cursor.close();
                                } catch (Exception e) {
                                    Toast.makeText(LivestockActivity.this, "자료가 없습니다.", Toast.LENGTH_SHORT).show();
                                }
                                break;
                        }
                        sqLite.close();

                        myListAdapter.notifyDataSetChanged();


                        Toast.makeText(LivestockActivity.this, "아직 실행할수 없습니다.", Toast.LENGTH_SHORT).show();



                }
            });

            btnSell.setTag(position);
            btnSell.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    sqLite = myDBhelper.getReadableDatabase();
                    Cursor cursor = sqLite.rawQuery("SELECT money FROM myWallet ", null);
                    cursor.moveToFirst();
                    String money = cursor.getString(0);
                    cursor.close();
                    sqLite.close();
                    sqLite = myDBhelper.getWritableDatabase();
                    int sum =(Integer.parseInt(money) + animals.get(position).price);
                    sqLite.execSQL("UPDATE myWallet SET money = " + sum + "");
                    sqLite.execSQL("DELETE from animal where num = " + animals.get(position).num + "");
                    sqLite.close();
                    tvMoney.setText(String.valueOf(sum));
                    animals.remove(position);
                    tvLevel.setText("동물 : "+itemData.kind+ myListAdapter.getCount()+" 마리");
                    myListAdapter.notifyDataSetChanged();
                }
            });

            ivPersonal.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Cursor cursor;
                    switch (pos) {
                        case 0:
                            sqLite = myDBhelper.getReadableDatabase();
                            cursor = sqLite.rawQuery("SELECT milk FROM stuff", null);
                            cursor.moveToFirst();
                            milk = cursor.getInt(0);
                            cursor.close();
                            sqLite.close();
                            sqLite = myDBhelper.getWritableDatabase();
                            sqLite.execSQL("UPDATE stuff SET milk = " + (milk+1));
                            sqLite.close();

                            Toast.makeText(LivestockActivity.this, "우유를 짰습니다.", Toast.LENGTH_SHORT).show();
                            break;
                        case 1:
                            sqLite = myDBhelper.getReadableDatabase();
                            cursor = sqLite.rawQuery("SELECT wool FROM stuff", null);
                            cursor.moveToFirst();
                            wool = cursor.getInt(0);
                            cursor.close();
                            sqLite.close();
                            sqLite = myDBhelper.getWritableDatabase();
                            sqLite.execSQL("UPDATE stuff SET wool = " + (wool+1));
                            sqLite.close();

                            Toast.makeText(LivestockActivity.this, "양털을 깎았습니다.", Toast.LENGTH_SHORT).show();
                            break;
                        case 2:
                            sqLite = myDBhelper.getReadableDatabase();
                            cursor = sqLite.rawQuery("SELECT meat FROM stuff", null);
                            cursor.moveToFirst();
                            meat = cursor.getInt(0);
                            cursor.close();
                            sqLite.close();
                            sqLite = myDBhelper.getWritableDatabase();
                            sqLite.execSQL("UPDATE stuff SET meat = " + (meat+1));
                            sqLite.close();

                            Toast.makeText(LivestockActivity.this, "고기를 얻었습니다.", Toast.LENGTH_SHORT).show();
                            break;
                        case 3:
                            sqLite = myDBhelper.getReadableDatabase();
                            cursor = sqLite.rawQuery("SELECT egg FROM stuff", null);
                            cursor.moveToFirst();
                            egg = cursor.getInt(0);
                            cursor.close();
                            sqLite.close();
                            sqLite = myDBhelper.getWritableDatabase();
                            sqLite.execSQL("UPDATE stuff SET egg = " + (egg+1));
                            sqLite.close();

                            Toast.makeText(LivestockActivity.this, "달걀을 얻었습니다.", Toast.LENGTH_SHORT).show();
                            break;
                    }
                }
            });
            return itemView;
        }
    }



    public double[] values(double age, double kgs) {
        double[] value = {age, kgs};
        return value;
    }
}