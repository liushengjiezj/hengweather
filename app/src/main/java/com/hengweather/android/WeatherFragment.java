package com.hengweather.android;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.hengweather.android.gson.Forecast;
import com.hengweather.android.gson.Weather;
import com.hengweather.android.service.AutoUpdateService;
import com.hengweather.android.util.HttpUtil;
import com.hengweather.android.util.L;
import com.hengweather.android.util.StaticClass;
import com.hengweather.android.util.Utility;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * Created by liushengjie on 2017/9/1.
 */

public class WeatherFragment extends Fragment {

    private Toolbar mToolbar;

    public SwipeRefreshLayout swipeRefresh;

    private String weatherId;

    private ScrollView weatherLayout;

    private TextView degreeText;

    private TextView weatherInfoText;

    private LinearLayout forecastLayout;

    private TextView aqiText;

    private TextView pm25Text;

    private TextView sportText;

    private TextView drsgText;

    private TextView fluText;

    private TextView uvText;

    private TextView cityText;

    private TextView updateText;

    public static final String TAG = "WeatherFragment";

    public CardView nowCardView;
    public CardView forecastCardView;
    public CardView aqiCardView;
    public CardView suggestionCardView;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (context instanceof MainActivity){
            MainActivity mainActivity = (MainActivity) context;
            mToolbar = (Toolbar) mainActivity.findViewById(R.id.toolbar);
        }
        L.d(TAG, "onAttach");
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        L.d(TAG, "onCreate");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_weather, container, false);
        weatherLayout = view.findViewById(R.id.weather_layout);
        cityText = view.findViewById(R.id.city_text);
        updateText = view.findViewById(R.id.update_time);
        degreeText = view.findViewById(R.id.degree_text);
        weatherInfoText = view.findViewById(R.id.weather_info_text);
        forecastLayout = view.findViewById(R.id.forecast_layout);
        aqiText = view.findViewById(R.id.aqi_text);
        pm25Text = view.findViewById(R.id.pm25_text);
        sportText = view.findViewById(R.id.sport_text);
        drsgText = view.findViewById(R.id.drsg_text);
        fluText = view.findViewById(R.id.flu_text);
        uvText = view.findViewById(R.id.uv_text);
        swipeRefresh = view.findViewById(R.id.swipe_refresh);
        swipeRefresh.setColorSchemeResources(R.color.colorPrimary); // 下拉刷新进度条的颜色

        nowCardView = view.findViewById(R.id.cardView_now);
        forecastCardView = view.findViewById(R.id.cardView_forecast);
        aqiCardView = view.findViewById(R.id.cardView_aqi);
        suggestionCardView = view.findViewById(R.id.cardView_suggestion);

        /*nowCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Toast.makeText(getActivity(), "Nice Today!", Toast.LENGTH_SHORT).show();
                Intent textIntent = new Intent(Intent.ACTION_SEND);
                textIntent.setType("text/plain");
                textIntent.putExtra(Intent.EXTRA_TEXT, "没有人在寒冷时拥抱你，没有人在燥热时凉快你。" +
                        "只有我一直陪伴你，嘘寒又问暖。看云卷云舒，感风起雨落。欢迎下载喵呜天气(MeowWeather)：" +
                        "https://pan.baidu.com/s/1bEF1Wa");
                startActivity(Intent.createChooser(textIntent, "分享"));
            }
        });*/

/*
        forecastCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent textIntent = new Intent(Intent.ACTION_SEND);
                textIntent.setType("text/plain");
                textIntent.putExtra(Intent.EXTRA_TEXT, "天气预报知多少，喵呜天气少不了。既不想被雨淋，" +
                        "又不想被日晒？那就快来下载「喵呜天气」吧：" +
                        "https://pan.baidu.com/s/1bEF1Wa ———— MeowWeather");
                startActivity(Intent.createChooser(textIntent, "分享"));
            }
        });

        aqiCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent textIntent = new Intent(Intent.ACTION_SEND);
                textIntent.setType("text/plain");
                textIntent.putExtra(Intent.EXTRA_TEXT, "天灰蒙蒙的，是雾还是霾？不要怕，「喵呜天气」告诉你：" +
                        "https://pan.baidu.com/s/1bEF1Wa ———— MeowWeather");
                startActivity(Intent.createChooser(textIntent, "分享"));
            }
        });

        suggestionCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent textIntent = new Intent(Intent.ACTION_SEND);
                textIntent.setType("text/plain");
                textIntent.putExtra(Intent.EXTRA_TEXT, "今晚要跑步吗？出门穿什么衣服呀？会不会感冒呢？" +
                        "不想被晒伤啊……快来下载「喵呜天气」，你可爱又贴心的生活小助手：" +
                        "https://pan.baidu.com/s/1bEF1Wa ———— MeowWeather");
                startActivity(Intent.createChooser(textIntent, "分享"));
            }
        });
*/


        weatherId = (String) getArguments().get("weather_id");
        weatherLayout.setVisibility(View.INVISIBLE);
        if (weatherId != null) {
            requestWeather(weatherId);
        }

        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (weatherId != null) {
                    requestWeather(weatherId);
                    Toast.makeText(getActivity(), "更新成功:-)", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getActivity(), "更新失败:-(", Toast.LENGTH_SHORT).show();
                }
            }
        });
        L.d(TAG, "onCreateView");
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        L.d(TAG, "onActivityCreated");
    }

    @Override
    public void onResume() {
        super.onResume();
        String weatherId = getActivity().getIntent().getStringExtra("weather_id");
        if (weatherId != null) {
            requestWeather(weatherId);
        } else {
            L.d(TAG,"WeatherId is null");
        }
        L.d(TAG, "onResume");
    }

    /*
* 处理并展示 Weather 实体类中的数据
* */
    private void showWeatherInfo(Weather weather) {
        String cityName = weather.basic.cityName;
        String updateTime = weather.basic.update.updateTime.split(" ")[1];
        String degree = weather.now.temperature + "°C";
        String weatherInfo = weather.now.more.info;

        // 存储通知栏的天气数据
        SharedPreferences.Editor editor = getActivity().getSharedPreferences("notification", Context.MODE_PRIVATE).edit();
        editor.putString("cityName", cityName);
        editor.putString("degree", degree);
        editor.putString("weatherInfo", weatherInfo);
        editor.apply();

        cityText.setText(cityName);
        //mToolbar.setTitle(cityName);
        updateText.setText("Update Time - " + updateTime);
        degreeText.setText(degree);
        weatherInfoText.setText(weatherInfo);
        forecastLayout.removeAllViews();
        for (Forecast forecast : weather.forecastList) {
            View view = LayoutInflater.from(getActivity()).inflate(R.layout.forecast_item,
                    forecastLayout, false);
            TextView dateText = view.findViewById(R.id.date_text);
            TextView infoText = view.findViewById(R.id.info_text);
            TextView maxText = view.findViewById(R.id.max_text);
            TextView minText = view.findViewById(R.id.min_text);
            dateText.setText(forecast.date);
            infoText.setText(forecast.more.info);
            maxText.setText(forecast.temperature.max);
            minText.setText(forecast.temperature.min);
            forecastLayout.addView(view);
        }
        if (weather.aqi != null) {
            aqiText.setText(weather.aqi.city.aqi);
            pm25Text.setText(weather.aqi.city.pm25);
        }
        String sport = "运动建议：" + weather.suggestion.sport.info;
        String drsg = "穿衣指数：" + weather.suggestion.drsg.info;
        String flu = "感冒指数：" + weather.suggestion.flu.info;
        String uv = "紫外线指数：" + weather.suggestion.uv.info;
        sportText.setText(sport);
        drsgText.setText(drsg);
        fluText.setText(flu);
        uvText.setText(uv);
        weatherLayout.setVisibility(View.VISIBLE);

        // 后台自动更新天气
        Intent intent = new Intent(getActivity(), AutoUpdateService.class);
        getActivity().startService(intent);
    }


    /*;
    * 根据天气 id 请求城市天气信息
    * */
    public void requestWeather(final String weatherId) {

        String weatherUrl = "https://free-api.heweather.com/v5/weather?city=" +
                weatherId + "&key=" + StaticClass.HE_WEATHER_KEY;
        HttpUtil.sendOkHttpRequest(weatherUrl, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getActivity(), "获取天气信息失败",
                                Toast.LENGTH_SHORT).show();
                        swipeRefresh.setRefreshing(false);
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String responseText = response.body().string();
                final Weather weather = Utility.handleWeatherResponse(responseText);
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (weather != null && "ok".equals(weather.status)) {
                            // 存储天气数据
                            SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(
                                    getActivity()).edit();
                            editor.putString("weather", responseText);
                            editor.apply();
                            showWeatherInfo(weather);
                            //Toast.makeText(getActivity(), "天气信息已最新:-)",
                            //      Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getActivity(), "获取天气信息失败:-(",
                                    Toast.LENGTH_SHORT).show();
                        }
                        swipeRefresh.setRefreshing(false);
                    }
                });
            }
        });

    }

}