/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lineelementsforgraph;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.geometry.Point2D;

/**
 *
 * @author nicke
 */
public class GraphWriter {
    
    public String file;
    
    private List<String> lines;
    
    public GraphWriter(String fileName) {
        file = fileName;
        lines = readGraphFile();
    }
    
    public List<String> readGraphFile() {
        List<String> l = Collections.EMPTY_LIST;
        if(!new File(file).exists())
            return l;
        try {
            l = Files.readAllLines(new File(file).toPath());
        } catch (IOException ex) {
            Logger.getLogger(GraphWriter.class.getName()).log(Level.SEVERE, null, ex);
        }
        return l;
    }
    
    public boolean writeGraphFile() {
        return writeGraphFile(lines);
    }
    
    public boolean writeGraphFile(List<String> l) {
        boolean successful;
        try {
            if(new File(file).exists())
                Files.write(new File(file).toPath(), l, StandardOpenOption.TRUNCATE_EXISTING);
            else
                Files.write(new File(file).toPath(), l, StandardOpenOption.CREATE);
            successful = true;
        } catch (IOException ex) {
            Logger.getLogger(GraphWriter.class.getName()).log(Level.SEVERE, null, ex);
            successful = false;
        }
        return successful;
    }
    
    public void emptyGraphFile() {
        String s = ";This file was created by Graph (http://www.padowan.dk)\n" +
                    ";Do not change this file from other programs.\n" +
                    "[Graph]\n" +
                    "Version = 4.4.2.543\n" +
                    "MinVersion = 2.5\n" +
                    "OS = Windows XP 5.1 Service Pack 3\n" +
                    "\n" +
                    "[Axes]\n" +
                    "xMin = -10\n" +
                    "xMax = 10\n" +
                    "xTickUnit = 1\n" +
                    "xGridUnit = 2\n" +
                    "yMin = -10\n" +
                    "yMax = 10\n" +
                    "yTickUnit = 1\n" +
                    "yGridUnit = 2\n" +
                    "AxesColor = clBlue\n" +
                    "GridColor = 0x00FF9999\n" +
                    "ShowLegend = 0\n" +
                    "Radian = 1\n" +
                    "\n" +
                    "[PointSeries1]\n" +
                    "FillColor = clRed\n" +
                    "LineColor = clBlue\n" +
                    "Size = 2\n" +
                    "Style = 0\n" +
                    "LabelPosition = 1\n" +
                    "PointCount = 1\n" +
                    "Points = 0,0;\n" +
                    "LegendText = Series 1\n" +
                    "\n" +
                    "[Data]\n" +
                    "TextLabelCount = 0\n" +
                    "FuncCount = 0\n" +
                    "PointSeriesCount = 1\n" +
                    "ShadeCount = 0\n" +
                    "RelationCount = 0\n" +
                    "OleObjectCount = 0\n" +
                    "";
        lines = new ArrayList<>();
        lines.addAll(Arrays.asList(s.split("\n")));
    }
    
    public boolean appendNewFunctionToGraph(String func, double from, double to) {
        return appendNewFunctionToGraph(func, from, to, "clBlue");
    }
    
    public boolean appendNewFunctionToGraph(String func, double from, double to, String color) {
        //List<String> lines = readGraphFile();
        
        int index;
        int funcCount = 1;
        for(index = 0; index < lines.size() && !lines.get(index).startsWith("[PointSeries") ; index++) {
            if(lines.get(index).startsWith("[Func"))
                funcCount++;
        }
        
        String toInsert = "[Func" + funcCount + "]\n"
                + "FuncType = 0\n"
                + "y = " + func + "\n"
                + "From = " + from + "\n"
                + "To = " + to + "\n"
                + "Color = " + color;
        for(String s : toInsert.split("\n"))
            lines.add(index++, s);
        lines.add(index, "");
        
        //return writeGraphFile(lines);
        return true;
    }
    
    @SuppressWarnings("empty-statement")
    public Point2D cornerCoordinates(boolean upper) {
        double x = 0;
        double y = 0;
        
        int index;
        for(index = 0; index < lines.size() && !lines.get(index).startsWith((upper ? "xMax = " : "xMin = ")) ; index++);
        String[] split = lines.get(index).split(" ");
        for(String s : split) {
            try {
                double d = Double.parseDouble(s);
                x = d;
            } catch(NumberFormatException nfe) {}
        }
        for(index = 0; index < lines.size() && !lines.get(index).startsWith((upper ? "yMax = " : "yMin = ")) ; index++);
        split = lines.get(index).split(" ");
        for(String s : split) {
            try {
                double d = Double.parseDouble(s);
                y = d;
            } catch(NumberFormatException nfe) {}
        }
        
        return new Point2D(x, y);
    }
    
    public void removeAllGeneratedFunctions() {
        boolean sameFunction = false;
        for(int i = 0; i < lines.size(); i++) {
            String line = lines.get(i);
            //if(line.equals(";auto-generated"))
            if(line.startsWith("[Func"))
                sameFunction = true;
            if(sameFunction)
                lines.remove(i--);
            if(line.trim().isEmpty())
                sameFunction = false;
            if(line.startsWith("Points =")) {
                lines.remove(i);
                lines.add(i, "Points = ");
            }
        }
    }
    
    @SuppressWarnings("empty-statement")
    public boolean appendNewPointSeriesToGraph(Point2D... points) {
        //List<String> lines = readGraphFile();
        
        int index;
        for(index = 0; index < lines.size() && !lines.get(index).startsWith("Points =") ; index++);
        
        if(index < lines.size()) {
            String pointsLine = lines.get(index);
            for(Point2D p : points)
                pointsLine += p.getX() + "," + p.getY() + ";";
            lines.set(index, pointsLine);
        } else {
            for(index = 0; index < lines.size() && !lines.get(index).startsWith("[Data") ; index++);
            String toInsert = "[PointSeries1]\n"
                    + "FillColor = clRed\n"
                    + "LineColor = clBlue\n"
                    + "Size = 3\n"
                    + "Style = 0\n"
                    + "LabelPosition = 1\n"
                    + "PointCount = 0\n"
                    + "Points = \n"
                    + "LegendText = Series 1";
            for(String s : toInsert.split("\n"))
                lines.add(index++, s);
            lines.add(index, "");
        }
        
        //return writeGraphFile(lines);
        return true;
    }
    
    @SuppressWarnings("empty-statement")
    public void setPointSize(int size) {
        int index;
        for(index = 0; index < lines.size() && !lines.get(index).startsWith("Size =") ; index++);
        lines.set(index, "Size = " + size);
        System.out.println("PointSize = " + size);
    }
    
}
