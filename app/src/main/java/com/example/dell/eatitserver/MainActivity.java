package com.example.dell.eatitserver;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import info.hoang8f.widget.FButton;

public class MainActivity extends AppCompatActivity {
    FButton fbtnsigninmain;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        fbtnsigninmain=(FButton)findViewById(R.id.fbtnsigninmain);
        fbtnsigninmain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(MainActivity.this,SignIn.class);
                startActivity(intent);
            }
        });
    }
}
