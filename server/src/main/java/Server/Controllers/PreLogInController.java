package Server.Controllers;

import Server.Events.ServerConnectionEvent;
import Server.SimpleServer;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import org.greenrobot.eventbus.EventBus;

import javax.swing.*;
import java.util.HashMap;
import java.util.Map;

public class PreLogInController {

    @FXML
    private Button EnterButton;

    @FXML
    private TextField PortTF;

    @FXML
    private ComboBox<String> cfgCB;

    @FXML
    private Label loadingLabel;

    @FXML
    private TextField passwordTF;

    @FXML
    private ComboBox<String> showSQLCB;

    @FXML
    private TextField usernameTF;

    @FXML
    private TextField schemaTF;



    @FXML
    void initialize() {
        //EventBus.getDefault().register(this);
        assert EnterButton != null : "fx:id=\"EnterButton\" was not injected: check your FXML file 'PreLogIn.fxml'.";
        assert PortTF != null : "fx:id=\"PortTF\" was not injected: check your FXML file 'PreLogIn.fxml'.";
        showSQLCB.getItems().addAll("false","true");
        showSQLCB.getSelectionModel().selectFirst();
        cfgCB.getItems().addAll("create-drop","update","create","create-only","drop","validate","none");
        cfgCB.getSelectionModel().selectFirst();
        usernameTF.setPromptText("According to hibernate.properties");
        passwordTF.setPromptText("According to hibernate.properties");
    }
    @FXML
    public void onEnter() throws InterruptedException {
        Map<String,String> properties = new HashMap<>();
        int port = 3000;
        if(PortTF.getText() != null && !PortTF.getText().isBlank())
        {
            try {
                 port = Integer.parseInt(PortTF.getText());
                        if(port >= 0 && port<= 65535) {

                            if (usernameTF.getText() != null && !usernameTF.getText().isBlank()) {
                                properties.put("hibernate.connection.username", usernameTF.getText());
                            }
                            if (passwordTF.getText() != null && !passwordTF.getText().isBlank()) {
                                properties.put("hibernate.connection.password", passwordTF.getText());
                            }
                            if (schemaTF.getText() != null && !schemaTF.getText().isBlank()) {
                                properties.put("hibernate.connection.url", schemaTF.getText());
                            }
                            properties.put("hibernate.hbm2ddl.auto", cfgCB.getSelectionModel().getSelectedItem());
                            properties.put("hibernate.show_sql", showSQLCB.getSelectionModel().getSelectedItem());
                            System.out.println("Properties: " + properties);
                            SimpleServer.getSessionFactory(properties);
                            Platform.runLater(() -> {
                                loadingLabel.setText("Loading...");
                                loadingLabel.setVisible(true);
                            });
                            System.out.println("Setting up server at " + port);
                            ServerConnectionEvent event = new ServerConnectionEvent(port);
                            EventBus.getDefault().post(event);
                        }
                        else
                        {
                            PortTF.setText("Port can only be 0-65,535");
                        }
            }
            catch (NumberFormatException e)
            {
                JOptionPane.showMessageDialog(null,"Invalid input","Input Error",JOptionPane.WARNING_MESSAGE);
            }

        }
        else {
            Platform.runLater(() -> loadingLabel.setText("Loading..."));
            if (usernameTF.getText() != null && !usernameTF.getText().isBlank()) {
                properties.put("hibernate.connection.username", usernameTF.getText());
            }
            if (passwordTF.getText() != null && !passwordTF.getText().isBlank()) {
                properties.put("hibernate.connection.password", passwordTF.getText());
            }
            properties.put("hibernate.hbm2ddl.auto", cfgCB.getSelectionModel().getSelectedItem());
            properties.put("hibernate.show_sql", showSQLCB.getSelectionModel().getSelectedItem());
            System.out.println("Properties: " + properties);
            SimpleServer.getSessionFactory(properties);
            Platform.runLater(() -> {
                loadingLabel.setText("Loading...");
                loadingLabel.setVisible(true);
            });
            System.out.println("Setting up server at " + port);
            ServerConnectionEvent event = new ServerConnectionEvent(port);
            EventBus.getDefault().post(event);
        }

    }
}
