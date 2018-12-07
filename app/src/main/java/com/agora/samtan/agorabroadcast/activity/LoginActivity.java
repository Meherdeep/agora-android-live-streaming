package com.agora.samtan.agorabroadcast.activity;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import java.lang.ref.WeakReference;

import io.agora.AgoraAPI;
import io.agora.IAgoraAPI;

import com.agora.samtan.agorabroadcast.R;
import com.agora.samtan.agorabroadcast.VideoActivity;
import com.agora.samtan.agorabroadcast.sginatutorial.AGApplication;
import com.agora.samtan.agorabroadcast.utils.Constant;
import com.agora.samtan.agorabroadcast.utils.ToastUtils;


public class LoginActivity extends Activity {
    private final String TAG = LoginActivity.class.getSimpleName();

    private EditText textAccountName;
    private TextView textViewVersion;
    private String appId;
    private String channelName;
    private String account;
    private boolean enableLoginBtnClick = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i("TAG", "method started");
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_login);

        appId = getString(R.string.private_app_id);

        textAccountName = (EditText) findViewById(R.id.account_name);
        textViewVersion = (TextView) findViewById(R.id.login_version);
        Intent intent = getIntent();
        channelName = intent.getStringExtra(VideoActivity.LOGIN_MESSAGE);
    }

    //login siginal
    public void onClickLogin(View v) {

        if (enableLoginBtnClick) {
            account = textAccountName.getText().toString();

            if (account == null || account.equals("")) {
                ToastUtils.show(new WeakReference<Context>(LoginActivity.this), getString(R.string.str_msg_not_empty));

            } else if (account.length() >= Constant.MAX_INPUT_NAME_LENGTH) {
                ToastUtils.show(new WeakReference<Context>(LoginActivity.this), getString(R.string.str_msg_not_128));

            } else if (account.contains(" ")) {
                ToastUtils.show(new WeakReference<Context>(LoginActivity.this), getString(R.string.str_msg_not_contains_space));

            } else {
                enableLoginBtnClick = false;

                AGApplication.the().getmAgoraAPI().login2(appId, account, "_no_need_token", 0, "", 5, 1);
            }
        }

    }

    private void addCallback() {

        AGApplication.the().getmAgoraAPI().callbackSet(new AgoraAPI.CallBack() {

            @Override
            public void onLoginSuccess(int i, int i1) {
                Log.i(TAG, "onLoginSuccess " + i + "  " + i1);
                initUI();
            }

            @Override
            public void onLoginFailed(final int i) {
                Log.i(TAG, "onLoginFailed " + i);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (i == IAgoraAPI.ECODE_LOGIN_E_NET) {
                            enableLoginBtnClick = true;
                            ToastUtils.show(new WeakReference<Context>(LoginActivity.this), getString(R.string.str_msg_net_bad));
                        }
                    }
                });
            }


            @Override
            public void onError(String s, int i, String s1) {
                Log.i(TAG, "onError s:" + s + " s1:" + s1);
            }
            @Override
            public void onChannelJoined(String channelID) {
                super.onChannelJoined(channelID);

            }

            @Override
            public void onChannelJoinFailed(String channelID, int ecode) {
                super.onChannelJoinFailed(channelID, ecode);

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        enableChannelBtnClick = true;
                        ToastUtils.show(new WeakReference<Context>(LoginActivity.this), getString(R.string.str_join_channe_failed));
                    }
                });
            }

            @Override
            public void onChannelUserList(String[] accounts, final int[] uids) {
                super.onChannelUserList(accounts, uids);

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Intent intent = new Intent(LoginActivity.this, MessageActivity.class);
//                        intent.putExtra("mode", stateSingleMode);
                        intent.putExtra("name", channelName);
                        intent.putExtra("selfname", account);
                        intent.putExtra("usercount", uids.length);
                        startActivity(intent);

                    }
                });
            }

            @Override
            public void onLogout(final int i) {

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (i == IAgoraAPI.ECODE_LOGOUT_E_KICKED) { //other login the account
                            ToastUtils.show(new WeakReference<Context>(LoginActivity.this), "Other login account ,you are logout.");

                        } else if (i == IAgoraAPI.ECODE_LOGOUT_E_NET) { //net
                            ToastUtils.show(new WeakReference<Context>(LoginActivity.this), "Logout for Network can not be.");

                        }

                        finish();

                    }
                });

            }

            @Override
            public void onMessageInstantReceive(final String account, int uid, final String msg) {
                Log.i(TAG, "onMessageInstantReceive  account = " + account + " uid = " + uid + " msg = " + msg);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        Constant.addMessageBean(account, msg);

                    }
                });
            }

        });
    }

    @Override

    protected void onResume() {
        super.onResume();

        addCallback();
        enableLoginBtnClick = true;
        StringBuffer version = new StringBuffer("" + AGApplication.the().getmAgoraAPI().getSdkVersion());

        if (version.length() >= 8) {
            String strVersion = "version " + version.charAt(2) + "." + version.charAt(4) + "." + version.charAt(6);
            textViewVersion.setText(strVersion);
        }

    }
    private String selfAccount;
    private final int REQUEST_CODE = 0x01;

    private boolean stateSingleMode = false; // single mode or channel mode
    private boolean enableChannelBtnClick = true;


    private void initUI() {
        AGApplication.the().getmAgoraAPI().channelJoin(channelName);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK) {
            if (data != null && data.getStringExtra("result").equals("finish")) {
                finish();
            }

        }
    }

//    @Override
//    protected void onResume() {
//        super.onResume();
//        addCallback();
//        enableChannelBtnClick = true;
//    }

    @Override
    public void onBackPressed() {
        AGApplication.the().getmAgoraAPI().logout();
        Constant.cleanMessageListBeanList();
        super.onBackPressed();
    }

    public void onClickFinish(View v) {
        AGApplication.the().getmAgoraAPI().logout();
        /** logout clear messagelist**/
        Constant.cleanMessageListBeanList();
        finish();
    }

}
