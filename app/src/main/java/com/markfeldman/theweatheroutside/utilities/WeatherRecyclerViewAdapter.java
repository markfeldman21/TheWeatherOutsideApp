package com.markfeldman.theweatheroutside.utilities;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.markfeldman.theweatheroutside.R;

public class WeatherRecyclerViewAdapter extends RecyclerView.Adapter<WeatherRecyclerViewAdapter.WeatherAdapterViewHolder> {
    private final String TAG = WeatherRecyclerViewAdapter.class.getSimpleName();
    private String[]  jsonResults;
    private WeatherRowClicked weatherRowClickedListener;

    public interface WeatherRowClicked{
        void onClicked(int id);
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
        String weatherData = jsonResults[position];
        holder.weatherData.setText(weatherData);

    }

    @Override
    public int getItemCount() {
        if (jsonResults==null){
            return 0;
        }
        return jsonResults.length;
    }

    public void setWeatherData(String[] weatherData) {
        jsonResults = weatherData;
        notifyDataSetChanged();
    }





    class WeatherAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        public TextView weatherData;

        public WeatherAdapterViewHolder(View itemView) {
            super(itemView);
            weatherData = (TextView) itemView.findViewById(R.id.weather_data);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int adapterPosition = getAdapterPosition();
            weatherRowClickedListener.onClicked(adapterPosition);
        }
    }
}
