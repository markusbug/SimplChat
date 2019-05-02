package com.simplchat.simplchat.simplchat;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

public class MainActivity extends AppCompatActivity {
    private SharedPreferences settings;
    private TextView main;
    private String email,pass;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        settings = getApplicationContext().getSharedPreferences("alles", Context.MODE_PRIVATE);
        this.main = (TextView) findViewById(R.id.textMessage);
        email = settings.getString("email","");
        pass = settings.getString("pass","");
    }
    private String getChats() throws IOException {
        URL url = null;
        String sessid = APIWorker.getSession(this.email,this.pass);
        System.out.println(sessid);
        try {
            url = new URL("https://simplchat.info/appfunk/getChats.php?sessid="+sessid);
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
        return content.toString();

    }
    public void displayChat(View v){
        String out = "";
        try {
            out = getChats();
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println(out);
        if(out.equals("nonew")){
            this.main.setText("No new messages.");
        } else if(out.equals("sessidinvalid")){
            this.main.setText("You need to relogin, session expired.");
        }
        else {
            String[] chats = out.split(".\\|.");
            String[] zwischen;
            System.out.println(chats.length);
            String[][] chat = new String[chats.length][2];
            for(int i = 0;i<chats.length;i++){
                zwischen = chats[i].split(".;.");
                chat[i][0] = zwischen[0];
                chat[i][1] = zwischen[1];
            }
            System.out.println(chat[0][0]);
            String text = "";
            System.out.println(chat[0][0]);
            for(int a = 0;a<chats.length;a++){
                text += "Von: "+chat[a][0]+".\nNachricht: "+chat[a][1]+"\n";
            }
            this.main.setText(text);
        }

    }
    public void newChat(View v){
        Intent in = new Intent(this, ChatActivity.class);
        startActivity(in);
    }
}