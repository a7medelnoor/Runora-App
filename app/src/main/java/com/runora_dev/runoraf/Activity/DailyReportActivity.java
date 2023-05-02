package com.runora_dev.runoraf.Activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.runora_dev.runoraf.R;
import com.runora_dev.runoraf.Webservice.DatabaseHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class DailyReportActivity extends AppCompatActivity {
    ListView l;
    DatabaseHelper databaseHelper;
    String[] items;
    Button bclose;
    private static final String TAG = "DailyReportActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_daily_report);
        l = findViewById(R.id.list);
        bclose = findViewById(R.id.btnDRclose);
        bclose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        databaseHelper = new DatabaseHelper(getApplicationContext());
        Cursor data = databaseHelper.getAllDailydata();
        System.out.println("Count : " + data.getCount());
        if (data.getCount() > 0) {
            items = new String[data.getCount() + 1];
            int i = 0;
            items[i] = "Breakfast : Calori - Lunch : Calori - Dinner : Calori";
            i++;
            while (data.moveToNext()) {
                String str = "" + data.getString(1) + " : " + data.getFloat(2);
                System.out.println("STR : " + str);
                str = str + " - " + data.getString(3) + " : " + data.getFloat(4);
                System.out.println("STR : " + str);
                str = str + " - " + data.getString(5) + " : " + data.getFloat(6);
                System.out.println("STR : " + str);
                items[i] = str;
                i++;
            }

            ArrayAdapter<String> arr;
            arr = new ArrayAdapter<String>(
                    this,
                    R.layout.support_simple_spinner_dropdown_item, items);
            l.setAdapter(arr);
        }
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("dailyFood");

        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Handle data change event
                List<String> items = new ArrayList<>();
                items.add("Breakfast : Calori - Lunch : Calori - Dinner : Calori");
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String str = "" + snapshot.child("food_name").getValue(String.class) + " : " + snapshot.child("calories").getValue(Float.class);
                    str = str + " - " + snapshot.child("food_name").getValue(String.class) + " : " + snapshot.child("calories").getValue(Float.class);
                    str = str + " - " + snapshot.child("food_name").getValue(String.class) + " : " + snapshot.child("calories").getValue(Float.class);
                    items.add(str);
                }

                ArrayAdapter<String> adapter = new ArrayAdapter<String>(DailyReportActivity.this,
                        android.R.layout.simple_list_item_1, items);
                l.setAdapter(adapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle database error
            }
        });
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Handle data change event
                List<String> items = new ArrayList<>();
                items.add("Breakfast : Calori - Lunch : Calori - Dinner : Calori");
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String str = "" + snapshot.child("food_name").getValue(String.class) + " : " + snapshot.child("calories").getValue(Float.class);
                    str = str + " - " + snapshot.child("food_name").getValue(String.class) + " : " + snapshot.child("calories").getValue(Float.class);
                    str = str + " - " + snapshot.child("food_name").getValue(String.class) + " : " + snapshot.child("calories").getValue(Float.class);
                    items.add(str);
                }

                ArrayAdapter<String> adapter = new ArrayAdapter<String>(DailyReportActivity.this, android.R.layout.simple_list_item_1, items);
                l.setAdapter(adapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle database error
            }
        });


    }

    public void onProfileClick(View v) {
        EditText e = findViewById(R.id.etweight);
        String weight = e.getText().toString();
        if (weight != null && weight.length() != 0) {
            SharedPreferences sharedPreferences = getSharedPreferences("UserData", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("weight", weight);
            editor.commit();
        }
        Intent ii = new Intent(this, Home.class);
        ii.putExtra("frag", "4");
        startActivity(ii);
    }
}