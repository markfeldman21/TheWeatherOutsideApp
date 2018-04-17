package com.markfeldman.theweatheroutside.utilities;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.markfeldman.theweatheroutside.R;
import com.markfeldman.theweatheroutside.data.WeatherContract;
import com.squareup.picasso.Picasso;


public class WeatherRecyclerViewAdapter extends RecyclerView.Adapter<WeatherRecyclerViewAdapter.WeatherAdapterViewHolder> {
    private final String TAG = WeatherRecyclerViewAdapter.class.getSimpleName();
    private Cursor mCursor;
    private WeatherRowClicked weatherRowClickedListener;

    public interface WeatherRowClicked{
        void onClicked(String weatherID);
    }

    public WeatherRecyclerViewAdapter(WeatherRowClicked weatherRowClickedListener){
        this.weatherRowClickedListener = weatherRowClickedListener;

    }


    @Override
    public WeatherAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        int layoutIdForListItem = R.layout.weather_list_layout;
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean shouldAttachToParentImmediately = false;

        View view = inflater.inflate(layoutIdForListItem, parent, shouldAttachToParentImmediately);
        return new WeatherAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(WeatherAdapterViewHolder holder, int position) {
        mCursor.moveToPosition(position);
        String weatherDate = mCursor.getString(mCursor.getColumnIndex(WeatherContract.WeatherData.COLUMN_DATE));
        String weatherDay = mCursor.getString(mCursor.getColumnIndex(WeatherContract.WeatherData.COLUMN_DAY_OF_WEEK));
        String humidity = mCursor.getString(mCursor.getColumnIndex(WeatherContract.WeatherData.COLUMN_HUMIDITY));
        String iconURL = mCursor.getString(mCursor.getColumnIndex(WeatherContract.WeatherData.COLUMN_ICON_URL));
        String conditions = mCursor.getString(mCursor.getColumnIndex(WeatherContract.WeatherData.COLUMN_CONDITIONS));
        String highCelcius = mCursor.getString(mCursor.getColumnIndex(WeatherContract.WeatherData.COLUMN_HIGH_TEMPC));
        String highFah = mCursor.getString(mCursor.getColumnIndex(WeatherContract.WeatherData.COLUMN_HIGH_TEMPF));
        Context context = holder.weatherImage.getContext();
        Picasso.get().load(iconURL).into(holder.weatherImage);

        holder.weatherData.setText(weatherDay + " " + weatherDate + " " + conditions + ". High Of " + highCelcius
        + "/" + highFah);

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

        private WeatherAdapterViewHolder(View itemView) {
            super(itemView);
            weatherImage = itemView.findViewById(R.id.weather_icon);
            weatherData = itemView.findViewById(R.id.weather_data);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int adapterPosition = getAdapterPosition();
            mCursor.moveToPosition(adapterPosition);
            weatherRowClickedListener.onClicked(mCursor.getString(mCursor.getColumnIndex(WeatherContract.WeatherData._ID)));
        }
    }
}
