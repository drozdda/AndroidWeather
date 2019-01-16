package com.example.david.project_drozd;

import android.app.Activity;
import android.os.Bundle;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.support.v7.app.NotificationCompat;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;

public class Settings extends Activity {

    Button weatherButton;
    CheckBox chkUnits;
    EditText edtCity;
    String city;
    String units;
    Switch swtNoti;

    NotificationCompat.Builder notification;
    private static final int uniqueID = 6472;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActionBar().hide();
        setContentView(R.layout.activity_settings);


        weatherButton = findViewById(R.id.btnWeather);

        swtNoti = findViewById(R.id.swtNoti);

        chkUnits = findViewById(R.id.chkUnits);

        edtCity = findViewById(R.id.edtCity);

        notification = new NotificationCompat.Builder(this);
        notification.setAutoCancel(true);

        //retrieve values from main page
        Bundle settingsData = getIntent().getExtras();
        if(settingsData != null){
            city = settingsData.getString("city");
            units = settingsData.getString("units");
            edtCity.setText(city);
            if(units.equals("imperial")){
                chkUnits.setChecked(true);
            }
            else if (units.equals("metric")){
                chkUnits.setChecked(false);
            }

            //launch weatherpage method
            weatherButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    weatherPage();

                }
            });

            //If switch is switched on, display notification
            swtNoti.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    notification();
                }
            });




        }
    }

    //notification for current temp
    public void notification(){
        notification.setSmallIcon(R.drawable.ic_notifications_black_24dp);
        notification.setTicker("Weather Update");
        notification.setWhen(System.currentTimeMillis());
        notification.setContentTitle("Current Temp");
        notification.setContentText(Function.temperature);

        Intent i = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, i, PendingIntent.FLAG_UPDATE_CURRENT);
        notification.setContentIntent(pendingIntent);

        NotificationManager nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        nm.notify(uniqueID, notification.build());


    }

    //sends city and units to weather page and laucnhes it
    public void weatherPage(){
        String city = edtCity.getText().toString();
        Intent i = new Intent(this, MainActivity.class);
        i.putExtra("units",chkUnits.isChecked());
        i.putExtra("city",city);
        startActivity(i);


    }

}
