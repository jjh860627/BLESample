package com.jjh.blesample.vo.measurement;

import org.joda.time.DateTime;

import java.io.Serializable;

/**
 * Created by jjh860627 on 2017. 10. 31..
 */

public class Glucose implements Serializable{
    private int seqenceNumber;
    private DateTime baseTime;
    private int timeOffset;
    private float glucose;
    private int unit;
    private int type;
    private int sampleLocation;
    private int sensorStatusAnnunciation;

    public int getSeqenceNumber() {
        return seqenceNumber;
    }

    public void setSeqenceNumber(int seqenceNumber) {
        this.seqenceNumber = seqenceNumber;
    }

    public DateTime getBaseTime() {
        return baseTime;
    }

    public void setBaseTime(DateTime baseTime) {
        this.baseTime = baseTime;
    }

    public int getTimeOffset() {
        return timeOffset;
    }

    public void setTimeOffset(int timeOffset) {
        this.timeOffset = timeOffset;
    }

    public float getGlucose() {
        return glucose;
    }

    public void setGlucose(float glucose) {
        this.glucose = glucose;
    }

    public int getUnit() {
        return unit;
    }

    public void setUnit(int unit) {
        this.unit = unit;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getSampleLocation() {
        return sampleLocation;
    }

    public void setSampleLocation(int sampleLocation) {
        this.sampleLocation = sampleLocation;
    }

    public int getSensorStatusAnnunciation() {
        return sensorStatusAnnunciation;
    }

    public void setSensorStatusAnnunciation(int sensorStatusAnnunciation) {
        this.sensorStatusAnnunciation = sensorStatusAnnunciation;
    }

    public String getUnitStr(){
        return unit == 0? "kg/L" : "mol/L";
    }

    public String getTypeStr(){
        String typeStr = null;
        for(Type t: Type.values()){
            if(t.index == type){
                typeStr = t.text;
                break;
            }
        }
        return typeStr;
    }

    public String getSampleLocationStr(){
        String sampleLocationStr = null;
        for(SampleLocation s: SampleLocation.values()){
            if(s.index == type){
                sampleLocationStr = s.text;
                break;
            }
        }
        return sampleLocationStr;
    }

    public String getSensorStatusAnnunciationStr(){
        StringBuilder sb = new StringBuilder();
        if(sensorStatusAnnunciation >= 0) {
            byte bitFlag = 0x01;
            for (SensorStatusAnnunciation sensorStatus : SensorStatusAnnunciation.values()) {
                sb.append(sensorStatus.name()).append(" = ")
                        .append((sensorStatusAnnunciation & bitFlag) == 1)
                        .append("\n");
                bitFlag *= 2;
            }
        }

        return sb.toString();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(super.toString()).append("\n")
                .append("seqenceNumber = ").append(seqenceNumber).append("\n")
                .append("baseTime = ").append(baseTime).append("\n")
                .append("timeOffset = ").append(timeOffset).append("\n")
                .append("glucose = ").append(glucose).append("\n")
                .append("unit = ").append(unit).append("(").append(getUnitStr()).append(")").append("\n")
                .append("type = ").append(type).append("(").append(getTypeStr()).append(")").append("\n")
                .append("sensorStatusAnnunciation = ").append(sensorStatusAnnunciation).append("\n")
                .append("sensorStatusAnnunciationStr = ").append("\n").append(getSensorStatusAnnunciationStr()).append("\n");

        return sb.toString();
    }

    public static enum Type{
        CAPILLARY_WHOLE_BLOOD(1, "Capillary Whole blood"),
        CAPILLARY_PLASMA(2, "Capillary Plasma"),
        VENOUS_WHOLE_BLOOD(3, "Venous Whole blood"),
        VENOUS_PLASMA(4, "Venous Plasma"),
        ARTERIAL_WHOLE_BLOOD(5, "Arterial Whole blood"),
        ARTERIAL_PLASMA(6, "Arterial Plasma"),
        UNDETERMINED_WHOLE_BLOOD(7, "Undetermined Whole blood"),
        UNDETERMINED_PLASMA(8, "Undetermined Plasma"),
        INTERSTITIAL_FLUID(9, "Interstitial Fluid (ISF)"),
        CONTROL_SOLUTION(10, "Control Solution");

        public final int index;
        public final String text;

        private Type(int index, String text){
            this.index = index;
            this.text = text;
        }
    }

    public static enum SampleLocation{
        FINGER(1, "Finger"),
        ALTERNATE_SITE_TEST(2, "Alternate Site Test (AST)"),
        EARLOBE(3, "Earlobe"),
        CONTROL_SOLUTION(4, "Control solution"),
        SAMPLE_LOCATION_VALUE_NOT_AVAILABLE(15, "Sample Location value not available");

        public final int index;
        public final String text;

        private SampleLocation(int index, String text){
            this.index = index;
            this.text = text;
        }
    }

    public static enum SensorStatusAnnunciation{
        DEVICE_BATTERY_LOW_AT_TIME_OF_MEASUREMENT,
        SENSOR_MALFUNCTION_OR_FAULTING_AT_TIME_OF_MEASUREMENT,
        SAMPLE_SIZE_FOR_BLOOD_OR_CONTROL_SOLUTION_INSUFFICIENT_AT_TIME_OF_MEASUREMENT,
        STRIP_INSERTION_ERROR,
        STRIP_TYPE_INCORRECT_FOR_DEVICE,
        SENSOR_RESULT_HIGHER_THAN_THE_DEVICE_CAN_PROCESS,
        SENSOR_RESULT_LOWER_THAN_THE_DEVICE_CAN_PROCESS,
        SENSOR_TEMPERATURE_TOO_HIGH_FOR_VALID_TEST_RESULT_AT_TIME_OF_MEASUREMENT,
        SENSOR_TEMPERATURE_TOO_LOW_FOR_VALID_TEST_RESULT_AT_TIME_OF_MEASUREMENT,
        SENSOR_READ_INTERRUPTED_BECAUSE_STRIP_WAS_PULLED_TOO_SOON_AT_TIME_OF_MEASUREMENT,
        GENERAL_DEVICE_FAULT_HAS_OCCURRED_IN_THE_SENSOR,
        TIME_FAULT_HAS_OCCURRED_IN_THE_SENSOR_AND_TIME_MAY_BE_INACCURATE;
    }

    public static enum GlucoseFeature{
        LOW_BATTERY_DETECTION_DURING_MEASUREMENT,
        SENSOR_MALFUNCTION_DETECTION,
        SENSOR_SAMPLE_SIZE,
        SENSOR_STRIP_INSERTION_ERROR_DETECTION,
        SENSOR_STRIP_TYPE_ERROR_DETECTION,
        SENSOR_RESULT_HIGH_LOW_DETECTION,
        SENSOR_TEMPERATURE_HIGH_LOW_DETECTION,
        SENSOR_READ_INTERRUPT_DETECTION,
        GENERAL_DEVICE_FAULT,
        TIME_FAULT	,
        MULTIPLE_BOND;
    }

    public static class MeasurementContext implements Serializable{
        private int sequenceNumber;
        private int carbohydrateID;
        private float carbohydrate;
        private int meal;
        private int tester;
        private int health;
        private int exerciseDuration;
        private int exerciseIntensity;
        private int medicationID;
        private int medicationUnit;
        private float medication;

        public int getSequenceNumber() {
            return sequenceNumber;
        }

        public void setSequenceNumber(int sequenceNumber) {
            this.sequenceNumber = sequenceNumber;
        }

        public int getCarbohydrateID() {
            return carbohydrateID;
        }

        public void setCarbohydrateID(int carbohydrateID) {
            this.carbohydrateID = carbohydrateID;
        }

        public float getCarbohydrate() {
            return carbohydrate;
        }

        public void setCarbohydrate(float carbohydrate) {
            this.carbohydrate = carbohydrate;
        }

        public int getMeal() {
            return meal;
        }

        public void setMeal(int meal) {
            this.meal = meal;
        }

        public int getTester() {
            return tester;
        }

        public void setTester(int tester) {
            this.tester = tester;
        }

        public int getHealth() {
            return health;
        }

        public void setHealth(int health) {
            this.health = health;
        }

        public int getExerciseDuration() {
            return exerciseDuration;
        }

        public void setExerciseDuration(int exerciseDuration) {
            this.exerciseDuration = exerciseDuration;
        }

        public int getExerciseIntensity() {
            return exerciseIntensity;
        }

        public void setExerciseIntensity(int exerciseIntensity) {
            this.exerciseIntensity = exerciseIntensity;
        }

        public int getMedicationID() {
            return medicationID;
        }

        public void setMedicationID(int medicationID) {
            this.medicationID = medicationID;
        }

        public int getMedicationUnit() {
            return medicationUnit;
        }

        public void setMedicationUnit(int medicationUnit) {
            this.medicationUnit = medicationUnit;
        }

        public float getMedication() {
            return medication;
        }

        public void setMedication(float medication) {
            this.medication = medication;
        }

        public String getCarbohydrateIDStr(){
            String carbohydrateIDStr = null;
            for(CarbohydrateID c : CarbohydrateID.values()){
                if(c.index == carbohydrateID){
                    carbohydrateIDStr = c.text;
                    break;
                }
            }
            return carbohydrateIDStr;
        }

        public String getMealStr(){
            String mealStr = null;
            for(Meal m : Meal.values()){
                if(m.index == meal){
                    mealStr = m.text;
                    break;
                }
            }
            return mealStr;
        }

        public String getTesterStr(){
            String testerStr = null;
            for(Tester t : Tester.values()){
                if(t.index == tester){
                    testerStr = t.text;
                    break;
                }
            }
            return testerStr;
        }

        public String getHealthStr(){
            String healthStr = null;
            for(Health h : Health.values()){
                if(h.index == tester){
                    healthStr = h.text;
                    break;
                }
            }
            return healthStr;
        }

        public String getMedicationIDStr(){
            String medicationIDStr = null;
            for(MedicationID m : MedicationID.values()){
                if(m.index == tester){
                    medicationIDStr = m.text;
                    break;
                }
            }
            return medicationIDStr;
        }

        public String getMedicationUnitStr(){
            return medicationUnit == 0? "kg" : "liters";
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append(super.toString()).append("\n")
                    .append("sequenceNumber = ").append(sequenceNumber).append("\n")
                    .append("carbohydrateID = ").append(carbohydrateID).append("(").append(getCarbohydrateIDStr()).append(")").append("\n")
                    .append("carbohydrate = ").append(carbohydrate).append("\n")
                    .append("meal = ").append(meal).append("(").append(getMealStr()).append(")").append("\n")
                    .append("tester = ").append(tester).append("(").append(getTesterStr()).append(")").append("\n")
                    .append("health = ").append(health).append("(").append(getHealthStr()).append(")").append("\n")
                    .append("exerciseDuration = ").append(exerciseDuration).append("\n")
                    .append("exerciseIntensity = ").append(exerciseIntensity).append("\n")
                    .append("medicationID = ").append(medicationID).append("(").append(getMedicationIDStr()).append(")").append("\n")
                    .append("medicationUnit = ").append(medicationUnit).append("(").append(getMedicationUnitStr()).append(")").append("\n")
                    .append("medication = ").append(medication).append("\n");

            return sb.toString();
        }

        public static enum CarbohydrateID{
            BREAKFAST(1, "Breakfast"),
            LUNCH(2, "Lunch"),
            DINNER(3, "Dinner"),
            SNACK(4, "Snack"),
            DRINK(5, "Drink"),
            SUPPER(6, "Supper"),
            BRUNCH(7, "Brunch");

            public final int index;
            public final String text;

            private CarbohydrateID(int index, String text){
                this.index = index;
                this.text = text;
            }
        }

        public static enum Meal{
            Preprandial(1, "Preprandial"),
            Postprandial(2, "Postprandial"),
            Fasting(3, "Fasting"),
            Casual(4, "Casual"),
            Bedtime(5, "Bedtime");

            public final int index;
            public final String text;

            private Meal(int index, String text){
                this.index = index;
                this.text = text;
            }
        }

        public static enum Tester{
            SELF(1, "Self"),
            HEALTH_CARE_PROFESSIONAL(2, "Health Care Professional"),
            LAB_TEST(3, "Lab test"),
            TESTER_VALUE_NOT_AVAILABLE(15, "Tester value not available");

            public final int index;
            public final String text;

            private Tester(int index, String text){
                this.index = index;
                this.text = text;
            }
        }

        public static enum Health{
            MINOR_HEALTH_ISSUES(1, "Minor health issues"),
            MAJOR_HEALTH_ISSUES(2, "Major health issues"),
            DURING_MENSES(3, "During menses"),
            UNDER_STRESS(4, "Under stress"),
            NO_HEALTH_ISSUES(5, "No health issues"),
            HEALTH_VALUE_NOT_AVAILABLE(15, "Health value not available");

            public final int index;
            public final String text;

            private Health(int index, String text){
                this.index = index;
                this.text = text;
            }
        }

        public static enum MedicationID{
            RAPID_ACTING_INSULIN(1, "Rapid acting insulin"),
            SHORT_ACTING_INSULIN(2, "Short acting insulin"),
            INTERMEDIATE_ACTING_INSULIN(3, "Intermediate acting insulin"),
            LONG_ACTING_INSULIN(4, "Long acting insulin"),
            PRE_MIXED_INSULIN(5, "Pre-mixed insulin");

            public final int index;
            public final String text;

            private MedicationID(int index, String text){
                this.index = index;
                this.text = text;
            }
        }

    }
}
