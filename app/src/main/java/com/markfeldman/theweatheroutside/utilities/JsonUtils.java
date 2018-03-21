package com.markfeldman.theweatheroutside.utilities;

import android.content.ContentValues;
import android.content.Context;
import android.nfc.Tag;
import android.preference.PreferenceManager;
import android.util.Log;

import com.markfeldman.theweatheroutside.data.WeatherPreferences;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.HttpURLConnection;

public final class JsonUtils {

    private static String TAG = JsonUtils.class.getSimpleName();

    public static String[] getSimpleWeatherStringsFromJson(Context context, String forecastJsonStr)
            throws JSONException {
        final String FORECAST_OBJECT = "forecast";
        final String SIMPLE_FORECAST = "simpleforecast";
        final String SIMPLE_FORECAST_ARRAY = "forecastday";
        String selectedPrefUnits = WeatherPreferences.getPreferredUnits(context);

        JSONObject overallResponse = new JSONObject(forecastJsonStr);
        JSONObject forecast = overallResponse.getJSONObject(FORECAST_OBJECT);
        JSONObject simpleForecast = forecast.getJSONObject(SIMPLE_FORECAST);
        JSONArray simpleForecastArray = simpleForecast.getJSONArray(SIMPLE_FORECAST_ARRAY);

        String [] parsedWeather = new String [simpleForecastArray.length()];

        for (int i = 0; i < simpleForecastArray.length(); i++){
            String day;
            String shortDescription;
            String highTempCelcius;
            String highTempF;
            String finalUnits = null;

            JSONObject simpleForecastIndividual = simpleForecastArray.getJSONObject(i);
            JSONObject simpleForecastDate = simpleForecastIndividual.getJSONObject("date");
            JSONObject simpleForecasteHigh = simpleForecastIndividual.getJSONObject("high");

            day = simpleForecastDate.getString("weekday");
            highTempCelcius = simpleForecasteHigh.getString("celsius");
            highTempF = simpleForecasteHigh.getString("fahrenheit");
            shortDescription = simpleForecastIndividual.getString("conditions");

            if (selectedPrefUnits.equals("imperial")){
                finalUnits = highTempCelcius;
            }else if (selectedPrefUnits.equals("metric")){
                finalUnits = highTempF;
            }
            parsedWeather[i] = day + " - " + shortDescription + " - " + finalUnits;
        }

        return parsedWeather;

    }


    public static ContentValues[] getFullWeatherDataFromJson(Context context, String forecastJsonStr) {
        /** This will be implemented in a future lesson **/
        return null;
    }
}