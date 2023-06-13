package Client.Controllers.MainViews.StudentViews;

import Client.Controllers.MainViews.ViewExamController;
import Client.Events.ExamEndedMessageEvent;
import Client.Events.ExamEndedEvent;
import Client.Events.StartExamEvent;
import Client.SimpleChatClient;
import Client.SimpleClient;
import Entities.Communication.Message;
import Entities.Enums;
import Entities.SchoolOwned.ClassExam;
import Entities.StudentOwned.ManualStudentExam;
import Entities.StudentOwned.StudentExam;
import Entities.Users.Student;
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
import java.io.*;
import java.net.URL;
import java.nio.file.Files;
import java.util.ResourceBundle;
import java.util.Timer;
import java.util.TimerTask;

public class StudentDoExamManualController {

    @FXML
    private ImageView dragAndDropImg;

    @FXML
    private Label timeLeftLabel;
    @FXML
    private Label fileRecievedLabel;


    private int timeInSeconds;
    private ClassExam mainClassExam;
    private final StudentExam studentExam = new StudentExam();

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
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /*@FXML
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
                FileInputStream fis = new FileInputStream(file);
                XWPFDocument document = new XWPFDocument(fis);
                System.out.println(document.getDocument().getBody());
                ManualStudentExam manualStudentExam = new ManualStudentExam(studentExam, serializeXWPFDocument(document));
                Message message = new Message(1, "Manual Exam for student ID: " + SimpleClient.getUser().getID());
                message.setData(manualStudentExam);
                SimpleClient.getClient().sendToServer(message);
                fileRecievedLabel.setText("File Uploaded!");
            } catch (IOException e) {
                e.printStackTrace();
            }
            System.out.println("File gotten: " + file);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }*/

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
        studentExam.setStudent(((Student) (SimpleClient.getUser())));
        studentExam.setClassExam(mainClassExam);
        studentExam.setStatus(Enums.submissionStatus.ToEvaluate);
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
        SimpleChatClient.getMainWindowController().LoadSceneToMainWindow("StudentChooseExam");
        JOptionPane.showMessageDialog(null, "Exam was successfully saved", "Success", JOptionPane.INFORMATION_MESSAGE);

    }

    /*public static byte[] serializeXWPFDocument(XWPFDocument document) throws IOException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(bos);
        oos.writeObject(document);
        oos.flush();
        byte[] serializedDocument = bos.toByteArray();
        oos.close();
        bos.close();
        return serializedDocument;
    }*/

    //Edan
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

                /*FileOutputStream fos = new FileOutputStream(file);
                ObjectOutputStream oos = new ObjectOutputStream(fos);
                String docInStringForm =  oos.toString();
                XWPFDocument document = new XWPFDocument(OPCPackage.create(oos));

                FileInputStream fis = new FileInputStream(file);
                XWPFDocument document = new XWPFDocument(fis);
                System.out.println(document.getDocument().getBody());
                ManualStudentExam manualStudentExam = new ManualStudentExam(studentExam, serializeXWPFDocument(document));*/

                ManualStudentExam manualStudentExam = new ManualStudentExam(studentExam, serializedDocument);
                Message message = new Message(1, "Manual Exam for student ID: " + SimpleClient.getUser().getID());
                message.setData(manualStudentExam);
                SimpleClient.getClient().sendToServer(message);
                fileRecievedLabel.setText("File Uploaded!");
            } catch (IOException e) {
                e.printStackTrace();
            }
            System.out.println("File gotten: " + file);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    //Edan
    public static byte[] serializeXWPFDocument(XWPFDocument document) {
        try {
            File file = new File("ManualExam.docx");
            FileOutputStream fos = new FileOutputStream(file);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            document.write(oos);
            oos.flush();
            oos.close();
            fos.close();
            byte[] serializedDocument = Files.readAllBytes(file.toPath());
            Boolean fileDeleted = file.delete();
            System.out.println("File deleted: " + fileDeleted);
            return serializedDocument;
        }
        catch (IOException e) {
            System.out.println("Error in serializeXWPFDocument: could not write to ByteArrayOutputStream");
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }


    //Edan
    /*public static ObjectOutputStream serializeFile(File file) throws IOException {
        FileOutputStream fileOut = new FileOutputStream("ManualExam.docx");
        ObjectOutputStream oos = new ObjectOutputStream(fileOut);
        oos.writeObject(file);
        oos.close();
        fileOut.close();

        return oos;
    }*/

    @Subscribe
    public void examEndedExternally(ExamEndedMessageEvent event) throws IOException {
        if(event.getClassExam().getID()  == mainClassExam.getID())
        {
            SimpleChatClient.getMainWindowController().LoadSceneToMainWindow("StudentChooseExam");
            JOptionPane.showMessageDialog(null, "Exam was ended by teacher has ran out of time", "Submission Exam", JOptionPane.WARNING_MESSAGE);
        }
    }
//    @Subscribe
//    public void addExtraTime(ExtraTimeMessageEvent event) {
//        if(event.getClassExam().getID()  == mainClassExam.getID())
//        {
//            timeInSeconds += event.getExtraTime();
//            JOptionPane.showMessageDialog(null, "Teacher has added " + event.getExtraTime() + " minutes to the exam", "Submission Exam", JOptionPane.WARNING_MESSAGE);
//        }
//    }

}



