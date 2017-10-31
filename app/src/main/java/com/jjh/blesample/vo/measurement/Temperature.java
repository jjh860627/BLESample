package com.jjh.blesample.vo.measurement;

import org.joda.time.DateTime;

import java.io.Serializable;

/**
 * Created by jjh860627 on 2017. 10. 31..
 */

public class Temperature implements Serializable {

    private float temperature;
    private int unit;
    private DateTime timestamp;
    private int temperatureType;

    public float getTemperature() {
        return temperature;
    }

    public void setTemperature(float temperature) {
        this.temperature = temperature;
    }

    public int getUnit() {
        return unit;
    }

    public void setUnit(int unit) {
        this.unit = unit;
    }

    public DateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(DateTime timestamp) {
        this.timestamp = timestamp;
    }

    public int getTemperatureType() {
        return temperatureType;
    }

    public void setTemperatureType(int temperatureType) {
        this.temperatureType = temperatureType;
    }

    public String getUnitStr(){
        return unit == 0? "Celsius" : "Fahrenheit";
    }

    public String getTemperatureTypeStr(){
        String tempType = null;
        for(TemperatureType type : TemperatureType.values()){
            if(type.index == temperatureType){
                tempType = type.text;
                break;
            }
        }
        return tempType;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(super.toString()).append("\n")
                .append("temperature = ").append(temperature).append("\n")
                .append("unit = ").append(unit).append("(").append(getUnitStr()).append(")").append("\n")
                .append("timestamp = ").append(timestamp).append("\n")
                .append("temperatureType = ").append(temperatureType).append("(").append(getTemperatureTypeStr()).append(")").append("\n");

        return sb.toString();
    }

    public static enum TemperatureType{
        ARMPIT(1, "Armpit"), BODY(2, "Body(general)"), EAR(3, "Ear(usually ear lobe)")
        , FINGER(4, "Finger"), GASTRO(5, "Gastro-intestinal Tract"), MOUTH(6, "Mouth")
        , RECTUM(7, "Rectum"), TOE(8, "Toe"), TYMPANUM(9, "Tympanum(ear drum)");

        public final String text;
        public final int index;
        private TemperatureType(int index, String text){
            this.index = index;
            this.text = text;
        }
    }
}
