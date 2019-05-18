package com.simplchat.simplchat.simplchat;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

public class ChatActivity extends AppCompatActivity {
    private EditText toET, messageET;
    private SharedPreferences settings;
    private String sessid;
    private String newQRCode;

    public static String convertStreamToString(InputStream is) throws Exception {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();
        String line = null;
        while ((line = reader.readLine()) != null) {
            sb.append(line).append("\n");
        }
        reader.close();
        return sb.toString();
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
    public void startQR(View v){
        Intent in = new Intent(this, DecoderActivity.class);
        startActivity(in);
    }
    public void addContact(String name){

    }

    public static String getStringFromFile(String filePath) throws Exception {
        File fl = new File(filePath);
        FileInputStream fin = new FileInputStream(fl);
        String ret = convertStreamToString(fin);
        //Make sure you close all streams.
        fin.close();
        return ret;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        Context context = getApplicationContext();
        File new_file = new File(context.getFilesDir().getPath() + "/contacts.txt");
        try {
            new_file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Could not create File");
        }
        Log.d("Create File", "File exists?" + new_file.exists());
        toET = findViewById(R.id.to);
        messageET = findViewById(R.id.message);
        settings = getApplicationContext().getSharedPreferences("alles", Context.MODE_PRIVATE);
        sessid = settings.getString("sessid", "");
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            this.newQRCode = extras.getString("qrcode");
            //The key argument here must match that used in the other activity
        }
        if (this.newQRCode == null) {
            //Nix
        } else {
            addContact(this.newQRCode);
            this.toET.setText(this.newQRCode);

            //Kontakt Speichern
            String output = this.newQRCode + "QQQQ";
            try {
                OutputStreamWriter outputStreamWriter = new OutputStreamWriter(context.openFileOutput("contacts.txt", Context.MODE_APPEND));
                outputStreamWriter.write(output);
                outputStreamWriter.close();
            } catch (IOException e) {
                Log.e("Exception", "File write failed: " + e.toString());
            }

        }
        LinearLayout myLayout = findViewById(R.id.contactsView);
        String[] howmuch = null;
        try {
            howmuch = getStringFromFile(context.getFilesDir().getPath() + "/contacts.txt").split("QQQQ");
        } catch (Exception e) {
            e.printStackTrace();
        }
        //System.out.println(howmuch[0]);
        if ((howmuch != null) && (!howmuch[0].equals(""))) {
            for (int i = 0; i < howmuch.length - 1; i++) {
                System.out.println("Button made");
                System.out.println(howmuch[i]);
                Button newButton = new Button(this);
                System.out.println();
                newButton.setText(howmuch[i]);

                final String[] finalHowmuch = howmuch;
                final int finalI = i;
                newButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        changeToText(finalHowmuch[finalI]);
                    }
                });
                myLayout.addView(newButton);
            }
        } else {
            System.out.println("It is null");
        }


    }

    public void changeToText(String text) {
        this.toET.setText(text);
    }
}
