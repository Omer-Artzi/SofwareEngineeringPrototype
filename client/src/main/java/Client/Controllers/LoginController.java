package Client.Controllers;

import Client.Events.UserMessageEvent;
import Client.SimpleChatClient;
import Client.SimpleClient;
import Entities.Communication.Message;
import javafx.animation.Interpolator;
import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.util.Duration;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class LoginController {
    @FXML
    private Button loginButton;

    @FXML
    private ImageView lockImageView;

    @FXML
    private PasswordField passwordTF;

    @FXML
    private TextField usernameTF;

    @FXML
    private Label wrongLoginLabel;
    private static TranslateTransition translateTransition;

    @FXML
    public void login(ActionEvent event) throws IOException, InterruptedException {
        checkLogin();
    }

    private void checkLogin() throws IOException, InterruptedException {
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
            // after we will connect this part to the database we will check if the User is a teacher/student/principal, and accordingly we will open the right Client.Main Screen
        }
        else if (usernameTF.getText().isEmpty() && passwordTF.getText().isEmpty()){
            wiggle();
            wrongLoginLabel.setText("Please enter your data");
        }
        else {
            wiggle();
            wrongLoginLabel.setText("Wrong username or password");
        }
    }

    @FXML
    void initialize() {
        EventBus.getDefault().register(this);

        System.out.println("Client.Controllers.LoginController registered");
        assert loginButton != null : "fx:id=\"loginButton\" was not injected: check your FXML file 'login.fxml'.";
        assert passwordTF != null : "fx:id=\"passwordTF\" was not injected: check your FXML file 'login.fxml'.";
        assert usernameTF != null : "fx:id=\"usernameTF\" was not injected: check your FXML file 'login.fxml'.";
        assert wrongLoginLabel != null : "fx:id=\"wrongLoginLabel\" was not injected: check your FXML file 'login.fxml'.";
        wrongLoginLabel.setAlignment(Pos.CENTER);


    }
    @Subscribe
    public void logIn(UserMessageEvent event) throws IOException {
        System.out.println("Logging in");
        if(Objects.equals(event.getStatus(), "Success"))
        {
            SimpleClient.getClient().setUser(event.getUser());
            //Client.SimpleChatClient.setRoot("TeacherMainScreen");
            EventBus.getDefault().unregister(this);
            SimpleChatClient.NewSetRoot("MainWindow");
        }
        else {
            Platform.runLater(() -> {
                try {
                    wiggle();
                    wrongLoginLabel.setText(event.getStatus());
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            });

        }
    }
    private void wiggle() throws InterruptedException {
        translateTransition = new TranslateTransition(Duration.millis(75), lockImageView);
            //translateTransition configuration
            translateTransition.setByX(10);
            translateTransition.setInterpolator(Interpolator.LINEAR);
            translateTransition.setAutoReverse(true);
            translateTransition.setCycleCount(4);
            // Start the RotateTransition
            translateTransition.play();

    }

}