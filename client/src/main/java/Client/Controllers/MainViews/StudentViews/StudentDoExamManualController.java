package Client.Controllers.MainViews.StudentViews;

import Client.Controllers.MainViews.SaveBeforeExit;
import Client.Controllers.MainViews.ViewExamController;
import Client.Events.ExamEndedEvent;
import Client.Events.ExamEndedMessageEvent;
import Client.Events.PrincipalApproveEvent;
import Client.Events.StartExamEvent;
import Client.SimpleChatClient;
import Client.SimpleClient;
import Entities.Communication.Message;
import Entities.Enums;
import Entities.SchoolOwned.ClassExam;
import Entities.SchoolOwned.Question;
import Entities.StudentOwned.ManualStudentExam;
import Entities.StudentOwned.StudentExam;
import Entities.Users.Student;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.DragEvent;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.*;
import java.util.Timer;

public class StudentDoExamManualController extends SaveBeforeExit {

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
    void fileDropped(DragEvent event) {

    }

    @FXML
    void fileDetected(DragEvent event) {
        try {
            ColorAdjust colorAdjust = new ColorAdjust();
            colorAdjust.setBrightness(0.5);
            dragAndDropImg.setEffect(colorAdjust);
            System.out.println("Changing Brightness");
        }
        catch (Exception e) {
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
    public void getExam(StartExamEvent event) {
        mainClassExam = event.getClassExam();
        timeInSeconds = (int) (mainClassExam.getExamTime()) * 60;
        ViewExamController.createManualExam(mainClassExam);
        studentExam.setStudent((Student) (SimpleClient.getUser()));
        studentExam.setClassExam(mainClassExam);
        studentExam.setStatus(Enums.submissionStatus.ToEvaluate);
        List<String> studentAnswers = new ArrayList<String>();
        studentExam.setStudentAnswers(studentAnswers);
        Timer timer = new Timer();
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                if (timeInSeconds > 0) {
                    timeInSeconds--;
                    Platform.runLater(() -> {
                        timeLeftLabel.setText("Time Left: " + String.format("%02d:%02d:%02d", timeInSeconds / 3600, (timeInSeconds%3600)/60, timeInSeconds % 60));
                    });

                } else {
                    try {
                        Message message = new Message(1, "Exam Fail: Time Ended");
                        studentExam.setStatus(Enums.submissionStatus.Unsubmitted);
                        studentExam.setGrade(-1);
                        message.setData(studentExam);
                        SimpleClient.getClient().sendToServer(message);
                        timer.cancel();
                        SimpleChatClient.setRoot("ChooseExam");
                        JOptionPane.showMessageDialog(null, "Exam time ended and no exam was submitted", "Submission Exam", JOptionPane.WARNING_MESSAGE);

                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        };
        timer.schedule(task, 0, 1000);

    }

    @Subscribe
    public void endExam(ExamEndedEvent event) throws IOException {
        JOptionPane.showMessageDialog(null, "Exam was successfully saved", "Success", JOptionPane.INFORMATION_MESSAGE);
        Platform.runLater(() -> {
            try {
                SimpleChatClient.setRoot("StudentMainScreen");
                EventBus.getDefault().unregister(this);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });



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
            try {
                byte[] serializedDocument = Files.readAllBytes(file.toPath());
                //ManualStudentExam manualStudentExam = new ManualStudentExam(studentExam, serializedDocument);
                studentExam.setExamFileByteArray(serializedDocument);
                studentExam.setStatus(Enums.submissionStatus.ToEvaluate);
                Message message = new Message(1, "Manual Exam for student ID: " + SimpleClient.getUser().getID());
                message.setData(studentExam);
                SimpleClient.getClient().sendToServer(message);
                fileRecievedLabel.setText("File Uploaded!");
            }
            catch (IOException e) {
                e.printStackTrace();
            }
            System.out.println("File gotten: " + file);
        }
        catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override @FXML
    public boolean PromptUserToSaveData(String sceneName) {
        if (fileRecievedLabel.getText().equals("File Uploaded!")) {
            try {
                Message message = new Message(1, "Manual Exam for student ID: " + SimpleClient.getUser().getID());
                studentExam.setStatus(Enums.submissionStatus.Unsubmitted);
                message.setData(studentExam);
                SimpleClient.getClient().sendToServer(message);
                SimpleChatClient.setRoot(sceneName);
                EventBus.getDefault().unregister(this);
                System.out.println("PromptUserToSaveData changing scene 2");
            }
            catch (IOException e) {
                throw new RuntimeException(e);
            }
            return false;
        }
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "You have yet to submit your exam. If you leave now you will fail the exam. Do you still wish to leave?", ButtonType.YES, javafx.scene.control.ButtonType.NO);
        alert.setTitle("Exam not submitted");
        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent()) {
            if (result.get() == ButtonType.YES) {
                System.out.println("Prompt to leave manual exam: YES");
                try {
                    Message message = new Message(1, "Manual Exam for student ID: " + SimpleClient.getUser().getID());
                    studentExam.setStatus(Enums.submissionStatus.Unsubmitted);
                    message.setData(studentExam);
                    SimpleClient.getClient().sendToServer(message);
                    SimpleChatClient.setRoot(sceneName);
                    EventBus.getDefault().unregister(this);
                    System.out.println("PromptUserToSaveData changing scene 2");
                }
                catch (IOException e) {
                    throw new RuntimeException(e);
                }
                return true;
            }
            else if (result.get().equals(ButtonType.NO)) {
                System.out.println("Prompt to leave manual exam: NO");
                return false;
            }
        }
        return false;
    }

    @Override
    public boolean CheckForUnsavedData() {
        System.out.println("CheckForUnsavedData in StudentDoExamManualController");
        return true;
    }



    @Subscribe
    public void examEndedExternally(ExamEndedMessageEvent event) throws IOException {
        if(event.getClassExam().getID()  == mainClassExam.getID())
        {
            SimpleChatClient.setRoot("StudentChooseExam");
            JOptionPane.showMessageDialog(null, "Exam was ended by teacher has ran out of time", "Submission Exam", JOptionPane.WARNING_MESSAGE);
        }
    }
    @Subscribe
    public void getExtraTime(PrincipalApproveEvent event) {
        Platform.runLater(()->{
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "The teacher has approved your request for extra time. Do you wish to continue?", ButtonType.YES, javafx.scene.control.ButtonType.NO);
            int addedTime = event.getExtraTime().getDelta();
            System.out.println("addedTime: " + addedTime);
            timeInSeconds += addedTime*60;
        });

    }

}



