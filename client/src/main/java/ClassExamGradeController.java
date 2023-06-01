import Entities.*;
import Entities.Teacher;
import Entities.ClassExam;
import Events.ClassExamGradeEvent;
import Events.StudentExamEvent;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import javafx.beans.property.SimpleStringProperty;
import org.mindrot.jbcrypt.BCrypt;

import javax.persistence.criteria.CriteriaBuilder;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

public class ClassExamGradeController {
    @FXML
    private TableView<StudentExam> ClassExamTv;
    @FXML
    private Button EditExamBtn;
    @FXML
    private TableColumn<StudentExam, String> GradeColumn;
    @FXML
    private TableColumn<StudentExam, String> IDColumn;
    @FXML
    private TableColumn<StudentExam, String> NameColumn;
    @FXML
    private TableColumn<StudentExam, String> StatusColumn;
    @FXML
    private Button ViewExamBtn;
    @FXML
    private ComboBox<String> SubjectCombo;
    @FXML
    private ComboBox<String> CourseCombo;
    @FXML
    private ComboBox<String> ExamIDCombo;

    List<Student> Students = new ArrayList<>();
    List<Question> Questions = new ArrayList<>();
    List<ExamForm> ExamForms = new ArrayList<>();
    List<Teacher> Teachers = new ArrayList<>();
    //Teacher clientTeacher;
    List<ClassExam> ClassExams = new ArrayList<>();
    List<Subject> Subjects = new ArrayList<>();
    Teacher clientTeacher;
    String chosenCourseStr;
    String chosenExamStr;
    String chosenSubjectStr;
    ClassExam chosenExam;

    @FXML
    void EditExamBtnAct(ActionEvent event) throws IOException {
        if(ClassExamTv.getSelectionModel().getSelectedItem() != null) {
            SimpleChatClient.setRoot("StudentExamGrade");
            StudentExamGradeController controller = (StudentExamGradeController) SimpleChatClient.getScene().getProperties().get("controller");
            EventBus.getDefault().post(new StudentExamEvent(ClassExamTv.getSelectionModel().getSelectedItem()));
        }
        else
        {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error!");
            alert.setHeaderText("Error: No Student Was Chosen");
            alert.show();
        }
    }

    @FXML
    void ExamIDComboAct(ActionEvent event) {
        chosenExamStr = ExamIDCombo.getSelectionModel().getSelectedItem();
        SetClassExamTv();
    }
    void SetClassExamTv()
    {
        ClassExamTv.getItems().clear();
        // Select the exam by id
        List<ClassExam> selectedExams = clientTeacher.getClassExam().stream().filter(item -> Integer.toString(item.getID()).equals(chosenExamStr))
                .collect(Collectors.toList());
        if (selectedExams.isEmpty())
            return;
        chosenExam = selectedExams.get(0);

        // Assign the table data sources
        NameColumn.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getStudent().getFullName()));
        IDColumn.setCellValueFactory(param -> new SimpleStringProperty(Long.toString(param.getValue().getStudent().getID())));
        GradeColumn.setCellValueFactory(new PropertyValueFactory<>("grade"));
        StatusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
        ClassExamTv.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);

        StatusColumn.setComparator(StatusColumn.getComparator().reversed());
        ClassExamTv.getSortOrder().add(StatusColumn);
        // Loading the data to the table
        if(chosenExam != null)
        {
            ClassExamTv.getItems().addAll(chosenExam.getStudentExams());
            ClassExamTv.sort();
        }
    }



    @FXML
    void CourseComboAct(ActionEvent event)
    {
        chosenCourseStr = CourseCombo.getSelectionModel().getSelectedItem();
        SetExamIDCombo();
    }

    void SetExamIDCombo()
    {

        ExamIDCombo.getItems().clear();
        ClassExamTv.getItems().clear();
        // select Exam
        // collect the courses of the subject
        List<ClassExam> teacherExams = clientTeacher.getClassExam();
        List<ClassExam> selectedExams = teacherExams.stream().filter(item-> item.getExamForm().getCourse().getName() == chosenCourseStr)
                .collect(Collectors.toList());

        if (selectedExams.isEmpty())
            return;
        for (int i = 0; i < selectedExams.size(); i++)
        {
            ExamIDCombo.getItems().add(Integer.toString(selectedExams.get(i).getID()));
        }
    }

    @FXML
    void SubjectComboAct(ActionEvent event) {
        chosenSubjectStr = SubjectCombo.getSelectionModel().getSelectedItem();
        SetCourseCombo();
    }

    void SetCourseCombo()
    {
        CourseCombo.getItems().clear();
        ExamIDCombo.getItems().clear();
        ClassExamTv.getItems().clear();

        // select course
        // collect the courses of the subject
        List<Course> teacherCourses = clientTeacher.getCourseList();
        List<Course> subjectCourses = teacherCourses.stream().filter(item-> item.getSubject().getName() == chosenSubjectStr)
                .collect(Collectors.toList());

        for (int i = 0; i < subjectCourses.size(); i++)
        {
            CourseCombo.getItems().add(subjectCourses.get(i).getName());
        }
    }


    @Subscribe
    public void SetTable(ClassExamGradeEvent event)
    {
        Platform.runLater(() -> {
            SubjectCombo.getSelectionModel().select(event.getSubjectStr());
            SetCourseCombo();
            CourseCombo.getSelectionModel().select(event.getCourseStr());
            SetExamIDCombo();
            ExamIDCombo.getSelectionModel().select(event.getExamIDStr());
            SetClassExamTv();
            EventBus.getDefault().unregister(this);
        });
    }

    @FXML
    void initialize() throws IOException {
        EventBus.getDefault().register(this);

        clientTeacher = (Teacher)SimpleClient.getClient().getUser();
        // Get teacher courses and return if the teacher not assigned to any course
        List<Course> teacherCourses = clientTeacher.getCourseList();
        if (teacherCourses.isEmpty())
        {
            System.out.println("Teacher Is not assign to any course");
            return;
        }

        // Get teacher assigned Subjects
        List<Subject> teacherSubjects = new ArrayList<>();
        for (int i = 0; i < teacherCourses.size(); i++)
        {
            Subject subject = teacherCourses.get(i).getSubject();
            if (!teacherSubjects.contains(subject))
                teacherSubjects.add(subject);
        }

        // select subject
        assert SubjectCombo != null : "fx:id=\"SubjectCombo\" was not injected: check your FXML file 'ClassExamGrade.fxml'.";
        for (int i = 0; i < teacherSubjects.size(); i++)
        {
            SubjectCombo.getItems().add(teacherSubjects.get(i).getName());
        }

    }

}
