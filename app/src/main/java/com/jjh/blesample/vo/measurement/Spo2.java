package com.jjh.blesample.vo.measurement;

import org.joda.time.DateTime;

import java.io.Serializable;

/**
 * Created by jjh860627 on 2017. 11. 1..
 */

public class Spo2 implements Serializable{
    private float spo2;
    private float pr;
    private DateTime timestamp;
    private int measurementStatus;
    private int deviceAndSensorStatus;
    private float pulseAmplitudeIndex;
    private boolean isDeviceClockSet;
    private MeasurementContinuous measurementContinuous;

    public float getSpo2() {
        return spo2;
    }

    public void setSpo2(float spo2) {
        this.spo2 = spo2;
    }

    public float getPr() {
        return pr;
    }

    public void setPr(float pr) {
        this.pr = pr;
    }

    public DateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(DateTime timestamp) {
        this.timestamp = timestamp;
    }

    public int getMeasurementStatus() {
        return measurementStatus;
    }

    public void setMeasurementStatus(int measurementStatus) {
        this.measurementStatus = measurementStatus;
    }

    public int getDeviceAndSensorStatus() {
        return deviceAndSensorStatus;
    }

    public void setDeviceAndSensorStatus(int deviceAndSensorStatus) {
        this.deviceAndSensorStatus = deviceAndSensorStatus;
    }

    public float getPulseAmplitudeIndex() {
        return pulseAmplitudeIndex;
    }

    public void setPulseAmplitudeIndex(float pulseAmplitudeIndex) {
        this.pulseAmplitudeIndex = pulseAmplitudeIndex;
    }

    public boolean isDeviceClockSet() {
        return isDeviceClockSet;
    }

    public void setDeviceClockSet(boolean deviceClockSet) {
        isDeviceClockSet = deviceClockSet;
    }

    public MeasurementContinuous getMeasurementContinuous() {
        return measurementContinuous;
    }

    public void setMeasurementContinuous(MeasurementContinuous measurementContinuous) {
        this.measurementContinuous = measurementContinuous;
    }

    public String getMeasurementStatusStr(){
        StringBuilder sb = new StringBuilder();
        int bitFlag = 0x10;
        for(MeasurementStatus ms : MeasurementStatus.values()){
            sb.append(ms.name()).append(" = ").append((measurementStatus & bitFlag) == 1).append("\n");
            bitFlag *= 2;
        }
        return sb.toString();
    }

    public String getDeviceAndSensorStatusStr(){
        StringBuilder sb = new StringBuilder();
        int bitFlag = 0x01;
        for(DeviceAndSensorStatus dass : DeviceAndSensorStatus.values()){
            sb.append(dass.name()).append(" = ").append((measurementStatus & bitFlag) == 1).append("\n");
            bitFlag *= 2;
        }
        return sb.toString();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(super.toString()).append("\n")
                .append("spo2 = ").append(spo2).append("\n")
                .append("pr = ").append(pr).append("\n")
                .append("measurementStatus = ").append(measurementStatus).append("(").append(getMeasurementStatusStr()).append(")").append("\n")
                .append("deviceAndSensorStatus = ").append(deviceAndSensorStatus).append("(").append(getDeviceAndSensorStatusStr()).append(")").append("\n")
                .append("pulseAmplitudeIndex = ").append(pulseAmplitudeIndex).append("\n");
        if(measurementContinuous == null) {
                sb.append("timestamp = ").append(timestamp).append("\n")
                  .append("isDeviceClockSet = ").append(isDeviceClockSet).append("\n");
        }else{
            sb.append("spo2Fast = ").append(measurementContinuous.getSpo2Fast()).append("\n")
              .append("prFast = ").append(measurementContinuous.getPrFast()).append("\n")
              .append("spo2Slow = ").append(measurementContinuous.getSpo2Slow()).append("\n")
              .append("prSlow = ").append(measurementContinuous.getPrSlow()).append("\n");
        }

        return sb.toString();
    }

    public static enum MeasurementStatus{
        MEASUREMENT_ONGOING,
        EARLY_ESTIMATED_DATA,
        VALIDATED_DATA,
        FULLY_QUALIFIED_DATA,
        DATA_FROM_MEASUREMENT_STORAGE,
        DATA_FOR_DEMONSTRATION,
        DATA_FOR_TESTING,
        CALIBRATION_ONGOING,
        MEASUREMENT_UNAVAILABLE,
        QUESTIONABLE_MEASUREMENT_DETECTED,
        INVALID_MEASUREMENT_DETECTED,
    }

    public static enum DeviceAndSensorStatus{
        EXTENDED_DISPLAY_UPDATE_ONGOING,
        EQUIPMENT_MALFUNCTION_DETECTED,
        SIGNAL_PROCESSING_IRREGULARITY_DETECTED,
        INADEQUITE_SIGNAL_DETECTED,
        POOR_SIGNAL_DETECTED,
        LOW_PERFUSION_DETECTED,
        ERRATIC_SIGNAL_DETECTED,
        NONPULSATILE_SIGNAL_DETECTED,
        QUESTIONABLE_PULSE_DETECTED,
        SIGNAL_ANALYSIS_ONGOING,
        SENSOR_INTERFACE_DETECTED,
        SENSOR_UNCONNECTED_TO_USER,
        UNKNOWN_SENSOR_CONNECTED,
        SENSOR_DISPLACED,
        SENSOR_MALFUNCTIONING,
        SENSOR_DISCONNECTED;
    }

    public static enum SupportedFeature{
        MEASUREMENT_STATUS,
        DEVICE_AND_SENSOR_STATUS,
        MEASUREMENT_STORAGE_FOR_SPOT_CHECK_MEASUREMENTS,
        TIMESTAMP_FOR_SPOT_CHECK_MEASUREMENTS,
        SPO2PR_FAST_METRIC,
        SPO2PR_SLOW_METRIC,
        PULSE_AMPLITUDE_INDEX_FIELD,
        MULTIPLE_BONDS;
    }

    public static class MeasurementContinuous{
        private float spo2Fast;
        private float prFast;
        private float spo2Slow;
        private float prSlow;

        public float getSpo2Fast() {
            return spo2Fast;
        }

        public void setSpo2Fast(float spo2Fast) {
            this.spo2Fast = spo2Fast;
        }

        public float getPrFast() {
            return prFast;
        }

        public void setPrFast(float prFast) {
            this.prFast = prFast;
        }

        public float getSpo2Slow() {
            return spo2Slow;
        }

        public void setSpo2Slow(float spo2Slow) {
            this.spo2Slow = spo2Slow;
        }

        public float getPrSlow() {
            return prSlow;
        }

        public void setPrSlow(float prSlow) {
            this.prSlow = prSlow;
        }
    }
}
