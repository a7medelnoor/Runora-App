package com.runora_dev.runora.Activity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.runora_dev.runora.R;
import com.runora_dev.runora.Webservice.DatabaseHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;



/*
Class Takes the daily food details in the form of breakfast, lunch and dinner
calculates each food item calori using the asynchoronous communication
store the details into the sqlite database

three seperate background jobs are used to fetch the calori detils of each food item using the JSON standard
 */
public class DailyActivity extends AppCompatActivity {

    String sb, sl, sd;
    EditText etb, etl, etd;
    TextView tvb, tvl, tvd;
    View v1, v2, v3;
    String bc, lc, dc;
    Button btnClose, btnRep;
    DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_daily);
        databaseHelper = new DatabaseHelper(getApplicationContext());
        etb = findViewById(R.id.etb);
        etl = findViewById(R.id.etl);
        etd = findViewById(R.id.etd);
        tvb = findViewById(R.id.tvb);
        tvl = findViewById(R.id.tvl);
        tvd = findViewById(R.id.tvd);
        btnRep = findViewById(R.id.btndailyrep);
        btnRep = findViewById(R.id.btnclose);
    }

    public void onClose(View v) {
        finish();
    }

    public void onReport(View v) {
        Intent obj = new Intent(getApplicationContext(), DailyReportActivity.class);
        startActivity(obj);
    }

    public void calculateClk(View view) {
        sb = etb.getText().toString();
        sl = etl.getText().toString();
        sd = etd.getText().toString();

        Toast.makeText(this, "Searching...", Toast.LENGTH_SHORT).show();
        new MyAsyncTask().execute();  //three background jobs
        new MyAsyncTaskL().execute();
        new MyAsyncTaskD().execute();
    }

    public void onSave(View v) {
        float fb, fl, fd;         //storing the values in to the sqlite database
        fb = Float.parseFloat(bc);
        fl = Float.parseFloat(lc);
        fd = Float.parseFloat(dc);
        String pattern = "dd-MM-yyyy";
        String dt = new SimpleDateFormat(pattern).format(new Date());
        System.out.println(sb + " " + fb + " " + sl + " " + fl + " " + sd + " " + fd + " " + dt);
        databaseHelper.addDailyFood(sb, fb, sl, fl, sd, fd, dt);
        Toast.makeText(getApplicationContext(), "Added Successfully", Toast.LENGTH_SHORT).show();
        this.finish();
    }


    /* #####AsyncTask Subclass################################################################### */
    private class MyAsyncTask extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... strings) {

            String allStrings;
            try {
                URL myUrl = new URL("https://api.nutritionix.com/v1_1/search/" +
                        sb + "?fields=item_name%2Citem_id%2Cnf_calories%2Cnf_total_fat" +
                        "&appId=3fe5fa47&appKey=61729b9d2d8612a629467f0cdbbd6d2c");
                HttpURLConnection connection = (HttpURLConnection) myUrl.openConnection();
                connection.setConnectTimeout(700);
                connection.connect();

                //Create a new InputStreamReader
                InputStreamReader streamReader = new InputStreamReader(connection.getInputStream());
                //Create a new buffered reader and String Builder
                BufferedReader reader = new BufferedReader(streamReader);

                String inputLine;
                StringBuilder stringBuilder = new StringBuilder();
                //Check if the line we are reading is not null
                while ((inputLine = reader.readLine()) != null) {
                    stringBuilder.append(inputLine);
                }
                reader.close();
                streamReader.close();
                allStrings = stringBuilder.toString();
                publishProgress(allStrings);

            } catch (Exception e) {
            }
            return "";
        }

        @Override
        protected void onProgressUpdate(String... values) {
            try {
                JSONObject j = new JSONObject(values[0]);

                JSONArray h = (JSONArray) j.get("hits");

                JSONObject rec = h.getJSONObject(0);

                JSONObject fields = rec.getJSONObject("fields");

                bc = fields.getString("nf_calories");

                tvb.setText("Break Fast: " + bc);

                v1 = findViewById(R.id.view0);
                v1.setVisibility(View.VISIBLE);
                v2 = findViewById(R.id.view1);
                v2.setVisibility(View.VISIBLE);
                v3 = findViewById(R.id.view2);
                v3.setVisibility(View.VISIBLE);


            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    /* #####AsyncTask Subclass################################################################### */
    private class MyAsyncTaskL extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... strings) {

            String allStrings;
            try {
                URL myUrl = new URL("https://api.nutritionix.com/v1_1/search/" +
                        sl + "?fields=item_name%2Citem_id%2Cnf_calories%2Cnf_total_fat" +
                        "&appId=3fe5fa47&appKey=61729b9d2d8612a629467f0cdbbd6d2c");
                HttpURLConnection connection = (HttpURLConnection) myUrl.openConnection();
                connection.setConnectTimeout(700);
                connection.connect();

                //Create a new InputStreamReader
                InputStreamReader streamReader = new InputStreamReader(connection.getInputStream());
                //Create a new buffered reader and String Builder
                BufferedReader reader = new BufferedReader(streamReader);

                String inputLine;
                StringBuilder stringBuilder = new StringBuilder();
                //Check if the line we are reading is not null
                while ((inputLine = reader.readLine()) != null) {
                    stringBuilder.append(inputLine);
                }
                reader.close();
                streamReader.close();
                allStrings = stringBuilder.toString();
                publishProgress(allStrings);

            } catch (Exception e) {
            }
            return "";
        }

        @Override
        protected void onProgressUpdate(String... values) {
            try {
                JSONObject j = new JSONObject(values[0]);

                JSONArray h = (JSONArray) j.get("hits");

                JSONObject rec = h.getJSONObject(0);

                JSONObject fields = rec.getJSONObject("fields");

                lc = fields.getString("nf_calories");

                tvl.setText("Lunch: " + lc);

                v1 = findViewById(R.id.view0);
                v1.setVisibility(View.VISIBLE);
                v2 = findViewById(R.id.view1);
                v2.setVisibility(View.VISIBLE);
                v3 = findViewById(R.id.view2);
                v3.setVisibility(View.VISIBLE);


            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    /* #####AsyncTask Subclass################################################################### */
    private class MyAsyncTaskD extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... strings) {

            String allStrings;
            try {
                URL myUrl = new URL("https://api.nutritionix.com/v1_1/search/" +
                        sd + "?fields=item_name%2Citem_id%2Cnf_calories%2Cnf_total_fat" +
                        "&appId=3fe5fa47&appKey=61729b9d2d8612a629467f0cdbbd6d2c");
                HttpURLConnection connection = (HttpURLConnection) myUrl.openConnection();
                connection.setConnectTimeout(700);
                connection.connect();

                //Create a new InputStreamReader
                InputStreamReader streamReader = new InputStreamReader(connection.getInputStream());
                //Create a new buffered reader and String Builder
                BufferedReader reader = new BufferedReader(streamReader);

                String inputLine;
                StringBuilder stringBuilder = new StringBuilder();
                //Check if the line we are reading is not null
                while ((inputLine = reader.readLine()) != null) {
                    stringBuilder.append(inputLine);
                }
                reader.close();
                streamReader.close();
                allStrings = stringBuilder.toString();
                publishProgress(allStrings);

            } catch (Exception e) {
            }
            return "";
        }

        @Override
        protected void onProgressUpdate(String... values) {
            try {
                JSONObject j = new JSONObject(values[0]);

                JSONArray h = (JSONArray) j.get("hits");

                JSONObject rec = h.getJSONObject(0);

                JSONObject fields = rec.getJSONObject("fields");

                dc = fields.getString("nf_calories");

                tvd.setText("Dinner: " + dc);

                v1 = findViewById(R.id.view0);
                v1.setVisibility(View.VISIBLE);
                v2 = findViewById(R.id.view1);
                v2.setVisibility(View.VISIBLE);
                v3 = findViewById(R.id.view2);
                v3.setVisibility(View.VISIBLE);


            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}
