package com.example.tp02;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import android.Manifest;

import android.content.pm.PackageManager;
import android.os.Build;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.READ_SMS) != PackageManager.PERMISSION_GRANTED){
            requestPermissions(new String[]{Manifest.permission.READ_SMS},1000);
        }else {
            SMSService.startCount(5,this);
        }
    }
}