import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Timer;
import java.util.TimerTask;

import Entities.ClassExam;
import Entities.Message;
import Events.CreateExamEvent;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.DragEvent;
import javafx.scene.input.MouseDragEvent;
import org.greenrobot.eventbus.Subscribe;

import javax.swing.*;

public class DoExamManualController {

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private ImageView dragAndDropImg;

    @FXML
    private Label timeLeftLabel;
    @FXML
    private Label fileRecievedLabel;
    private ClassExam classExam;

    private int timeInSeconds;
     private ClassExam mainClassExam;

    @FXML
    void fileDropped(DragEvent event){
            System.out.println("File Dropped");
            ColorAdjust colorAdjust = new ColorAdjust();
            colorAdjust.setBrightness(0);
            dragAndDropImg.setEffect(colorAdjust);
            dragAndDropImg.setImage(new Image("/Images/accept.png"));
            fileRecievedLabel.setText("File Uploaded!");
            List<File> files = event.getDragboard().getFiles();
            System.out.println("File gotten: " + files.get(0));
    }
    @FXML
    void fileDetected(DragEvent event) {
        try {
            ColorAdjust colorAdjust = new ColorAdjust();
            colorAdjust.setBrightness(0.5);
            dragAndDropImg.setEffect(colorAdjust);
            System.out.println("Changing Brightness");
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
    @FXML
    void fileExited(DragEvent event) {
        try {
            ColorAdjust colorAdjust = new ColorAdjust();
            colorAdjust.setBrightness(0);
            dragAndDropImg.setEffect(colorAdjust);
            System.out.println("Changing Brightness");
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    @FXML
    void initialize() {
        timeInSeconds = mainClassExam.getTime();
        assert dragAndDropImg != null : "fx:id=\"dragAndDropImg\" was not injected: check your FXML file 'DoExamManual.fxml'.";
        assert timeLeftLabel != null : "fx:id=\"timeLeftLabel\" was not injected: check your FXML file 'DoExamManual.fxml'.";
        fileRecievedLabel.setAlignment(Pos.CENTER);
        fileRecievedLabel.setText("Drop File Here");
        timeLeftLabel.setAlignment(Pos.CENTER);
        ViewExamController.createManualExam(classExam.getExamForm());
        Timer timer = new Timer();
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                if(timeInSeconds>0) {
                    timeInSeconds--;
                    Platform.runLater(() ->{timeLeftLabel.setText("Time Left: " + String.format("%02d",timeInSeconds/60)+ ":" + String.format("%02d",timeInSeconds%60));});

                }
                else {
                    try {
                        //SimpleClient.getClient().sendToServer(new Message(1,"Exam Fail: Time Ended"));
                        timer.cancel();
                        SimpleChatClient.setScene("ChooseExam",1024,768);
                        JOptionPane.showMessageDialog(null,"Exam time ended and no exam was submitted", "Submission Exam", JOptionPane.WARNING_MESSAGE);

                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }

            }
            };
        timer.schedule(task,0, 1000);
        }
        @Subscribe
    void createManualExam(CreateExamEvent event)
        {

        }
    }


