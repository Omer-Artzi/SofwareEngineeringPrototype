package Client.Controllers.MainViews.StudentViews;

import Client.Controllers.MainViews.SaveBeforeExit;
import Client.Events.StudentExamEvent;
import Client.Events.StudentExamsMessageEvent;
import Client.SimpleChatClient;
import Client.SimpleClient;
import Entities.Enums;
import Entities.SchoolOwned.ClassExam;
import Entities.Communication.Message;
import Entities.SchoolOwned.ExamForm;
import Entities.StudentOwned.StudentExam;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.util.Callback;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import javax.persistence.criteria.CriteriaBuilder;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class StudentViewExamController extends SaveBeforeExit {

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private TableView<StudentExam> ExamTV;

    @FXML
    private TableColumn<StudentExam, ClassExam> courseColumn;

    @FXML
    private TableColumn<StudentExam, String> examTypeColumn;

    @FXML
    private TableColumn<StudentExam,  Integer> gradeColumn;

    @FXML
    private TableColumn<StudentExam, ClassExam> subjectColumn;

    int rowNumber = 0;


    @FXML
    void OnClickEvent(MouseEvent event) throws IOException {
        if (event.getClickCount() == 2) {
            // can watch only automatic tests
            if(ExamTV.getSelectionModel().getSelectedItem() != null &&
                    ExamTV.getSelectionModel().getSelectedItem().getClassExam().getExamType() == Enums.ExamType.Automatic) {
                SimpleChatClient.setRoot("TeacherGradeStudentExam");
                EventBus.getDefault().post(new StudentExamEvent(ExamTV.getSelectionModel().getSelectedItem()));
                EventBus.getDefault().unregister(this);
            }
        }
    }


    @FXML
    void initialize() throws IOException {
        if(!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
        assert ExamTV != null : "fx:id=\"ExamTV\" was not injected: check your FXML file 'StudentViewExam.fxml'.";
        assert courseColumn != null : "fx:id=\"courseColumn\" was not injected: check your FXML file 'StudentViewExam.fxml'.";
        assert examTypeColumn != null : "fx:id=\"examTypeColumn\" was not injected: check your FXML file 'StudentViewExam.fxml'.";
        assert gradeColumn != null : "fx:id=\"gradeColumn\" was not injected: check your FXML file 'StudentViewExam.fxml'.";
        assert subjectColumn != null : "fx:id=\"subjectColumn\" was not injected: check your FXML file 'StudentViewExam.fxml'.";
        setTable();
        Message message = new Message(1, "Get Student Exams For Student ID: " + SimpleClient.getUser().getID());
        SimpleClient.getClient().sendToServer(message);
    }
    @Subscribe
    public void displayStudentExams(StudentExamsMessageEvent event)
    {
        ExamTV.getItems().clear();
        List<StudentExam> studentExams = event.getStudentExams().stream().filter(studentExam ->
                studentExam.getStatus() == Enums.submissionStatus.Approved).collect(Collectors.toList());
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

        examTypeColumn.setCellValueFactory(param ->  {

        if (param.getValue().getClassExam().getExamType() == Enums.ExamType.Manual)
            return new SimpleStringProperty("Manual");
        else
            return new SimpleStringProperty("Automatic");

        });
        gradeColumn.setCellValueFactory(new PropertyValueFactory<>("grade"));



        Callback<TableColumn<StudentExam, Integer>, TableCell<StudentExam, Integer>> cellFactory =
                column -> new TableCell<StudentExam, Integer>() {
                    @Override
                    protected void updateItem(Integer item, boolean empty) {
                        super.updateItem(item, empty);
                        if (!empty) {
                            setText(String.valueOf(item));

                            if (item >= 50) {
                                setStyle("-fx-background-color: #65A873; -fx-alignment: CENTER;");
                            } else {
                                setStyle("-fx-background-color: #DC6F6F; -fx-alignment: CENTER;");
                            }
                        } else {
                            setText(null);
                            setStyle(null);
                        }
                    }
                };
        gradeColumn.setCellFactory(cellFactory);


        gradeColumn.setStyle( "-fx-alignment: CENTER;");
        examTypeColumn.setStyle( "-fx-alignment: CENTER;");
        examTypeColumn.setStyle( "-fx-alignment: CENTER;");
        courseColumn.setStyle( "-fx-alignment: CENTER;");
        subjectColumn.setStyle( "-fx-alignment: CENTER;");

    }

}

