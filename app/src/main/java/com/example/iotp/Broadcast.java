package com.example.iotp;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.core.app.NotificationCompat;


public class Broadcast extends BroadcastReceiver {


    private static final String TAG ="Broadcast" ;

    @Override
    public void onReceive(Context context, Intent intent) {//알람 시간이 되었을때 onReceive를 호출함
        //NotificationManager 안드로이드 상태바에 메세지를 던지기위한 서비스 불러오고
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        Intent notificationIntent = new Intent(context, ResultActivity.class);
        String goodsName=intent.getStringExtra("goodsName");
        String txt=intent.getStringExtra("memo");

        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
                | Intent.FLAG_ACTIVITY_SINGLE_TOP);

        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, notificationIntent, 0);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "default");
        //OREO API 26 이상에서는 채널 필요
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
        builder.setSmallIcon(R.drawable.baseline_alarm_black_18dp); //mipmap 사용시 Oreo 이상에서 시스템 UI 에러남


        String channelName ="알람 채널";
        String description = "정해진 시간에 알람합니다.";
        int importance = NotificationManager.IMPORTANCE_HIGH; //소리와 알림메시지를 같이 보여줌

        NotificationChannel channel = new NotificationChannel("default", channelName, importance);
        channel.setDescription(description);

        if (notificationManager != null) {
            // 노티피케이션 채널을 시스템에 등록
            notificationManager.createNotificationChannel(channel);
        }
    }else builder.setSmallIcon(R.mipmap.baseline_alarm_black_18dp); // Oreo 이하에서 mipmap 사용하지 않으면 Couldn't create icon: StatusBarIcon 에러남


        builder.setDefaults(NotificationCompat.DEFAULT_ALL)
                .setWhen(System.currentTimeMillis())
                .setTicker("{Time to watch some cool stuff!}")
                .setContentTitle(goodsName)
                .setContentText(txt)
                .setContentInfo("INFO")
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);





            // 노티피케이션 동작시킴
            notificationManager.notify((int)(System.currentTimeMillis()/1000), builder.build());

    }
}

