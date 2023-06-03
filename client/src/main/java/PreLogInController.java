import java.io.IOException;
import java.time.format.DateTimeFormatter;


import Events.ErrorEvent;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import javax.swing.*;


public class PreLogInController {
	@FXML
	private int msgId;
	@FXML
	private TextField IPTF;
	@FXML
	private TextField PortTF;
	@FXML
	private Button enterButton;




	@Subscribe
	public void errorEvent(ErrorEvent event){
		DateTimeFormatter dtf = DateTimeFormatter.ofPattern("HH:mm:ss");
		Platform.runLater(() -> {
			Alert alert = new Alert(Alert.AlertType.ERROR,
					String.format("Message:\nId: %d\nData: %s\nTimestamp: %s\n",
							event.getMessage().getId(),
							event.getMessage().getMessage(),
							event.getMessage().getTimeStamp().format(dtf))
			);
			alert.setTitle("Error!");
			alert.setHeaderText("Error:");
			alert.show();
		});
	}

	@FXML
	void initialize() {
		EventBus.getDefault().register(this);

	}
	@FXML
	public void onEnter() throws IOException {
		String IPText = IPTF.getText();
		try {
			System.out.println("Connecting to " + IPText +":"+ PortTF.getText());
			if (IPText != null && PortTF.getText() != null) {
				int portNum = Integer.parseInt(PortTF.getText());
				SimpleClient.setPortNum(portNum);
				SimpleClient.setIP(IPText);
			} else {
				JOptionPane.showMessageDialog(null, "Credentials not entered, trying to connect to localhost:3000");
				SimpleClient.setIP("localhost");
				SimpleClient.setPortNum(3000);
			}
		}
		catch (Exception e)
		{
			System.out.println("Invalid input, connecting to localhost:3000");
			SimpleClient.setIP("localhost");
			SimpleClient.setPortNum(3000);
		}
		SimpleClient.getClient().openClientConnect();

	}







}
