package com.tools.payhelper;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Base64;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.tools.payhelper.bean.QrCodeBean;
import com.tools.payhelper.bean.WMessageBean;
import com.tools.payhelper.utils.Constants;
import com.tools.payhelper.utils.DBManager;
import com.tools.payhelper.utils.EncrptyUtil;
import com.tools.payhelper.utils.HttpUtil;
import com.tools.payhelper.utils.LogListAdapter;
import com.tools.payhelper.utils.LogUtil;
import com.tools.payhelper.utils.PayHelperUtils;
import com.tools.payhelper.utils.SharedUtil;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

import rx.Subscriber;
import ua.naiksoftware.stomp.Stomp;
import ua.naiksoftware.stomp.StompHeader;
import ua.naiksoftware.stomp.client.StompClient;
import ua.naiksoftware.stomp.client.StompCommand;
import ua.naiksoftware.stomp.client.StompMessage;

public class MainActivity extends Activity {

    private ListView listView;

    private BillReceived billReceived;

    private StompClient mStompClient;

    private LogListAdapter adapter;

    private List<String> logData = new ArrayList<>();

    private Timer timer;

    //what 1.log 2.qrcode 3.
    private Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case 1000:
                    String log = msg.getData().getString("log") + "";
                    LogUtil.d("收到日志 : " + log);
                    if (logData.size() > 100) {
                        logData.clear();
                        logData.add("日志清理完成...");
                        logData.add(log);
                    } else {
                        if (log.contains("当前登录")) {
                            boolean logined = false;
                            for (String string : logData) {
                                if (string.contains("当前登录")) {
                                    logined = true;
                                    break;
                                }
                            }
                            if (!logined) {
                                logData.add(log);
                            }
                        } else {
                            logData.add(log);
                        }
                    }
                    adapter.notifyDataSetChanged();
                    listView.post(() ->
                            listView.smoothScrollToPosition(logData.size() - 1)
                    );
                    break;
                case 1001:
                    PayHelperUtils.startAPP(MainActivity.this);
                    break;
                case 0:
                    break;
                case 1:
                    sendQRCodeMessage(msg);
                    break;
                case 4:
                    String message = (String) msg.obj;
                    sendQRFailMessage(message);
                    break;
                case 7:
                    sendUnionPayQRCodeMessage(msg);
                    break;
            }
            return false;
        }
    });

    private void sendQRCodeMessage(Message msg) {
        PayHelperUtils.startAPP(this);
        DBManager v4 = new DBManager(CustomApplcation.getInstance().getApplicationContext());
        List<QrCodeBean> qrCodeList = v4.FindQrCodes((String) msg.obj);

        if (qrCodeList.size() > 0) {
            QrCodeBean qrCodeBean = qrCodeList.get(0);

            WMessageBean wMessageBean = new WMessageBean();
            wMessageBean.setType(1);

            WMessageBean.DataobjBean dataobjBean = new WMessageBean.DataobjBean();
            dataobjBean.setAmount(Double.parseDouble(qrCodeBean.getMoney()));
            dataobjBean.setPayurl(qrCodeBean.getPayurl());
            dataobjBean.setRemark(qrCodeBean.getMark());
            dataobjBean.setPaytype(qrCodeBean.getType());

            String currentLoginAccount = SharedUtil.getString(CustomApplcation.getInstance(), qrCodeBean.getType());
            if (!TextUtils.isEmpty(currentLoginAccount)) {
                dataobjBean.setAccount(currentLoginAccount);
            }

            wMessageBean.setDataobj(dataobjBean);

            StompHeader stompHeader = new StompHeader(StompHeader.DESTINATION, "/qrcode");
            StompHeader stompHeadertoken = new StompHeader("token", "123");
            List<StompHeader> stompHeaders = new ArrayList<>();
            stompHeaders.add(stompHeader);
            stompHeaders.add(stompHeadertoken);

            Gson gson = new Gson();
            String message = EncrptyUtil.getBASE64(gson.toJson(wMessageBean, WMessageBean.class));
            StompMessage stompMessage = new StompMessage(StompCommand.SEND, stompHeaders, message);
            mStompClient.send(stompMessage).subscribe(new Subscriber<Object>() {
                @Override
                public void onCompleted() {
                    LogUtil.d("Send QRCode Completed");
                }

                @Override
                public void onError(Throwable e) {
                    LogUtil.d("Send QRCode Error : " + e.getMessage());
                }

                @Override
                public void onNext(Object o) {

                }
            });
        }
    }

    private void sendQRFailMessage(String failReason) {
        WMessageBean wMessageBean = new WMessageBean();
        wMessageBean.setType(4);

        WMessageBean.DataobjBean dataobjBean = new WMessageBean.DataobjBean();
        dataobjBean.setRemark(failReason);
        dataobjBean.setId(SharedUtil.getString(MainActivity.this, "id"));
        wMessageBean.setDataobj(dataobjBean);

        StompHeader stompHeader = new StompHeader(StompHeader.DESTINATION, "/qrcode");
        StompHeader stompHeadertoken = new StompHeader("token", "123");
        List<StompHeader> stompHeaders = new ArrayList<>();
        stompHeaders.add(stompHeader);
        stompHeaders.add(stompHeadertoken);

        Gson gson = new Gson();
        String message = EncrptyUtil.getBASE64(gson.toJson(wMessageBean, WMessageBean.class));
        StompMessage stompMessage = new StompMessage(StompCommand.SEND, stompHeaders, message);
        mStompClient.send(stompMessage).subscribe(new Subscriber<Object>() {
            @Override
            public void onCompleted() {
                LogUtil.d("Send Qr fail Completed");
            }

            @Override
            public void onError(Throwable e) {
                LogUtil.d("Send Qr fail Error : " + e.getMessage());
            }

            @Override
            public void onNext(Object o) {

            }
        });
    }

    private void sendUnionPayQRCodeMessage(Message msg) {
        Intent intent = (Intent) msg.obj;
        WMessageBean wMessageBean = new WMessageBean();
        wMessageBean.setType(1);

        WMessageBean.DataobjBean dataobjBean = new WMessageBean.DataobjBean();
        dataobjBean.setAmount(Double.parseDouble(intent.getStringExtra("money")));
        dataobjBean.setPayurl(intent.getStringExtra("payurl"));
        dataobjBean.setRemark(intent.getStringExtra("remark"));
        dataobjBean.setPaytype("unionpay");

        wMessageBean.setDataobj(dataobjBean);

        StompHeader stompHeader = new StompHeader(StompHeader.DESTINATION, "/qrcode");
        StompHeader stompHeadertoken = new StompHeader("token", "123");
        List<StompHeader> stompHeaders = new ArrayList<>();
        stompHeaders.add(stompHeader);
        stompHeaders.add(stompHeadertoken);

        Gson gson = new Gson();
        String message = EncrptyUtil.getBASE64(gson.toJson(wMessageBean, WMessageBean.class));
        StompMessage stompMessage = new StompMessage(StompCommand.SEND, stompHeaders, message);
        mStompClient.send(stompMessage).subscribe(new Subscriber<Object>() {
            @Override
            public void onCompleted() {
                LogUtil.d("Send QRCode Completed");
            }

            @Override
            public void onError(Throwable e) {
                LogUtil.d("Send QRCode Error : " + e.getMessage());
            }

            @Override
            public void onNext(Object o) {

            }
        });
    }

    private TimerTask timerTask = new TimerTask() {
        @Override
        public void run() {
            if (mStompClient == null || !mStompClient.isConnected()) {
                sendLogMsg("开始连接...");
                createWebSocketClient();
            } else {
                mStompClient.send(null, "").subscribe(new Subscriber<Void>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                    }

                    @Override
                    public void onNext(Void aVoid) {
                    }
                });
            }
        }
    };

    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        getWindow().addFlags(0x80);
        setContentView(R.layout.activity_main);
        sendLogMsg("系统启动...");

        findViewById(R.id.logout).setOnClickListener((View v) ->
                showQuitDialog()
        );
        LinearLayout testGroup = (LinearLayout) findViewById(R.id.test_group);
        if (HttpUtil.isDebug) {
            testGroup.setVisibility(View.VISIBLE);
            EditText money = (EditText) findViewById(R.id.money);
            EditText mark = (EditText) findViewById(R.id.mark);
            findViewById(R.id.see).setOnClickListener((View v) -> {
                Intent broadCastIntent = new Intent();
                broadCastIntent.setAction(Constants.XGJ_GET_ORDER_HTTP_ACTION);
                broadCastIntent.putExtra("type", "dingding_group");
                broadCastIntent.putExtra("money", money.getText().toString());
                broadCastIntent.putExtra("remark", mark.getText().toString());
                sendBroadcast(broadCastIntent);
            });
        } else {
            testGroup.setVisibility(View.GONE);
        }

        TextView loginID = (TextView) findViewById(R.id.login_id);
        String currentID = "ID:" + SharedUtil.getString(this, SharedUtil.USERNAME);
        loginID.setText(currentID);
        loginID.setTextColor(Color.RED);

        listView = (ListView) findViewById(R.id.log_list);
        adapter = new LogListAdapter(this, logData);
        listView.setAdapter(adapter);

        billReceived = new BillReceived();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Constants.BILLRECEIVED_ACTION);
        intentFilter.addAction(Constants.MSGRECEIVED_ACTION);
        intentFilter.addAction(Constants.QRCODERECEIVED_ACTION);
        intentFilter.addAction(Constants.QRCODE_FAILED_ACTION);
        intentFilter.addAction(Constants.UNION_PAY_NOTIFICATION_ACTION);
        intentFilter.addAction(Constants.UNION_PAY_GET_QRCODE_RESULT_ACTION);
        intentFilter.addAction(Constants.XGJ_GET_ORDER_RESULT_ACTION);
        intentFilter.addAction(Constants.XGJ_GET_ORDER_NO_RESULT_ACTION);
        registerReceiver(this.billReceived, intentFilter);

        timer = new Timer();
        timer.schedule(timerTask, 1500, 30 * 1000);
    }

    private void createWebSocketClient() {
        createStompClient();
        registerMemberStompTopic();
    }

    //创建对象registerStompTopic
    private void createStompClient() {
        mStompClient = Stomp.over(Stomp.ConnectionProvider.OKHTTP, HttpUtil.getInstance().getSOCKET_URL());
        List<StompHeader> headers = new ArrayList<>();
        StompHeader header = new StompHeader(StompHeader.ID, SharedUtil.getString(this, SharedUtil.ID));
        headers.add(header);
        mStompClient.connect(headers);
        mStompClient.lifecycle().subscribe(lifecycleEvent -> {
            switch (lifecycleEvent.getType()) {
                case OPENED:
                    sendLogMsg("连接成功...");
                    break;
                case ERROR:
                    sendLogMsg("连接失败：" + lifecycleEvent.getException().getMessage());
                    break;
                case CLOSED:
                    sendLogMsg("连接关闭...");
                    break;
            }
        });
    }

    //订阅面向个人的消息
    private void registerMemberStompTopic() {
        String group = "/user/" + SharedUtil.getString(this, "id") + "/message";
        mStompClient.topic(group).subscribe(stompMessage ->
                processWSMessage(stompMessage.getPayload())
        );
    }

    public void onBackPressed() {
        showQuitDialog();
    }

    protected void onDestroy() {
        timer.cancel();
        if (mStompClient != null && mStompClient.isConnected()) {
            mStompClient.disconnect();
        }
        unregisterReceiver(this.billReceived);
        super.onDestroy();
    }

    public boolean onKeyDown(int arg3, KeyEvent arg4) {
        if (arg4.getKeyCode() == 4) {
            showQuitDialog();
        }
        return super.onKeyDown(arg3, arg4);
    }

    class BillReceived extends BroadcastReceiver {

        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (!TextUtils.isEmpty(action)) {
                if (action.contentEquals(Constants.QRCODERECEIVED_ACTION)) {
                    String bill_money = intent.getStringExtra("money");
                    String bill_mark = intent.getStringExtra("mark");
                    String bill_type = intent.getStringExtra("type");
                    String payurl = intent.getStringExtra("payurl");
                    new DBManager(CustomApplcation.getInstance().getApplicationContext()).addQrCode(new QrCodeBean(bill_money, bill_mark, bill_type, payurl, String.valueOf(System.currentTimeMillis())));
                    Message message = handler.obtainMessage(1);
                    message.obj = bill_mark;
                    handler.sendMessage(message);
                    if (TextUtils.equals(bill_type, "ddredbag")) {
                        sendLogMsg("生成成功,金额：" + bill_money + "，备注：" + bill_mark);
                    } else {
                        sendLogMsg("生成成功,金额：" + bill_money + "，备注：" + bill_mark + "，二维码：" + payurl);
                    }
                    LogUtil.d("生成成功,金额：" + bill_money + "，备注：" + bill_mark + "，二维码：" + payurl);
                }
                if (action.contentEquals(Constants.QRCODE_FAILED_ACTION)) {
                    Message message = handler.obtainMessage(4);
                    message.obj = intent.getStringExtra("reason");
                    handler.sendMessage(message);
                }
                if (action.contentEquals(Constants.MSGRECEIVED_ACTION)) {
                    sendLogMsg(intent.getStringExtra("msg"));
                }
                if (action.contentEquals(Constants.UNION_PAY_NOTIFICATION_ACTION)) {
                    handler.sendEmptyMessage(1001);
                    String dbOrderId = intent.getStringExtra("dbOrderId");
                    String remark = intent.getStringExtra("remark");
                    String money = intent.getStringExtra("money");
                    String orderTime = intent.getStringExtra("orderTime");
                    String payaccountno = intent.getStringExtra("payaccountno");
                    String payaccountname = intent.getStringExtra("payaccountname");
                    sendLogMsg(Long.valueOf(money) / 100f + " 元已到账！");
                    PayHelperUtils.notifyUnionPayTrade(MainActivity.this, dbOrderId, money, remark, orderTime, payaccountno, payaccountname);
                }
                if (action.contentEquals(Constants.UNION_PAY_GET_QRCODE_RESULT_ACTION)) {
                    handler.sendEmptyMessage(1001);
                    String payurl = intent.getStringExtra("payurl");
                    sendLogMsg("QRCode URL: " + payurl);
                    Message message = new Message();
                    message.what = 7;
                    message.obj = intent;
                    handler.sendMessage(message);
                }
                if (action.contentEquals(Constants.XGJ_GET_ORDER_RESULT_ACTION)) {
                    String money = intent.getStringExtra("money");
                    String type = intent.getStringExtra("type");
                    String payType;
                    if ("1".equals(type)) {
                        payType = "支付宝";
                    } else if ("2".equals(type)) {
                        payType = "微信";
                    } else {
                        payType = "其他";
                    }
                    String dt = intent.getStringExtra("dt");
                    String business = intent.getStringExtra("business");
                    sendLogMsg("查询到新的星POS订单,金额：" + money + " ，支付类型:" + payType + ",支付时间：" + dt + " ,商户：" + business);
                    PayHelperUtils.notifyXPosTrade(MainActivity.this, money, type, dt);
                }
            }
        }
    }

    private void sendLogMsg(String msg) {
        Message message = new Message();
        message.what = 1000;
        Bundle bundle = new Bundle();
        String time = String.valueOf(new SimpleDateFormat("MM-dd HH:mm:ss", Locale.CHINA).format(new Date(System.currentTimeMillis())));
        bundle.putString("log", time + ": " + msg);
        message.setData(bundle);
        handler.sendMessage(message);
    }

    private void processWSMessage(String message) {
        String decodeMessage = new String(Base64.decode(message, Base64.NO_WRAP));
        Gson gson = new Gson();
        WMessageBean wMessageBean = gson.fromJson(decodeMessage, WMessageBean.class);
        switch (wMessageBean.getType()) {
            case 0:
                createQRCode(wMessageBean.getDataobj());
                break;
        }
    }

    private void createQRCode(WMessageBean.DataobjBean createCodeBean) {
        switch (createCodeBean.getPaytype()) {
            case "unionpay":
                if (!PayHelperUtils.isAppRunning(this, "com.unionpay")) {
                    PayHelperUtils.startAPP(this, "com.unionpay");
                }
                break;
        }
        sendLogMsg("收到生成二维码请求: 类型 = " + createCodeBean.getPaytype() + " ,金额 = " + createCodeBean.getAmount() + " ,备注 = " + createCodeBean.getRemark() + " ,userid = " + createCodeBean.getAppuserid());
        PayHelperUtils.sendAppMsg(String.valueOf(createCodeBean.getAmount()), createCodeBean.getRemark(), createCodeBean.getAppuserid(), createCodeBean.getPaytype(), this);
    }

    private void showQuitDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("退出");
        builder.setMessage("确认退出？");
        builder.setNegativeButton("取消", null);
        builder.setPositiveButton("确认", (DialogInterface dialog, int which) -> {
            SharedUtil.putString(this, SharedUtil.ID, "");
            SharedUtil.putString(this, SharedUtil.MD5_KEY, "");
            SharedUtil.putString(this, SharedUtil.AES_KEY, "");
            finish();
        });
        android.app.AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
}

