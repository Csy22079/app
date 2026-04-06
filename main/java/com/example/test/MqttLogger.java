package com.example.test;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MqttLogger {
    private static final String TAG = "MqttLogger";
    private static final String LOG_FILE_NAME = "mqtt_log.txt";
    private Context context;

    public MqttLogger(Context context) {
        this.context = context;
    }

    /**
     * 保存MQTT消息到日志文件
     * @param message MQTT消息内容
     */
    public void saveMessage(String topic, String message) {
        try {
            // 获取当前时间
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
            String currentTime = sdf.format(new Date());
            
            // 格式化日志内容
            String logEntry = "[" + currentTime + "] Topic: " + topic + " | Message: " + message + "\n";
            
            // 写入文件
            FileOutputStream fos = context.openFileOutput(LOG_FILE_NAME, Context.MODE_APPEND);
            fos.write(logEntry.getBytes());
            fos.close();
            
            Log.d(TAG, "Saved message to log: " + logEntry);
        } catch (IOException e) {
            Log.e(TAG, "Error saving message to log", e);
        }
    }

    /**
     * 读取日志文件内容
     * @return 日志内容
     */
    public String readLog() {
        try {
            FileInputStream fis = context.openFileInput(LOG_FILE_NAME);
            InputStreamReader isr = new InputStreamReader(fis);
            BufferedReader br = new BufferedReader(isr);
            
            StringBuilder logContent = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                logContent.append(line).append("\n");
            }
            
            br.close();
            isr.close();
            fis.close();
            
            return logContent.toString();
        } catch (IOException e) {
            Log.e(TAG, "Error reading log file", e);
            return "暂无日志记录";
        }
    }

    /**
     * 清空日志文件
     */
    public void clearLog() {
        try {
            FileOutputStream fos = context.openFileOutput(LOG_FILE_NAME, Context.MODE_PRIVATE);
            fos.write("".getBytes());
            fos.close();
            Log.d(TAG, "Log cleared");
        } catch (IOException e) {
            Log.e(TAG, "Error clearing log file", e);
        }
    }
}