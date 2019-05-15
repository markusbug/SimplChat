package com.simplchat.simplchat.simplchat;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {
    private SharedPreferences settings;
    private TextView main;
    private String email,pass;
    private Timer timer;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        settings = getApplicationContext().getSharedPreferences("alles", Context.MODE_PRIVATE);
        this.main = (TextView) findViewById(R.id.textMessage);
        email = settings.getString("email","");
        pass = settings.getString("pass","");
    }

    /**
     * Wenn man in die App zurückkehrt, nur dann aktualisiert es den Chat
     */
    @Override
    protected void onResume() {
        super.onResume();
        this.timer = new Timer();
        this.timer.schedule(new Refresher(), 0, 1000);
    }

    /**
     * Zerstört die Aktualisierung, beim Verlassen der App um Recourssen zu sparen
     */
    @Override
    protected void onPause() {
        timer.cancel();
        super.onPause();
    }

    /**
     * Aktualisiert die Chatansicht jede Sekunde.
     */
    private class Refresher extends TimerTask   {

        @Override
        public void run() {
            displayChat();
        }
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
        String textan = content.toString();
        return textan;

    }
    public void displayChat(){
        String out = "";
        try {
            out = getChats();
            //System.out.println(out);
        } catch (IOException e) {
            e.printStackTrace();
        }
        //System.out.println(out);
        if(out.equals("nonew")) {
        } else if(out.equals("sessidinvalid")){
            this.main.setText("You need to relogin, session expired.");
        }
        else {
            String[] chats = out.split(".\\|.");
            String[] zwischen;
            //System.out.println(chats.length);
            String[][] chat = new String[chats.length][2];
            for(int i = 0;i<chats.length;i++){
                zwischen = chats[i].split(".;.");
                chat[i][0] = zwischen[0];
                chat[i][1] = zwischen[1];
            }
            //System.out.println(chat[0][0]);
            String text = "";
            //System.out.println(chat[0][0]);
            for(int a = 0;a<chats.length;a++){
                text += "Von: "+chat[a][0]+".\nNachricht: "+chat[a][1]+"\n";
            }
            this.main.setText(this.main.getText() + text);
        }

    }
    public void newChat(View v){
        Intent in = new Intent(this, ChatActivity.class);
        startActivity(in);
    }
}
