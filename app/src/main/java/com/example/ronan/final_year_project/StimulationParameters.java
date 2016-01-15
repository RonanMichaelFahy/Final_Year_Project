package com.example.ronan.final_year_project;

import java.io.Serializable;

/**
 * Created by Ronan on 15/01/2016.
 */
public class StimulationParameters implements Serializable {

    private int rampUpTime;
    private int rampDownTime;
    private int pulseWidth;
    private int pulseFrequency;
    private int intensity;

    public StimulationParameters(){
        this(0, 0, 0, 0, 0);
    }

    public StimulationParameters(int rampUpTime, int getRampDownTime, int pulseWidth, int pulseFrequency, int intensity) {
        setIntensity(intensity);
        setPulseFrequency(pulseFrequency);
        setPulseWidth(pulseWidth);
        setRampDownTime(rampDownTime);
        setRampUpTime(rampUpTime);
    }

    public int getRampUpTime() {
        return this.rampUpTime;
    }

    public void setRampUpTime(int rampUpTime) {
        this.rampUpTime = rampUpTime;
    }

    public int getRampDownTime() {
        return this.rampDownTime;
    }

    public void setRampDownTime(int rampDownTime) {
        this.rampDownTime = rampDownTime;
    }

    public int getPulseWidth() {
        return this.pulseWidth;
    }

    public void setPulseWidth(int pulseWidth) {
        this.pulseWidth = pulseWidth;
    }

    public int getPulseFrequency() {
        return this.pulseFrequency;
    }

    public void setPulseFrequency(int pulseFrequency) {
        this.pulseFrequency = pulseFrequency;
    }

    public int getIntensity() {
        return this.intensity;
    }

    public void setIntensity(int intensity) {
        this.intensity = intensity;
    }
}
