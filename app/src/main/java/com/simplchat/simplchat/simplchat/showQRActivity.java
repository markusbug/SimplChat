package com.simplchat.simplchat.simplchat;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import net.glxn.qrgen.android.QRCode;




import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

public class showQRActivity extends AppCompatActivity {
    private SharedPreferences settings;
    private ImageView qrCode;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_qr);
        qrCode = (ImageView) findViewById(R.id.imageView2);
        settings = getApplicationContext().getSharedPreferences("alles", Context.MODE_PRIVATE);
        String email = settings.getString("email","");
        String pass = settings.getString("pass","");
        String sessid = "";
        try {
            sessid = APIWorker.getSession(email,pass);
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this,"Failed!",Toast.LENGTH_LONG).show();
        }
        //Jetzt API
        URL url = null;
        try {
            url = new URL("https://simplchat.info/appfunk/getPermanentKey.php?sessid="+sessid);
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
        String inputLine = null;
        StringBuffer content = new StringBuffer();
        while (true) {
            try {
                if (!((inputLine = in.readLine()) != null)) break;
            } catch (IOException e) {
                e.printStackTrace();
            }
            content.append(inputLine);
        }
        try {
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        String permanentID = content.toString();


        Bitmap myBitmap = QRCode.from(permanentID).withSize(500,500).bitmap();
        qrCode.setImageBitmap(myBitmap);

    }
}
