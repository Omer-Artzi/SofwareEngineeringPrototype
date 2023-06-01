
import Entities.Message;
import Events.UserMessageEvent;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class LoginController {
    @FXML
    private Button loginButton;

    @FXML
    private PasswordField passwordTF;

    @FXML
    private TextField usernameTF;

    @FXML
    private Label wrongLoginLabel;

    // This method is called when the user clicks on the login button.
    // It sends a NewLoginMessage to the server with the username and password entered by the user.

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
        String username = usernameTF.getText();
        String password = passwordTF.getText();
        if(!username.isEmpty() && !password.isEmpty() &&!username.isBlank() && !password.isBlank()) {
            Message credentials = new Message(1, "Login");
            List<String> user = new ArrayList<>();
            user.add(username);
            user.add(password);
            credentials.setData(user);
            SimpleClient.getClient().sendToServer(credentials);
            System.out.println("Login message sent");
            // after we will connect this part to the database we will check if the User is a teacher/student/principal, and accordingly we will open the right Main Screen
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
        EventBus.getDefault().register(this);
        assert loginButton != null : "fx:id=\"loginButton\" was not injected: check your FXML file 'login.fxml'.";
        assert passwordTF != null : "fx:id=\"passwordTF\" was not injected: check your FXML file 'login.fxml'.";
        assert usernameTF != null : "fx:id=\"usernameTF\" was not injected: check your FXML file 'login.fxml'.";
        assert wrongLoginLabel != null : "fx:id=\"wrongLoginLabel\" was not injected: check your FXML file 'login.fxml'.";

    }
    @Subscribe
    public void logIn(UserMessageEvent event) throws IOException {
        System.out.println("Logging in");
        if(event.getStatus() == "Success")
        {
            SimpleClient.getClient().setUser(event.getUser());
            //SimpleChatClient.setRoot("TeacherMainScreen");
            SimpleChatClient.NewSetRoot("MainWindow");
        }
        else {
            wrongLoginLabel.setText("E-mail address or password is wrong");
        }
    }

}