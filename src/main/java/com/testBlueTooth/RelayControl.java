package com.testBlueTooth;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;
import android.widget.RadioGroup.OnCheckedChangeListener;

import static java.io.File.separator;

public class RelayControl extends Activity implements OnCheckedChangeListener {

    private int i = 0;
    private int TIME = 1000;
    static int a, b, c, d, e = 0;
    static String wendu,shidu,zaoyin,guangzhao,xinlv;
    static DecimalFormat df = new DecimalFormat("#.00");
    public static boolean isRecording = false;// 线程控制标记
    private Button releaseCtrl, btSend, btCancel, analyse;
    private OutputStream outStream = null;
    private EditText _txtRead;
    private TextView _txtSend;
    private ConnectedThread manageThread;
    private Handler mHandler;
    private EditText editText_tem, editText_wet, editText_ray, editText_noi, editText_hea;
    private String encodeType = "GBK";

    //xiaolai
    private StringBuffer buffer;
    private String[] text = {};
    private List<Integer> list;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.relaycontrol);
        editText_tem = (EditText) findViewById(R.id.et_tem);
        editText_wet = (EditText) findViewById(R.id.et_wet);
        editText_noi = (EditText) findViewById(R.id.et_noi);
        editText_ray = (EditText) findViewById(R.id.et_ray);
        editText_hea = (EditText) findViewById(R.id.et_hea);
        analyse = (Button)findViewById(R.id.analyse);
        buffer = new StringBuffer();
        list = new ArrayList<Integer>();
        //接收线程启动
        manageThread = new ConnectedThread();
        mHandler = new MyHandler();
        manageThread.Start();
        findMyView();
        setMyViewListener();
        setTitle("返回前需先关闭socket连接");
        _txtRead.setCursorVisible(true);      //接收区不可见,设置输入框中的光标不可见
        _txtRead.setFocusable(true);           //无焦点
        timer.schedule(task, 5000, 2000); // 1s后执行task,经过1s再次执行
    }



    //保存
    private void findMyView() {
        releaseCtrl = (Button) findViewById(R.id.button1);
        btSend = (Button) findViewById(R.id.btSend);
        _txtRead = (EditText) findViewById(R.id.etShow);
        _txtSend = (TextView) findViewById(R.id.tvSend);
    }

    private void setMyViewListener() {
        // 监听RadioButton
        releaseCtrl.setOnClickListener(new ClickEvent());
        btSend.setOnClickListener(new ClickEvent());
    }

    @Override
    public void onDestroy() {
        try {
            testBlueTooth.btSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (testBlueTooth.btAdapt.isEnabled()) {
            testBlueTooth.btAdapt.disable();
        }
        super.onDestroy();
    }

    public void onCheckedChanged(RadioGroup group, int checkedId) {

        switch (checkedId) {
            default:
                break;
        }
    }

    private class ClickEvent implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            if (v == releaseCtrl)// 释放连接
            {
                manageThread.Stop();
                setTitle("socket连接已关闭");
            } else if (v == btCancel)
            {
                manageThread.Stop();
                testBlueTooth.serverThread.cancel();
            } else if (v == btSend) {
                String infoSend = _txtSend.getText().toString();
                sendMessage(infoSend);
                setTitle("发送成功");
            }
        }
    }

    public void sendMessage(String message) {

        //控制模块
        try {
            outStream = testBlueTooth.btSocket.getOutputStream();
        } catch (IOException e) {
            Toast.makeText(getApplicationContext(), " Output stream creation failed.", Toast.LENGTH_SHORT);
        }
        byte[] msgBuffer = null;
        try {
            msgBuffer = message.getBytes(encodeType);//编码
        } catch (UnsupportedEncodingException e1) {
            e1.printStackTrace();
            Log.e("write", "Exception during write encoding GBK ", e1);
        }
        try {
            outStream.write(msgBuffer);
            Toast.makeText(getApplicationContext(), "发送数据中..", Toast.LENGTH_SHORT);
            setTitle("成功发送指令:" + message);
        } catch (IOException e) {
            Toast.makeText(getApplicationContext(), "发送数据失败", Toast.LENGTH_SHORT);
        }
    }

    class ConnectedThread extends Thread {
        private InputStream inStream = null;// 蓝牙数据输入流
        private long wait;
        private Thread thread;
        public ConnectedThread() {
            isRecording = false;
            this.wait = 50;
            thread = new Thread(new ReadRunnable());
        }

        public void Stop() {
            isRecording = false;
        }

        public void Start() {
            isRecording = true;
            State aa = thread.getState();
            if (aa == State.NEW) {
                thread.start();
            } else thread.resume();
        }

        private class ReadRunnable implements Runnable {
            public void run() {
                while (isRecording) {
                    try {
                        inStream = testBlueTooth.btSocket.getInputStream();
                    } catch (IOException e) {
                        Toast.makeText(getApplicationContext(), " input stream creation failed.", Toast.LENGTH_SHORT);
                    }
                    int length = 20;
                    byte[] temp = new byte[length];
                    if (inStream != null) {
                        try {
                            int len = inStream.read(temp, 0, length - 1);
                            //Log.e("available", String.valueOf(len));
                            if (len > 0) {
                                byte[] btBuf = new byte[len];
                                System.arraycopy(temp, 0, btBuf, 0, btBuf.length);
                                //读编码
                                String readStr1 = new String(btBuf, encodeType);
                                mHandler.obtainMessage(01, len, -1, readStr1).sendToTarget();
                            }
                            Thread.sleep(wait);// 延时一定时间缓冲数据
                        } catch (Exception e) {
                            mHandler.sendEmptyMessage(00);
                        }
                    }
                }
            }
        }
    }

    //控件响应代码
    private class MyHandler extends Handler {
        @Override
        public void dispatchMessage(Message msg) {
            switch (msg.what) {
                case 00:
                    isRecording = false;
                    _txtRead.setText("");
                    _txtRead.setHint("socket连接已关闭");
                    //_txtRead.setText("inStream establishment Failed!");
                    Log.e("info33", buffer.toString().trim());
                    break;

                case 01:
                    String info = (String) msg.obj;
                    //Log.e("info", info.trim());
                    if (!info.equals("")) {
                        buffer.append(info.trim());

                        _txtRead.append(info);
                    }
                    break;

                default:
                    break;
            }
        }
    }


    Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            if (msg.what == 1) {
                String contain = _txtRead.getText().toString();
                //测试用的数据
                String contain2 = "Read sensor: OK\r\n" +
                        "Humidity (%): 48.00\r\n" +
                        "Temperature (oC): 26.00\r\n" +
                        "Temperature (oF): 78.80\r\n" +
                        "Temperature (K): 299.15\r\n" +
                        "Dew Point (oC): 14.16\r\n" +
                        "Dew PointFast (oC): 14.13\r\n" +
                        "The current UV index is:0\r\n" +
                        "heart rate sensor:136\r\n" +
                        "The loudness is:259\r\n" +
                        "\r\n" +
                        "\r\n" +
                        "Read sensor: OK\r\n" +
                        "Humidity (%): 51.00\r\n" +
                        "Temperature (oC): 25.00\r\n" +
                        "Temperature (oF): 77.00\r\n" +
                        "Temperature (K): 298.15\r\n" +
                        "Dew Point (oC): 14.18\r\n" +
                        "Dew PointFast (oC): 14.15\r\n" +
                        "The current UV index is:0\r\n" +
                        "heart rate sensor:126\r\n" +
                        "The loudness is:349\r\n" +
                        "\r\n" +
                        "\r\n" +
                        "Read sensor: OK\r\n" +
                        "Humidity (%): 49.00\r\n" +
                        "Temperature (oC): 25.00\r\n" +
                        "Temperature (oF): 77.00\r\n" +
                        "Temperature (K): 298.15\r\n" +
                        "Dew Point (oC): 13.56\r\n" +
                        "Dew PointFast (oC): 13.53\r\n" +
                        "The current UV index is:0\r\n" +
                        "heart rate sensor:120\r\n" +
                        "The loudness is:290\r\n" +
                        "\r\n" +
                        "\r\n" +
                        "Read sensor: OK\r\n" +
                        "Humidity (%): 47.00\r\n" +
                        "Temperature (oC): 27.00\r\n" +
                        "Temperature (oF): 80.60\r\n" +
                        "Temperature (K): 300.15\r\n" +
                        "Dew Point (oC): 14.75\r\n" +
                        "Dew PointFast (oC): 14.71\r\n" +
                        "The current UV index is:0\r\n" +
                        "heart rate sensor:116\r\n" +
                        "The loudness is:322\r\n" +
                        "\r\n" +
                        "\r\n" +
                        "Read sensor: OK\r\n" +
                        "Humidity (%): 48.00\r\n" +
                        "Temperature (oC): 26.00\r\n" +
                        "Temperature (oF): 78.80\r\n" +
                        "Temperature (K): 299.15\r\n" +
                        "Dew Point (oC): 14.16\r\n" +
                        "Dew PointFast (oC): 14.13\r\n" +
                        "The current UV index is:0\r\n" +
                        "heart rate sensor:114\r\n" +
                        "The loudness is:347\r\n" +
                        "\r\n" +
                        "\r\n" +
                        "Read sensor: OK\r\n" +
                        "Humidity (%): 48.00\r\n" +
                        "Temperature (oC): 26.00\r\n" +
                        "Temperature (oF): 78.80\r\n" +
                        "Temperature (K): 299.15\r\n" +
                        "Dew Point (oC): 14.16\r\n" +
                        "Dew PointFast (oC): 14.13\r\n" +
                        "The current UV index is:0\r\n" +
                        "heart rate sensor:113\r\n" +
                        "The loudness is:390\r\n" +
                        "\r\n" +
                        "\r\n" +
                        "Read sensor: OK\r\n" +
                        "Humidity (%): 52.00\r\n" +
                        "Temperature (oC): 25.00\r\n" +
                        "Temperature (oF): 77.00\r\n" +
                        "Temperature (K): 298.15\r\n" +
                        "Dew Point (oC): 14.48\r\n" +
                        "Dew PointFast (oC): 14.45\r\n" +
                        "The current UV index is:0\r\n" +
                        "heart rate sensor:113\r\n" +
                        "The loudness is:374\r\n" +
                        "\r\n" +
                        "\r\n" +
                        "Read sensor: OK\r\n" +
                        "Humidity (%): 46.00\r\n" +
                        "Temperature (oC): 25.00\r\n" +
                        "Temperature (oF): 77.00\r\n" +
                        "Temperature (K): 298.15\r\n" +
                        "Dew Point (oC): 12.60\r\n" +
                        "Dew PointFast (oC): 12.56\r\n" +
                        "The current UV index is:0\r\n" +
                        "heart rate sensor:112\r\n" +
                        "The loudness is:344\r\n" +
                        "\r\n" +
                        "\r\n" +
                        "Read sensor: OK\r\n" +
                        "Humidity (%): 48.00\r\n" +
                        "Temperature (oC): 24.00\r\n" +
                        "Temperature (oF): 75.20\r\n" +
                        "Temperature (K): 297.15\r\n" +
                        "Dew Point (oC): 12.33\r\n" +
                        "Dew PointFast (oC): 12.30\r\n" +
                        "The current UV index is:0\r\n" +
                        "heart rate sensor:112\r\n" +
                        "The loudness is:350\r\n" +
                        "\r\n" +
                        "\r\n" +
                        "Read sensor: OK\r\n" +
                        "Humidity (%): 47.00\r\n" +
                        "Temperature (oC): 26.00\r\n" +
                        "Temperature (oF): 78.80\r\n" +
                        "Temperature (K): 299.15\r\n" +
                        "Dew Point (oC): 13.84\r\n" +
                        "Dew PointFast (oC): 13.80\r\n" +
                        "The current UV index is:0\r\n" +
                        "heart rate sensor:112\r\n" +
                        "The loudness is:345\r\n" +
                        "\r\n" +
                        "\r\n" +
                        "Read sensor: OK\r\n" +
                        "Humidity (%): 53.00\r\n" +
                        "Temperature (oC): 25.00\r\n" +
                        "Temperature (oF): 77.00\r\n" +
                        "Temperature (K): 298.15\r\n" +
                        "Dew Point (oC): 14.78\r\n" +
                        "Dew PointFast (oC): 14.74\r\n" +
                        "The current UV index is:0\r\n" +
                        "heart rate sensor:99\r\n" +
                        "The loudness is:296\r\n" +
                        "\r\n" +
                        "\r\n" +
                        "Read sensor: OK\r\n" +
                        "Humidity (%): 48.00\r\n" +
                        "Temperature (oC): 24.00\r\n" +
                        "Temperature (oF): 75.20\r\n" +
                        "Temperature (K): 297.15\r\n" +
                        "Dew Point (oC): 12.33\r\n" +
                        "Dew PointFast (oC): 12.30\r\n" +
                        "The current UV index is:0\r\n" +
                        "heart rate sensor:80\r\n" +
                        "The loudness is:369\r\n" +
                        "\r\n" +
                        "\r\n" +
                        "Read sensor: OK\r\n" +
                        "Humidity (%): 48.00\r\n" +
                        "Temperature (oC): 24.00\r\n" +
                        "Temperature (oF): 75.20\r\n" +
                        "Temperature (K): 297.15\r\n" +
                        "Dew Point (oC): 12.33\r\n" +
                        "Dew PointFast (oC): 12.30\r\n" +
                        "The current UV index is:0\r\n" +
                        "heart rate sensor:69\r\n" +
                        "The loudness is:311";
                String[] sourceStrArray = contain.split("\r\n");
                for (int i = 0; i < sourceStrArray.length; i++) {
                    c++;
                    if ("".equals(sourceStrArray[i])) continue;
                    if ("".equals(sourceStrArray[i])) continue;
                    Log.e("dayin", "第" + i + "个是" + sourceStrArray[i]);
                    try{
                        for (int h = 1; h < i; h++) {
                            //1、下面是动态读取湿度
                            if (i == (11 * h - 9)) {
                                if ("".equals(sourceStrArray[i].toString()))continue;
                                if ("Read sensor".equals(sourceStrArray[i].toString()))continue;
                                if ("Humidity (%)".equals(sourceStrArray[i].toString()))continue;
                                if ("Temperature (oC)".equals(sourceStrArray[i].toString()))continue;
                                if ("Temperature (oF)".equals(sourceStrArray[i].toString()))continue;
                                if ("Temperature (K)".equals(sourceStrArray[i].toString()))continue;
                                if ("Dew Point (oC)".equals(sourceStrArray[i].toString()))continue;
                                if ("Dew PointFast (oC)".equals(sourceStrArray[i].toString()))continue;
                                if ("The current UV index is".equals(sourceStrArray[i].toString()))continue;
                                if ("heart rate sensor".equals(sourceStrArray[i].toString()))continue;
                                if ("The loudness is".equals(sourceStrArray[i].toString()))continue;
                                String[] temp = sourceStrArray[i].split(":");
                                if (null!=temp[1]){
                                    editText_wet.setText(temp[1].toString());
                                    shidu = temp[1].toString();
                                }else {
                                    continue;
                                }
                            }
                            //动态读取温度
                            if (i == (11 * h - 8)) {
                                Log.e("test", sourceStrArray[i]);
                                if ("".equals(sourceStrArray[i].toString()))continue;
                                if ("Read sensor".equals(sourceStrArray[i].toString()))continue;
                                if ("Humidity (%)".equals(sourceStrArray[i].toString()))continue;
                                if ("Temperature (oC)".equals(sourceStrArray[i].toString()))continue;
                                if ("Temperature (oF)".equals(sourceStrArray[i].toString()))continue;
                                if ("Temperature (K)".equals(sourceStrArray[i].toString()))continue;
                                if ("Dew Point (oC)".equals(sourceStrArray[i].toString()))continue;
                                if ("Dew PointFast (oC)".equals(sourceStrArray[i].toString()))continue;
                                if ("The current UV index is".equals(sourceStrArray[i].toString()))continue;
                                if ("heart rate sensor".equals(sourceStrArray[i].toString()))continue;
                                if ("The loudness is".equals(sourceStrArray[i].toString()))continue;
                                if (sourceStrArray[i].length()<19){continue;}
                                String[] temp = sourceStrArray[i].split(":");
                                try{
                                    if (null!=temp[1]) {
                                        editText_tem.setText(temp[1].toString());
                                        wendu = temp[1].toString();
                                    } else {
                                        continue;
                                    }
                                }catch (ArrayIndexOutOfBoundsException e3){
                                    throw e3;
                                }
                            }
                            //动态读取紫外线强度
                            if ((i == (11 * h - 3))&&(i<sourceStrArray[i].length())) {
                                Log.e("test", sourceStrArray[i]);
                                if ("".equals(sourceStrArray[i].toString()))continue;
                                if ("Read sensor".equals(sourceStrArray[i].toString()))continue;
                                if ("Humidity (%)".equals(sourceStrArray[i].toString()))continue;
                                if ("Temperature (oC)".equals(sourceStrArray[i].toString()))continue;
                                if ("Temperature (oF)".equals(sourceStrArray[i].toString()))continue;
                                if ("Temperature (K)".equals(sourceStrArray[i].toString()))continue;
                                if ("Dew Point (oC)".equals(sourceStrArray[i].toString()))continue;
                                if ("Dew PointFast (oC)".equals(sourceStrArray[i].toString()))continue;
                                if ("The current UV index is".equals(sourceStrArray[i].toString()))continue;
                                if ("heart rate sensor".equals(sourceStrArray[i].toString()))continue;
                                if ("The loudness is".equals(sourceStrArray[i].toString()))continue;
                                String[] temp = sourceStrArray[i].split(":");
                                try {
                                    if (null!=temp[1]) {
                                        editText_ray.setText(temp[1].toString());
                                        guangzhao = temp[1].toString();
                                    } else {
                                        continue;
                                    }
                                }catch (ArrayIndexOutOfBoundsException e4){
                                    throw e4;
                                }

                            }
                            //动态读取心率
                            if (i == (11 * h - 2)) {
                                Log.e("test", sourceStrArray[i]);
                                if ("Read sensor".equals(sourceStrArray[i].toString()))continue;
                                if ("Humidity (%)".equals(sourceStrArray[i].toString()))continue;
                                if ("Temperature (oC)".equals(sourceStrArray[i].toString()))continue;
                                if ("Temperature (oF)".equals(sourceStrArray[i].toString()))continue;
                                if ("Temperature (K)".equals(sourceStrArray[i].toString()))continue;
                                if ("Dew Point (oC)".equals(sourceStrArray[i].toString()))continue;
                                if ("Dew PointFast (oC)".equals(sourceStrArray[i].toString()))continue;
                                if ("The current UV index is".equals(sourceStrArray[i].toString()))continue;
                                if ("heart rate sensor".equals(sourceStrArray[i].toString()))continue;
                                if ("The loudness is".equals(sourceStrArray[i].toString()))continue;
                                String[] temp = sourceStrArray[i].split(":");
                                try {
                                    if (null!=temp[1]) {
                                        editText_hea.setText(temp[1].toString());
                                        xinlv = temp[1].toString();
                                    } else {
                                        continue;
                                    }
                                }catch (ArrayIndexOutOfBoundsException e5){
                                    throw  e5;
                                }
                            }
                            //动态读取噪音强度
                            if ((i == (11 * h - 1))) {
                                Log.e("zaoyin1", sourceStrArray[i]);
                                if ("".equals(sourceStrArray[i].toString()))continue;
                                if ("Read sensor".equals(sourceStrArray[i].toString()))continue;
                                if ("Humidity (%)".equals(sourceStrArray[i].toString()))continue;
                                if ("Temperature (oC)".equals(sourceStrArray[i].toString()))continue;
                                if ("Temperature (oF)".equals(sourceStrArray[i].toString()))continue;
                                if ("Temperature (K)".equals(sourceStrArray[i].toString()))continue;
                                if ("Dew Point (oC)".equals(sourceStrArray[i].toString()))continue;
                                if ("Dew PointFast (oC)".equals(sourceStrArray[i].toString()))continue;
                                if ("The current UV index is".equals(sourceStrArray[i].toString()))continue;
                                if ("heart rate sensor".equals(sourceStrArray[i].toString()))continue;
                                if ("The loudness is".equals(sourceStrArray[i].toString()))continue;

                                String[] temp = sourceStrArray[i].split(":");
                                try{
                                    if (null!=temp[1]) {
                                        editText_noi.setText(temp[1].toString());
                                        zaoyin = temp[1].toString();
                                        Log.e("zaoyin1", sourceStrArray[i]);
                                    } else {
                                        continue;
                                    }
                                }catch (ArrayIndexOutOfBoundsException e6){
                                    throw e6;
                                }
                            } else {
                                continue;
                            }
                        }
                    }catch (ArrayIndexOutOfBoundsException e){
                        throw e;
                    }
                }
            }
         //跳转到数据分析界面
            analyse.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //跳转到数据分析
                    Intent intent = new Intent(RelayControl.this, DataAnalysis.class);
                    Bundle bundle = new Bundle();
                    bundle.putString("wendu", editText_tem.getText().toString());
                    bundle.putString("shidu", editText_wet.getText().toString());
                    bundle.putString("zaoyin", editText_noi.getText().toString());
                    bundle.putString("guangzhao", editText_ray.getText().toString());
                    bundle.putString("xinlv", editText_wet.getText().toString());
                    intent.putExtras(bundle);
                    startActivity(intent);
                }
            });
            super.handleMessage(msg);
        };
    };
    Timer timer = new Timer();
    TimerTask task = new TimerTask() {

        @Override
        public void run() {
            // 需要做的事:发送消息
            Message message = new Message();
            message.what = 1;
            handler.sendMessage(message);
        }
    };




}