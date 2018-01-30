package com.greens1995.myapplication;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        boolean isHook = CheckHook.isHook(this);
        boolean isRoot = CheckRoot.isDeviceRooted();
        boolean isVirtual = CheckVirtual.isRunInVirtual();
        boolean isEmulator = EmulatorDetector.isEmulator();
        ((TextView) findViewById(R.id.text)).setText("is virtual " + isVirtual + ",isHook " + isHook + ",isRoot " + isRoot+ ",isEmulator " +isEmulator);
    }
}
