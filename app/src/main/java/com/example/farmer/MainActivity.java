package com.example.farmer;

import android.content.Context;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class MainActivity extends AppCompatActivity {

    private EditText inputHumidity, inputN, inputP, inputK, inputTemp, inputRainfall;
    private TextView outputCrop;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize UI elements
        inputHumidity = findViewById(R.id.inputHumidity);
        inputN = findViewById(R.id.inputN);
        inputP = findViewById(R.id.inputP);
        inputK = findViewById(R.id.inputK);
        inputTemp = findViewById(R.id.inputTemp);
        inputRainfall = findViewById(R.id.inputRainfall);
        outputCrop = findViewById(R.id.outputCrop);

        Button buttonRecommend = findViewById(R.id.buttonRecommend);
        buttonRecommend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recommendCrop();
            }
        });
    }

    private void recommendCrop() {
        // Get input values
        double humidity = Double.parseDouble(inputHumidity.getText().toString());
        double N = Double.parseDouble(inputN.getText().toString());
        double P = Double.parseDouble(inputP.getText().toString());
        double K = Double.parseDouble(inputK.getText().toString());
        double temperature = Double.parseDouble(inputTemp.getText().toString());
        double rainfall = Double.parseDouble(inputRainfall.getText().toString());

        // Create an instance of CropRecommendation
        CropRecommendation cropRec = new CropRecommendation(this, humidity, N, P, K, temperature, rainfall);
        // Display the recommended crop
        outputCrop.setText("The recommended crop is: " + cropRec.suggestCrop());
    }

    private class CropRecommendation {
        private double inputHumidity;
        private double inputN;
        private double inputP;
        private double inputK;
        private double inputTemp;
        private double inputRainfall;
        private Context context;

        public CropRecommendation(Context context, double humidity, double N, double P, double K, double temperature, double rainfall) {
            this.context = context;
            this.inputHumidity = humidity;
            this.inputN = N;
            this.inputP = P;
            this.inputK = K;
            this.inputTemp = temperature;
            this.inputRainfall = rainfall;
        }

        public String suggestCrop() {
            String line;
            String cvsSplitBy = ",";
            double minDifference = Double.MAX_VALUE;
            String recommendedCrop = "";

            try {
                AssetManager assetManager = context.getAssets();
                InputStream inputStream = assetManager.open("crop_data.csv");
                BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
                br.readLine(); // Skip header

                while ((line = br.readLine()) != null) {
                    String[] cropData = line.split(cvsSplitBy);
                    double N = Double.parseDouble(cropData[0]);
                    double P = Double.parseDouble(cropData[1]);
                    double K = Double.parseDouble(cropData[2]);
                    double temperature = Double.parseDouble(cropData[3]);
                    double humidity = Double.parseDouble(cropData[4]);
                    double rainfall = Double.parseDouble(cropData[6]);
                    String crop = cropData[7];

                    double difference = Math.abs(inputN - N) + Math.abs(inputP - P) + Math.abs(inputK - K)
                            + Math.abs(inputTemp - temperature) + Math.abs(inputHumidity - humidity)
                            + Math.abs(inputRainfall - rainfall);

                    if (difference < minDifference) {
                        minDifference = difference;
                        recommendedCrop = crop;
                    }
                }
                br.close();
            } catch (IOException e) {
                e.printStackTrace();
                return "Error reading data.";
            }

            return recommendedCrop.isEmpty() ? "No recommendation found." : recommendedCrop;
        }
    }
}
