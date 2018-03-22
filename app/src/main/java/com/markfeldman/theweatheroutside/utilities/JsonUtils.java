package com.markfeldman.theweatheroutside.utilities;

import android.content.ContentValues;
import android.content.Context;
import com.markfeldman.theweatheroutside.data.WeatherPreferences;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public final class JsonUtils {

    private static String TAG = JsonUtils.class.getSimpleName();

    public static String[] getSimpleWeatherStringsFromJson(Context context, String forecastJsonStr)
            throws JSONException {
        final String FORECAST_OBJECT = "forecast";
        final String SIMPLE_FORECAST = "simpleforecast";
        final String SIMPLE_FORECAST_ARRAY = "forecastday";
        final String DATE_OBJECT = "date";
        final String DAY_HIGH = "high";
        final String WEEKDAY = "weekday";
        final String DAY_NUMBER = "day";
        final String MONTH = "monthname";
        final String YEAR = "year";
        final String CELCIUS = "celsius";
        final String FAHREN = "fahrenheit";
        final String CONDITIONS = "conditions";
        final String HUMIDITY = "avehumidity";
        final String ICON_URL = "icon_url";
        String selectedPrefUnits = WeatherPreferences.getPreferredUnits(context);

        JSONObject overallResponse = new JSONObject(forecastJsonStr);
        JSONObject forecast = overallResponse.getJSONObject(FORECAST_OBJECT);
        JSONObject simpleForecast = forecast.getJSONObject(SIMPLE_FORECAST);
        JSONArray simpleForecastArray = simpleForecast.getJSONArray(SIMPLE_FORECAST_ARRAY);

        String [] parsedWeather = new String [simpleForecastArray.length()];

        for (int i = 0; i < simpleForecastArray.length(); i++){
            String day;
            String dayNumber;
            String month;
            String year;
            String finalDate;
            String humidity;
            String iconURL;
            String conditions;
            String highTempCelcius;
            String highTempF;
            String finalUnits = null;


            JSONObject simpleForecastIndividual = simpleForecastArray.getJSONObject(i);
            JSONObject simpleForecastDate = simpleForecastIndividual.getJSONObject(DATE_OBJECT);
            JSONObject simpleForecasteHigh = simpleForecastIndividual.getJSONObject(DAY_HIGH);

            day = simpleForecastDate.getString(WEEKDAY);
            month = simpleForecastDate.getString(MONTH);
            year = simpleForecastDate.getString(YEAR);
            dayNumber = simpleForecastDate.getString(DAY_NUMBER);
            highTempCelcius = simpleForecasteHigh.getString(CELCIUS);
            highTempF = simpleForecasteHigh.getString(FAHREN);
            conditions = simpleForecastIndividual.getString(CONDITIONS);
            humidity = simpleForecastIndividual.getString(HUMIDITY);
            iconURL = simpleForecastIndividual.getString(ICON_URL);
            finalDate = month + " " + dayNumber + " " + year;

            if (selectedPrefUnits.equals("imperial")){
                finalUnits = highTempCelcius;
            }else if (selectedPrefUnits.equals("metric")){
                finalUnits = highTempF;
            }
            parsedWeather[i] = finalDate + " - " + conditions +
                    " - " + finalUnits + ". Humidity = " + humidity + ". Icon:  " + iconURL;
        }

        return parsedWeather;

    }


    public static ContentValues[] getFullWeatherDataFromJson(Context context, String forecastJsonStr) {
        /** This will be implemented in a future lesson **/
        return null;
    }
}