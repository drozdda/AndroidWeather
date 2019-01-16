package com.example.david.project_drozd;

import android.os.AsyncTask;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormat;
import java.util.Date;
import java.util.Locale;

public class Function {

    public static String temperature;


    //url to access weather data
    private static final String OPEN_WEATHER_MAP_URL =
            "http://api.openweathermap.org/data/2.5/weather?q=%s&units=%s";

    //key to gain permission to weather data
    private static final String OPEN_WEATHER_MAP_API = "ea74c9457120e4bc6b2412e04006f053";


    //Determine the weather icon
    public static String setIcon(int properID, long sunrise, long sunset){
        int id = properID / 100;
        String icon = "";
        if(properID == 800){
            long currentTime = new Date().getTime();
            if(currentTime>=sunrise && currentTime<sunset) {
                icon = "&#xf00d;";
            } else {
                icon = "&#xf02e;";
            }
        } else {
            if (id == 2) {
                icon = "&#xf01e;";

            } else if (id == 3) {
                icon = "&#xf01c;";

            } else if (id == 7) {
                icon = "&#xf014;";

            } else if (id == 8) {
                icon = "&#xf013;";

            } else if (id == 6) {
                icon = "&#xf01b;";

            } else if (id == 5) {
                icon = "&#xf019;";

            }
        }
        return icon;
    }


    //provide processFinish with strings
    public interface AsyncResponse {

        void processFinish(String string1, String string2, String string3, String string4, String string5, String string6, String string7, String string8);
    }





    public static class placeIdTask extends AsyncTask<String, Void, JSONObject> {

        public AsyncResponse delegate = null;
        public placeIdTask(AsyncResponse asyncResponse) {
            delegate = asyncResponse;
        }

        //send city and unit to getWeather
        @Override
        protected JSONObject doInBackground(String... params) {

            JSONObject jsonWeather = null;
            jsonWeather = getWeather(params[0], params[1]);


            return jsonWeather;
        }

        @Override
        protected void onPostExecute(JSONObject json) {
            try {
                if(json != null){
                    JSONObject details = json.getJSONArray("weather").getJSONObject(0);
                    JSONObject main = json.getJSONObject("main");
                    JSONObject wind = json.getJSONObject("wind");
                    DateFormat df = DateFormat.getDateTimeInstance();


                    String city = json.getString("name").toUpperCase(Locale.US) + ", " + json.getJSONObject("sys").getString("country");
                    String description = details.getString("description").toUpperCase(Locale.US);
                    String tempUnit;
                    if (MainActivity.units.equals("metric")){
                        tempUnit = " C";
                    }
                    else{
                        tempUnit = " F";
                    }
                    temperature = String.format("%.2f", main.getDouble("temp"))+ "°"+tempUnit;
                    String humidity = main.getString("humidity") + "%";
                    String speedUnit;
                    if( MainActivity.units.equals("metric")){
                        speedUnit = " km/h";
                    }
                    else{
                        speedUnit = " mph";
                    }
                    String windSpeed = wind.getString("speed") + speedUnit;
                    String updatedOn = df.format(new Date(json.getLong("dt")*1000));
                    String iconText = setIcon(details.getInt("id"),
                            json.getJSONObject("sys").getLong("sunrise") * 1000,
                            json.getJSONObject("sys").getLong("sunset") * 1000);

                    delegate.processFinish(city, description, temperature, humidity, windSpeed, updatedOn, iconText, ""+ (json.getJSONObject("sys").getLong("sunrise") * 1000));


                }
            } catch (JSONException e) {

            }



        }
    }





    //Connect with the open weather api
    public static JSONObject getWeather(String city, String units){
        try {
            URL url = new URL(String.format(OPEN_WEATHER_MAP_URL, city, units));
            HttpURLConnection connection =
                    (HttpURLConnection)url.openConnection();

            connection.addRequestProperty("x-api-key", OPEN_WEATHER_MAP_API);

            //copy all the weather data from the api
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(connection.getInputStream()));

            StringBuffer json = new StringBuffer(1024);
            String tmp="";
            while((tmp=reader.readLine())!=null)
                json.append(tmp).append("\n");
            reader.close();

            JSONObject data = new JSONObject(json.toString());

            //If value is anything but 200, connection has failed
            if(data.getInt("cod") != 200){
                return null;
            }

            return data;
        }catch(Exception e){
            return null;
        }
    }
}
