package lineelementsforgraph;

import com.proudapes.jlatexmathfx.Control.LateXMathControl;
import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.control.TextField;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

/**
 * @author Nicklas Boserup
 */
public class LineElementsForGraph extends Application {
    
    File graphFile;
    Calculations calcs;
    
    @Override
    public void start(Stage primaryStage) {
        GridPane root = new GridPane();
        root.setPadding(new Insets(5, 10, 10, 10));
        root.setVgap(5);
        root.setStyle("-fx-base: rgb(50,50,50);");
        int rowIndex = 0;
        
        ColumnConstraints cc0 = new ColumnConstraints();
        cc0.setHgrow(Priority.SOMETIMES);
        ColumnConstraints cc1 = new ColumnConstraints();
        cc1.setHgrow(Priority.ALWAYS);
        ColumnConstraints cc2 = new ColumnConstraints();
        cc2.setHgrow(Priority.SOMETIMES);
        root.getColumnConstraints().addAll(cc0, cc1, cc2);
        
        root.add(largeLabel("Linjeelementer i Graph", 7), 0, rowIndex++, 3, 1);
        root.add(new Separator(), 0, rowIndex++, 3, 1);
        
        Button saveFile = new Button("Gem linjeelementerne som...");
        saveFile.setOnAction((e) -> {
            FileChooser saveDialog = new FileChooser();
            FileChooser.ExtensionFilter ext = new FileChooser.ExtensionFilter("Graph fil (.grf)", "*.grf");
            saveDialog.getExtensionFilters().add(ext);
            saveDialog.setTitle("Gem linjeelementerne som");
            File f = saveDialog.showSaveDialog(primaryStage);
            System.out.println(f);
            if(f != null) {
                if(!f.getAbsolutePath().endsWith(".grf"))
                    f = new File(f.getAbsolutePath() + ".grf");
                graphFile = f;
            }
        });
        root.add(saveFile, 0, rowIndex++, 3, 1);
        
        root.add(new Separator(), 0, rowIndex++, 3, 1);
        root.add(largeLabel("Funktion", 3), 0, rowIndex++, 3, 1);
        TextField function = new TextField();
        function.setPromptText("differential-ligning...");
        //root.add(new Label("dy/dx = "), 0, rowIndex);
        final LateXMathControl dydx = latexLabel("\\frac{dy}{dx} =");
        root.add(dydx, 0, rowIndex);
        root.add(function, 1, rowIndex++, 2, 1);
        TextField x_var = new TextField("x");
        x_var.setPromptText("x");
        root.add(new Label("x-variabel"), 0, rowIndex);
        root.add(x_var, 1, rowIndex++, 2, 1);
        TextField y_var = new TextField("y");
        y_var.setPromptText("y");
        root.add(new Label("y-variabel"), 0, rowIndex);
        root.add(y_var, 1, rowIndex++, 2, 1);
        
        x_var.textProperty().addListener((ob, o, n) -> dydx.setFormula("\\frac{d"+y_var.getText()+"}{d"+n+"}="));
        y_var.textProperty().addListener((ob, o, n) -> dydx.setFormula("\\frac{d"+n+"}{d"+x_var.getText()+"}="));
        
        root.add(new Separator(), 0, rowIndex++, 3, 1);
        root.add(largeLabel("Antal linjeelementer", 3), 0, rowIndex++, 3, 1);
        NumSetLink x_num = new NumSetLink("Horisontalt", 1, 100, 10, true);
        root.addRow(rowIndex++, x_num.label, x_num.slider, x_num.textField);
        NumSetLink y_num = new NumSetLink("Vertikalt", 1, 100, 10, true);
        root.addRow(rowIndex++, y_num.label, y_num.slider, y_num.textField);
        
        root.add(new Separator(), 0, rowIndex++, 3, 1);
        root.add(largeLabel("Indstillinger for linjeelementer", 3), 0, rowIndex++, 3, 1);
        NumSetLink lineLength = new NumSetLink("Længde", 0.1, 2, 1, false);
        root.addRow(rowIndex++, lineLength.label, lineLength.slider, lineLength.textField);
        NumSetLink pointSize = new NumSetLink("Punktstørrelse", 0, 5, 1, true);
        root.addRow(rowIndex++, pointSize.label, pointSize.slider, pointSize.textField);
        
        root.add(new Separator(), 0, rowIndex++, 3, 1);
        Button generate = new Button("Generér linjeelementer");
        generate.setOnAction(e -> {
            boolean shouldLaunch = !graphFile.exists();
            
            calcs = new Calculations(graphFile.getAbsolutePath(), x_var.getText().trim(), y_var.getText().trim(), function.getText().trim());
            calcs.length = lineLength.getDoubleValue();
            calcs.addLineElements(x_num.getIntValue(), y_num.getIntValue(), pointSize.getIntValue());
            //calcs.setPointSize(pointSize.getIntValue());
            
            if(shouldLaunch) {
                new Thread(() -> {
                    try {
                        Desktop.getDesktop().open(graphFile.getAbsoluteFile());
                    } catch (IOException ex) {
                        Logger.getLogger(LineElementsForGraph.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }).start();
            }
        });
        root.add(generate, 0, rowIndex++, 2, 1);
        
        
        
        Scene scene = new Scene(root);
        
        primaryStage.setTitle("LinjeElementer");
        primaryStage.setScene(scene);
        primaryStage.sizeToScene();
        primaryStage.show();
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        /*Calculations calcs = new Calculations("/home/nicke/Documents/LinjeElementGraph2.grf", "x", "y", "1-0.5*y");
        calcs.length = 1;
        //calcs.addLineElements(50, 50, new Point2D(-10, -10), new Point2D(10, 10));
        
        calcs.addLineElements(50, 50);
        */
        
        launch(args);
    }
    
    private Label largeLabel(String label, double size) {
        Label l = new Label(label);
        l.setFont(new Font(l.getFont().getName(), l.getFont().getSize()+size));
        return l;
    }
    
    private LateXMathControl latexLabel(String latex) {
        LateXMathControl control = new LateXMathControl(latex);
        control.setPrefWidth(0);
        control.setPrefHeight(0);
        control.setTextColor(Color.WHITE);
        return control;
    }
    
}
