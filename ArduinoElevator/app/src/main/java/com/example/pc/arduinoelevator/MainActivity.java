package com.example.pc.arduinoelevator;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {
    LinearLayout layout;
    TextView tvTest, tvFloor1, tvFloor2, tvFloor3;
    BluetoothAdapter bluetoothAdapter;
    final static int REQUEST_ENABLE_BT = 10;
    int mPairedDeviceCount = 0;
    Set<BluetoothDevice> mDevices;
    BluetoothDevice mRemoteDevice;
    BluetoothSocket mSocket = null;
    OutputStream mOutputStream;
    InputStream mInputStream;
    Thread mWorkerThread = null;
    Handler delayHandler = new Handler();
    char mCharDelimiter = '\n';
    byte readBuffer[];
    int readBufferPosition;
    int current_floor = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        layout = (LinearLayout) findViewById(R.id.layout);
        tvTest = (TextView) findViewById(R.id.tvTest);
        tvFloor1 = (TextView) findViewById(R.id.tvFloor1);
        tvFloor2 = (TextView) findViewById(R.id.tvFloor2);
        tvFloor3 = (TextView) findViewById(R.id.tvFloor3);

        checkBlueTooth();
        sendData("start");  // 앱이 실행됐다는 것을 아두이노로 보낸다.

        tvFloor1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tvFloor1.setBackgroundResource(R.drawable.button1_on);

                if (current_floor == 2) {
                    layout.setBackgroundResource(R.drawable.floor2_down);
                } else if (current_floor == 3) {
                    layout.setBackgroundResource(R.drawable.floor3_down);

                    delayHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            layout.setBackgroundResource(R.drawable.floor2_down);
                        }
                    }, 5000);
                }

                sendData("" + 1);
            }
        });
        tvFloor2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tvFloor2.setBackgroundResource(R.drawable.button2_on);

                if (current_floor == 1) {
                    layout.setBackgroundResource(R.drawable.floor1_up);
                } else if (current_floor == 3) {
                    layout.setBackgroundResource(R.drawable.floor3_down);
                }

                sendData("" + 2);
            }
        });
        tvFloor3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tvFloor3.setBackgroundResource(R.drawable.button3_on);

                if (current_floor == 1) {
                    layout.setBackgroundResource(R.drawable.floor1_up);

                    delayHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            layout.setBackgroundResource(R.drawable.floor2_up);
                        }
                    }, 5000);
                } else if (current_floor == 2) {
                    layout.setBackgroundResource(R.drawable.floor2_up);
                }

                sendData("" + 3);
            }
        });
    }

    // 실제 전송 실행
    void sendData(String msg) {
        try {
            mOutputStream.write(msg.getBytes());
        } catch (Exception e) {
            // 문자열 전송 중에 에러발생한 경우
        }
    }

    // 실제 블루투스 체크
    void checkBlueTooth() {
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        if (bluetoothAdapter == null) {
            //finish();
        } else {
            if (!bluetoothAdapter.isEnabled()) {
                Intent enableBTIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBTIntent, REQUEST_ENABLE_BT);  // 블루투스 장치 권한 요청
            } else {
                selectDevice();
            }
        }
    }

    // 블루투스 연결을 예, 아니오 선택하면 수행되는 메서드
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_ENABLE_BT:
                if (resultCode == RESULT_OK) {
                    selectDevice();
                } else if (resultCode == RESULT_CANCELED) {
                    //finish(); 블루투스 권한 요청을 거절했습니다.
                }
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    // 블루투스 장치 선택
    void selectDevice() {
        mDevices = bluetoothAdapter.getBondedDevices();
        mPairedDeviceCount = mDevices.size();

        if (mPairedDeviceCount == 0) {
            finish();
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("블루투스 장치 선택");
        List<String> listItems = new ArrayList<String>();

        for (BluetoothDevice device : mDevices) {
            listItems.add(device.getName());
        }
        listItems.add("취소");
        final CharSequence items[] = listItems.toArray(new CharSequence[listItems.size()]);
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (which == mPairedDeviceCount) {
                    //finish(); 블루투스 장치 연결을 취소했습니다.
                } else {
                    connectToSelectedDevices(items[which].toString());
                }
            }
        });
        builder.setCancelable(false);   // 뒤로가기 버튼 사용금지
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    // 선택한 장치에 연결(페어링) 시도
    void connectToSelectedDevices(String selectedDeviceName) {
        mRemoteDevice = getDeviceFromBondedList(selectedDeviceName);
        UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");

        try {
            mSocket = mRemoteDevice.createRfcommSocketToServiceRecord(uuid);
            mSocket.connect();
            mOutputStream = mSocket.getOutputStream();
            mInputStream = mSocket.getInputStream();
            beginListenForData();
        } catch (Exception e) {

        }
    }

    // 연결 장치 목록에서 선택할 장치
    BluetoothDevice getDeviceFromBondedList(String name) {
        BluetoothDevice selectedDevice = null;

        for (BluetoothDevice device : mDevices) {
            if (name.equals(device.getName())) {
                selectedDevice = device;
            }
        }
        return selectedDevice;
    }

    // 앱이 종료되면 연결된 모든 장치 close
    @Override
    protected void onDestroy() {
        try {
            mWorkerThread.interrupt();
            mInputStream.close();
            mOutputStream.close();
            mSocket.close();
        } catch (Exception e) {

        }
        super.onDestroy();
    }

    // 데이터 수신 준비 및 처리
    void beginListenForData() {
        final Handler handler = new Handler();
        readBuffer = new byte[1024];    // 수신 버퍼
        readBufferPosition = 0;         // 버퍼 내 수신 문자 저장 위치
        // 문자열 수신 쓰레드
        mWorkerThread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (!Thread.currentThread().isInterrupted()) {
                    try {
                        int bytesAvailable = mInputStream.available();  // 수신 데이터 확인
                        if (bytesAvailable > 0) {
                            byte packetBytes[] = new byte[bytesAvailable];
                            mInputStream.read(packetBytes);

                            for (int i = 0; i < bytesAvailable; i++) {
                                byte b = packetBytes[i];
                                if (b == mCharDelimiter) {
                                    byte encodeBytes[] = new byte[readBufferPosition];
                                    System.arraycopy(readBuffer, 0, encodeBytes, 0, encodeBytes.length);
                                    final String data = new String(encodeBytes, "UTF-8");
                                    readBufferPosition = 0;
                                    handler.post(new Runnable() {
                                        @Override
                                        public void run() {
                                            // 수신된 문자열에 대한 작업처리
//                                            tvTest.setText(data);

                                            if (data.trim().equals("1")) {
                                                current_floor = 1;
                                                tvFloor1.setBackgroundResource(R.drawable.button1_off);
                                                layout.setBackgroundResource(R.drawable.floor1);
                                            } else if (data.trim().equals("2")) {
                                                current_floor = 2;
                                                tvFloor2.setBackgroundResource(R.drawable.button2_off);
                                                layout.setBackgroundResource(R.drawable.floor2);
                                            } else if (data.trim().equals("3")) {
                                                current_floor = 3;
                                                tvFloor3.setBackgroundResource(R.drawable.button3_off);
                                                layout.setBackgroundResource(R.drawable.floor3);
                                            }
                                        }
                                    });
                                } else {
                                    readBuffer[readBufferPosition++] = b;
                                }
                            }
                        }
                    } catch (IOException e) {
                        // 데이터 수신 중 오류 발생
                    }
                }
            }
        });
        mWorkerThread.start();
    }
}
