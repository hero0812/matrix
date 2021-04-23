/*
 * Tencent is pleased to support the open source community by making wechat-matrix available.
 * Copyright (C) 2018 THL A29 Limited, a Tencent company. All rights reserved.
 * Licensed under the BSD 3-Clause License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://opensource.org/licenses/BSD-3-Clause
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package sample.tencent.matrix.battery;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;

import com.tencent.matrix.Matrix;
import com.tencent.matrix.batterycanary.BatteryEventDelegate;
import com.tencent.matrix.batterycanary.BatteryMonitorPlugin;
import com.tencent.matrix.plugin.Plugin;
import com.tencent.matrix.util.MatrixLog;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

//import com.tencent.matrix.batterycanary.BatteryCanaryPlugin;

/**
 * Created by zhangshaowen on 17/6/13.
 */

public class TestBatteryActivity extends Activity {
    private static final String TAG = "Matrix.TestBatteryActivity";

    private PendingIntent getAlarmPendingIntent(final Context context, final int id, Intent intent) {
        PendingIntent pendingIntent;
        if (android.os.Build.VERSION.SDK_INT >= 29) {
            int requestCode = id % 450 + 50;
            pendingIntent = PendingIntent.getBroadcast(context, requestCode, intent, PendingIntent.FLAG_CANCEL_CURRENT);
            MatrixLog.i(TAG, "getAlarmPendingIntent() id:%s requestCode:%s", id, requestCode);
        } else {
            pendingIntent = PendingIntent.getBroadcast(context, id, intent, PendingIntent.FLAG_CANCEL_CURRENT);
        }
        return pendingIntent;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

       Plugin plugin = Matrix.with().getPluginByClass(BatteryMonitorPlugin.class);
       if (!plugin.isPluginStarted()) {
           if (!BatteryEventDelegate.isInit()) {
               BatteryEventDelegate.init(this.getApplication());
           }

           MatrixLog.i(TAG, "plugin-battery start");
           plugin.start();
       }
//
//        final AlarmManager am = (AlarmManager) getApplicationContext().getSystemService(Context.ALARM_SERVICE);
//        if (am == null) {
//            MatrixLog.e(TAG, "am == null");
//            return;
//        }
//
//        Intent intent = new Intent();
//        intent.setAction("ALARM_ACTION(" + String.valueOf(Process.myPid()) + ")");
//        intent.putExtra("ID", 1);
//        final PendingIntent pendingIntent = getAlarmPendingIntent(getApplicationContext(), (int) 1, intent);
//        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
//            am.setExact(AlarmManager.ELAPSED_REALTIME_WAKEUP, 200*1000, pendingIntent);
//        } else {
//            am.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, 200*1000, pendingIntent);
//        }

//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                final AlarmManager am = (AlarmManager) getApplicationContext().getSystemService(Context.ALARM_SERVICE);
//                am.cancel(pendingIntent);
//                pendingIntent.cancel();
//            }
//        }).start();



        // Test make notification
        // if (BatteryCanary.getMonitorFeature(NotificationMonitorFeature.class) != null) {
        //     tryNotify();
        // }
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                Runtime.getRuntime().gc();
//                Runtime.getRuntime().runFinalization();
//                Runtime.getRuntime().gc();
//            }
//        }).start();
    }

    void tryNotify() {
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        String channelId = "TEST_CHANNEL_ID";
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            NotificationChannel channel = new NotificationChannel(channelId, "TEST_CHANNEL_NAME", NotificationManager.IMPORTANCE_DEFAULT);
            manager.createNotificationChannel(channel);
        }

        Intent intent = new Intent(this, TestBatteryActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);

        Notification notification = new NotificationCompat.Builder(this, channelId)
                .setContentTitle("NOTIFICATION_TILE")
                .setContentText(tryGetAppRunningNotificationText())
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .setSmallIcon(com.tencent.matrix.batterycanary.R.drawable.ic_launcher)
                .setWhen(System.currentTimeMillis())
                .build();

        notificationManager.notify(16657, notification);
    }

    private String tryGetAppRunningNotificationText() {
        Resources resources = Resources.getSystem();
        if (resources != null) {
            int appRunningNotifyTextId = resources.getIdentifier(
                    "app_running_notification_text",
                    "string",
                    "android"
            );
            if (appRunningNotifyTextId > 0) {
                return resources.getString(appRunningNotifyTextId);
            }
        }
        return null;
    }
}
