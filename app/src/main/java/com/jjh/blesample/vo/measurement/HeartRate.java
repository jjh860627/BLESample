package com.jjh.blesample.vo.measurement;

import java.io.Serializable;

/**
 * Created by jjh860627 on 2017. 10. 31..
 */

public class HeartRate implements Serializable {

    private int heartRate;
    private int sensorContactStatus;
    private int energyExpended;
    private int interval;

    public int getHeartRate() {
        return heartRate;
    }

    public void setHeartRate(int heartRate) {
        this.heartRate = heartRate;
    }

    public int getSensorContactStatus() {
        return sensorContactStatus;
    }

    public void setSensorContactStatus(int sensorContactStatus) {
        this.sensorContactStatus = sensorContactStatus;
    }

    public int getEnergyExpended() {
        return energyExpended;
    }

    public void setEnergyExpended(int energyExpended) {
        this.energyExpended = energyExpended;
    }

    public int getInterval() {
        return interval;
    }

    public void setInterval(int interval) {
        this.interval = interval;
    }

    public String getSensorContactStatusStr(){
        return SensorContactStatus.values()[sensorContactStatus].text;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(super.toString()).append("\n")
                .append("heartRate = ").append(heartRate).append("\n")
                .append("sensorContactStatus = ").append(sensorContactStatus).append("(").append(getSensorContactStatusStr()).append(")").append("\n")
                .append("energyExpended = ").append(energyExpended).append("\n")
                .append("interval = ").append(interval).append("\n");

        return sb.toString();
    }

    public static enum SensorContactStatus{
        NOT_SUPPORT(0, "No body movement"),
        NOT_SUPPROT2(1, "Cuff fits properly"),
        SUPPORT_BUT_NOT_DETECTED(2, "No irregular pulse detected"),
        SUPPORT_AND_DETECTED(3, "Pulse rate is within the range");

        public final String text;
        public final int index;

        private SensorContactStatus(int index, String text){
            this.index = index;
            this.text = text;
        }
    }
}
