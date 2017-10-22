package com.erprakash.macaddressdemo;

import android.content.Context;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.NetworkInterface;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Collections;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    EditText edittext;
    Button checkLocation;
    TextView details ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        details = (TextView) findViewById(R.id.details);
        edittext = (EditText)findViewById(R.id.edittext);
        checkLocation = (Button) findViewById(R.id.checkLocation);
        String adress = getMacAddress();
        String ipAddress = Utils.getIPAddress(true);
        String det = "Your Ip address : "+ipAddress +"\nMac address: "+adress;
        details.setText(det);
        Log.i("Address",adress);
        Log.i("IP address",ipAddress);
    }

    private class DownloadTask extends AsyncTask<String, Void , String> {

        @Override
        protected String doInBackground(String... urls) {

            String string = "";
            URL url;
            HttpURLConnection urlConnection = null;

            try {
                url = new URL(urls[0]);
                urlConnection = (HttpURLConnection) url.openConnection();
                InputStream in = urlConnection.getInputStream();
                InputStreamReader reader = new InputStreamReader(in);

                int data = reader.read();// read by character
                while (data != -1) {
                    char current = (char) data;
                    string += current;
                    data = reader.read();
                }
                return string;

            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            if(result == null)
                Toast.makeText(getApplicationContext(),"Could not find IP",Toast.LENGTH_SHORT).show();
            else{
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    String ipInfo = jsonObject.getString("ip");
                    String lon = jsonObject.getString("longitude");
                    String lat = jsonObject.getString("latitude");
                    String region_name = jsonObject.getString("region_name");
                    String zip_code = jsonObject.getString("zip_code");
                    String city = jsonObject.getString("city");
                    String country_name = jsonObject.getString("country_name");
                    TextView string = (TextView) findViewById(R.id.locationDetails);
                    string.setText("IP: "+ipInfo+"\nlatitude: "+lat+"\nLongitude: "+lon+"\nCountry: "+country_name+"\ncity: "+city+"\nregion name: "+region_name+"\nZip code: "+zip_code);
                }catch (Exception e){
                    e.printStackTrace();
                }

            }
        }
    }

        public void location(View view){
            // to hide the keyboard after tapped the button
            InputMethodManager mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            mgr.hideSoftInputFromWindow(edittext.getWindowToken(),0);
            //-------------------------------------------------------------------
            try {
                String hostname = URLEncoder.encode(edittext.getText().toString(),"UTF-8");
                DownloadTask task = new DownloadTask();
                task.execute("http://freegeoip.net/json/"+hostname);//https://api.ipdata.co/
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
                Toast.makeText(getApplicationContext(),"UnsupportedEncodedException",Toast.LENGTH_SHORT).show();
            }
        }



    public String getMacAddress(){
        StringBuilder res1 = new StringBuilder("");

        try {
            // get all the interfaces
            List<NetworkInterface> all = Collections.list(NetworkInterface.getNetworkInterfaces());
            //find network interface wlan0
            for (NetworkInterface networkInterface : all) {
                if (!networkInterface.getName().equalsIgnoreCase("wlan0")) continue;
                //get the hardware address (MAC) of the interface
                byte[] macBytes = networkInterface.getHardwareAddress();
                if (macBytes == null) {
                    return "";
                }
                res1 = new StringBuilder();
                for (byte b : macBytes) {
                    //gets the last byte of b
                    res1.append(Integer.toHexString(b & 0xFF) + ":");
                }

                if (res1.length() > 0) {
                    res1.deleteCharAt(res1.length() - 1);
                }

            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return res1.toString();
    }
}

