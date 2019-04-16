package com.example.weatherforecast;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import java.util.Objects;

import androidx.work.Worker;
import androidx.work.WorkerParameters;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.simplexml.SimpleXmlConverterFactory;

import static com.example.weatherforecast.MainActivity.serviceKey;

public class MyWorker extends Worker {
    AirKorea airKorea;
    String title , message;
    Context context;

    public MyWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
        this.context = context;
    }

    @NonNull
    @Override
    public Result doWork() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://openapi.airkorea.or.kr/")
                .addConverterFactory(SimpleXmlConverterFactory.createNonStrict())
                .build();

        WeatherForecastService service = retrofit.create(WeatherForecastService.class);
        Call<AirKorea> call = service.getDustInfo(serviceKey,"서울",1,1.3f);
        call.enqueue(new Callback<AirKorea>() {
            @Override
            public void onResponse(Call<AirKorea> call, Response<AirKorea> response) {
                if (response.isSuccessful()){
                    airKorea = response.body();
                    title = airKorea.body.items.itemlist.get(0).getStationName();
                    message = airKorea.body.items.itemlist.get(0).getPm10Grade();
                    Log.d("잡워커2 : ", message);
                    if (Integer.parseInt(message)>2) {
                        sendNotification(title, "미세먼지 나쁨");
                    }

                }
            }

            @Override
            public void onFailure(Call<AirKorea> call, Throwable t) {

            }
        });
        return Result.success();
    }
    private void sendNotification(String title, String message){
        NotificationCompat.Builder notificationBuilder;
        /**
         * 오레오 버전부터는 Notification Channel이 없으면 푸시가 생성되지 않는 현상이 있습니다.
         * **/
        if (Build.VERSION.SDK_INT >= 26) {
            Log.d("오레오", "ㅎㅇ");
            String channel = "channel";
            String channel_nm = "channelName";

            NotificationManager notichannel = (android.app.NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            NotificationChannel channelMessage = new NotificationChannel(channel, channel_nm,
                    android.app.NotificationManager.IMPORTANCE_DEFAULT);
            channelMessage.setDescription("채널에 대한 설명.");
            channelMessage.enableLights(true);
            channelMessage.enableVibration(true);
            channelMessage.setShowBadge(false);
            channelMessage.setVibrationPattern(new long[]{100, 200});
            Objects.requireNonNull(notichannel).createNotificationChannel(channelMessage);

            notificationBuilder =
                    new NotificationCompat.Builder(context, channel);
        } else {
            notificationBuilder =
                    new NotificationCompat.Builder(context, "");
        }

        notificationBuilder.setSmallIcon(R.mipmap.icon_dust)
                .setContentTitle(title)
                .setContentText(message)
                .setAutoCancel(true)
                .setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE);

        NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        if (notificationManager != null) {
            notificationManager.notify(9999, notificationBuilder.build());
        }

    }
}
