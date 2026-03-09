import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

public class CalculatorController {

    @FXML private TextField number1Field;
    @FXML private TextField number2Field;
    @FXML private Label resultLabel;

    @FXML
    private void onCalculateClick() {
        try {
            double num1 = Double.parseDouble(number1Field.getText());
            double num2 = Double.parseDouble(number2Field.getText());

            double sum = num1 + num2;
            double product = num1 * num2;
            double subtract = num1 - num2;
            double division = 0;
            if (num2 != 0) {
                division = num1 / num2;
            } else {
                resultLabel.setText("Cannot divide by zero!");
                return;
            }

            division = Math.round(division * 100.0) / 100.0;

            resultLabel.setText("Sum: " + sum + ", Product: " + product + ", Subtract: " + subtract + ", Division: " + division);

            // Save to DB
            ResultService.saveResult(num1, num2, sum, product, subtract, division);

        } catch (NumberFormatException e) {
            resultLabel.setText("Please enter valid numbers!");
        }
    }
}