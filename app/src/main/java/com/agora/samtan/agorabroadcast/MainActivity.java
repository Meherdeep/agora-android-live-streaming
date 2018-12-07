package com.agora.samtan.agorabroadcast;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;

import io.agora.rtc.Constants;

public class MainActivity extends AppCompatActivity {

    int channelProfile;
    public static final String channelMessage = "com.agora.samtan.agorabroadcast.CHANNEL";
    public static final String profileMessage = "com.agora.samtan.agorabroadcast.PROFILE";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void onRadioButtonClicked(View view) {
        boolean checked = ((RadioButton) view).isChecked();
        switch (view.getId()) {
            case R.id.host:
                if (checked) {
                    channelProfile = Constants.CLIENT_ROLE_BROADCASTER;
                }
                break;
            case R.id.audience:
                if (checked) {
                    channelProfile = Constants.CLIENT_ROLE_AUDIENCE;
                }
                break;
        }
    }

    public void onSubmit(View view) {
        EditText channel = (EditText) findViewById(R.id.channel);
        String channelName = channel.getText().toString();
        Intent intent = new Intent(this, VideoActivity.class);
        intent.putExtra(channelMessage, channelName);
        intent.putExtra(profileMessage, channelProfile);
        startActivity(intent);
    }
}
