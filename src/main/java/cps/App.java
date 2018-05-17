package cps;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import javax.swing.*;
import java.awt.*;

public class App 
{
    public static void main( String[] args )
    {
        final double objectVelocity = Double.parseDouble(JOptionPane.showInputDialog("Object velocity: "));
        final double signalVelocity = Double.parseDouble(JOptionPane.showInputDialog("Signal velocity: "));
        final double signalPeriod = Double.parseDouble(JOptionPane.showInputDialog("Signal period: "));
        final int samplingFrequency = Integer.parseInt(JOptionPane.showInputDialog("Sampling frequency: "));
        final double notificationPeriod = Double.parseDouble(JOptionPane.showInputDialog("Notification period: "));


        XYSeries sentSignal = Signal.signal(signalPeriod, 0.0, samplingFrequency);

        JFrame frame = new JFrame();
        ChartPanel sentSignalChart = new ChartPanel(ChartFactory.createXYLineChart("sent signal", "", "",
                new XYSeriesCollection(sentSignal), PlotOrientation.VERTICAL, false, false, false));
        ChartPanel receivedSignalChart = new ChartPanel(null);
        ChartPanel correlatedSignalChart = new ChartPanel(null);
        JLabel distance = new JLabel("Distance [Real | Estimate]: ");
        frame.setLayout(new GridLayout(0, 1));
        frame.add(sentSignalChart);
        frame.add(receivedSignalChart);
        frame.add(correlatedSignalChart);
        frame.add(distance);
        frame.setMinimumSize(new Dimension(800, 600));
        frame.setVisible(true);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        double time = 0.0;
        double realObjectDistance = 0.0;
        double estimateObjectDistance = 0.0;

        long currTime = System.currentTimeMillis();

        while (true) {
            if (System.currentTimeMillis() - currTime > notificationPeriod * 1000) {
                time += notificationPeriod;
                realObjectDistance = objectVelocity * time;
                XYSeries receivedSignal = Signal.signal(signalPeriod, 2 * realObjectDistance / signalVelocity, samplingFrequency);
                XYSeries correlatedSignal = Signal.correlate(sentSignal, receivedSignal);

                JFreeChart chart2 = ChartFactory.createXYLineChart("received signal", "", "",
                        new XYSeriesCollection(receivedSignal), PlotOrientation.VERTICAL, false, false, false);
                JFreeChart chart3 = ChartFactory.createXYLineChart("correlation", "", "",
                        new XYSeriesCollection(correlatedSignal), PlotOrientation.VERTICAL, false, false, false);

                receivedSignalChart.setChart(chart2);
                correlatedSignalChart.setChart(chart3);


                estimateObjectDistance = Signal.estimateDistance(correlatedSignal, samplingFrequency, signalVelocity);
                distance.setText("Distance [Real | Estimate]: " + realObjectDistance + " | " + estimateObjectDistance);
                currTime = System.currentTimeMillis();
            }
        }
    }
}
