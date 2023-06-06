
import Entities.*;
import Events.ExamMessageEvent;
import Events.StartExamEvent;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import javax.swing.*;
import java.io.IOException;
import java.util.List;

public class StudentChooseExamController {
    @FXML
    private ComboBox<Course> CoursesCB;

    @FXML
    private TableView<ClassExam> ExamsTV;

    @FXML
    private TableColumn<ClassExam, Integer> IDColumn;
    @FXML
    private TableColumn<ClassExam, HSTS_Enums.ExamType> examTypeColumn;
    @FXML
    private TableColumn<ClassExam, Subject> subjectColumn;
    @FXML
    private TableColumn<ClassExam, Course> courseColumn;

    @FXML
    private Button startExamButton;

    @FXML
    private Button backButton;

    @FXML
    private TableColumn<ClassExam, SimpleStringProperty> codeColumn;

    @FXML
    private TextField examCodeTF;

    @FXML
    private ComboBox<Subject> subjectsCB;

    @FXML
    private TextField IDTF;

    @FXML
    private TableColumn<ClassExam, Integer> timeColumm;

    @FXML
    void initialize() throws IOException {
        EventBus.getDefault().register(this);
        IDColumn.setCellValueFactory(new PropertyValueFactory<>("ID"));
        codeColumn.setCellValueFactory(new PropertyValueFactory<>("code"));
        timeColumm.setCellValueFactory(new PropertyValueFactory<>("examTime"));
        subjectColumn.setCellValueFactory(new PropertyValueFactory<>("subject"));
        courseColumn.setCellValueFactory(new PropertyValueFactory<>("course"));
        examTypeColumn.setCellValueFactory(new PropertyValueFactory<>("examType"));
        examTypeColumn.setCellFactory(tc -> new TableCell<>(){
            @Override
            protected void updateItem(HSTS_Enums.ExamType item, boolean empty) {
                super.updateItem(item, empty); // must be called
                if (empty || item == null) {
                    setText(null);
                } else {
                    // replace with desired format
                    setText(item.name());
                }
            }
        });

        Message message = new Message(1,"Get class exams for student ID: " + SimpleClient.getUser().getID());
        SimpleClient.getClient().sendToServer(message);
        IDTF.setDisable(true);
    }
    @FXML
    void onSubjectSelection() throws IOException {
        List<ClassExam> classExams = ((Student)(SimpleClient.getUser())).getClassExams();
        ExamsTV.getItems().clear();
       Subject selectedSubject = subjectsCB.getSelectionModel().getSelectedItem();
       if(selectedSubject != null)
       {
           for(ClassExam classExam:classExams)
           {
        if(classExam.getSubject().equals(selectedSubject))
        {
           ExamsTV.getItems().add(classExam);
        }
       }
       }
       ExamsTV.refresh();
    }
    @FXML
    void onCourseSelection()
    {
        List<ClassExam> classExams = ((Student)(SimpleClient.getUser())).getClassExams();
        ExamsTV.getItems().clear();
        Course selectCourse = CoursesCB.getSelectionModel().getSelectedItem();
        if(selectCourse != null)
        {
            for(ClassExam classExam:classExams)
            {
                if(classExam.getCourse().equals(selectCourse))
                {
                    ExamsTV.getItems().add(classExam);
                }
            }
        }
        ExamsTV.refresh();

    }
    @FXML
    public void startExam() throws IOException {
        ClassExam selectedExam = ExamsTV.getSelectionModel().getSelectedItem();
        if( selectedExam != null ) {
            if (selectedExam.getExamType() == HSTS_Enums.ExamType.Manual) {
                //TODO: change to "real" ID
                if(selectedExam.getCode().equals(examCodeTF.getText()) ) {
                    SimpleChatClient.getMainWindowController().LoadSceneToMainWindow("StudentDoExamManual");
                }
                else {
                    JOptionPane.showMessageDialog(null,"Incorrect Exam Code or ID", "Error", JOptionPane.WARNING_MESSAGE);
                }
            } else//Digital Exam
            {
                if(selectedExam.getCode().equals(examCodeTF.getText())&& Integer.parseInt(IDTF.getText()) == (SimpleClient.getUser().getID())) {
                    SimpleChatClient.getMainWindowController().LoadSceneToMainWindow("StudentDoExamDigital");
                }
                else {
                    JOptionPane.showMessageDialog(null,"Incorrect Exam Code", "Error", JOptionPane.WARNING_MESSAGE);
                }
            }
            EventBus.getDefault().post(new StartExamEvent(selectedExam));
        }
        else {
             JOptionPane.showMessageDialog(null,"Please select an exam", "Error", JOptionPane.WARNING_MESSAGE);
        }

    }
    @FXML
    public void chooseType()
    {
        ClassExam selectedExam = ExamsTV.getSelectionModel().getSelectedItem();
        if( selectedExam != null )
        {
            if( selectedExam.getExamType() == HSTS_Enums.ExamType.Automatic)
            {
                IDTF.setDisable(false);
            }
            else
            {
                IDTF.setDisable(true);
            }
        }

    }
    @Subscribe
    public  void displayExams(ExamMessageEvent event)
    {
        List<ClassExam> classExams = event.getClassExams();
        ((Student)SimpleClient.getUser()).setClassExams(classExams);
        for(ClassExam classExam: classExams)
        {
            for(StudentExam studentExam: classExam.getStudentExams())
            {
                if(studentExam.getStudent().equals(SimpleClient.getUser()) && studentExam.getStatus() == HSTS_Enums.submissionStatus.NotTaken)
                {
                    ExamsTV.getItems().add(classExam);
                }

            }
            if(!subjectsCB.getItems().contains(classExam.getSubject()))
            {
                subjectsCB.getItems().add(classExam.getSubject());
            }
            if(!CoursesCB.getItems().contains(classExam.getCourse()))
            {
                CoursesCB.getItems().add(classExam.getCourse());
            }
        }
    }


}