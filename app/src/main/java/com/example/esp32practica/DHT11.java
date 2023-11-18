package com.example.esp32practica;


public class DHT11 {

    private float Temperatura;
    private float Humedad;

    public float getTemperatura() {
        return Temperatura;
    }

    public void setTemperatura(float temperatura) {
        Temperatura = temperatura;
    }

    public float getHumedad() {
        return Humedad;
    }

    public void setHumedad(float humedad) {
        Humedad = humedad;
    }

    public DHT11(float temperatura, float humedad) {
        Temperatura = temperatura;
        Humedad = humedad;
    }

    public DHT11() {
    }
}
