package com.simplchat.simplchat.simplchat;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

public class ChangePasswordActivity extends AppCompatActivity {
    private SharedPreferences settings;
    private Button submit;
    private EditText newpass;
    String sessid;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);
        submit = (Button) findViewById(R.id.button4);
        newpass = (EditText) findViewById(R.id.editText3);
        settings = getApplicationContext().getSharedPreferences("alles", Context.MODE_PRIVATE);
        String email = settings.getString("email","");
        String pass = settings.getString("pass","");
        sessid = "";
        try {
            sessid = APIWorker.getSession(email,pass);
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this,"Failed!",Toast.LENGTH_LONG).show();
        }

    }
    public void changePass(View v) throws IOException {
        URL url = null;
        try {
            url = new URL("https://simplchat.info/appfunk/newPass.php?sessid="+this.sessid+"&newpass="+this.newpass.getText().toString());
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
        BufferedReader in = null;
        try {
            in = new BufferedReader(
                    new InputStreamReader(con.getInputStream()));
        } catch (IOException e) {
            e.printStackTrace();
        }
        String inputLine;
        StringBuffer content = new StringBuffer();
        while ((inputLine = in.readLine()) != null) {
            content.append(inputLine);
        }
        try {
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
//        Toast.makeText(getApplicationContext(),content.toString(),Toast.LENGTH_LONG).show();
        if(content.toString().equals("success")){
            Toast.makeText(this, "Successful!",Toast.LENGTH_LONG);
            Intent anc = new Intent(this, MainActivity.class);
            startActivity(anc);

        }
    }
}
