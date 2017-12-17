package lineelementsforgraph;

import java.text.DecimalFormat;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;

/**
 * @author Nicklas Boserup
 */
public class NumSetLink {
    
    public Label label;
    public Slider slider;
    public TextField textField;
    
    public NumSetLink(String label, double minValue, double maxValue, double defaultValue, boolean integerValue) {
        this.label = new Label(label);
        slider = new Slider(minValue, maxValue, 0);
        textField = new TextField(""+defaultValue);
        textField.setPrefColumnCount(4);
        textField.setAlignment(Pos.BASELINE_RIGHT);
        textField.setOnAction((e) -> {
            try {
                double val = Double.parseDouble(textField.getText().replace(',', '.'));
                slider.setValue(val);
            } catch(NumberFormatException nfe) {}
        });
        textField.focusedProperty().addListener((l) -> {
            try {
                double val = Double.parseDouble(textField.getText().replace(',', '.'));
                slider.setValue(val);
            } catch(NumberFormatException nfe) {}
        });
        slider.valueProperty().addListener((l) -> {
            textField.setText(new DecimalFormat(integerValue ? "#0" : "#0.00").format(slider.getValue()).replace('.', ','));
        });
        slider.setValue(defaultValue);
    }
    
    public double getDoubleValue() {
        return slider.getValue();
    }
    
    public int getIntValue() {
        return Integer.parseInt(textField.getText());
    }
    
}
