import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Timer;
import java.util.TimerTask;

import Entities.*;
import Events.CreateExamEvent;
import Events.ManualExamEvent;
import Events.StartExamEvent;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.DragEvent;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import javax.swing.*;

public class StudentDoExamManualController {

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


    private int timeInSeconds;
     private ClassExam mainClassExam;
     private StudentExam studentExam = new StudentExam();

    @FXML
    void fileDropped(DragEvent event){

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
        File file = null;
        try {
            System.out.println("File Dropped");
            ColorAdjust colorAdjust = new ColorAdjust();
            colorAdjust.setBrightness(0);
            dragAndDropImg.setEffect(colorAdjust);
            dragAndDropImg.setImage(new Image("/Images/accept.png"));

            file = event.getDragboard().getFiles().get(0);
            try
            {
                FileInputStream fis = new FileInputStream(file);
                 DocumentWrapper document = new DocumentWrapper(fis);
                 System.out.println(document.getDocument().getBody());
                ManualStudentExam manualStudentExam = new ManualStudentExam(studentExam, document);
                Message message = new Message(1, "Manual Exam for student ID: " + SimpleClient.getUser().getID());
                message.setData(manualStudentExam);
                SimpleClient.getClient().sendToServer(message);
                fileRecievedLabel.setText("File Uploaded!");
            } catch (IOException e) {
                e.printStackTrace();
            }
            System.out.println("File gotten: " + file);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

    }

    @FXML
    void initialize() {
        EventBus.getDefault().register(this);

        assert dragAndDropImg != null : "fx:id=\"dragAndDropImg\" was not injected: check your FXML file 'StudentDoExamManual.fxml'.";
        assert timeLeftLabel != null : "fx:id=\"timeLeftLabel\" was not injected: check your FXML file 'StudentDoExamManual.fxml'.";
        fileRecievedLabel.setAlignment(Pos.CENTER);
        fileRecievedLabel.setText("Drop File Here");
        timeLeftLabel.setAlignment(Pos.CENTER);


        }
        @Subscribe
       public  void getExam(StartExamEvent event)
        {
            mainClassExam = event.getClassExam();
            timeInSeconds = (int)(mainClassExam.getExamTime()) * 60;
            ViewExamController.createManualExam(mainClassExam);
            studentExam.setStudent(((Student)(SimpleClient.getUser())));
            studentExam.setClassExam(mainClassExam);
            studentExam.setStatus(HSTS_Enums.StatusEnum.ToEvaluate);
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
                            SimpleClient.getClient().sendToServer(new Message(1,"Exam Fail: Time Ended"));
                            timer.cancel();
                            SimpleChatClient.setRoot("ChooseExam");
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
    public void endExam(ManualExamEvent event) throws IOException {
            JOptionPane.showMessageDialog(null,"Exam was successfully saved", "Success", JOptionPane.INFORMATION_MESSAGE);
            SimpleChatClient.getMainWindowController().LoadSceneToMainWindow("ChooseExam");
        }
    }


