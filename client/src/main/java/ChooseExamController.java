import Entities.*;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ChooseExamController {
    @FXML
    private ComboBox<Course> CoursesCB;

    @FXML
    private TableView<ClassExam> ExamsTV;

    @FXML
    private TableColumn<ClassExam, Integer> IDColumn;

    @FXML
    private Button ViewDIgitalButton;

    @FXML
    private Button ViewManualButton;

    @FXML
    private Button backButton;

    @FXML
    private TableColumn<ClassExam, SimpleStringProperty> codeColumn;

    @FXML
    private TextField examCodeTF;

    @FXML
    private ComboBox<Subject> subjectsCB;

    @FXML
    private TableColumn<ClassExam, Integer> timeColumm;

    @FXML
    void initialize() throws IOException {
        Student student = new Student();
        List<Subject> subjects = new ArrayList<>();
        Subject subject = new Subject("Math");
        Course course = new Course();
        course.setSubject(subject);
        List<Course> courses = new ArrayList<>();
        courses.add(course);
        subject.setCourses(courses);
        student.setSubjects(subjects);
        student.setCourses(courses);
        SimpleClient.setUser(student);
        subjectsCB.getItems().addAll(((Student)(SimpleClient.getUser())).getSubjects());
        System.out.println(student.getSubjects());
        subjectsCB.getItems().addAll(student.getSubjects());
        subjectsCB.getItems().add(subject);

        ExamsTV.getItems().addAll(((Student)(SimpleClient.getUser())).getClassExams());
    }
    @FXML
    void backToHomePage() throws IOException {
        SimpleChatClient.setRoot("HomePage");
    }
    @FXML
    void onSubjectSelection() throws IOException {

       Subject selectedSubject = subjectsCB.getSelectionModel().getSelectedItem();
       //SimpleClient.getClient().sendToServer();
        CoursesCB.getItems().addAll(selectedSubject.getCourses());
    }
    @FXML
    void onCourseSelection()
    {

    }
    @FXML
    void takeManual() throws IOException {

        SimpleChatClient.setScene("DoExamManual", 263,232);

    }
    @FXML
    void takeDigital()
    {

    }

}
