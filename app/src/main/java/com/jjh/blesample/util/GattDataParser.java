package com.jjh.blesample.util;

import android.bluetooth.BluetoothGattCharacteristic;

import com.jjh.blesample.vo.measurement.BP;
import com.jjh.blesample.vo.measurement.Glucose;
import com.jjh.blesample.vo.measurement.HeartRate;
import com.jjh.blesample.vo.measurement.Spo2;
import com.jjh.blesample.vo.measurement.Temperature;
import com.jjh.blesample.vo.measurement.Weight;

import org.joda.time.DateTime;

/**
 * Created by jjh860627 on 2017. 10. 30..
 */

public class GattDataParser {

    public BP parseBPData(BluetoothGattCharacteristic characteristic){

        byte flag = characteristic.getValue()[0];

        int unit = (flag & 0x01);

        float sys = characteristic.getFloatValue(BluetoothGattCharacteristic.FORMAT_SFLOAT, 1);
        float dia = characteristic.getFloatValue(BluetoothGattCharacteristic.FORMAT_SFLOAT, 3);
        float map = characteristic.getFloatValue(BluetoothGattCharacteristic.FORMAT_SFLOAT, 5);

        int index = 7;

        DateTime timestamp = null;
        if ((flag & 0x02) != 0) { //Time stamp(0: Not presented, 1: presented)
            timestamp = getDateTimeFromCharacteristic(characteristic, index);
            index += 7;
        }

        float pulseRate = -1;
        if ((flag & 0x04) != 0) { //Pulse Rate(0: Not presented, 1: Presented)
            pulseRate = characteristic.getFloatValue(BluetoothGattCharacteristic.FORMAT_SFLOAT, index);
            index += 2;
        }

        int userId = -1;
        if ((flag & 0x08) != 0) { //User ID(0: Not presented, 1: Presented)
            userId = characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT8, index);
            index += 1;
        }

        int measurementStatus = -1;
        if ((flag & 0x10) != 0) {
            measurementStatus = characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT16, index);
        }

        BP bp = new BP();

        bp.setSys(sys);
        bp.setDia(dia);
        bp.setMap(map);
        bp.setTimestamp(timestamp);
        bp.setPulseRate(pulseRate);
        bp.setUserId(userId);
        bp.setMeasurementStatus(measurementStatus);

        return bp;
    }

    public HeartRate parseHeartRateData(BluetoothGattCharacteristic characteristic){

        byte flag = characteristic.getValue()[0];

        int format = BluetoothGattCharacteristic.FORMAT_UINT8;
        if((flag & 0x01) != 0){ //Heart Rate Value Format
            format = BluetoothGattCharacteristic.FORMAT_UINT16;
        }

        int heartRateValue = characteristic.getIntValue(format, 1);

        int sensorContactStatus = (flag >> 1) & 0x03;

        int index = 2;
        int energyExpended = -1;
        if((flag & 0x08) != 0){ //Energy Expended
            energyExpended =  characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT16, index);
            index += 2;
        }

        int interval = -1;
        if((flag & 0x10) != 0){
            interval = characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT16, index);
        }

        HeartRate heartRate = new HeartRate();

        heartRate.setHeartRate(heartRateValue);
        heartRate.setSensorContactStatus(sensorContactStatus);
        heartRate.setEnergyExpended(energyExpended);
        heartRate.setInterval(interval);

        return heartRate;
    }

    public Temperature parseTemperatureData(BluetoothGattCharacteristic characteristic){

        float temperatureValue = characteristic.getFloatValue(BluetoothGattCharacteristic.FORMAT_FLOAT, 1);

        byte flag = characteristic.getValue()[0];

        int unit = (flag & 0x01);

        int index = 5;
        DateTime timestamp = null;
        if ((flag & 0x02) != 0) { //Time stamp(0: Not presented, 1: presented)
            timestamp = getDateTimeFromCharacteristic(characteristic, index);
            index += 7;
        }

        int temperatureType = -1;
        if ((flag & 0x04) != 0) { //Temperature type(0: Not presented, 1: presented)
            temperatureType = characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT8, index);
        }

        Temperature temperature = new Temperature();
        temperature.setTemperature(temperatureValue);
        temperature.setUnit(unit);
        temperature.setTimestamp(timestamp);
        temperature.setTemperatureType(temperatureType);

        return temperature;
    }

    public Glucose parseGlucoseData(BluetoothGattCharacteristic characteristic){
        byte flag = characteristic.getValue()[0];

        int sequenceNum = characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT16, 1);

        DateTime baseDateTime = getDateTimeFromCharacteristic(characteristic, 3);

        int index = 10;
        int timeOffset = -1;
        if((flag & 0x01) != 0){
            timeOffset = characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_SINT16, index);
            index += 2;
        }

        int unit = flag & 0x04;

        float gluco = characteristic.getFloatValue(BluetoothGattCharacteristic.FORMAT_SFLOAT, index);
        if(unit == 0){
            gluco *= 100000; // kg/L => mm/gL
        }
        index += 2;

        int type = -1;
        int sampleLocation = -1;
        if((flag & 0x02) != 0){
            int typeSampleLocation = characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT8, index);
            index += 1;
            type = typeSampleLocation & 0x0F;
            sampleLocation = (typeSampleLocation >> 4) & 0x0F;
        }

        int sensorStatus = -1;
        if((flag & 0x08) != 0) {
            sensorStatus = characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT16, index);
        }

        Glucose glucose = new Glucose();
        glucose.setBaseTime(baseDateTime);
        glucose.setGlucose(gluco);
        glucose.setSampleLocation(sampleLocation);
        glucose.setSensorStatusAnnunciation(sensorStatus);
        glucose.setSeqenceNumber(sequenceNum);
        glucose.setTimeOffset(timeOffset);
        glucose.setType(type);
        glucose.setUnit(unit);

        return glucose;
    }

    public Glucose.MeasurementContext parseGlucoseContextData(BluetoothGattCharacteristic characteristic){
        byte flag = characteristic.getValue()[0];

        int sequenceNumber = characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT16, 1);

        int index = 3;
        int carbohydrateID = -1;
        float carbohydrate = -1.0f;
        if((flag & 0x01) != 0){ //Carbohydrate ID and Carbohydrate Present
            carbohydrateID = characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT8, index);
            index += 1;
            carbohydrate = characteristic.getFloatValue(BluetoothGattCharacteristic.FORMAT_SFLOAT, index);
            index += 2;
        }

        int meal = -1;
        if((flag & 0x02) != 0){ //Meal Present
            meal = characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT8, index);
            index += 1;
        }

        int tester = -1;
        int health = -1;
        if((flag & 0x04) != 0){ //Tester-Health Present
            int testerHealth = characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT8, index);
            index += 1;
            tester = testerHealth & 0x0F;
            health = (testerHealth >> 4) & 0x0F;
        }

        int exerciseDuration = -1;
        int exerciseIntensity = -1;
        if((flag & 0x08) != 0){ //Exercise Duration And Exercise Intensity Present
            exerciseDuration = characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT16, index);
            index += 2;
            exerciseIntensity = characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT8, index);
            index += 1;
        }

        int medicationID = -1;
        int medicationUnit = -1;
        float medication = -1;
        if((flag & 0x10) != 0){ //Medication ID And Medication Present
            medicationID = characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT8, index);
            index += 1;
            medicationUnit = flag & 0x20;
            medication = characteristic.getFloatValue(BluetoothGattCharacteristic.FORMAT_SFLOAT, index);
            index += 2;
        }

        Glucose.MeasurementContext measurementContext = new Glucose.MeasurementContext();
        measurementContext.setSequenceNumber(sequenceNumber);
        measurementContext.setCarbohydrateID(carbohydrateID);
        measurementContext.setCarbohydrate(carbohydrate);
        measurementContext.setMeal(meal);
        measurementContext.setTester(tester);
        measurementContext.setHealth(health);
        measurementContext.setExerciseDuration(exerciseDuration);
        measurementContext.setExerciseIntensity(exerciseIntensity);
        measurementContext.setMedicationID(medicationID);
        measurementContext.setMedicationUnit(medicationUnit);
        measurementContext.setMedication(medication);

        return measurementContext;
    }

    public Weight parseWeightScaleData(BluetoothGattCharacteristic characteristic){
        byte flag = characteristic.getValue()[0];

        int unit = flag & 0x01;
        int weightValue = characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT16, 1);
        int index = 3;

        DateTime timestamp = null;
        if((flag & 0x02) != 0){
            timestamp = getDateTimeFromCharacteristic(characteristic, index);
            index += 7;

        }

        int userID = -1;
        if((flag & 0x04) != 0){
            userID = characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT8, index);
            index += 1;
        }

        int bmi = -1;
        int height = -1;
        if((flag & 0x08) != 0){
            bmi = characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT16, index);
            index += 2;
            height = characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT16, index);
        }

        Weight weight = new Weight();
        weight.setUnit(unit);
        weight.setWeight(weightValue);
        weight.setTimestamp(timestamp);
        weight.setUserId(userID);
        weight.setBmi(bmi);
        weight.setHeight(height);

        return weight;
    }

    public Spo2 parseSpo2Data(BluetoothGattCharacteristic characteristic){
        byte flag = characteristic.getValue()[0];

        float spo2Value = characteristic.getFloatValue(BluetoothGattCharacteristic.FORMAT_SFLOAT, 1);
        float pr = characteristic.getFloatValue(BluetoothGattCharacteristic.FORMAT_SFLOAT, 3);

        int index = 5;

        DateTime timestamp = null;
        if((flag & 0x01) != 0){
            timestamp = getDateTimeFromCharacteristic(characteristic, index);
            index += 7;
        }

        int measurementStatus = -1;
        if((flag & 0x02) != 0){
            measurementStatus = characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT16, index);
            index += 2;
        }

        int deviceAndSensorStatus = -1;
        if((flag & 0x04) != 0){
            //8 + 16 = 24bit
            deviceAndSensorStatus = characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT8, index) + characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT16, index + 1) << 8;
            index += 3;
        }

        float pulseAmplitudeIndex = -1;
        if((flag & 0x08) != 0){
            pulseAmplitudeIndex = characteristic.getFloatValue(BluetoothGattCharacteristic.FORMAT_SFLOAT, index);
            index += 2;
        }

        boolean isDeviceClockSet = (flag & 0x10) == 1;

        Spo2 spo2 = new Spo2();
        spo2.setSpo2(spo2Value);
        spo2.setPr(pr);
        spo2.setTimestamp(timestamp);
        spo2.setMeasurementStatus(measurementStatus);
        spo2.setDeviceAndSensorStatus(deviceAndSensorStatus);
        spo2.setPulseAmplitudeIndex(pulseAmplitudeIndex);
        spo2.setDeviceClockSet(isDeviceClockSet);

        return spo2;
    }

    public Spo2 parseSpo2ContinuousData(BluetoothGattCharacteristic characteristic){
        byte flag = characteristic.getValue()[0];

        float spo2Value = characteristic.getFloatValue(BluetoothGattCharacteristic.FORMAT_SFLOAT, 1);
        float pr = characteristic.getFloatValue(BluetoothGattCharacteristic.FORMAT_SFLOAT, 3);

        int index = 5;

        float spo2Fast = -1;
        float prFast = -1;
        if((flag & 0x01) != 0){
            spo2Fast = characteristic.getFloatValue(BluetoothGattCharacteristic.FORMAT_SFLOAT, index);
            index += 2;
            prFast = characteristic.getFloatValue(BluetoothGattCharacteristic.FORMAT_SFLOAT, index);
            index += 2;
        }

        float spo2Slow = -1;
        float prSlow = -1;
        if((flag & 0x02) != 0){
            spo2Slow = characteristic.getFloatValue(BluetoothGattCharacteristic.FORMAT_SFLOAT, index);
            index += 2;
            prSlow = characteristic.getFloatValue(BluetoothGattCharacteristic.FORMAT_SFLOAT, index);
            index += 2;
        }

        int measurementStatus = -1;
        if((flag & 0x04) != 0){
            measurementStatus = characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT16, index);
            index += 2;
        }

        int deviceAndSensorStatus = -1;
        if((flag & 0x08) != 0){
            //8 + 16 = 24bit
            deviceAndSensorStatus = characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT8, index) + characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT16, index + 1) << 8;
            index += 3;
        }

        float pulseAmplitudeIndex = -1;
        if((flag & 0x10) != 0){
            pulseAmplitudeIndex = characteristic.getFloatValue(BluetoothGattCharacteristic.FORMAT_SFLOAT, index);
            index += 2;
        }

        Spo2 spo2 = new Spo2();
        spo2.setSpo2(spo2Value);
        spo2.setPr(pr);
        spo2.setMeasurementStatus(measurementStatus);
        spo2.setDeviceAndSensorStatus(deviceAndSensorStatus);
        spo2.setPulseAmplitudeIndex(pulseAmplitudeIndex);

        Spo2.MeasurementContinuous measurementContinuous = new Spo2.MeasurementContinuous();
        measurementContinuous.setSpo2Fast(spo2Fast);
        measurementContinuous.setPrFast(prFast);
        measurementContinuous.setSpo2Slow(spo2Slow);
        measurementContinuous.setPrSlow(prSlow);

        spo2.setMeasurementContinuous(measurementContinuous);

        return spo2;

    }


    private DateTime getDateTimeFromCharacteristic(BluetoothGattCharacteristic characteristic, int startIndex){
        int year = characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT16, startIndex);
        startIndex += 2;
        int month = characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT8, startIndex++);
        int day = characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT8, startIndex++);
        int hours = characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT8, startIndex++);
        int minutes = characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT8, startIndex++);
        int seconds = characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT8, startIndex++);

        return new DateTime(year, month, day, hours, minutes, seconds);
    }
}
