package com.company.oddle.weatherapp;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class Climate extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_climate);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        String url_for_activity = new String();
        String loc = new String();
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            loc = extras.getString("location");
            url_for_activity = extras.getString("url_for_activity");
        }
        Toast.makeText(getApplicationContext(), url_for_activity, Toast.LENGTH_SHORT).show();
        final String URL = url_for_activity; final String location = loc;
        final TextView textClimate = (TextView) findViewById(R.id.textClimate);
        final ImageView imageView = (ImageView) findViewById(R.id.imageView);

        new Thread(new Runnable() {
            public void run() {

                try {
                    HttpClient httpclient = new DefaultHttpClient();
                    HttpResponse response = httpclient.execute(new HttpGet(URL));
                    StatusLine statusLine = response.getStatusLine();
                    if (statusLine.getStatusCode() == HttpStatus.SC_OK) {
                        ByteArrayOutputStream out = new ByteArrayOutputStream();
                        response.getEntity().writeTo(out);
                        final String responseString = out.toString();
                        out.close();

                        JSONObject jObject = new JSONObject(responseString);
                        JSONArray jArray = jObject.getJSONArray("hourly_forecast");

                        final StringBuilder sb = new StringBuilder();
                        int indexoftime0 = 0;
                        for (int i=0; i < jArray.length(); i++)
                        {
                            try {
                                JSONObject oneObject = jArray.getJSONObject(i).getJSONObject("FCTTIME");
                                String oneObjectsItem = oneObject.getString("hour");
                                if(oneObjectsItem.equals("0")) { indexoftime0 = i; break; }

                            } catch (JSONException e) {}
                        }

                        JSONObject ctempobj = jArray.getJSONObject(indexoftime0).getJSONObject("temp");
                        String ctemp = ctempobj.getString("metric");
                        JSONObject ftempobj = jArray.getJSONObject(indexoftime0).getJSONObject("temp");
                        String ftemp = ftempobj.getString("english");
                        JSONObject conditionobj = jArray.getJSONObject(indexoftime0);
                        final String condition = conditionobj.getString("condition");

                        final String temp = ctemp + " \u2103" + " / " + ftemp + " \u2109";
                        sb.append(location);
                        sb.append("\n\n");
                        sb.append(temp);
                        sb.append("\n\n");
                        sb.append(condition);


                        textClimate.post(new Runnable() {
                            public void run() {
                                textClimate.setText(sb.toString());
                                Drawable drawable;

                                drawable = getResources().getDrawable(R.drawable.cloud_1x); //default


                                if(condition.toLowerCase().contains("snow")) {drawable = getResources().getDrawable(R.drawable.snow_1x);}
                                if(condition.toLowerCase().contains("drizzle") ||
                                        condition.toLowerCase().contains("rain") ||
                                        condition.toLowerCase().contains("mist") ||
                                        condition.toLowerCase().contains("spray"))
                                {drawable = getResources().getDrawable(R.drawable.rain_1x);}
                                if(condition.toLowerCase().contains("partly cloudy") ||
                                        condition.toLowerCase().contains("funnel cloud") ||
                                        condition.toLowerCase().contains("scattered cloud"))
                                {drawable = getResources().getDrawable(R.drawable.cloudy_1x);}
                                if(condition.toLowerCase().contains("mostly cloudy"))
                                {drawable = getResources().getDrawable(R.drawable.very_cloudy_1x);}
                                if(condition.toLowerCase().contains("fog") ||
                                        condition.toLowerCase().contains("fog patches") ||
                                        condition.toLowerCase().contains("patches of fog") ||
                                        condition.toLowerCase().contains("shallow fog") ||
                                        condition.toLowerCase().contains("partial fog"))
                                {drawable = getResources().getDrawable(R.drawable.fog_cloudy_1x);}
                                if(condition.toLowerCase().contains("freezing fog"))
                                {drawable = getResources().getDrawable(R.drawable.fog_1x);}
                                if(condition.toLowerCase().contains("hail"))
                                {drawable = getResources().getDrawable(R.drawable.hail_1x);}
                                if(condition.toLowerCase().contains("rain"))
                                {drawable = getResources().getDrawable(R.drawable.rain_1x);}
                                if(condition.toLowerCase().contains("thunder"))
                                {drawable = getResources().getDrawable(R.drawable.thunderstorm_1x);}
                                if(condition.toLowerCase().contains("moon"))
                                {drawable = getResources().getDrawable(R.drawable.moon_1x);}
                                if(condition.toLowerCase().contains("overcast"))
                                {drawable = getResources().getDrawable(R.drawable.cloudy_1x);}

                                imageView.setImageDrawable(drawable);
                            }
                        });

                    } else {
                        //Closes the connection.
                        response.getEntity().getContent().close();
                        throw new IOException(statusLine.getReasonPhrase());
                    }
                } catch(Exception e) { e.getStackTrace(); }

            }
        }).start();

    }

}
