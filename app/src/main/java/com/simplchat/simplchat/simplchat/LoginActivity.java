package com.simplchat.simplchat.simplchat;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Authenticator;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.net.PasswordAuthentication;
import java.net.ProtocolException;
import java.net.Proxy;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;
import java.net.URLConnection.*;

import static android.provider.Telephony.Carriers.PASSWORD;

public class LoginActivity extends AppCompatActivity {
    Button login, regi;
    private EditText emailET, passET;
    private String email, pass;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //Temporäre Solution für Requests
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        login = (Button) findViewById(R.id.button);
        emailET = (EditText) findViewById(R.id.editText);
        passET = (EditText) findViewById(R.id.editText2);
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    loginStart();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        regi = (Button) findViewById(R.id.button2);
        regi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    regiStart();
                } catch (IOException e) {
                    Toast.makeText(getApplicationContext(),"Failed",Toast.LENGTH_LONG).show();
                    e.printStackTrace();
                }
            }
        });
        this.email = this.emailET.getText().toString();
        this.pass = this.passET.getText().toString();
        //Toast.makeText(getApplicationContext(),"https://simplchat.info/register.php?email="+this.emailET.getText().toString()+"&pass="+this.passET.getText().toString()+"", Toast.LENGTH_LONG).show();
    }

    private void regiStart() throws IOException {
        URL url = null;
        try {
            url = new URL("https://simplchat.info/appfunk/register.php?email="+this.emailET.getText().toString()+"&pass="+this.passET.getText().toString()+"");
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        HttpURLConnection con = null;
        try {
            con = (HttpURLConnection) url.openConnection();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            con.setRequestMethod("GET");
        } catch (ProtocolException e) {
            e.printStackTrace();
        }
        int status = 0;
        try {
            status = con.getResponseCode();
        } catch (IOException e) {
            e.printStackTrace();
        }
        BufferedReader in;
        in = new BufferedReader(
                    new InputStreamReader(con.getInputStream()));
        String inputLine;
        StringBuffer content = new StringBuffer();
        while ((inputLine = in.readLine()) != null) {
            content.append(inputLine);
        }
        in.close();
        Toast.makeText(getApplicationContext(),content,Toast.LENGTH_LONG).show();

    }

    private void loginStart() throws IOException {
        URL url = null;
        try {
            url = new URL("https://simplchat.info/appfunk/login.php?email="+this.emailET.getText().toString()+"&pass="+this.passET.getText().toString()+"");
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        HttpURLConnection con = null;
        try {
            con = (HttpURLConnection) url.openConnection();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            con.setRequestMethod("GET");
        } catch (ProtocolException e) {
            e.printStackTrace();
        }
        int status = 0;
        try {
            status = con.getResponseCode();
        } catch (IOException e) {
            e.printStackTrace();
        }
        BufferedReader in;
        in = new BufferedReader(
                new InputStreamReader(con.getInputStream()));
        String inputLine;
        StringBuffer content = new StringBuffer();
        while ((inputLine = in.readLine()) != null) {
            content.append(inputLine);
        }
        in.close();
        if(content.toString().equals("wrongpass")){
            Toast.makeText(getApplicationContext(),"Wrong Password",Toast.LENGTH_LONG);
        }else if(content.toString().equals("Error")){
            Toast.makeText(getApplicationContext(),"No Password or Email",Toast.LENGTH_LONG);
        } else {
            Toast.makeText(getApplicationContext(),"Logged in",Toast.LENGTH_LONG).show();
            SharedPreferences settings = getApplicationContext().getSharedPreferences("sessid",0);
            SharedPreferences.Editor editor = settings.edit();
            editor.putString("sessid",content.toString());
            Intent starter = new Intent(this,MainActivity.class);
            startActivity(starter);
        }
    }
}
