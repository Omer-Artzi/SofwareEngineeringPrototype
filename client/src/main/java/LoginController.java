//import il.cshaifasweng.OCSFMediatorExample.entities.NewLoginMessage;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import java.io.IOException;

public class LoginController {
    @FXML
    private Button loginButton;

    @FXML
    private PasswordField passwordTF;

    @FXML
    private TextField usernameTF;

    @FXML
    private Label wrongLoginLabel;

    /*
     * This method is called when the user clicks on the login button.
     * It sends a NewLoginMessage to the server with the username and password
     * entered by the user.
     * @param event
     */
    /*
    @FXML
    void login(ActionEvent event) {
        String username = usernameTF.getText();
        String password = passwordTF.getText();
        NewLoginMessage logMessage = new NewLoginMessage(1, "Login", username, password);
        try {
            SimpleClient.getClient().sendToServer(logMessage);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
     */

    @FXML
    public void login(ActionEvent event) throws IOException {
        checkLogin();
    }

    private void checkLogin() throws IOException {

        if(usernameTF.getText().toString().equals("user") && passwordTF.getText().toString().equals("1234")) {
            wrongLoginLabel.setText("Login successful");
            // after we will connect this part to the database we will check if the User is a teacher/student/principal, and accordingly we will open the right Main Screen
            // in case that the user is a Teacher:
            SimpleChatClient.setRoot("TeacherMainScreen");

            // in case that the user is a Student:
            //m.changeScene("StudentMainScreen.fxml");

            // in case that the user is a Principal:
            //m.changeScene("PrincipalMainScreen.fxml");
        }
        else if (usernameTF.getText().isEmpty() && passwordTF.getText().isEmpty()){
            wrongLoginLabel.setText("Please enter your data");
        }
        else {
            wrongLoginLabel.setText("Wrong username or password");
        }
    }

    @FXML
    void initialize() {
        assert loginButton != null : "fx:id=\"loginButton\" was not injected: check your FXML file 'login.fxml'.";
        assert passwordTF != null : "fx:id=\"passwordTF\" was not injected: check your FXML file 'login.fxml'.";
        assert usernameTF != null : "fx:id=\"usernameTF\" was not injected: check your FXML file 'login.fxml'.";
        assert wrongLoginLabel != null : "fx:id=\"wrongLoginLabel\" was not injected: check your FXML file 'login.fxml'.";

    }

}