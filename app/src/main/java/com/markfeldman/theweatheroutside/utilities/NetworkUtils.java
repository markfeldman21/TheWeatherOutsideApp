package com.markfeldman.theweatheroutside.utilities;

import android.net.Uri;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public final class NetworkUtils {

    private static final String TAG = NetworkUtils.class.getSimpleName();

    private static final String STATIC_WEATHER_URL =
            "http://api.wunderground.com/api/f9d9bc3cc3834375/forecast10day";

    private static final String FORECAST_BASE_URL = STATIC_WEATHER_URL;

    /* The format we want our API to return */
    private static final String format = "json";
    /* The units we want our API to return */
    private static final String units = "metric";
    /* The number of days we want our API to return */
    private static final int numDays = 14;

    final static String QUERY_PARAM = "q";
    final static String FILE_TYPE = ".json";
    final static String LAT_PARAM = "lat";
    final static String LON_PARAM = "lon";
    final static String FORMAT_PARAM = "mode";
    final static String UNITS_PARAM = "units";
    final static String DAYS_PARAM = "cnt";

    private static URL URLStringbuildUrl(String locationQuery) {
        Uri builtUri = Uri.parse(FORECAST_BASE_URL).buildUpon()
                .appendPath(QUERY_PARAM)
                .appendPath(locationQuery)
                .appendPath(FILE_TYPE)
                .build();

        URL url = null;
        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        assert url != null;
        return url;
    }


    public static URL buildUrl(Double lat, Double lon) {
        /** This will be implemented in a future lesson **/
        return null;
    }


    public static String okHttpDataRetrieval(String weatherLocation) throws IOException {
        URL url = URLStringbuildUrl(weatherLocation);
        String responseBody = null;
        Log.d(TAG, "IN UTILS = " + url);


        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        try {
            InputStream in = urlConnection.getInputStream();

            Scanner scanner = new Scanner(in);
            scanner.useDelimiter("\\A");

            boolean hasInput = scanner.hasNext();
            String response = null;
            if (hasInput) {
                response = scanner.next();
            }
            scanner.close();
            Log.d("TAG", "INSIDE NETWORK UTILS RESPONSE IS " + response);
            return response;
        } finally {
            urlConnection.disconnect();
        }
    }
/*
        try{
            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder()
                    .url(url)
                    .build();
            Response response = client.newCall(request).execute();
            if (response!=null){
                responseBody = response.body().string();
                Log.d("TAG", "INSIDE NETWORK UTILS RESPONSE IS " + responseBody);
            }

        }catch (Exception e){
            e.printStackTrace();
        }
        return responseBody;
    }
*/
}