package com.runora_dev.runora.Activity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.runora_dev.runora.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;


/*
class used to calculate the calori and fat details of any food item enterted in the textbox
 */
public class ApiActivity extends AppCompatActivity {

    String food;
    EditText et;
    TextView tv1, tv2, tv3, tv4, tv5;
    View v1, v2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_api);

        et = findViewById(R.id.editText);
        tv1 = findViewById(R.id.textView13);
        tv2 = findViewById(R.id.textView16);
        tv3 = findViewById(R.id.textView17);
        tv4 = findViewById(R.id.textView18);
        tv5 = findViewById(R.id.textView19);

    }

    public void onCloseApi(View v) {
        finish();
    }

    public void calculateClk(View view) {
        food = et.getText().toString();
        Toast.makeText(this, "Searching...", Toast.LENGTH_SHORT).show();
        new MyAsyncTask().execute();
    }
    /*
    This calss uses a asynchronous (background communication) to fetch the details from the nutrionix.com site using the
    API. The API retrives the result in the form a JSON object.
     */

    /* #####AsyncTask Subclass################################################################### */
    private class MyAsyncTask extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... strings) { //sending the request to fetch the values in background

            String allStrings;
            try {
                URL myUrl = new URL("https://api.nutritionix.com/v1_1/search/" +
                        food + "?fields=item_name%2Citem_id%2Cnf_calories%2Cnf_total_fat" +
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
                while ((inputLine = reader.readLine()) != null) { // reading the result
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
                JSONObject j = new JSONObject(values[0]);   //converting the result in to JSON

                JSONArray h = (JSONArray) j.get("hits");

                JSONObject rec = h.getJSONObject(0);

                JSONObject fields = rec.getJSONObject("fields");

                String calories = fields.getString("nf_calories");
                String fat = fields.getString("nf_total_fat");
                String name = fields.getString("item_name"); //retrive respective fields


                tv2.setText("Nutrition Facts");   //disply the values in the textview
                tv3.setText("Amount: " + name);
                tv4.setText("Calories: " + calories);
                tv5.setText("Total Fat: " + fat);
                v1 = findViewById(R.id.view);
                v1.setVisibility(View.VISIBLE);
                v2 = findViewById(R.id.view);
                v2.setVisibility(View.VISIBLE);


            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}
