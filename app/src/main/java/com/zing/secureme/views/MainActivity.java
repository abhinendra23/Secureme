package com.zing.secureme.views;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import com.zing.secureme.SharedViewModel;

import com.zing.secureme.Constants.AlarmMode;
import com.zing.secureme.Constants.MySharedPreferenceConstants;
import com.zing.secureme.Constants.TimeConstants;

import com.zing.secureme.databinding.ActivityMainBinding;
import com.zing.secureme.receivers.AlarmReceiver;
import com.zing.secureme.receivers.BootReceiver;
import com.zing.secureme.utils.MyAlarmManager;
import com.zing.secureme.utils.TimeDefinerString;

public class MainActivity extends AppCompatActivity {

    private AlarmMode mAlarmMode;
    private ActivityMainBinding mBinding;

    private SharedPreferences mSharedPreferences;
    private SharedPreferences.Editor mEditor;
    private float ANIM_ON_START = 0f;
    private float ANIM_ON_END = 0.43f;
    private float ANIM_OFF_START = 0.5f;
    private float ANIM_OFF_END = 1f;

    private long savedTime;

    private SharedViewModel mSharedViewModel;
    private AlarmManager alarmManager;
    boolean status = false;

    private Intent mIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(mBinding.getRoot());


        mSharedPreferences = getSharedPreferences(MySharedPreferenceConstants.sharedPreferenceName, MODE_PRIVATE);
        mEditor = mSharedPreferences.edit();

        alarmManager = MyAlarmManager.getAlarmManager(this);

        mSharedViewModel = new ViewModelProvider(this).get(SharedViewModel.class);

        savedTime = mSharedPreferences.getLong(MySharedPreferenceConstants.KEY_LONG_TIME, 0);

        mIntent = new Intent(getApplicationContext(), AlarmReceiver.class);

        //checking for existing alarm
        if (MyAlarmManager.getPendingIntent(getApplicationContext(), PendingIntent.FLAG_NO_CREATE) != null) {
            mAlarmMode = AlarmMode.ON;
            mSharedViewModel.setAlarmStatus(true);
            animateSwitchToggle(ANIM_ON_START, ANIM_ON_END);
        } else {
            mAlarmMode = AlarmMode.OFF;
            mSharedViewModel.setAlarmStatus(false);
            animateSwitchToggle(ANIM_OFF_START, ANIM_OFF_END);
        }

        mSharedViewModel.setDataTime(savedTime);

        mSharedViewModel.getDataTime().observe(this, new Observer<Long>() {
            @Override
            public void onChanged(Long aLong) {
                if (aLong > 0 && mAlarmMode == AlarmMode.ON) {
                    alarmManager.setInexactRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP,
                            SystemClock.elapsedRealtime() + aLong,
                            aLong,
                            MyAlarmManager.getPendingIntent(getApplicationContext(),
                                    PendingIntent.FLAG_UPDATE_CURRENT));
                    mBinding.tvMainStatus.setText(TimeDefinerString.getTimeDefiner(aLong));
                } else
                    mBinding.tvMainStatus.setText(TimeDefinerString.getTimeDefiner(0));

                //save last selected time
                mEditor.putLong(MySharedPreferenceConstants.KEY_LONG_TIME, aLong);
                mEditor.apply();
                savedTime = aLong;
            }
        });

        mSharedViewModel.getAlarmStatus().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                status = aBoolean;
            }
        });

        mBinding.animSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                handleAlarmSwitch();
            }
        });
        mBinding.ivAlarm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlarmEditorFragment alarmEditorFragment = new AlarmEditorFragment();
                alarmEditorFragment.show(getSupportFragmentManager(), "Dialog");
            }
        });

//        mBinding.ivGithub.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Uri uri = Uri.parse("https://github.com/yasinhajiloo/WashHandsReminder");
//                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
//                startActivity(intent);
//            }
//        });
//
//        mBinding.ivStar.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                PackageManager packageManager = getApplicationContext().getPackageManager();
//                Intent intent = new Intent(Intent.ACTION_EDIT);
//                intent.setData(Uri.parse("bazaar://details?id=" + getApplicationContext().getPackageName()));
//                intent.setPackage("com.farsitel.bazaar");
//                if (intent.resolveActivity(packageManager) != null)
//                    startActivity(intent);
//                else {
//                    Toast.makeText(MainActivity.this, "Appropriate program not found!", Toast.LENGTH_SHORT).show();
//                }
//            }
//        });
    }

    private void handleAlarmSwitch() {
        switch (mAlarmMode) {
            // current state it's on so we should turn off the alarm
            case ON:
                animateSwitchToggle(ANIM_OFF_START, ANIM_OFF_END);
                mAlarmMode = AlarmMode.OFF;
                mSharedViewModel.setDataTime(0);
                mSharedViewModel.setAlarmStatus(false);
                if (alarmManager != null) {

                    //disable receiver
                    ComponentName receiver = new ComponentName(this, BootReceiver.class);
                    PackageManager pm = getPackageManager();

                    pm.setComponentEnabledSetting(receiver,
                            PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                            PackageManager.DONT_KILL_APP);

                    PendingIntent pendingIntent = MyAlarmManager.getPendingIntent(getApplicationContext(), PendingIntent.FLAG_UPDATE_CURRENT);
                    alarmManager.cancel(pendingIntent);
                    pendingIntent.cancel();
                }
                break;

            // current state it's off so we should turn on the alarm
            case OFF:
                animateSwitchToggle(ANIM_ON_START, ANIM_ON_END);
                mAlarmMode = AlarmMode.ON;

                //enable receiver
                ComponentName receiver = new ComponentName(this, BootReceiver.class);
                PackageManager pm = getPackageManager();

                pm.setComponentEnabledSetting(receiver,
                        PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                        PackageManager.DONT_KILL_APP);

                if (savedTime > 0) {
                    mSharedViewModel.setAlarmStatus(true);
                    mSharedViewModel.setDataTime(savedTime);
                } else {
                    mSharedViewModel.setAlarmStatus(true);
                    //setting one hour by default when user selected nothing for time
                    mSharedViewModel.setDataTime(TimeConstants.ONE_HOUR);
                }
                break;
        }
    }

    private void animateSwitchToggle(float start, float end) {
        mBinding.animSwitch.setMinAndMaxProgress(start, end);
        mBinding.animSwitch.playAnimation();
    }

}