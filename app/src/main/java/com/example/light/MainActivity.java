package com.example.light;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    ArrayList<lightclass> data = new ArrayList<>();
    EditText editText;
    database database;
    Lightadapter lightadapter;
    private SQLiteDatabase d;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        final OkHttpClient httpClient = new OkHttpClient();
        final String baseurl = "http://api.nightlights.io/months/1993.3-1993.4/states/";


        final database sqLiteDatabase = new database(this);

        //Cursor cursor = sqLiteDatabase.fetch();
        findViewById(R.id.btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editText = findViewById(R.id.et);
                String name = editText.getText().toString();
                InputMethodManager input = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);

                input.hideSoftInputFromWindow(getCurrentFocus().getWindowToken() , InputMethodManager.HIDE_NOT_ALWAYS);


//                try {
//                    run();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
                offline();


//                Request request = new Request().Builder().url(baseurl + editText + "/districts").build();


            }
        });

        database = new database(MainActivity.this);
        data = database.getdata();
        lightadapter = new Lightadapter(data, MainActivity.this);


//        RecyclerView rv = findViewById(R.id.recycler);
//        rv.setLayoutManager(new LinearLayoutManager(this , LinearLayoutManager.HORIZONTAL ,false));
//        final Lightadapter lightadapter = new Lightadapter(data , this);
//
//        rv.setAdapter(lightadapter);


    }

    void run() throws IOException {
        final String baseurl = "http://api.nightlights.io/months/1993.3-1993.4/states/";

        OkHttpClient client = new OkHttpClient();

        final Request request = new Request.Builder()
                .url(baseurl + editText.getText() + "/districts")
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                call.cancel();
            }

            @Override
            public void onResponse(Call call, final Response response) throws IOException {

                final String myResponse = response.body().string();
                Log.d("response", myResponse);
//                final String result = response.body().string();
                String resut = myResponse;
                Gson gson = new Gson();


//                Type listType = new TypeToken<>().getType();
//
//                final ArrayList light =  gson.fromJson(resut , listType);

                final lightclass[] arr = gson.fromJson(resut, lightclass[].class);
                for (int i = 0; i < arr.length; i++) {
                    insert(String.valueOf(arr[i].getMonth()), String.valueOf(arr[i].getCount()),
                            String.valueOf(arr[i].getVis_median()), String.valueOf(arr[i].getYear()));
                }
                for (int i = 0; i < arr.length; i++) {
                    insert(String.valueOf(arr[i].getKey()), String.valueOf(arr[i].getSatellite()));
                }

                MainActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        RecyclerView recyclerView = findViewById(R.id.recycler);
                        recyclerView.setLayoutManager((new LinearLayoutManager(MainActivity.this.getBaseContext())));
                        recyclerView.setAdapter(new Lightadapter(

                                new ArrayList<>(Arrays.asList(arr))

                                , MainActivity.this));
                    }
                });


            }
        });
    }

    public boolean insert(String Month, String count, String VisMedian, String Year) {
//    SQLiteDatabase sqLiteDatabase = database.getInstance(this).getWritableDatabase();
        SQLiteDatabase sqLiteDatabase = new database(this).getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put("Month", Month);
        contentValues.put("Year", Year);
        contentValues.put("count", count);
        contentValues.put("vismedian", VisMedian);
        long newRow = sqLiteDatabase.insert("satellite", null, contentValues);

//            contentValues.put("" , );
//            sqLiteDatabase.insert("key2" , null , contentValues);
        // Toast.makeText(this, "The new Row Id is " + newRow, Toast.LENGTH_LONG).show();
        return true;
    }

    public boolean insert(String district, String value) {
//    SQLiteDatabase sqLiteDatabase = database.getInstance(this).getWritableDatabase();
        SQLiteDatabase sqLiteDatabase = new database(this).getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put("district", district);
        contentValues.put("value", value);

        sqLiteDatabase.insert("key2", null, contentValues);
        // Toast.makeText(this, "The new Row Id is " + newRow, Toast.LENGTH_LONG).show();
        return true;
    }

    public void offline() {
        boolean connected = false;
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(this.CONNECTIVITY_SERVICE);
        if (connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {
            //we are connected to a network
            try {
                run();
            } catch (IOException e) {
                e.printStackTrace();
            }
            connected = true;

        } else {
            String Text = editText.getText().toString();
            String selectquery = "SELECT * FROM Satellite INNER JOIN key2 where Satellite.rowid = key2.ID AND  key2.district like '" + Text +"%' group by key2.district";
            SQLiteDatabase sqLiteDatabase = database.getInstance(this).getReadableDatabase();
            Cursor cursor = sqLiteDatabase.rawQuery(selectquery, null);
            connected = false;

            ArrayList<lightclass> data = new ArrayList<>();


            StringBuffer stringBuffer = new StringBuffer();
            lightclass dataModel = null;
            while (cursor.moveToNext()) {
                try {
                    dataModel = new lightclass("key", "satellite", "vis_median", 1993, 3, 3);
                    String count = cursor.getString(cursor.getColumnIndexOrThrow("count"));
                    String vismedian = cursor.getString(cursor.getColumnIndexOrThrow("VisMedian"));
                    String year = cursor.getString(cursor.getColumnIndexOrThrow("Year"));
                    String month = cursor.getString(cursor.getColumnIndexOrThrow("Month"));
                    dataModel.setCount(Integer.parseInt(count));
                    dataModel.setMonth(Integer.parseInt(month));
                    dataModel.setYear(Integer.parseInt(year));
                    dataModel.setVis_median(vismedian);
                    stringBuffer.append(dataModel);
                    // stringBuffer.append(dataModel);
                    data.add(dataModel);
                } catch (IllegalArgumentException e) {
                    e.printStackTrace();
                }
            }


            Lightadapter Myadapter = new Lightadapter(data , this);
            RecyclerView recyclerView = findViewById(R.id.recycler);
            recyclerView.setLayoutManager((new LinearLayoutManager(MainActivity.this.getBaseContext())));
            recyclerView.setAdapter(Myadapter);

            for (lightclass mo : data) {

                Log.d("Hellomo", "" + mo.getCount());
            }
            //recyclerView.addItemDecoration(new DividerItemDecoration(getBaseContext(),LinearLayoutManager.VERTICAL));


        }


    }
}