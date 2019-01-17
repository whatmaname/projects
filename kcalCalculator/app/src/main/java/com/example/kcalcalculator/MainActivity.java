package com.example.kcalcalculator;

import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.AssetManager;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Adapter;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class MainActivity extends AppCompatActivity {
    Button btnKindRice,btnKindNoodle,btnKindSoup,btnKindBread,btnKindExtra,btnExercise,btnStatus,btnReset;
    TextView tvKcal;
    String kind;
    SQLiteDatabase sqLite;
    String folderPath = "/data/data/com.example.kcalcalculator/databases";
    String filePath = "/data/data/com.example.kcalcalculator/databases/kcal";
    String[] spin= {"밥류","면류","빵류","국,찌개류","기타","운동"};
    String[] kinds={"kindrice","kindnoodle","kindbread","kindsoup","kindextra","gym"};
    double value[];
    int kcal;
    AlertDialog dataDialog;
    AlertDialog.Builder dlg;
    View dataDialogView;
    Spinner spnData;
    Button btnDataAdd;
    EditText edtData,edtData2;
    public void copyDB(Context mContext) {
        AssetManager manager = mContext.getAssets();
        File folder = new File(folderPath);
        File file = new File(filePath);
        FileOutputStream fos = null;
        BufferedOutputStream bos = null;
        try {
            InputStream is = manager.open("kcal",AssetManager.ACCESS_BUFFER);
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
        setContentView(R.layout.activity_main);
        try {
            boolean dbcheked = isCheckDB(this);
            Log.d("db check :",String.valueOf(dbcheked));
            if (!dbcheked) {
                copyDB(this);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


        btnKindRice=(Button)findViewById(R.id.btnKindRice);
        btnKindNoodle=(Button)findViewById(R.id.btnKindNoodle);
        btnKindSoup=(Button)findViewById(R.id.btnKindSoup);
        btnKindBread=(Button)findViewById(R.id.btnKindBread);
        btnKindExtra=(Button)findViewById(R.id.btnKindExtra);
        btnExercise=(Button)findViewById(R.id.btnExercise);
        tvKcal=(TextView)findViewById(R.id.tvKcal);
        btnStatus=(Button)findViewById(R.id.btnStatus);
        btnReset=(Button)findViewById(R.id.btnReset);


        btnKindRice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                kind="kindrice";
                carryData(kind);
            }
        });
        btnKindBread.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                kind="kindbread";
                carryData(kind);
            }
        });
        btnKindNoodle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                kind="kindnoodle";
                carryData(kind);
            }
        });
        btnKindSoup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                kind="kindsoup";
                carryData(kind);
            }
        });
        btnKindExtra.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                kind="kindextra";
                carryData(kind);
            }
        });
        btnExercise.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                kind="gym";
                carryData(kind);
            }
        });
        btnStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent= new Intent(getApplicationContext(),ChartActivity.class);
                intent.putExtra("chart",value);
                startActivity(intent);
            }
        });
        btnReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tvKcal.setText("0kcal");
                kcal=0;
            }
        });
        //확인을 위한 다이얼로그
        dlg=new AlertDialog.Builder(MainActivity.this);
        dlg.setTitle("경고");
        dlg.setMessage("정말 데이터를 삭제 하시겠습니까?");
        dlg.setPositiveButton("확인", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                sqLite=SQLiteDatabase.openDatabase(filePath,null,SQLiteDatabase.OPEN_READWRITE);
                sqLite.execSQL("DELETE FROM kindrice");
                sqLite.execSQL("DELETE FROM kindextra");
                sqLite.execSQL("DELETE FROM kindsoup");
                sqLite.execSQL("DELETE FROM kindnoodle");
                sqLite.execSQL("DELETE FROM kindbread");
                sqLite.execSQL("DELETE FROM gym");
                sqLite.close();
                Toast.makeText(MainActivity.this, "데이터가 삭제 되었습니다.", Toast.LENGTH_SHORT).show();
                dialogInterface.dismiss();
            }
        });
        dlg.setNegativeButton("취소",null);

        //데이터 수정을 위한 다이얼로그
        dataDialog = new AlertDialog.Builder(MainActivity.this).create();
        dataDialogView = (View) View.inflate(MainActivity.this,
                R.layout.datadialog, null);
        dataDialog.setTitle("데이터 추가");
        edtData=(EditText)dataDialogView.findViewById(R.id.edtData);
        edtData2=(EditText)dataDialogView.findViewById(R.id.edtData2);
        btnDataAdd=(Button)dataDialogView.findViewById(R.id.btnDataAdd);
        spnData=(Spinner)dataDialogView.findViewById(R.id.spnData);

        dataDialog.setView(dataDialogView);
        final ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item,spin);
        spnData.setAdapter(adapter);


        //추가와 수정기능
        btnDataAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sqLite=SQLiteDatabase.openDatabase(filePath,null,SQLiteDatabase.OPEN_READWRITE);
                String spiner = kinds[spnData.getSelectedItemPosition()];
                if(!edtData.getText().toString().equals("")||!edtData2.getText().toString().equals("")) {
                    String dataName = edtData.getText().toString();
                    int datakcal = Integer.parseInt(edtData2.getText().toString());
                    switch (btnDataAdd.getText().toString()) {
                        case "추가":
                            try {
                                sqLite.execSQL("INSERT INTO " + spiner + " VALUES ('" +dataName +"', "+datakcal+");");
                            }catch (Exception e){
                                e.printStackTrace();
                                Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                            dataDialog.dismiss();
                            Log.d("data : ",dataName);
                            break;
                        case "수정":
                            try{
                            sqLite.execSQL("UPDATE "+spiner+" SET kcal = "+datakcal+" WHERE food = '"+dataName+"';");}
                            catch (SQLiteException se){
                                Toast.makeText(MainActivity.this, "음식이름을 확인 해주세요.", Toast.LENGTH_SHORT).show();
                            }
                            catch (Exception e){
                                e.printStackTrace();
                            }
                            dataDialog.dismiss();
                            break;
                    }
                }else {
                    Toast.makeText(MainActivity.this, "먼저 데이터를 입력해주세요", Toast.LENGTH_SHORT).show();
                }
                Log.d("kinds position : ", spiner);

            }
        });
    }
    //액티비티 값 전달
    void carryData(String kind){
        Intent mintent = new Intent(getApplicationContext(),kcalActivity.class);
        mintent.putExtra("kind",kind);
        mintent.putExtra("kcal",kcal);
        startActivityForResult(mintent,0);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode==RESULT_OK){
            switch (requestCode){
                case 0:
                    value= new double[]{data.getDoubleExtra("result",0), 1200};
                    tvKcal.setText(data.getIntExtra("Text",0)+" kcal");
                    kcal=data.getIntExtra("Text",0);
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        menu.add(0,1,0,"데이터 추가");
        menu.add(0,2,0,"데이터 수정");
        menu.add(0,3,0,"데이터 비우기");
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case 1:
                btnDataAdd.setText("추가");
                dataDialog.show();
                return true;
            case 2:
                btnDataAdd.setText("수정");
                dataDialog.show();
                return true;
            case 3:
                dlg.show();
                return true;
        }
        return false;
    }
}
