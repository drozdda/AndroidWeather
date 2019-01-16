package com.example.david.project_drozd;

import android.app.Activity;
import android.os.Bundle;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.text.Html;
import android.widget.TextView;
import android.widget.Button;
import android.content.Intent;
import android.view.View;

public class MainActivity extends Activity {

    TextView cityField, detailsField, currentTemperatureField, humidity_field, wind_field, weatherIcon, updatedField;

    Button settingsButton;

    Typeface weatherFont;

    String city;

    public static String units;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActionBar().hide();
        setContentView(R.layout.activity_main);

        //Get city and unit data from the settings class
        Bundle settingsData = getIntent().getExtras();
        //If no data skip, otherwise error
        if(settingsData != null){
            city = settingsData.getString("city");
            Boolean unitStatus = settingsData.getBoolean("units");
            if(unitStatus){
                units = "imperial";
            }
            else{
                units = "metric";
            }

            //save new values of units and city
            savePref();
        }


        //load saved value of city and units to be able to display weather
        loadPref();

        if (city.isEmpty()){
            city = "Toronto";
        }
        if (units.isEmpty()){
            units ="metric";
        }



        //Weather icons
        weatherFont = Typeface.createFromAsset(getApplicationContext().getAssets(), "fonts/weathericons-regular-webfont.ttf");

        cityField = findViewById(R.id.txtCity);
        updatedField = findViewById(R.id.txtUpdate);
        detailsField = findViewById(R.id.txtDetails);
        currentTemperatureField = findViewById(R.id.txtTemp);
        humidity_field = findViewById(R.id.txtHumidity);
        wind_field = findViewById(R.id.txtWind);
        weatherIcon = findViewById(R.id.iconWeather);
        settingsButton =  findViewById(R.id.btnSettings);
        weatherIcon.setTypeface(weatherFont);

        //When settins is clicked, launch the settingsPage method
        settingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                settingsPage();

            }
        });


        Function.placeIdTask asyncTask =new Function.placeIdTask(new Function.AsyncResponse() {
            public void processFinish(String weather_city, String weather_description, String weather_temperature, String weather_humidity, String weather_windSpeed, String weather_updatedOn, String weather_iconText, String sun_rise) {

                //set text
                cityField.setText(weather_city);
                updatedField.setText(weather_updatedOn);
                detailsField.setText(weather_description);
                currentTemperatureField.setText(weather_temperature);
                humidity_field.setText("Humidity: "+weather_humidity);
                wind_field.setText("Wind: "+weather_windSpeed);
                weatherIcon.setText(Html.fromHtml(weather_iconText));

            }
        });



        //Give the city and unit value to fetch the correct weather data
        asyncTask.execute(city, units);

    }

    //send city and unit to settings page and launch it
    public void settingsPage(){
        Intent i = new Intent(this, Settings.class);
        i.putExtra("units",units);
        i.putExtra("city",city);
        startActivity(i);


    }

    //saves values
    private void savePref() {
        SharedPreferences sharedPreferences = getSharedPreferences("Settings", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("city",city);
        editor.putString("units",units);
        editor.commit();
    }

    //retrieved saved values
    public void loadPref() {
        SharedPreferences sharedPreferences = getSharedPreferences("Settings", Context.MODE_PRIVATE);
        city = sharedPreferences.getString("city","");
        units = sharedPreferences.getString("units","");
    }


}
