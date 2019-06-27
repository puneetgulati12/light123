package com.example.light;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    ArrayList<lightclass> data = new ArrayList<>();
    EditText editText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        final OkHttpClient httpClient = new OkHttpClient();
        final String baseurl = "http://api.nightlights.io/months/1993.3-1993.4/states/";
        findViewById(R.id.btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editText = findViewById(R.id.et);
                String name = editText.getText().toString();
                try {
                    run();
                } catch (IOException e) {
                    e.printStackTrace();
                }
//                Request request = new Request().Builder().url(baseurl + editText + "/districts").build();


            }
        });

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

            sqLiteDatabase.insert("key2" , null , contentValues);
           // Toast.makeText(this, "The new Row Id is " + newRow, Toast.LENGTH_LONG).show();
            return true;
        }

        public void getdata () {


//        final String TABLE_NAME = "";
//
//        String selectquery = "SELECT * FROM TABLE_KEY , TABLE_SATELLITE" ;
//        SQLiteDatabase sqLiteDatabase = database.getInstance(this).getReadableDatabase();
//            Cursor cursor = sqLiteDatabase.rawQuery(selectquery, null);
//            String data[] = null;
//
//            if (cursor.moveToFirst()){
//
//            }
//
//            return  data;
            boolean connected = false;
            ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(this.CONNECTIVITY_SERVICE);
            if (connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                    connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {
                //we are connected to a network
                
                connected = true;

            } else {
                connected = false;

            }


        }
    }

