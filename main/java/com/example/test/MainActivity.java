package com.example.test;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;


import android.app.Activity;
import android.content.Context;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.os.Vibrator;

import com.example.test.AliyunIoTSignUtil;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;




public class MainActivity extends AppCompatActivity {
    private static final String TAG =MainActivity.class.getSimpleName();

    //-------------------------------控件
    private TextView msgTextView;       //收发消息
    private TextView msgTextView2;      //温度
    private TextView msgTextView3;      //开关
    private TextView msgTextView5;      //连接
    private TextView msgTextView4;      //配置状态
    private TextView msgTextView6;      //光照
    private TextView msgTextView7;      //CO2


    private TextView msgTextView8;      //光照
    private TextView msgTextView9;      //CO2

    private TextView msgTextView10;      //CO2

    private TextView msgTextView11;      //CO2
    private TextView msgTextView12;      //CO2

    private TextView msgTextView13;      //CO2
    private TextView msgTextView14;      //CO2

    private EditText edit1; //编辑框1
    private EditText edit2; //编辑框2
    private EditText edit3; //编辑框3

    private Button activate_button0;    //配置
    //-------------------------------常量
    final int POST_DEVICE_PROPERTIES_SUCCESS = 1002;
    final int POST_DEVICE_PROPERTIES_ERROR = 1003;
    final int POST_DEVICE_PROPERTIES_RX = 1004;

    //MQTT阿里云参数  这3个只需要改成自己的就行了
    private String productKey="a1hKq8J0y7h";
    private String deviceName="my_device_app";
    private String deviceSecret="3310d98abcb66c267ed35f3c1ad7f0bb";


    private String responseBody = "";
    private MqttClient mqttClient=null;
    //-------------------------------指令
    //private final String payloadJson1="{\"LED\":1}";
    //private final String payloadJson2="{\"LED\":0}";
    private final String payloadJson1="{\"SW4\":\"0\"}";
    private final String payloadJson2="{\"SW1\":\"0\"}";
    private final String payloadJson3="{\"SW2\":\"0\"}";
    private final String payloadJson4="{\"SW3\":\"0\"}";
    private final String payloadJson5="{\"SW5\":\"0\"}";
    
    // 添加日志管理器
    private MqttLogger mqttLogger;
    
    //=====================================================================

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            String result;
            int i = 0;
            switch (msg.what) {
                case POST_DEVICE_PROPERTIES_SUCCESS:
                    //  showToast("发送数据成功");
                    //System.out.println("hello1");
                    break;
                case POST_DEVICE_PROPERTIES_ERROR:
                    showToast("发送数据失败");
                    //System.out.println("hello2");
                    break;

                case POST_DEVICE_PROPERTIES_RX:     //接收到消息
                    // Toast.makeText(MainActivity.this,"接收到东西" ,Toast.LENGTH_SHORT).show();
                    //Toast.makeText(MainActivity.this, msg.obj.toString(), Toast.LENGTH_SHORT).show();//打印显示
                    result = msg.obj.toString();//substring()字符串截取功能
                    //msgTextView2.setText(temper);
                    Log.d("my", result);
                    JSONObject jsonObject = null;
                    try {
                        jsonObject = new JSONObject(result);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    int SystemTimer = jsonObject.optInt("SystemTimer");
                    int ALARM_STATUS = jsonObject.optInt("ALARM_STATUS");
                    int LOCK_STATUS = jsonObject.optInt("LOCK_STATUS");



//                    double Energy_data = jsonObject.optDouble("Energy_data");//
//
                    //Log.d("======", relay_status);
//                    String WaterAvg = jsonObject.optString("WaterAvg");
                    //jsonObject != null

//                        msgTextView2.setText(showstr.substring(1, 4) + "°");// 获取
                        msgTextView2.setText("同步：" + SystemTimer);

                    if(ALARM_STATUS == 0)
                    {
                        msgTextView3.setText("状态：" + "正常" );
                    }
                    else   {
                        msgTextView3.setText("状态：" + "异常" );
                    }
                    if(LOCK_STATUS == 0)
                    {
                        msgTextView10.setText("门锁：" + "OFF" );
                    }
                    else   {
                        msgTextView10.setText("门锁：" + "ON" );
                    }



//                    String rx=msg.obj.toString();
//                    System.out.println(rx);
//                    double i=Double.parseDouble(rx);
                        msgTextView5.setText("连接：成功");


                    break;
            }
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        // 初始化日志管理器
        mqttLogger = new MqttLogger(this);
        
//-------------------------------两个控件联动
        msgTextView = findViewById(R.id.msgTextView);   //收发消息
        msgTextView2 = findViewById(R.id.msgTextView2); //温度
        msgTextView3 = findViewById(R.id.msgTextView3); //开关
        msgTextView4= findViewById(R.id.msgTextView4);  //配置状态
        msgTextView5 = findViewById(R.id.msgTextView5); //连接状态
        msgTextView6 = findViewById(R.id.msgTextView6); //内容
        msgTextView7 = findViewById(R.id.msgTextView7); //内容
        msgTextView8 = findViewById(R.id.msgTextView8); //内容
        msgTextView9 = findViewById(R.id.msgTextView9); //内容
        msgTextView10 = findViewById(R.id.msgTextView10); //内容
        msgTextView11 = findViewById(R.id.msgTextView11); //内容
        msgTextView12 = findViewById(R.id.msgTextView12); //内容

        msgTextView13 = findViewById(R.id.msgTextView13); //内容
        msgTextView14 = findViewById(R.id.msgTextView14); //内容

        edit1= findViewById(R.id.edit1); //编辑框1
        edit2= findViewById(R.id.edit2); //编辑框2
        edit3= findViewById(R.id.edit3); //编辑框3

        activate_button0 = findViewById(R.id.activate_button0);//配置
//-------------------------------配置
        activate_button0.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //String inputText1=edit1.getText().toString();//获取编辑框内的东西
                //String inputText2=edit2.getText().toString();
                //String inputText3=edit3.getText().toString();

                msgTextView4.setText("配置：成功");//文本显示
                //阿里云MQTT参数
                //productKey=inputText1;
                //deviceName=inputText2;
                //deviceSecret=inputText3;

                edit1.setText(productKey);
                edit2.setText(deviceName);
                edit3.setText(deviceSecret);

                Toast.makeText(MainActivity.this,"配置成功" ,Toast.LENGTH_SHORT).show();//打印显示
            }
        });
        findViewById(R.id.activate_button).setOnClickListener((l) -> {
            new Thread(() -> initAliyunIoTClient()).start();
            Toast.makeText(MainActivity.this,"连接" ,Toast.LENGTH_SHORT).show();
        });
        findViewById(R.id.post_button1).setOnClickListener((l) -> {
            new Thread(() -> postDeviceProperties1()).start();

        });
        findViewById(R.id.post_button2).setOnClickListener((l) -> {
            new Thread(() -> postDeviceProperties2()).start();

        });
        findViewById(R.id.post_button3).setOnClickListener((l) -> {
            new Thread(() -> postDeviceProperties3()).start();

        });
        findViewById(R.id.post_button4).setOnClickListener((l) -> {
            new Thread(() -> postDeviceProperties4()).start();

        });
        
        // 添加查看日志按钮点击事件
        findViewById(R.id.log_view_button).setOnClickListener((l) -> {
            showLogDialog();
        });
        
        // 添加清空日志按钮点击事件
        findViewById(R.id.log_clear_button).setOnClickListener((l) -> {
            mqttLogger.clearLog();
            Toast.makeText(MainActivity.this, "日志已清空", Toast.LENGTH_SHORT).show();
        });
    }
    //=====================================================================连接
    private void initAliyunIoTClient() {
        try {
            String clientId = "12345"+ System.currentTimeMillis();

            Map<String, String> params = new HashMap<String, String>(16);
            params.put("productKey", productKey);
            params.put("deviceName", deviceName);
            params.put("clientId", clientId);
            String timestamp = String.valueOf(System.currentTimeMillis());
            params.put("timestamp", timestamp);

            //cn-shanghai
            String targetServer ="tcp://"+ productKey + ".iot-as-mqtt.cn-shanghai.aliyuncs.com:1883";

            String mqttclientId = clientId + "|securemode=3,signmethod=hmacsha1,timestamp=" + timestamp + "|";
            String mqttUsername = deviceName + "&" + productKey;
            String mqttPassword = AliyunIoTSignUtil.sign(params, deviceSecret, "hmacsha1");

            connectMqtt(targetServer, mqttclientId, mqttUsername, mqttPassword);
        } catch (Exception e) {
            e.printStackTrace();
            responseBody = e.getMessage();
            mHandler.sendEmptyMessage(POST_DEVICE_PROPERTIES_ERROR);
        }
    }
    //=====================================================================连接阿里云函数
    public void connectMqtt(String url, String clientId, String mqttUsername, String mqttPassword) throws Exception {

        MemoryPersistence persistence = new MemoryPersistence();
        mqttClient = new MqttClient(url, clientId, persistence);
        MqttConnectOptions connOpts = new MqttConnectOptions();
        // MQTT 3.1.1
        connOpts.setMqttVersion(4);
        connOpts.setAutomaticReconnect(true);
        connOpts.setCleanSession(true);

        connOpts.setUserName(mqttUsername);
        connOpts.setPassword(mqttPassword.toCharArray());
        connOpts.setKeepAliveInterval(60);//设置会话心跳时间

        mqttClient.connect(connOpts);
        Log.d(TAG, "connected " + url);

        mqttClient.setCallback(new MqttCallback() {//回调
            @Override
            public void connectionLost(Throwable throwable) {//设置重连

            }

            @Override
            public void messageArrived(String s, MqttMessage mqttMessage) throws Exception {
                //处理消息
                //mHandler.sendEmptyMessage(POST_DEVICE_PROPERTIES_RX);

                System.out.println("messageArrived----------");
                
                // 保存MQTT消息到日志
                mqttLogger.saveMessage(s, mqttMessage.toString());
                
                Message msg = new Message();
                msg.what = POST_DEVICE_PROPERTIES_RX;   //收到消息标志位
                //msg.obj = s + "---" + mqttMessage.toString();
                msg.obj = mqttMessage.toString();//收到的数据
                mHandler.sendMessage(msg);      //hander 回传
            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {

            }
        });
    }
    //=====================================================================开灯
    private void postDeviceProperties1() {

        try {

            Random random = new Random();

            //上报数据
            String payload = String.format(payloadJson1, String.valueOf(System.currentTimeMillis()), 10 + random.nextInt(20), 50 + random.nextInt(50));
            responseBody = payload;
            MqttMessage message = new MqttMessage(payload.getBytes("utf-8"));
            message.setQos(1);


            String pubTopic = "/" + productKey + "/" + deviceName + "/user/update";
            mqttClient.publish(pubTopic, message);
            Log.d(TAG, "publish topic=" + pubTopic + ",payload=" + payload);
            mHandler.sendEmptyMessage(POST_DEVICE_PROPERTIES_SUCCESS);

            //mHandler.postDelayed(() -> postDeviceProperties1(), 5 * 1000);
        } catch (Exception e) {
            e.printStackTrace();
            //responseBody = e.getMessage();//发送失败
            mHandler.sendEmptyMessage(POST_DEVICE_PROPERTIES_ERROR);
            Log.e(TAG, "postDeviceProperties error " + e.getMessage(), e);
        }
    }
    //=====================================================================关灯
    private void postDeviceProperties2() {

        try {

            Random random = new Random();

            //上报数据
            String payload = String.format(payloadJson2, String.valueOf(System.currentTimeMillis()), 10 + random.nextInt(20), 50 + random.nextInt(50));
            responseBody = payload;
            MqttMessage message = new MqttMessage(payload.getBytes("utf-8"));
            message.setQos(1);


            String pubTopic = "/" + productKey + "/" + deviceName + "/user/update";
            mqttClient.publish(pubTopic, message);
            Log.d(TAG, "publish topic=" + pubTopic + ",payload=" + payload);
            mHandler.sendEmptyMessage(POST_DEVICE_PROPERTIES_SUCCESS);

            //mHandler.postDelayed(() -> postDeviceProperties2(), 5 * 1000);
        } catch (Exception e) {
            e.printStackTrace();
            //responseBody = e.getMessage();//发送失败
            mHandler.sendEmptyMessage(POST_DEVICE_PROPERTIES_ERROR);

            Log.e(TAG, "postDeviceProperties error " + e.getMessage(), e);
        }
    }
    private void postDeviceProperties3() {

        try {

            Random random = new Random();

            //上报数据
            String payload = String.format(payloadJson3, String.valueOf(System.currentTimeMillis()), 10 + random.nextInt(20), 50 + random.nextInt(50));
            responseBody = payload;
            MqttMessage message = new MqttMessage(payload.getBytes("utf-8"));
            message.setQos(1);

            String pubTopic = "/" + productKey + "/" + deviceName + "/user/update";
            mqttClient.publish(pubTopic, message);
            Log.d(TAG, "publish topic=" + pubTopic + ",payload=" + payload);
            mHandler.sendEmptyMessage(POST_DEVICE_PROPERTIES_SUCCESS);

            //mHandler.postDelayed(() -> postDeviceProperties2(), 5 * 1000);
        } catch (Exception e) {
            e.printStackTrace();
            //responseBody = e.getMessage();//发送失败
            mHandler.sendEmptyMessage(POST_DEVICE_PROPERTIES_ERROR);

            Log.e(TAG, "postDeviceProperties error " + e.getMessage(), e);
        }
    }
    private void postDeviceProperties4() {

        try {

            Random random = new Random();

            //上报数据
            String payload = String.format(payloadJson4, String.valueOf(System.currentTimeMillis()), 10 + random.nextInt(20), 50 + random.nextInt(50));
            responseBody = payload;
            MqttMessage message = new MqttMessage(payload.getBytes("utf-8"));
            message.setQos(1);

            String pubTopic = "/" + productKey + "/" + deviceName + "/user/update";
            mqttClient.publish(pubTopic, message);
            Log.d(TAG, "publish topic=" + pubTopic + ",payload=" + payload);
            mHandler.sendEmptyMessage(POST_DEVICE_PROPERTIES_SUCCESS);

            //mHandler.postDelayed(() -> postDeviceProperties2(), 5 * 1000);
        } catch (Exception e) {
            e.printStackTrace();
            //responseBody = e.getMessage();//发送失败
            mHandler.sendEmptyMessage(POST_DEVICE_PROPERTIES_ERROR);

            Log.e(TAG, "postDeviceProperties error " + e.getMessage(), e);
        }
    }
    
    // 显示日志对话框
    private void showLogDialog() {
        String logContent = mqttLogger.readLog();
        
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("MQTT消息日志");
        
        if (logContent.isEmpty()) {
            builder.setMessage("暂无日志记录");
        } else {
            builder.setMessage(logContent);
        }
        
        builder.setPositiveButton("确定", null);
        builder.show();
    }
    
    //==================================================================================打印函数
    private void showToast(String msg) {
        msgTextView.setText(msg + "\n" + responseBody);
    }

}