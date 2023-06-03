
import Entities.*;
import Events.StartExamEvent;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import javax.swing.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class StudentChooseExamController {
    @FXML
    private ComboBox<Course> CoursesCB;

    @FXML
    private TableView<ClassExam> ExamsTV;

    @FXML
    private TableColumn<ClassExam, Integer> IDColumn;

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
        IDColumn.setCellValueFactory(new PropertyValueFactory<>("ID"));
        codeColumn.setCellValueFactory(new PropertyValueFactory<>("code"));
        timeColumm.setCellValueFactory(new PropertyValueFactory<>("examTime"));
        subjectsCB.getItems().addAll(((Student)(SimpleClient.getUser())).getSubjects());
      List<ClassExam> classExams =  ((Student)(SimpleClient.getUser())).getClassExams();
        ExamsTV.getItems().addAll(classExams);
      for(ClassExam classExam: classExams)
      {
          for(StudentExam studentExam: classExam.getStudentExams())
          {
              if(studentExam.getStudent().equals(((Student) SimpleClient.getUser())) && studentExam.getStatus() == HSTS_Enums.StatusEnum.NotTaken)
              {
                  ExamsTV.getItems().add(classExam);
              }

          }
      }
        IDTF.setDisable(true);
    }
    @FXML
    void backToHomePage() throws IOException {
        SimpleChatClient.setRoot("HomePage");
    }
    @FXML
    void onSubjectSelection() throws IOException {

       Subject selectedSubject = subjectsCB.getSelectionModel().getSelectedItem();
       if(selectedSubject != null)
       {
       List<Course> courses = ((Student)(SimpleClient.getUser())).getCourses();
       for(Course course: courses) {
           if (course.getSubject() == selectedSubject)
               CoursesCB.getItems().add(course);
       }
       }
    }
    @FXML
    void onCourseSelection()
    {
        List<ClassExam> classExams = ((Student)(SimpleClient.getUser())).getClassExams();
        Course selectedCourse = CoursesCB.getSelectionModel().getSelectedItem();
        if(classExams != null && selectedCourse != null)
        {
            for(ClassExam classExam: classExams)
            {
                if(classExam.getCourse() == selectedCourse)
                {
                    ExamsTV.getItems().add(classExam);
                }
            }
        }

    }
    @FXML
    public void startExam() throws IOException {

        ClassExam selectedExam = ExamsTV.getSelectionModel().getSelectedItem();
        if( selectedExam != null ) {
            if (selectedExam.getExamType() == HSTS_Enums.ExamType.Automatic) {
                //TODO: change to "real" ID
                if(selectedExam.getCode().equals(examCodeTF.getText()) && Integer.parseInt(IDTF.getText()) == ((Student)(SimpleClient.getUser())).getID()) {
                    SimpleChatClient.getMainWindowController().LoadSceneToMainWindow("StudentDoExamDigital");
                }
                else {
                    JOptionPane.showMessageDialog(null,"Incorrect Exam Code or ID", "Error", JOptionPane.WARNING_MESSAGE);
                }
            } else {

                if(selectedExam.getCode().equals(examCodeTF.getText())) {
                    SimpleChatClient.getMainWindowController().LoadSceneToMainWindow("StudentDoExamManual");
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


}
