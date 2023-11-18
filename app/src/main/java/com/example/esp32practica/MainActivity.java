package com.example.esp32practica;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import android.widget.CompoundButton;
import android.widget.Toast;

import android.os.Bundle;
import android.util.Log;

import com.electroncia117.electronica117_ui.Grafica;
import com.electroncia117.electronica117_ui.IndicadorCircular;
import com.electroncia117.electronica117_ui.LED;
import com.electroncia117.electronica117_ui.Termometro;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseException;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

public class MainActivity extends AppCompatActivity {
    /** Vistas **/
    private static final String PATH_HUMIDITY = "Humidity";
    private static final String PATH_TEMPERATURE = "Temperature";
    private static final String PATH_RGB = "RGB";
    private static final String PATH_LED = "Led";
    private Grafica graficaTemperatura;
    private Grafica graficaHumedad;
    private Termometro termometroTemperatura;
    private IndicadorCircular indicadorCircularHumedad;
    private LED led;
    private SwitchCompat LedSw;

    /** Objetos **/
    private DHT11 dht11;
    private RGB rgb;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        graficaTemperatura = findViewById(R.id.GraficaTemperatura);
        graficaHumedad = findViewById(R.id.GraficaHumdeda);
        termometroTemperatura = findViewById(R.id.termometroTemperatura);
        indicadorCircularHumedad = findViewById(R.id.IndicadorCircularHumedad);
        led = findViewById(R.id.LED);
        LedSw = findViewById(R.id.LedSw);


        /** Configurar vistas **/
        graficaTemperatura.setMaxAmplitude(50);
        graficaTemperatura.setText("°C");

        termometroTemperatura.setMax(50);
        termometroTemperatura.setUnits("°C");
        termometroTemperatura.gradientEnabled(true);

        graficaHumedad.setMaxAmplitude(100);
        graficaHumedad.setText("%");
        graficaHumedad.setColorGradient_1(getColor(R.color.Gradiente1));
        graficaHumedad.setColorGradient_2(getColor(R.color.Gradiente2));

        indicadorCircularHumedad.setMax(100);
        indicadorCircularHumedad.setUnits("%");
        indicadorCircularHumedad.gradientEnabled(true);
        indicadorCircularHumedad.setColorGradient_1(getColor(R.color.Gradiente1));
        indicadorCircularHumedad.setColorGradient_2(getColor(R.color.Gradiente2));




        try{
            FirebaseDatabase database = FirebaseDatabase.getInstance();
            DatabaseReference myRefH = database.getReference(PATH_HUMIDITY);
            DatabaseReference myRefT = database.getReference(PATH_TEMPERATURE);
            DatabaseReference myRefR = database.getReference(PATH_RGB);
            DatabaseReference myRefL = database.getReference(PATH_LED);

            dht11 = new DHT11();
            rgb = new RGB();

            /** Set el listener al switch **/
            LedSw.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    myRefL.setValue(isChecked);

                }
            });

            myRefR.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    Integer red = snapshot.child("R").getValue(Integer.class);
                    Integer green = snapshot.child("G").getValue(Integer.class);
                    Integer blue = snapshot.child("B").getValue(Integer.class);
                    if (red != null && green != null && blue != null) {
                        rgb.setR(red);
                        rgb.setG(green);
                        rgb.setB(blue);
                        led.setRGB(rgb.getR(), rgb.getG(), rgb.getB());
                    } else {
                        Log.e("MainActivity", "RGB data is null");
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Log.e("MainActivity", "RGB data is null");
                }
            });

            myRefH.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    Float humidity = snapshot.getValue(Float.class);
                    if (humidity != null) {
                        dht11.setHumedad(humidity);
                        indicadorCircularHumedad.setValue(humidity);
                        graficaHumedad.setValue(humidity);
                    } else {
                        Log.e("MainActivity", "Humidity data is null");
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Log.e("MainActivity", "Humidity data is null");
                }
            });

            myRefT.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    Float temperature = snapshot.getValue(Float.class);
                    if (temperature != null) {
                        dht11.setTemperatura(temperature);
                        termometroTemperatura.setTemperature(dht11.getTemperatura());
                        graficaTemperatura.setValue(dht11.getTemperatura());
                    }else{
                        Log.e("MainActivity", "Temperature data is null");
                    }

                }
                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Log.e("TAG ERROR", "Error en Data", error.toException());
                }
            });

        }catch(DatabaseException e){
            Log.e("TAG", "Error en el bloque de código", e);
        }


    }

    @Override
    protected void onStop() {
        super.onStop();
    }
}