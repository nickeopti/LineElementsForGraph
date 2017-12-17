/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lineelementsforgraph;

import java.io.File;
import javafx.geometry.Point2D;
import net.objecthunter.exp4j.Expression;
import net.objecthunter.exp4j.ExpressionBuilder;

/**
 *
 * @author nicke
 */
public class Calculations {
    
    public String x_var;
    public String y_var;
    public String function;
    public double length = 2;
    
    public String file;
    private GraphWriter graph;
    
    public Calculations(String file, String x, String y) {
        this(file, x, y, "");
    }
    
    public Calculations(String file, String x, String y, String func) {
        this.x_var = x;
        this.y_var = y;
        function = func;
        this.file = file;
        graph = new GraphWriter(this.file);
    }
    
    public void addTangentLine(double x_0, double y_0) {
        double a;
        try {
        Expression ex = new ExpressionBuilder(function).variables(x_var, y_var).build().setVariable(x_var, x_0).setVariable(y_var, y_0);
        a = ex.evaluate();
        } catch(ArithmeticException ae) {return;}
        
        double d = Math.abs(Math.sqrt(a*a+1) * (length / (a*a + 1)));
        double from = x_0 - d/2;
        double to   = x_0 + d/2;
        
        graph.appendNewFunctionToGraph("a*(x-x_0)+y_0".replace("y_0", ""+y_0).replace("x_0", ""+x_0).replace("a", ""+a), from, to);
        graph.appendNewPointSeriesToGraph(new Point2D(x_0, y_0));
        //graph.writeGraphFile();
    }
    
    public void addLineElements(int x_num, int y_num, int pointSize) {
        if(new File(file).exists())
            graph.readGraphFile();
        else
            graph.emptyGraphFile();
        graph.setPointSize(pointSize);
        addLineElements(x_num, y_num, graph.cornerCoordinates(false), graph.cornerCoordinates(true));
        
        System.out.println(graph.cornerCoordinates(false));
        System.out.println(graph.cornerCoordinates(true));
    }
    
    public void addLineElements(int x_num, int y_num, Point2D lowerLeft, Point2D upperRight) {
        //graph.readGraphFile();
        graph.removeAllGeneratedFunctions();
        
        if(upperRight.getX() <= lowerLeft.getX() || upperRight.getY() <= lowerLeft.getY())
            return;
        for(int x = 0; x < x_num; x++) {
            for(int y = 0; y < y_num; y++) {
                addTangentLine((upperRight.getX()-lowerLeft.getX())/(x_num-1)*x+lowerLeft.getX(), (upperRight.getY()-lowerLeft.getY())/(y_num-1)*y+lowerLeft.getY());
            }
        }
        graph.writeGraphFile();
    }
    
    public void setPointSize(int size) {
        graph.setPointSize(size);
    }
    
    private String transformToEnglish(String eq) {
        return eq.replace(',', '.');
    }
    
    private String transformFromEnglish(String eq) {
        return eq.replace('.', ',');
    }
    
}
