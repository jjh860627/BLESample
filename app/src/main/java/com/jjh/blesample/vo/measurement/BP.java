package com.jjh.blesample.vo.measurement;

import org.joda.time.DateTime;

import java.io.Serializable;

/**
 * Created by jjh860627 on 2017. 10. 31..
 */

public class BP implements Serializable{

    private float sys;
    private float dia;
    private float map;
    private int unit;
    private DateTime timestamp;
    private float pulseRate;
    private int userId;
    private int measurementStatus;

    public float getSys() {
        return sys;
    }

    public void setSys(float sys) {
        this.sys = sys;
    }

    public float getDia() {
        return dia;
    }

    public void setDia(float dia) {
        this.dia = dia;
    }

    public float getMap() {
        return map;
    }

    public void setMap(float map) {
        this.map = map;
    }

    public DateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(DateTime timestamp) {
        this.timestamp = timestamp;
    }

    public float getPulseRate() {
        return pulseRate;
    }

    public void setPulseRate(float pulseRate) {
        this.pulseRate = pulseRate;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getMeasurementStatus() {
        return measurementStatus;
    }

    public void setMeasurementStatus(int measurementStatus) {
        this.measurementStatus = measurementStatus;
    }

    public String getUnitStr(){
        return unit == 0 ? "mmHg" : "kPa";
    }

    public String getMeasurementStatusStr(){
        if(measurementStatus < 0 ) return null;

        StringBuilder sb = new StringBuilder();

        sb.append(MeasurementStatus.BODY_MOVEMENT_DETECTION.getFlagValue(measurementStatus & 0x01)).append("\n");
        sb.append(MeasurementStatus.CUFF_FIT_DETECTION.getFlagValue(measurementStatus & 0x02)).append("\n");
        sb.append(MeasurementStatus.IRREGULAR_PULSE_DETECTION.getFlagValue(measurementStatus & 0x04)).append("\n");
        sb.append(MeasurementStatus.PULSE_RATE_RANGE_DETECTION.getFlagValue((measurementStatus >> 3) & 0x03)).append("\n");
        sb.append(MeasurementStatus.MEASUREMENT_POSITION_DETECTION.getFlagValue(measurementStatus & 0x20));

        return sb.toString();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(super.toString()).append("\n")
                .append("sys = ").append(sys).append("\n")
                .append("dia = ").append(dia).append("\n")
                .append("map = ").append(map).append("\n")
                .append("pulserate = ").append(pulseRate).append("\n")
                .append("unit = ").append(unit).append("(").append(getUnitStr()).append(")").append("\n")
                .append("timestamp = ").append(timestamp).append("\n")
                .append("userId = ").append(userId).append("\n")
                .append("measurementStatus = ").append(measurementStatus).append("\n")
                .append("measurementStatusStr = ").append("\n").append(getMeasurementStatusStr()).append("\n");

        return sb.toString();
    }

    public static enum MeasurementStatus{
        BODY_MOVEMENT_DETECTION(0, "No body movement", "Body movement during measurement"),
        CUFF_FIT_DETECTION(1, "Cuff fits properly", "Cuff too loose"),
        IRREGULAR_PULSE_DETECTION(2, "No irregular pulse detected", "irregular pulse detected"),
        PULSE_RATE_RANGE_DETECTION(3, "Pulse rate is within the range", "Pulse rate exceeds upper limit", "Pulse rate is less than lower limit"),
        MEASUREMENT_POSITION_DETECTION(4, "Proper measurement position", "Improper measurement position");

        public final String values[];
        public final int index;

        private MeasurementStatus(int index, String... values){
            this.index = index;
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

    public static enum BPFeature{
        BODY_MOVEMENT_DETECTION_SUPPORT,
        CUFF_FIT_DETECTION_SUPPORT,
        IRREGULAR_PULSE_DETECTION_SUPPORT,
        PULSE_RATE_RANGE_DETECTION_SUPPORT,
        MEASUREMENT_POSITION_DETECTION_SUPPORT,
        MULTIPLE_BOND_SUPPORT;
    }
}
