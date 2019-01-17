package com.example.pc.mypet;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.CursorIndexOutOfBoundsException;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Date;
import java.text.Format;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    Button btnStuff, btnStatus, btnBuyd, btnPlus, btnMinus, btnCancel, btnCancel2, btnReset, btnSold;
    EditText edtBuy;
    ListView Livestock;
    TextView tvMoney, tvDialog, subDialog, tvStuff[] = new TextView[4], tvTime;
    int[] stuff = {R.id.tvMilk, R.id.tvWool, R.id.tvMeat, R.id.tvEgg};
    ImageView ivDialog;
    myDBhelper dBhelper;
    SQLiteDatabase sqlLite;
    List<Animal> animals = new ArrayList<>();
    View dialogView;
    View dialogView2;
    AlertDialog dialog;
    AlertDialog dialog2;


    MyListAdapter listAdapter;
    int plus;
    int pos, chk;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTitle("Livestock Simulator");

        dialog = new AlertDialog.Builder(MainActivity.this).create();

        dBhelper = new myDBhelper(this);
        dialogView = (View) View.inflate(MainActivity.this,
                R.layout.dialog, null);

        dialogView2 = (View) View.inflate(MainActivity.this,
                R.layout.dialog2, null);
        Livestock = (ListView) findViewById(R.id.Livestock);
        tvMoney = (TextView) findViewById(R.id.tvMoney);
        tvTime = (TextView) findViewById(R.id.tvTime);
        btnStatus = (Button) findViewById(R.id.btnStatus);
        btnStuff = (Button) findViewById(R.id.btnStuff);
        btnReset = (Button) findViewById(R.id.btnReset);



        for (int i = 0; i < stuff.length; i++) {
            tvStuff[i] = (TextView) dialogView2.findViewById(stuff[i]);
        }
        btnCancel2 = (Button) dialogView2.findViewById(R.id.btnCancel2);
        btnSold = (Button) dialogView2.findViewById(R.id.btnSold);
        dialog2 = new AlertDialog.Builder(MainActivity.this).create();
        dialog2.setView(dialogView2);
        dialog2.setTitle("생산물");

        sqlLite = dBhelper.getReadableDatabase();
        Cursor cursor = sqlLite.rawQuery("SELECT money FROM myWallet ", null);
        cursor.moveToFirst();
        String money = cursor.getString(0);
        cursor.close();
        tvMoney.setText(money);
        sqlLite.close();

        Button btnBuyd = (Button) dialogView.findViewById(R.id.btnBuyd);
        Button btnCancel = (Button) dialogView.findViewById(R.id.btnCancel);
        Button btnPlus = (Button) dialogView.findViewById(R.id.btnPlus);
        Button btnMinus = (Button) dialogView.findViewById(R.id.btnMinus);
        edtBuy = (EditText) dialogView.findViewById(R.id.edtBuy);
        ivDialog = (ImageView) dialogView.findViewById(R.id.ivdialog);
        tvDialog = (TextView) dialogView.findViewById(R.id.tvDialog);
        subDialog = (TextView) dialogView.findViewById(R.id.tvSubDialog);

        dialog.setView(dialogView);
        dialog.setTitle("구매창");

        String kinds[] = {"소", "양", "돼지", "닭"};
        for (int i = 0; i < kinds.length; i++) {
            animals.add(new Animal(objectValue(kinds[i]), kinds[i], i));
        }
        listAdapter = new MyListAdapter(this, animals);
        Livestock.setAdapter(listAdapter);


        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                plus = 0;
                edtBuy.setText(String.valueOf(plus));
                dialog.dismiss();
            }
        });

        plus = Integer.parseInt(edtBuy.getText().toString());
        btnPlus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                plus++;
                edtBuy.setText(String.valueOf(plus));
            }
        });
        btnMinus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                plus = (plus >= 0 ? plus - 1 : 0);
                edtBuy.setText(String.valueOf(plus));

            }
        });
        //구매 대화창 안에 구매버튼
        btnBuyd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                sqlLite = dBhelper.getReadableDatabase();
                try {
                    Cursor cursor1 = sqlLite.rawQuery("SELECT num FROM animal ORDER BY num desc LIMIT 1", null);
                    cursor1.moveToFirst();
                    int num = cursor1.getInt(0);
                    sqlLite.close();
                    cursor1.close();
                    sqlLite = dBhelper.getWritableDatabase();
                    int sum = Integer.parseInt(tvMoney.getText().toString()) - animals.get(pos).price * plus;
                    if (sum > 0) {
                        for (int i = 0; i < plus; i++) {
                            sqlLite.execSQL("INSERT INTO animal " +
                                    " VALUES(" + (num + i + 1) + ",'" + animals.get(pos).kind + "'," + animals.get(pos).age
                                    + "," + animals.get(pos).kgs
                                    + "," + animals.get(pos).price + ",'" + animals.get(pos).getMale() + "')");
                        }
                    } else {
                        Toast.makeText(MainActivity.this, "잔액이 부족합니다.", Toast.LENGTH_SHORT).show();
                    }
                    sqlLite.execSQL("UPDATE myWallet SET money = " + sum + ";");
                    sqlLite.close();
                    tvMoney.setText(String.valueOf(sum));
                } catch (CursorIndexOutOfBoundsException ce) {
                    sqlLite = dBhelper.getWritableDatabase();
                    int sum = Integer.parseInt(tvMoney.getText().toString()) - animals.get(pos).price * plus;
                    if (sum > 0) {
                        for (int i = 0; i < plus; i++) {
                            sqlLite.execSQL("INSERT INTO animal " +
                                    " VALUES(" + i + ",'" + animals.get(pos).kind + "'," + animals.get(pos).age
                                    + "," + animals.get(pos).kgs
                                    + "," + animals.get(pos).price + ",'" + animals.get(pos).getMale() + "')");
                        }
                        sqlLite.execSQL("UPDATE myWallet SET money = " + sum + ";");
                        sqlLite.close();
                        tvMoney.setText(String.valueOf(sum));
                    } else {
                        Toast.makeText(MainActivity.this, "잔액이 부족합니다.", Toast.LENGTH_SHORT).show();
                    }

                }
                Log.d("구매개수", plus + "마리 구매");
                plus = 0;
                edtBuy.setText(String.valueOf(plus));
                dialog.dismiss();

            }
        });

        btnStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), LivestockActivity.class);
                intent.putExtra("chk", chk);
                startActivityForResult(intent, 0);
            }
        });

        btnStuff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sqlLite = dBhelper.getReadableDatabase();
                Cursor cursor2 = sqlLite.rawQuery("SELECT * FROM stuff", null);
                int i = 0;
                try {
                    while (cursor2.moveToNext()) {
                        for (int o = 0; o < stuff.length; o++) {
                            tvStuff[i].setText(cursor2.getInt(i) + " 개");
                            i++;
                        }
                    }
                } catch (NullPointerException ne) {
                    ne.printStackTrace();
                }
                cursor2.close();
                sqlLite.close();

                dialog2.show();
            }
        });

        btnSold.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int length = tvStuff[0].getText().toString().length();
                int length2 = tvStuff[1].getText().toString().length();
                int length3 = tvStuff[2].getText().toString().length();
                int length4 = tvStuff[3].getText().toString().length();

                int sold = Integer.parseInt(tvStuff[0].getText().subSequence(0, length - 1).toString().trim()) * 1000 +
                        Integer.parseInt(tvStuff[1].getText().subSequence(0, length2 - 1).toString().trim()) * 1500 +
                        Integer.parseInt(tvStuff[2].getText().subSequence(0, length3 - 1).toString().trim()) * 1200 +
                        Integer.parseInt(tvStuff[3].getText().subSequence(0, length4 - 1).toString().trim()) * 500;

                sqlLite = dBhelper.getReadableDatabase();
                Cursor cursor = sqlLite.rawQuery("SELECT money FROM myWallet ", null);
                cursor.moveToFirst();
                String money = cursor.getString(0);
                cursor.close();
                sqlLite.close();

                String sum = String.valueOf(Integer.parseInt(money) + sold);

                sqlLite = dBhelper.getWritableDatabase();
                sqlLite.execSQL("UPDATE stuff SET milk = 0, wool = 0, meat = 0, egg = 0 ");
                sqlLite.execSQL("UPDATE myWallet SET money = '" + sum + "' WHERE num = 1");
                sqlLite.close();

                Toast.makeText(MainActivity.this, "수집품들을 팔았습니다.", Toast.LENGTH_SHORT).show();

                for (int i = 0; i < stuff.length; i++) {
                    tvStuff[i].setText("0 개 ");
                }


            }
        });

        btnCancel2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog2.dismiss();
            }
        });

        btnReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sqlLite = dBhelper.getWritableDatabase();
                sqlLite.execSQL("UPDATE stuff SET milk = 0, wool = 0, meat = 0, egg = 0 ");
                sqlLite.execSQL("DELETE FROM animal");
                sqlLite.execSQL("UPDATE myWallet SET money = '50000000' where num = 1");
                sqlLite.close();

                tvMoney.setText("50000000");
                Toast.makeText(MainActivity.this, "초기화 되었습니다.", Toast.LENGTH_SHORT).show();

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            if (requestCode == 0) {
                sqlLite = dBhelper.getReadableDatabase();
                try {
                    Cursor cursor = sqlLite.rawQuery("SELECT money FROM myWallet", null);
                    cursor.moveToFirst();
                    tvMoney.setText(cursor.getString(0));
                    cursor.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public class MyListAdapter extends BaseAdapter {
        private Context context;
        private List<Animal> itemDataList;
        private LayoutInflater minflater;

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
                itemView = minflater.inflate(R.layout.listviewitem, null);
            }
            ImageView ivLivestock = (ImageView) itemView.findViewById(R.id.ivLivestock);
            TextView tvLivestock = (TextView) itemView.findViewById(R.id.tvLivestock);
            TextView tvStatus = (TextView) itemView.findViewById(R.id.tvStatus);
            Button btnBuy = (Button) itemView.findViewById(R.id.btnBuy);
            final CheckBox rdoAnimal = (CheckBox) itemView.findViewById(R.id.rdoAnimal);

            btnBuy.setTag(position);
            //리스트뷰에는 동물정보만


            btnBuy.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    pos = position;
                    ivDialog.setImageResource(animals.get(position).getImgID());
                    tvDialog.setText(animals.get(position).getInfo());
                    subDialog.setText(animals.get(position).getStatus());
                    dialog.show();
                }
            });
            rdoAnimal.setTag(position);
            rdoAnimal.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (rdoAnimal.isChecked()) {
                        chk = position;
                    }
                }
            });

            Animal itemData = itemDataList.get(position);
            ivLivestock.setImageResource(itemData.getImgID());
            tvLivestock.setText(itemData.getInfo());
            tvStatus.setText(itemData.getStatus());

            return itemView;
        }
    }

    public double[] objectValue(String kind) {
        double rand[] = new double[2];
        if (kind.toString().equals("소")) {
            rand[0] = (int) (Math.random() * 22) + 1;
            if (rand[0] <= 10) {
                double random = Math.random() * 500;
                rand[1] = (int) ((random > 300 ? random : 450) * 100) / 100.0;
            } else {
                double random = Math.random() * 1000;
                rand[1] = (int) ((random > 700 ? random : 850) * 100) / 100.0;
            }
        } else if (kind.toString().equals("양")) {
            rand[0] = (int) (Math.random() * 10) + 1;
            if (rand[0] <= 5) {
                double random = Math.random() * 50;
                rand[1] = (int) ((random > 50 ? random + 50 : 75) * 100) / 100;
            } else {
                double random = Math.random() * 160;
                rand[1] = (int) ((random > 100 ? random + 100 : 130) * 100) / 100;
            }
        } else if (kind.toString().equals("닭")) {
            rand[0] = (int) (Math.random() * 10) + 1;
            if (rand[0] <= 5) {
                double random = Math.random() * 1;
                rand[1] = (int) ((random > 0 ? random + 1 : 1) * 100) / 100;
            } else {
                double random = Math.random() * 3;
                rand[1] = (int) ((random > 1.5 ? random + 3 : 2) * 100) / 100;
            }
        } else if (kind.toString().equals("돼지")) {
            rand[0] = (int) (Math.random() * 15) + 1;
            if (rand[0] <= 8) {
                double random = Math.random() * 150;
                rand[1] = (int) ((random > 100 ? random + 150 : 120) * 100) / 100.0;
            } else {
                double random = Math.random() * 300;
                rand[1] = (int) ((random > 250 ? random + 300 : 270) * 100) / 100.0;
            }
        }
        return rand;
    }
}
