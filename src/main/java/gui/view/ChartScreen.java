package gui.view;

import common.Job;
import common.TaskJob;
import gui.model.ChartModel;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;

import java.util.ArrayList;
import java.util.List;

public class ChartScreen {

    GraphicsContext _gc;
    double _canvasHeight;
    double _canvasWidth;
    int _barColorIndex = 0;

    double _canvasBuffer = 100;

    public ChartScreen(GraphicsContext graphicsContext){
        _gc = graphicsContext;
        _canvasHeight = _gc.getCanvas().getHeight();
        _canvasWidth = _gc.getCanvas().getWidth();
    }

    public void drawChart(ChartModel model){


        double YAxisSize = _canvasHeight - _canvasBuffer*2;
        double XAxisSize = _canvasWidth - _canvasBuffer*2;

        double YBarSize = YAxisSize/model.getProcessorNumber()*0.8;
        double YBarBufferSize = YAxisSize/model.getProcessorNumber()*0.1;

        _gc.clearRect(0, 0, _canvasWidth, _canvasHeight);

        // grey background
        _gc.setFill(Color.color(0.2, 0.2, 0.2, 0.4));
        _gc.fillRect(0, 0 ,_canvasWidth, _canvasHeight);

        _gc.setFont(new Font("Arial", 18));
        _gc.setStroke(Color.BLACK);

        List<List<Job>> jobList = model.getJobList();

        for( int i = 0; i < model.getProcessorNumber(); i++){

            for( int j = 0; j < jobList.get(i).size() ; j++) {

                if (jobList.get(i).get(j).getClass().getTypeName().equals("common.TaskJob")) {

                    double widthX = ((double)jobList.get(i).get(j).getDuration() /  model.getMaximumTime()) * XAxisSize;
                    double xPos = _canvasBuffer + (getStartDuration(jobList.get(i).get(j), i, jobList) * XAxisSize / model.getMaximumTime());
                    double yPos = _canvasBuffer + i * YAxisSize / model.getProcessorNumber() + YBarBufferSize;

                    _gc.setFill(getColorList().get(_barColorIndex % getColorList().size()));
                    _barColorIndex++;

                    _gc.fillRect(xPos, yPos, widthX, YBarSize);
                    _gc.strokeRect(xPos, yPos, widthX, YBarSize);

                    _gc.setFill(Color.BLACK);
                    _gc.setTextAlign(TextAlignment.CENTER);
                    _gc.fillText(
                            ((TaskJob)jobList.get(i).get(j)).getName(),
                            xPos + widthX * 0.5,
                            yPos + YBarSize * 0.5
                    );
                }
            }
        }

        //draw axes
        _gc.setLineWidth(3);
        _gc.strokeLine(100, _canvasHeight - _canvasBuffer, _canvasWidth - _canvasBuffer, _canvasHeight - _canvasBuffer);
        _gc.strokeLine(100, _canvasHeight - _canvasBuffer, _canvasBuffer, _canvasBuffer);

        double tickMagnitude = Math.round(Math.log10(model.getMaximumTime()) - 1);
        if(tickMagnitude <= 0){
            tickMagnitude = 1;
        }

        int tickTime = (int)Math.pow(10, tickMagnitude)/2;
        double xTickSize = tickTime/model.getMaximumTime() * XAxisSize;
        double yTickSize = YAxisSize/model.getProcessorNumber();

        _gc.setFont(new Font("Arial", 14));

        for(int i = 1; i <= model.getMaximumTime()/tickTime; i++){
            _gc.strokeLine(_canvasBuffer + i*xTickSize, _canvasHeight - _canvasBuffer + 8, _canvasBuffer + i*xTickSize, _canvasHeight -_canvasBuffer - 8);
            _gc.fillText(Integer.toString(tickTime*i), _canvasBuffer + i*xTickSize, _canvasHeight - _canvasBuffer + 8 + 20);
        }

        for(int i = 0; i < model.getProcessorNumber(); i++){
            _gc.strokeLine(_canvasBuffer - 8, _canvasBuffer + (i+0.5)*yTickSize, _canvasBuffer + 8, _canvasBuffer + (i+0.5)*yTickSize);
            _gc.fillText("Processor " + (i + 1), _canvasBuffer - 8 - 40, _canvasBuffer + (i+0.5)*yTickSize + 4);
        }

        //draw title
        _gc.setFont(new Font("Arial", 24));
        _gc.fillText("Best Found Schedule", _canvasWidth/2, _canvasBuffer - _canvasBuffer/2);


    }

    private double getStartDuration(Job job, int processorNum, List<List<Job>> jobList){

        int startDuration = 0;

        for( int i = 0; i < jobList.get(processorNum).indexOf(job); i++){
            startDuration += jobList.get(processorNum).get(i).getDuration();
        }

        return startDuration;

    }

    private List<Color> getColorList(){

        List<Color> list = new ArrayList<Color>();


        for(int i = 0; i < 20; i++){
            double r = 50;
            double g = 50;
            double b = 50;

            r += Math.random() * 100;
            g += Math.random() * 100;
            b += Math.random() * 100;

            list.add(Color.color(r/255, g/255 ,b/255));
        }

        return list;
    }
}

















