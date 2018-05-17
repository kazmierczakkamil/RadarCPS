package cps;

import org.jfree.data.xy.XYDataItem;
import org.jfree.data.xy.XYSeries;

public class Signal {
    public static XYSeries signal(double period, double t0, int samplingFrequency) {
        XYSeries signal = new XYSeries("signal");

        double omega = 2 * Math.PI / period;
        double phase = t0 * omega;
        double samplingPeriod = 1.0 / samplingFrequency;

        for (int i = 0; i < samplingFrequency * period; i++) {
            signal.add(samplingPeriod * i, Math.sin(omega * samplingPeriod * i + phase) + square(omega * samplingPeriod * i + phase, period));
        }

        return signal;
    }


    public static XYSeries correlate(XYSeries first, XYSeries second) {
        XYSeries correlatedSignal = new XYSeries("correlated");

        for (int i = -(second.getItemCount()-1), j = 0; i < first.getItemCount(); i++, j++) {
            double sum = 0.0;

            for (int k = 0; k < first.getItems().size(); k++) {
                try {
                    double firstValue = first.getDataItem(k).getYValue();
                    double secondValue = second.getDataItem(k-i).getYValue();
                    sum += firstValue * secondValue;
                } catch (IndexOutOfBoundsException e) { }
            }
            correlatedSignal.add(j, sum);
        }
        return correlatedSignal;
    }

    public static double estimateDistance(XYSeries correlatedSignal, int samplingFrequency, double signalVelocity) {
        int samples = correlatedSignal.getItemCount();

        XYDataItem maxValueSample = new XYDataItem(0.0, -Double.MAX_VALUE);
        for (int i = samples / 2; i < samples; i++) {
            if (correlatedSignal.getDataItem(i).getYValue() > maxValueSample.getYValue()) {
                maxValueSample = correlatedSignal.getDataItem(i);
            }
        }

        int midSampleIndex = samples / 2;
        int maxValueSampleIndex = (int)maxValueSample.getXValue();
        int diff = maxValueSampleIndex - midSampleIndex;
        double samplingPeriod = 1.0 / samplingFrequency;
        double diffTime = diff * samplingPeriod;
        return signalVelocity * diffTime / 2;
    }

    static double sgn(double x) {
        if (x == 0) return 0;
        return Math.abs(x) / x;
    }

    static double square(double x, double period) {
        return sgn(Math.sin(x / period));
    }


}
