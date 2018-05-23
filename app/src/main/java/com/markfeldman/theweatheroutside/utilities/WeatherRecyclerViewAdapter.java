package com.markfeldman.theweatheroutside.utilities;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.markfeldman.theweatheroutside.R;
import com.markfeldman.theweatheroutside.data.WeatherContract;
import com.markfeldman.theweatheroutside.data.WeatherPreferences;
import com.squareup.picasso.Picasso;


public class WeatherRecyclerViewAdapter extends RecyclerView.Adapter<WeatherRecyclerViewAdapter.WeatherAdapterViewHolder> {
    private final String TAG = WeatherRecyclerViewAdapter.class.getSimpleName();
    private Context context;
    private Cursor mCursor;
    private WeatherRowClicked weatherRowClickedListener;
    private static final int VIEW_TYPE_TODAY = 0;
    private static final int VIEW_TYPE_FUTURE_DAY = 1;

    public interface WeatherRowClicked{
        void onClicked(String weatherID);
    }

    public WeatherRecyclerViewAdapter(WeatherRowClicked weatherRowClickedListener, Context context){
        this.weatherRowClickedListener = weatherRowClickedListener;
        this.context = context;

    }


    @Override
    public WeatherAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        int layoutId;

        switch (viewType) {
            case VIEW_TYPE_TODAY: {
                layoutId = R.layout.weather_list_today_layout;
                break;
            }

            case VIEW_TYPE_FUTURE_DAY: {
                layoutId = R.layout.weather_list_layout;
                break;
            }

            default:
                throw new IllegalArgumentException("Invalid view type, value of " + viewType);
        }
        Context context = parent.getContext();
        int layoutIdForListItem = R.layout.weather_list_layout;
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean shouldAttachToParentImmediately = false;

        View view = inflater.inflate(layoutId, parent, shouldAttachToParentImmediately);
        return new WeatherAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(WeatherAdapterViewHolder holder, int position) {
        mCursor.moveToPosition(position);
        String finalUnit = null;
        String weatherDate = mCursor.getString(mCursor.getColumnIndex(WeatherContract.WeatherData.COLUMN_DATE));
        String weatherDay = mCursor.getString(mCursor.getColumnIndex(WeatherContract.WeatherData.COLUMN_DAY_OF_WEEK));
        String humidity = mCursor.getString(mCursor.getColumnIndex(WeatherContract.WeatherData.COLUMN_HUMIDITY));
        String icon = mCursor.getString(mCursor.getColumnIndex(WeatherContract.WeatherData.COLUMN_ICON));
        String conditions = mCursor.getString(mCursor.getColumnIndex(WeatherContract.WeatherData.COLUMN_CONDITIONS));
        String highCelcius = mCursor.getString(mCursor.getColumnIndex(WeatherContract.WeatherData.COLUMN_HIGH_TEMPC));
        String highFah = mCursor.getString(mCursor.getColumnIndex(WeatherContract.WeatherData.COLUMN_HIGH_TEMPF));
        int iconResource = WeatherUtils.whichIconToUse(icon);


        String prefUnit = WeatherPreferences.getPreferredUnits(context);
        if (prefUnit.equals("celcius")){
            finalUnit = highCelcius + " \u2103";
        }else if (prefUnit.equals("fahrenheit")) {
            finalUnit = highFah + "\u2109";
        }

        holder.weatherData.setText(weatherDay);
        holder.weatherImage.setImageResource(iconResource);
        holder.weatherConditions.setText(conditions);
        holder.weatherTemp.setText(finalUnit);

    }

    @Override
    public int getItemCount() {
        if (mCursor==null){
            return 0;
        }
        return mCursor.getCount();
    }

    public void setWeatherData(Cursor weatherData) {
        mCursor = weatherData;
        notifyDataSetChanged();
    }





    class WeatherAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        private TextView weatherData;
        private ImageView weatherImage;
        private TextView weatherConditions;
        private TextView weatherTemp;

        private WeatherAdapterViewHolder(View itemView) {
            super(itemView);
            weatherImage = itemView.findViewById(R.id.weather_icon);
            weatherData = itemView.findViewById(R.id.weather_data);
            weatherConditions = itemView.findViewById(R.id.main_weather_conditions);
            weatherTemp = itemView.findViewById(R.id.main_weather_degrees);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int adapterPosition = getAdapterPosition();
            mCursor.moveToPosition(adapterPosition);
            weatherRowClickedListener.onClicked(mCursor.getString(mCursor.getColumnIndex(WeatherContract.WeatherData._ID)));
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return VIEW_TYPE_TODAY;
        } else {
            return VIEW_TYPE_FUTURE_DAY;
        }
    }
}
