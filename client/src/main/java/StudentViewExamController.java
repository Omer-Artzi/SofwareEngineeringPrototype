import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import Entities.*;
import Events.StudentExamEvent;
import Events.StudentExamsMessageEvent;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import org.greenrobot.eventbus.Subscribe;

import javax.persistence.criteria.CriteriaBuilder;

public class StudentViewExamController {

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private TableView<StudentExam> ExamTV;

    @FXML
    private TableColumn<StudentExam, Course> courseColumn;

    @FXML
    private TableColumn<StudentExam, ClassExam> examCodeColumn;

    @FXML
    private TableColumn<StudentExam,  Integer> gradeColumn;

    @FXML
    private TableColumn<StudentExam, Subject> subjectColumn;

    @FXML
    void initialize() throws IOException {
        assert ExamTV != null : "fx:id=\"ExamTV\" was not injected: check your FXML file 'StudentViewExam.fxml'.";
        assert courseColumn != null : "fx:id=\"courseColumn\" was not injected: check your FXML file 'StudentViewExam.fxml'.";
        assert examCodeColumn != null : "fx:id=\"examCodeColumn\" was not injected: check your FXML file 'StudentViewExam.fxml'.";
        assert gradeColumn != null : "fx:id=\"gradeColumn\" was not injected: check your FXML file 'StudentViewExam.fxml'.";
        assert subjectColumn != null : "fx:id=\"subjectColumn\" was not injected: check your FXML file 'StudentViewExam.fxml'.";
        subjectColumn.setCellValueFactory(new PropertyValueFactory<>("subject"));
        courseColumn.setCellValueFactory(new PropertyValueFactory<>("course"));
        examCodeColumn.setCellValueFactory(new PropertyValueFactory<>("client"));
        gradeColumn.setCellValueFactory(new PropertyValueFactory<>("client"));
        Message message = new Message(1,"Get Student Exams For Student ID: " + SimpleClient.getUser().getID());
        SimpleClient.getClient().sendToServer(message);
    }
    @Subscribe
    public void displayStudentExams(StudentExamsMessageEvent event)
    {
        List<StudentExam> studentExams = event.getStudentExams();
        if(studentExams != null)
        {
            ExamTV.getItems().addAll(studentExams);
        }
    }

}

