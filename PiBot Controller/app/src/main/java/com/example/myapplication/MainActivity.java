package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private EditText IpEdTxt, usernameEdTxt, passwordEdTxt;
    private Button submitBttn;
    private TextView errorTxtView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        IpEdTxt = (EditText) findViewById(R.id.IpAddrEdTxt);
//        usernameEdTxt = (EditText) findViewById(R.id.usernameEdTxt);
//        passwordEdTxt = (EditText) findViewById(R.id.passwordEdTxt);
        submitBttn = (Button) findViewById(R.id.submitButton);
        errorTxtView = (TextView)findViewById(R.id.errorTxtView);

        //Set onclick listener
        submitBttn.setOnClickListener(view -> {
            String IP = IpEdTxt.getText().toString();
//            String Username = usernameEdTxt.getText().toString();
//            String Password = passwordEdTxt.getText().toString();

//            if(IP.isEmpty() || Username.isEmpty() || Password.isEmpty()){
            if(IP.isEmpty()){
                errorTxtView.setText("Error: Please fill in requried field");
                errorTxtView.setVisibility(View.VISIBLE);
            }else{
                Intent intent = new Intent(this, MainScreen.class);
                intent.putExtra("IpAddress", IP);
                startActivity(intent);
            }
        });
    }
}