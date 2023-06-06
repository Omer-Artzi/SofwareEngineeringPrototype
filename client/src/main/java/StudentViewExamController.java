import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import Entities.*;
import Events.StudentExamEvent;
import Events.StudentExamsMessageEvent;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import org.greenrobot.eventbus.EventBus;
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
    private TableColumn<StudentExam, ClassExam> courseColumn;

    @FXML
    private TableColumn<StudentExam, ClassExam> examCodeColumn;

    @FXML
    private TableColumn<StudentExam,  Integer> gradeColumn;

    @FXML
    private TableColumn<StudentExam, ClassExam> subjectColumn;

    @FXML
    void initialize() throws IOException {
        EventBus.getDefault().register(this);
        assert ExamTV != null : "fx:id=\"ExamTV\" was not injected: check your FXML file 'StudentViewExam.fxml'.";
        assert courseColumn != null : "fx:id=\"courseColumn\" was not injected: check your FXML file 'StudentViewExam.fxml'.";
        assert examCodeColumn != null : "fx:id=\"examCodeColumn\" was not injected: check your FXML file 'StudentViewExam.fxml'.";
        assert gradeColumn != null : "fx:id=\"gradeColumn\" was not injected: check your FXML file 'StudentViewExam.fxml'.";
        assert subjectColumn != null : "fx:id=\"subjectColumn\" was not injected: check your FXML file 'StudentViewExam.fxml'.";
        setTable();
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
    private void setTable()
    {
        subjectColumn.setCellValueFactory(new PropertyValueFactory<>("classExam"));
        subjectColumn.setCellFactory(tc -> new TableCell<>(){
            @Override
            protected void updateItem(ClassExam item, boolean empty) {
                super.updateItem(item, empty); // must be called
                if (empty || item == null) {
                    setText(null);
                } else {
                    // replace with desired format
                    setText(item.getSubject().getName());
                }
            }
        });
        courseColumn.setCellValueFactory(new PropertyValueFactory<>("classExam"));
        courseColumn.setCellFactory(tc -> new TableCell<>(){
            @Override
            protected void updateItem(ClassExam item, boolean empty) {
                super.updateItem(item, empty); // must be called
                if (empty || item == null) {
                    setText(null);
                } else {
                    // replace with desired format
                    setText(item.getCourse().getName());
                }
            }
        });
        examCodeColumn.setCellValueFactory(new PropertyValueFactory<>("classExam"));
        examCodeColumn.setCellFactory(tc -> new TableCell<>(){
            @Override
            protected void updateItem(ClassExam item, boolean empty) {
                super.updateItem(item, empty); // must be called
                if (empty || item == null) {
                    setText(null);
                } else {
                    // replace with desired format
                    setText(item.getCode());
                }
            }
        });
        gradeColumn.setCellValueFactory(new PropertyValueFactory<>("grade"));
        ExamTV.setRowFactory(tv -> new TableRow<StudentExam>() {
            @Override
            protected void updateItem(StudentExam exam, boolean empty) {
                super.updateItem(exam, empty);
                if (exam == null || empty) {
                    setStyle("");
                } else {
                    if (exam.getGrade() > 50) {
                        setStyle("-fx-background-color: green;");
                    } else {
                        setStyle("-fx-background-color: red;");
                    }
                }
            }
        });
    }

}

