package com.jjh.blesample.attrs;

import com.jjh.blesample.util.CommonUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static com.jjh.blesample.util.CommonUtils.getUUIDHead;

/**
 * Created by jjh860627 on 2017. 10. 27..
 */

public class GattAttributes {

    private static Map<Integer, Service> serviceMap = new HashMap<Integer,Service>();
    static{
        for(Service service: Service.values()){
            serviceMap.put(service.uuid, service);
        }
    }


    private static Map<Integer, Characteristic> characteristicMap = new HashMap<Integer,Characteristic>();
    static{
        for(Characteristic characteristic: Characteristic.values()){
            characteristicMap.put(characteristic.uuid, characteristic);
        }
    }

    public static enum Service{
        GENERIC_ACCESS(0x1800, "Generic Access Service"),
        GENERIC_ATTRIBUTE(0x1801, "Generic Attribute Service"),
        HEART_RATE(0x180D, "Heart Rate Service"),
        HEALTH_THERMOMETER(0x1809, "Health Thermometer Service"),
        BLOOD_PRESSURE(0x1810, "Blood Pressure Service"),
        GLUCOSE(0x1808, "Glucose Service"),
        WEIGHT_SCALE(0x181D, "Weight Scale Service"),
        PULSE_OXIMITER_SERVICE(0x1822, "Pulse Oximeter Service");

        public final int uuid;
        public final String name;
        private Service(int uuid, String name){
            this.uuid = uuid;
            this.name = name;
        }

        public boolean isMatchedWithUUID(UUID uuid){
            if(uuid == null ) return false;
            return this.uuid == getUUIDHead(uuid);
        }

    }

    public static enum Characteristic{
        /* Generic Access Service*/
        DEVICE_NAME(0x2A00, "Device Name(Read)"),
        APPEARANCE(0x2A01, "Appearance(Read)"),
        CENTRAL_ADDRESS_RESOLUTION(0x2AA6, "Central Address Resolution"),
        /* Heart Rate Service */
        HEART_RATE_MEASUREMENT(0x2A37, "Heart Rate Measurement(Notify)"),
        BODY_SENSOR_LOCATION(0x2A38, "Body Sensor Location(Read)"),
        HEART_RATE_CONTROL_POINT(0x2A39, "Heart Rate Control Point(Write)"),
        /* Health Thermometer Service */
        TEMPERATURE_MEASUREMENT(0x2A1C, "Temperature Measurement(Indicate)"),
        TEMPERATURE_TYPE(0x2A1D, "Temperature Type(Read)"),
        INTERMEDIATE_TEMPERATURE(0x2A1E, "Intermediate Temperature(Notify)"),
        MEASUREMENT_INTERVAL(0x2A21, "Measurement Interval(Read)"),
        /* Blood Pressure Service */
        BLOOD_PRESSURE_MEASUREMENT(0x2A35, "Blood Pressure Measurement(Indicate)"),
        INTERMEDIATE_CUFF_PRESSURE(0x2A36, "Intermediate Cuff Pressure(Notify)"),
        BLOOD_PRESSURE_FEATURE(0x2A49, "Blood Pressure Feature(Read)"),
        /* Glucose Service */
        GLUCOSE_MEASUREMENT(0x2A18, "Glucose Measurement(Notify)"),
        GLUCOSE_MEASUREMENT_CONTEXT(0x2A34, "Glucose Measurement Context(Notify)"),
        GLUCOSE_FEATURE(0x2A51, "Glucose Feature(Read)"),
        GLUCOSE_RECORD_ACCESS_CONTROL_POINT(0x2A52, "Record Access Control Point(Write,Indicate)"),
        /* Weight Scale */
        WEIGHT_SCALE_FEATURE(0x2A9E, "Weight Scale Feature(Read)"),
        WEIGHT_MEASUREMENT(0x2A9D, "Weight Measurement(Indicate)");

        public final int uuid;
        public final String name;
        private Characteristic(int uuid, String name){
            this.uuid = uuid;
            this.name = name;
        }

        public boolean isMatchedWithUUID(UUID uuid){
            if(uuid == null ) return false;
            return this.uuid == getUUIDHead(uuid);
        }

    }


    public static String getServiceName(UUID uuid){
        int uuidHead = CommonUtils.getUUIDHead(uuid);
        if(serviceMap.containsKey(uuidHead)){
            return serviceMap.get(uuidHead).name;
        }else{
            return "Unknwon Service";
        }
    }

    public static String getCharacteristicName(UUID uuid){
        int uuidHead = CommonUtils.getUUIDHead(uuid);
        if(characteristicMap.containsKey(uuidHead)){
            return characteristicMap.get(uuidHead).name;
        }else{
            return "Unknwon Characteristic";
        }
    }
}
