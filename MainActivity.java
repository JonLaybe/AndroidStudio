package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.sax.Element;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.select.Elements;

public class MainActivity extends AppCompatActivity {
    private EditText _textCity;
    private Button _mainBtn;
    private TextView _resultText;
    private ImageView _imageWeather;
    private TextView _precipitation;

    private void RefreshValues() {
        _resultText.setText(R.string.waitAnswer);
        _precipitation.setText(R.string.nullText);
        _imageWeather.setImageResource(0);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ParserWeather parserWeather = new ParserWeather("https://yandex.ru/pogoda/");

        _textCity = findViewById(R.id.nameCity);
        _mainBtn = findViewById(R.id.mainBtn);
        _resultText = findViewById(R.id.textResult);
        _precipitation = findViewById(R.id.precipitation);
        _imageWeather = findViewById(R.id.imagePrecipitation);
        _textCity.setText("Moscow");

        _mainBtn.setOnClickListener(new View.OnClickListener() { //https://yandex.ru/pogoda/moscow
            @Override
            public void onClick(View view) {
                if (_textCity.getText().toString().trim().equals("")) {
                    Toast.makeText(MainActivity.this, R.string.noUserInput, Toast.LENGTH_LONG).show();
                }else{
                    RefreshValues();
                    parserWeather.GetWeather(_textCity.getText().toString());
                }
            }
        });
    }
    private class ParserWeather {
        private String _url;

        public ParserWeather(String url){
            _url = url;
        }

        public String Url(){
            return _url;
        }

        public void GetWeather(String nameCity) {
            WebRequestAsync task = new WebRequestAsync(_url + nameCity);
            task.start();
        }
        private void RunMainTask(TextView context, String message) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    context.setText(message);
                }
            });
        }
        private void SendImage(String stateWeather){
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    switch (stateWeather){
                        case "Пасмурно": _imageWeather.setImageResource(R.drawable.weather_murky); break;
                    }
                }
            });
        }
        class WebRequestAsync extends Thread {
            private String _url;
            public WebRequestAsync(String url) {
                _url = url;
            }
            @Override
            public void run() {
                try {
                    org.jsoup.nodes.Element documentTemperature = Jsoup.connect(_url).get().selectFirst("div.fact__temp_size_s");
                    org.jsoup.nodes.Element documentPrecipitation = Jsoup.connect(_url).get().selectFirst("div.day-anchor");

                    RunMainTask(_resultText, documentTemperature.text()); // температура
                    SendImage(documentPrecipitation.text()); // картинка погоды
                    RunMainTask(_precipitation, documentPrecipitation.text()); // погода
                }catch (Exception e) {
                    RunMainTask(_resultText, getResources().getString(R.string.notFoundCity));
                }
            }
            public String GetUrl(){
                return _url;
            }
        }
    }
}