package com.company.oddle.weatherapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

public class MainActivity extends AppCompatActivity {

    List<String> values = new ArrayList<String>();
    //correct way is to use arraylist then toarray for listview and update the adapter in UI thread
    String API_Key = new String("6e959edf1bc82b53");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final ListView listView = (ListView) findViewById(R.id.listView);
        //for(int i=0; i<values.length; i++) values[i]="";
        final ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(),
                android.R.layout.simple_list_item_1, android.R.id.text1, values);
        listView.setAdapter(adapter);

        EditText enter = (EditText) findViewById(R.id.enter);

        final LinkedHashMap hm = new LinkedHashMap();

        enter.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable s) {

                final String URL = new String("http://autocomplete.wunderground.com/aq?query=" + s.toString());

                new Thread(new Runnable() {
                    public void run() {

                        try {
                            //standard code for http requests
                            HttpClient httpclient = new DefaultHttpClient();
                            HttpResponse response = httpclient.execute(new HttpGet(URL));
                            StatusLine statusLine = response.getStatusLine();
                            if (statusLine.getStatusCode() == HttpStatus.SC_OK) {
                                ByteArrayOutputStream out = new ByteArrayOutputStream();
                                response.getEntity().writeTo(out);
                                final String responseString = out.toString();
                                out.close();

                                JSONObject jObject = new JSONObject(responseString);
                                JSONArray jArray = jObject.getJSONArray("RESULTS");

                                //for(int i=0; i<values.length; i++) values[i]="";
                                int numOfCities=0;
                                hm.clear();
                                for (int i=0; i < jArray.length(); i++)
                                {
                                    try {
                                        JSONObject oneObject = jArray.getJSONObject(i);
                                        String oneObjectsItem = oneObject.getString("name");
                                        String type = oneObject.getString("type");
                                        if(type.equals("city")) {
                                            //values[numOfCities++] = oneObjectsItem;
                                            String url = oneObject.getString("l");
                                            hm.put(oneObjectsItem, url.split(";")[0]+".json");
                                        }

                                    } catch (JSONException e) {}
                                }

                                listView.post(new Runnable() {
                                    public void run() {
                                        values.clear(); ((ArrayAdapter<String>)listView.getAdapter()).notifyDataSetChanged();
                                        Set set = hm.entrySet();
                                        Iterator i = set.iterator();
                                        int n=0;

                                        while(i.hasNext()) {
                                            Map.Entry me = (Map.Entry)i.next();
                                            values.add(n++, me.getKey().toString());
                                        }
                                        ((ArrayAdapter<String>)listView.getAdapter()).notifyDataSetChanged();
                                        //Toast.makeText(getApplicationContext(), values.toString(), Toast.LENGTH_LONG).show();
                                    }
                                });

                                // ListView Item Click Listener
                                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                                    @Override
                                    public void onItemClick(AdapterView<?> parent, View view,
                                                            int position, long id) {

                                        int itemPosition = position;
                                        String itemValue = (String) listView.getItemAtPosition(position);
                                        String url_for_activity = "http://api.wunderground.com/api/6e959edf1bc82b53/hourly"
                                                                    + hm.get(itemValue);
                                        if(itemValue == null || itemValue.equals("")) return;
                                        //Toast.makeText(getApplicationContext(), url_for_activity, Toast.LENGTH_LONG).show();

                                        Intent i = new Intent(getApplicationContext(), Climate.class);
                                        i.putExtra("location", itemValue);
                                        i.putExtra("url_for_activity", url_for_activity);
                                        startActivity(i);
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

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                //Toast.makeText(getApplicationContext(), "beforeTextChanged", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //Toast.makeText(getApplicationContext(), "onTextChanged", Toast.LENGTH_SHORT).show();
            }

        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
