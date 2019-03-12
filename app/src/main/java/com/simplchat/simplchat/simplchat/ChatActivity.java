package com.simplchat.simplchat.simplchat;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

public class ChatActivity extends AppCompatActivity {
    private EditText toET, messageET;
    private SharedPreferences settings;
    private String sessid;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        toET = (EditText) findViewById(R.id.to);
        messageET = (EditText) findViewById(R.id.message);
        settings = getApplicationContext().getSharedPreferences("alles", Context.MODE_PRIVATE);
        sessid = settings.getString("sessid","");
    }
    public void sendMessage(View v){
        if(!toET.getText().toString().equals("")){
            if(!messageET.getText().toString().equals("")){
                try {
                    sending(this.toET.getText().toString(),this.messageET.getText().toString());
                } catch (IOException e) {
                    e.printStackTrace();
                }
                this.messageET.setText("");
            } else {
                Toast.makeText(getApplicationContext(),"Fill out the Text-field.", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(getApplicationContext(),"Fill out the to-field.", Toast.LENGTH_SHORT).show();
        }
    }
    private void sending(String ton, String textn) throws IOException {
        URL url = null;
        try {
            url = new URL("https://simplchat.info/appfunk/sendChat.php?sessid="+this.sessid+"&an="+ton+"&text="+textn);
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
//        Toast.makeText(getApplicationContext(),content.toString(),Toast.LENGTH_LONG).show();
        if(content.toString().equals("sent")){
            Toast.makeText(getApplicationContext(),"Sended Successfully!",Toast.LENGTH_SHORT).show();
        } else if(content.toString().equals("nouser")){
            Toast.makeText(getApplicationContext(),"Could not send Message",Toast.LENGTH_SHORT).show();
        }

    }
}
