package com.websarva.wings.android.test;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public class MainActivity extends AppCompatActivity {
    /**
     * 緯度を表示する表示するTextViewフィールド
     */
    private TextView _tvLatitude;
    /**
     * 経度を表示するTextViewフィールド
     */
    private TextView _tvLongitude;


    /**
     * 緯度フィールド。
     */
    private double _latitude = 0;

    /**
     * 経度フィールド
     */
    private double _longitude = 0;

    public void onMapSearchButtonClick(View view) {
        //入力欄に入力されたキーワード文字列を取得。
        EditText etSearchWord = findViewById(R.id.etSearchWord);
        String searchWord = etSearchWord.getText().toString();

        try {
            //入力されたキーワードをURLエンコード
            searchWord = URLEncoder.encode(searchWord, "UTF-8");
            //マップと連携するURI文字列を生成。
            String uriStr = "geo:0,0?q=" + searchWord;
            //URI文字列からURIオブジェクトを生成。
            Uri uri = Uri.parse(uriStr);
            //Intentオブジェクトを生成。
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            //アクティビティを起動
            startActivity(intent);
        } catch (UnsupportedEncodingException ex) {
            Log.e("IntentStartActivity", "検索キーワード変換失敗", ex);
        }
    }

    public void onMapShowCurrentButtonClick(View view) {
        //フィールドの緯度と経度の値をもとにマップアプリと連携するURI文字列を生成。
        String uriStr = "geo:" + _latitude + "," + _longitude;
        //URI文字列から URIオブジェクトを生成。
        Uri uri = Uri.parse(uriStr);
        //Intenオブジェクトを生成。
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        //アクティビティを起動。
        startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intent_start);
        //緯度と経度を表示するTextViewフィールドの中身を取得。
        _tvLatitude = findViewById(R.id.tvLatitude);
        _tvLongitude = findViewById(R.id.tvLongitude);
        //LocationManagerオブジェクトを取得。
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        //位置情報が更新された際のリストオブジェクトを生成。
        GPSLocationListener locationListener = new GPSLocationListener();
        //ACCESS許可がおりていないなら・・・
        if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.
                ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            //ACCESS_FINE_LOCATIONの許可を求めるダイアルログを表示。その際、リクエストコード1000に設定。
            String[] permisssions = {Manifest.permission.ACCESS_FINE_LOCATION};
            ActivityCompat.requestPermissions(MainActivity.this, permisssions, 1000);
            //onCreate()メソッドを終了。
            return;
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults){
        //ACCESS_FINE_LOCATIONに対するパーミッションダイアログでかつ許可を選択したなら・・・
        if (requestCode == 1000 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
            //LocationManagerオブジェクトを取得。
            LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
            //位置情報が更新された際のリスなオブジェクトを生成。
            GPSLocationListener locationListener = new GPSLocationListener();
            //再度ACCESS_FINE_LOCATTIONの許可が下りていないかどうかのチェックをし、下りていないなら処理を中止。
            if(ActivityCompat.checkSelfPermission(MainActivity.this,
                    Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
                return;
            }
            //位置情報の追跡開始。
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,0,0,locationListener);
        }
    }

    private class GPSLocationListener implements LocationListener {
        @Override
        public void onLocationChanged(Location location){
            //引数のLocationオブジェクトから緯度を取得。
            _latitude = location.getLatitude();
            //引数のLocationオブジェクトから経度を取得。
            _longitude = location.getLongitude();
            //取得した緯度をTextViewに表示。
            _tvLatitude.setText(Double.toString(_latitude));
            //取得下経度をTextViewに表示。
            _tvLongitude.setText(Double.toString(_longitude));
        }
        @Override
        public void onStatusChanged(String provider, int status, Bundle extras){};
        @Override
        public void onProviderEnabled(String provider){};
        @Override
        public void onProviderDisabled(String provider){};
    }
}