package com.example.kcalcalculator;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

public class kcalActivity extends AppCompatActivity {
    ListView listCal;
    TextView tvResult, tvTitle, tvCal;
    Button btnSend;
    EditText edtSearch ,edtCount;
    SQLiteDatabase sqLite;
    String folderPath = "/data/data/com.example.kcalcalculator/databases";
    String filePath = "/data/data/com.example.kcalcalculator/databases/kcal";
    String kind;
    int kcal;
    AlertDialog dialog;
    View dialogView;
    myListAdapter listAdapter;
    ArrayList<itemdata> itemdatas = new ArrayList<>() ;

    //asset에 있는 DB를 직접 사용 못하므로 복사해서 사용
    public void copyDB(Context mContext) {
        AssetManager manager = mContext.getAssets();
        File folder = new File(folderPath);
        File file = new File(filePath);
        FileOutputStream fos = null;
        BufferedOutputStream bos = null;
        try {
            InputStream is = manager.open("kcal");
            BufferedInputStream bis = new BufferedInputStream(is);
            if (folder.exists()) {

            } else {
                folder.mkdirs();
            }
            if (file.exists()) {
                file.delete();
                file.createNewFile();
            }
            fos = new FileOutputStream(file);
            bos = new BufferedOutputStream(fos);
            int read = -1;
            byte buffer[] = new byte[1024];
            while ((read = bis.read(buffer, 0, 1024)) != -1) {
                bos.write(buffer, 0, read);
            }
            bos.flush();
            bos.close();
            bis.close();
            fos.close();
            is.close();
        } catch (IOException e) {
        }
    }

        //있는지 확인
        public boolean isCheckDB(Context context) {
            File file = new File(filePath);
            long newdb_size = 0;
            long olddb_size = file.length();
            AssetManager manager = context.getAssets();
            try {
                InputStream is = manager.open("kcal");
                newdb_size = is.available();
            } catch (IOException e) {

            }
            if (file.exists()) {
                if (newdb_size != olddb_size) {
                    return false;
                } else {
                    return true;
                }
            }
            return false;
        }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kcal);
        listCal = (ListView) findViewById(R.id.listCal);
        tvResult = (TextView) findViewById(R.id.tvResult);
        btnSend = (Button) findViewById(R.id.btnSend);
        edtSearch = (EditText)findViewById(R.id.edtSearch);

        try {
           boolean dbcheked = isCheckDB(this);
            if (!dbcheked) {
               copyDB(this);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        //칼로리 계산 다이얼로그
        dialog = new AlertDialog.Builder(kcalActivity.this).create();
        dialogView = (View) View.inflate(kcalActivity.this,
                R.layout.dialog, null);

        Button btnPlus = (Button) dialogView.findViewById(R.id.btnPlus);
        Button btnMinus = (Button) dialogView.findViewById(R.id.btnMinus);
        Button btnDAdd = (Button) dialogView.findViewById(R.id.btnDialogAdd);
        edtCount = (EditText)dialogView.findViewById(R.id.edtCount);
        tvTitle = (TextView)dialogView.findViewById(R.id.tvTitle);
        tvCal = (TextView)dialogView.findViewById(R.id.tvCal);

        dialog.setView(dialogView);
        dialog.setTitle("추가");

        //계산했던 칼로리와 종류를 받아 음식종류를 정하고 계산했던 칼로리를 맨위에띄움
        Intent intent = getIntent();
        kind = intent.getStringExtra("kind");
        kcal = intent.getIntExtra("kcal",0);
        tvResult.setText(kcal+"kcal");

        listAdapter = new myListAdapter(kcalActivity.this, selectData(kind));
        listCal.setAdapter(listAdapter);
        //메인액티비티로 계산된 칼로리 보냄
        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!tvResult.getText().toString().equals("")) {
                    Intent sendIntent = new Intent();
                    sendIntent.putExtra("result", (double)minus4Letters(tvResult.getText().toString()));
                    sendIntent.putExtra("Text",minus4Letters(tvResult.getText().toString()));
                    setResult(RESULT_OK, sendIntent);
                    finish();
                } else {
                    Toast.makeText(kcalActivity.this, "입력값이 없습니다.", Toast.LENGTH_SHORT).show();
                }
            }
        });
        //다이얼로그 +버튼
        btnPlus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                float num = 0;
                if(!edtCount.getText().toString().trim().equals("")){
                num = Float.parseFloat(edtCount.getText().toString().trim());}
                else {
                num = 0;
                }
                if(!kind.equals("gym")) {
                    num += 0.5f;
                }else {
                    num -= 0.5f;
                }
                num = (int) (num * 10) / 10.0f;

                edtCount.setText(String.valueOf(num));

            }
        });
        //다이얼로그 -버튼
        btnMinus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                float num = 0;
                if(!edtCount.getText().toString().trim().equals("")){
                    num = Float.parseFloat(edtCount.getText().toString().trim());}
                else {
                    num = 0;
                }
                if(!kind.equals("gym")) {
                    num -= 0.5f;
                }else {
                    num += 0.5f;
                }
                num = (int) (num * 10) / 10.0f;

                edtCount.setText(String.valueOf(num));
            }
        });
        //dialog에서 칼로리를 추가
        btnDAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int cal = minus4Letters(tvCal.getText().toString());
                if (tvResult.getText().toString().equals("")||tvResult.getText().toString().equals(null)){
                    if(!edtCount.getText().toString().equals("")){
                    tvResult.setText(String.valueOf((int)(cal*Float.parseFloat(edtCount.getText().toString())))+"kcal");}
                    else{
                       tvResult.setText("0kcal");
                    }
                }else{
                    tvResult.setText(String.valueOf(minus4Letters(tvResult.getText().toString())+
                            (int)(cal*Float.parseFloat(edtCount.getText().toString())))+"kcal");
                }
                edtCount.setText("0");
                dialog.dismiss();
            }
        });
        //음식 검색용 editText
        edtSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                listAdapter.getFilter().filter(charSequence.toString());
            }

            @Override
            public void afterTextChanged(Editable editable) {
                String filterText = editable.toString();
                if(filterText.length()>0){
                    listCal.setFilterText(filterText);
                }else{
                    listCal.clearTextFilter();
                }
            }
        });
    }
    //db에서 intent로받아온 종류를 토대로 음식을 나열함
    ArrayList<itemdata> selectData(String kind) {
       sqLite=SQLiteDatabase.openDatabase(filePath,null,SQLiteDatabase.OPEN_READWRITE);
        Cursor cursor = sqLite.rawQuery("SELECT * FROM " + kind + ";", null);
        cursor.moveToFirst();
        while (cursor.moveToNext()) {
            itemdatas.add(new itemdata(cursor.getString(0), cursor.getInt(1)));
        }
        cursor.close();
        sqLite.close();
        return itemdatas;
    }
    //칼로리 계산을 위해 TextView에서 kcal를 빼기위한 메서드
    int minus4Letters(String str){
        int arg = Integer.parseInt(String.valueOf(str.trim().subSequence(0,str.length()-4)));
        return arg;
    }
    //커스텀 리스트뷰를 위한 어댑터
    public class myListAdapter extends BaseAdapter implements Filterable {
        private Context context;
        private ArrayList<itemdata> itemDataList;
        private ArrayList<itemdata> filteredItemDataList;
        private LayoutInflater minflater;
        itemdata itemData;
        Filter listFilter;

        myListAdapter(Context c, ArrayList<itemdata> itemDataList) {
            context = c;
            this.minflater = LayoutInflater.from(c);
            this.itemDataList = itemDataList;
            filteredItemDataList=itemDataList;
        }


        @Override
        public int getCount() {
            return filteredItemDataList.size();
        }

        @Override
        public Object getItem(int i) {
            return filteredItemDataList.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(final int position, View view, ViewGroup viewGroup) {
            View itemView = view;
            ViewHolder holder;

            if (itemView == null) {
                holder=new ViewHolder();
                itemView = minflater.inflate(R.layout.kcallist, null);

                holder.titleOfMenu=(TextView) itemView.findViewById(R.id.titleOfMenu);
                holder.calOfMenu = (TextView) itemView.findViewById(R.id.calOfMenu);
                holder.btnAdd = (Button)(Button) itemView.findViewById(R.id.btnAdd);

                itemView.setTag(holder);
            }else{
            holder=(ViewHolder)itemView.getTag();
            }

           itemData = filteredItemDataList.get(position);

           holder.titleOfMenu.setText(itemData.getTitleTxt());
            holder.calOfMenu.setText(itemData.getSubTitle());

            holder.btnAdd.setTag(position);
            holder.btnAdd.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    itemData = filteredItemDataList.get(position);
                    tvTitle.setText(itemData.getTitleTxt());
                    tvCal.setText(itemData.getSubTitle());
                    dialog.show();
                    Log.d("position",position+" "+itemdatas.get(position).getTitleTxt());
                }
            });
            return itemView;
        }

        class ViewHolder {
            TextView titleOfMenu;
            TextView calOfMenu;
            Button btnAdd;
        }
        //필터기능
        @Override
        public Filter getFilter() {
            if (listFilter == null) {
                listFilter = new ListFilter();
            }
            return listFilter;
        }

        private class ListFilter extends Filter {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String fiterString = charSequence.toString().toLowerCase();
                FilterResults results = new FilterResults();

                final ArrayList<itemdata> itemList = itemDataList;
                   int count = itemList.size();
                   final ArrayList<itemdata> itemLists = new ArrayList<itemdata>();

                   itemdata fiterableStirng;
                    for (int i = 0; i < count; i ++) {
                        fiterableStirng = itemList.get(i);
                        if(fiterableStirng.getTitleTxt().toLowerCase().contains(fiterString)){
                            itemLists.add(fiterableStirng);
                        }
                    }
                    results.values = itemLists;
                    results.count = itemLists.size();

                return results;
            }


            @Override
            protected void publishResults(CharSequence charSequence, FilterResults results) {
                filteredItemDataList = (ArrayList<itemdata>) results.values;
                notifyDataSetChanged();

            }
        }

    }

}
