package com.jjh.blesample.vo.measurement;

import org.joda.time.DateTime;

import java.io.Serializable;

/**
 * Created by jjh860627 on 2017. 10. 31..
 */

public class Weight implements Serializable {
    int unit;
    int weight;
    DateTime timestamp;
    int userId;
    int bmi;
    int height;

    public int getUnit() {
        return unit;
    }

    public void setUnit(int unit) {
        this.unit = unit;
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }

    public DateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(DateTime timestamp) {
        this.timestamp = timestamp;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getBmi() {
        return bmi;
    }

    public void setBmi(int bmi) {
        this.bmi = bmi;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public String getUnitStr(){
        return unit == 0? "kg" : "lb";
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(super.toString()).append("\n")
                .append("unit = ").append(unit).append("(").append(getUnitStr()).append(")").append("\n")
                .append("weight = ").append(weight).append("\n")
                .append("timestamp = ").append(timestamp).append("\n")
                .append("userId = ").append(userId).append("\n")
                .append("bmi = ").append(bmi).append("\n")
                .append("height = ").append(height).append("\n");

        return sb.toString();
    }

    public static enum WeightScaleFeature{
        TIME_STAMP_SUPPORTED("False", "True"),
        MULTIPLE_USERS_SUPPORTED("False, True"),
        BMI_SUPPORTED("False, True"),
        WEIGHT_MEASUREMENT_RESOLUTION("Not Specified", "Resolution of 0.5 kg or 1 lb", "Resolution of 0.2 kg or 0.5 lb", "Resolution of 0.1 kg or 0.2 lb", "Resolution of 0.05 kg or 0.1 lb", "Resolution of 0.02 kg or 0.05 lb", "Resolution of 0.01 kg or 0.02 lb", "Resolution of 0.005 kg or 0.01 lb"),
        HEIGHT_MEASUREMENT_RESOLUTION("Not Specified", "Resolution of 0.01 meter or 1 inch", "Resolution of 0.005 meter or 0.5 inch", "Resolution of 0.001 meter or 0.1 inch");

        public final String[] values;

        private WeightScaleFeature(String... values){
            this.values = values;
        }

        public String getFlagValue(int flag){
            if(flag > values.length-1){
                return null;
            }else{
                return values[flag];
            }
        }
    }
}

