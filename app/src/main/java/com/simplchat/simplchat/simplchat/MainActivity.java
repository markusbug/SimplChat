package com.simplchat.simplchat.simplchat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {
    private SharedPreferences settings;
    private String email,pass;
    private Timer timer;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        settings = getApplicationContext().getSharedPreferences("alles", Context.MODE_PRIVATE);
        email = settings.getString("email","");
        pass = settings.getString("pass","");
        int MY_PERMISSIONS_REQUEST_CAMERA=0;
        // Here, this is the current activity
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED)
        {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA))
            {

            }
            else
            {
                ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.CAMERA}, MY_PERMISSIONS_REQUEST_CAMERA );
                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        }
        Context context = getApplicationContext();
        File new_file = new File(context.getFilesDir().getPath() + "/chats.txt");
        try {
            new_file.createNewFile();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        Log.d("Create File", "File exists?" + new_file.exists());
        LinearLayout myLayout = findViewById(R.id.linearLayout);
        String[] howmuch = null;
        try {
            howmuch = ChatActivity.getStringFromFile(context.getFilesDir().getPath() + "/chats.txt").split("QQQQ");
        } catch (Exception e) {
            e.printStackTrace();
        }
        //System.out.println(howmuch[0]);
        if ((howmuch != null) && (!howmuch[0].equals(""))) {
            for (int i = 0; i < howmuch.length - 1; i++) {
                System.out.println("Button made");
                System.out.println(howmuch[i]);
                Button newButton = new Button(this);
                newButton.setText(howmuch[i]);
                myLayout.addView(newButton);
            }
        } else {
            System.out.println("It is null");
        }
    }

    @Override

    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();

        inflater.inflate(R.menu.my_menu, menu);

        return true;

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
            Toast.makeText(this, "You need to relogin, session expired.", Toast.LENGTH_LONG).show();
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
                text += "Von: " + chat[a][0] + ".\nNachricht: " + chat[a][1] + "QQQQ";
            }
            Context context = getApplicationContext();
            try {
                OutputStreamWriter outputStreamWriter = new OutputStreamWriter(context.openFileOutput("chats.txt", Context.MODE_APPEND));
                outputStreamWriter.write(text);
                outputStreamWriter.close();
            } catch (IOException e) {
                Log.e("Exception", "File write failed: " + e.toString());
            }
        }

    }
    public void newChat(View v){
        Intent in = new Intent(this, ChatActivity.class);
        startActivity(in);
    }
    public boolean startQRViewer(MenuItem item){
        Intent in = new Intent(this, showQRActivity.class);
        startActivity(in);
        return true;
    }
    public boolean startPassChanger(MenuItem item){
        Intent in = new Intent(this, ChangePasswordActivity.class);
        startActivity(in);
        return true;
    }
}
