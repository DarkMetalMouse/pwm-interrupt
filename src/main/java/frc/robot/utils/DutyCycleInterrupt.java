package frc.robot.utils;

import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.SynchronousInterrupt;
import edu.wpi.first.wpilibj.SynchronousInterrupt.WaitResult;

/**
 * DutyCycleInterrupt
 */
public class DutyCycleInterrupt {

    private final SynchronousInterrupt _interrupt;

    private static final double maxFreqHz = 268; // From the datasheet
    private static final double timeoutMS = 2 * (1 / maxFreqHz) / 1000; // From the datasheet

    public DutyCycleInterrupt(SynchronousInterrupt interrupt) {
        this._interrupt = interrupt;
        this._interrupt.setInterruptEdges(true, true); // enable rising and falling edge interrupts

    }

    public DutyCycleInterrupt(DigitalInput source) {
        this(new SynchronousInterrupt(source));
    }

    public double getFrequencyHz() {
        double lastTime = _interrupt.getRisingTimestamp();
        double time = System.currentTimeMillis();
        double newTime;
        do {
            newTime = _interrupt.getRisingTimestamp();
        } while (newTime == lastTime && (System.currentTimeMillis() - time) <= timeoutMS);

        if (newTime == lastTime) {
            return 1 / (newTime - lastTime);
        } else {
            return -1;
        }
    }

    public double getDutyCyclePercent() {
        double frequencyHz = getFrequencyHz();
        if(frequencyHz == -1)
            return -1;
        double lastRising = _interrupt.getRisingTimestamp();
        double lastFalling = _interrupt.getFallingTimestamp();
        double deltaSeconds = lastFalling - lastRising;

        if (deltaSeconds > 0) { // delta is the on time
            return deltaSeconds * frequencyHz; // onTime / period == percent; 1/period == frequency
        } else { // delta is the off time (negative)
            return 1 + deltaSeconds * frequencyHz; // (period - (-offTime)) / period == percent 
        }
        

    }

}