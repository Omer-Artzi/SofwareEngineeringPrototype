package Client.Controllers.MainViews.StaffViews;

import Client.Controllers.MainViews.SaveBeforeExit;
import Client.Controllers.Sidebars.SideBar;
import Client.Events.*;
import Client.SimpleChatClient;
import Client.SimpleClient;
import Entities.Communication.Message;
import Entities.SchoolOwned.ClassExam;
import Entities.SchoolOwned.Course;
import Entities.SchoolOwned.ExamForm;
import Entities.SchoolOwned.Subject;
import Entities.StudentOwned.StudentExam;
import Entities.Users.Person;
import Entities.Users.Principal;
import Entities.Users.Teacher;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class PreviewTestFormController extends SaveBeforeExit {

    @FXML
    private ComboBox<String> CourseCombo;


    @FXML
    private ComboBox<String> SubjectCombo;

    @FXML
    private TableView<ExamForm> ExamFormTV;

    @FXML
    private TableColumn<ExamForm, String> ExamIDCol;

    @FXML
    private TableColumn<ExamForm, String> LastUsedCol;

    @FXML
    private TableColumn<ExamForm, String> CreatorCol;

    @FXML
    private Button DuplicateBtn;

    @FXML
    private Button EditBtn;




    @FXML
    void DuplicateAct(ActionEvent event) throws IOException {
        if (ExamFormTV.getSelectionModel().getSelectedItem() != null) {
            SimpleChatClient.setRoot("TeacherAddTestForm");
            EventBus.getDefault().post(new LoadExamEvent(ExamFormTV.getSelectionModel().getSelectedItem(), "Duplicate"));
            EventBus.getDefault().unregister(this);
        }
    }

    @FXML
    void EditBtnAct(ActionEvent event) throws IOException {
        if (ExamFormTV.getSelectionModel().getSelectedItem() != null) {
            SimpleChatClient.setRoot("TeacherAddTestForm");
            EventBus.getDefault().post(new LoadExamEvent(ExamFormTV.getSelectionModel().getSelectedItem(), "Edit"));
            EventBus.getDefault().unregister(this);
        }
    }


    @FXML
    void ExamFormClick(MouseEvent event) throws IOException {

        if (event.getClickCount() == 2)
        {
            if (ExamFormTV.getSelectionModel().getSelectedItem() != null)
            {
                ClassExam classExam = new ClassExam(ExamFormTV.getSelectionModel().getSelectedItem());
                SimpleChatClient.setRoot("TeacherGradeStudentExam");
                EventBus.getDefault().post(new StudentExamEvent(new StudentExam(classExam)));
                EventBus.getDefault().unregister(this);
            }
        }
    }
    Person client;
    List<Subject> subjects;
    List<Course> courses;

    String chosenSubjectStr;
    String chosenCourseStr;


    @FXML
    void CourseComboAct(ActionEvent event) {
        ExamFormTV.getItems().clear();
        chosenCourseStr = CourseCombo.getSelectionModel().getSelectedItem();

        List<ExamForm> examForms = courses.stream().filter(course ->
                course.getName().startsWith(chosenCourseStr)).collect(Collectors.toList()).get(0).getExamForms();

        if (examForms != null)
            ExamFormTV.getItems().addAll(examForms);
    }


    @FXML
    void SubjectComboAct(ActionEvent event) {
        chosenSubjectStr = SubjectCombo.getSelectionModel().getSelectedItem();

        if(!ExamFormTV.getItems().isEmpty()) {
            ExamFormTV.getItems().clear();
        }

        if(!CourseCombo.getItems().isEmpty()) {
            CourseCombo.getItems().clear();
        }

        // collect the courses of the subject
        List<Course> subjectCourses = courses;
        subjectCourses = subjectCourses.stream().filter(item-> item.getSubject().getName().startsWith(chosenSubjectStr))
                .collect(Collectors.toList());

        // Sort Combo
        Collections.sort(subjectCourses, Comparator.comparing(Course::getName));
        for (int i = 0; i < subjectCourses.size(); i++)
        {
            CourseCombo.getItems().add(subjectCourses.get(i).getName());
        }
    }

    @Subscribe
    public void GotSubjects(SubjectMessageEvent event) throws InterruptedException {
        Platform.runLater(() ->
        {
            subjects = event.getSubjects();
            Collections.sort(subjects, Comparator.comparing(Subject::getName));


            if (client instanceof Teacher)
            {
                Message message = new Message(0, "Get teacher's Courses");
                message.setData(client);
                try {
                    SimpleClient.getClient().sendToServer(message);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }

            else if (client instanceof Principal)
            {
                try {
                    SimpleClient.getClient().sendToServer(new Message(0, "Get All Courses"));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }

        });
    }

    @Subscribe
    public void GotCourses(CourseMessageEvent event) throws InterruptedException {
        Platform.runLater(() ->
        {
            courses = event.getCourses();
            if (courses != null && subjects != null)
            {
                CourseCombo.setDisable(false);
                SubjectCombo.setDisable(false);
                SubjectCombo.getItems().addAll(subjects.stream().map(subject -> subject.getName()).collect(Collectors.toList()));

                if(chosenSubjectStr != null)
                {
                    SubjectCombo.getSelectionModel().select(chosenSubjectStr);
                    CourseCombo.getSelectionModel().select(chosenCourseStr);
                }

            }
        });
    }

    @Subscribe
    public void ReturnFromStudentGrade(ClassExamGradeEvent event) throws InterruptedException {
        // reselect previous items
        Platform.runLater(() -> {
            chosenSubjectStr = event.getSubjectStr();
            chosenCourseStr = event.getCourseStr();
        });
    }

    private String FormatDate(Date date, boolean withTime)
    {
        SimpleDateFormat formatter;
        if (withTime)
            formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        else
            formatter = new SimpleDateFormat("dd/MM/yyyy");
        return formatter.format(date);
    }
    @FXML
    void initialize() throws IOException {
        if(!EventBus.getDefault().isRegistered(this))
            EventBus.getDefault().register(this);
        client = SimpleClient.getUser();
        if (client instanceof Teacher)
        {
            Message message = new Message(0, "Get teacher's Subjects");
            message.setData(client);
            SimpleClient.getClient().sendToServer(message);
        }

        else if (client instanceof Principal)
        {
            SimpleClient.getClient().sendToServer(new Message(0, "Get All Subjects"));
            EditBtn.setDisable(true);
            EditBtn.setVisible(false);
            DuplicateBtn.setDisable(true);
            DuplicateBtn.setVisible(false);
        }
        CourseCombo.setDisable(true);
        SubjectCombo.setDisable(true);

        // Class Exam config
        CreatorCol.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getCreator().getFullName()));
        LastUsedCol.setCellValueFactory(param -> new SimpleStringProperty(FormatDate(param.getValue().getLastUsed(), false)));
        ExamIDCol.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getExamFormID()));
        ExamFormTV.getSortOrder().add(ExamIDCol);
        CreatorCol.setStyle( "-fx-alignment: CENTER;");
        LastUsedCol.setStyle( "-fx-alignment: CENTER;");
        ExamIDCol.setStyle( "-fx-alignment: CENTER;");


    }

}
