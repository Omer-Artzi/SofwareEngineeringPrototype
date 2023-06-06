import javafx.fxml.Initializable;
import javafx.scene.shape.Circle;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;

import java.net.URL;
import java.util.ResourceBundle;


public class ProgressCircleController implements Initializable {
    public Circle circle;
    public Label questionNumber;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }

    public void setNumber(Integer number) {
        this.questionNumber.setText(number.toString());
    }

    public void setDefaultColor(){
        circle.setFill(Color.web("#DAE0E2"));
        questionNumber.setTextFill(Color.valueOf("black"));
    }

    public void setCurrentQuestionColor(){
        circle.setFill(Color.web("#0ABDE3"));
        questionNumber.setTextFill(Color.valueOf("white"));
    }

    public void setWrongAnswerColor(){
        circle.setFill(Color.web("#EC4849"));
        questionNumber.setTextFill(Color.valueOf("white"));
    }


    public void setRightAnswerColor(){
        circle.setFill(Color.web("#75DA8B"));
        questionNumber.setTextFill(Color.valueOf("white"));
    }
}
