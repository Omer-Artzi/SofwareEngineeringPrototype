package Client.Controllers.MainViews.StaffViews;

import Client.Controllers.MainViews.SaveBeforeExit;
import Client.Controllers.Sidebars.SideBar;
import Client.Events.CourseMessageEvent;
import Client.Events.RefreshPerson;
import Client.Events.SubjectMessageEvent;
import Client.SimpleClient;
import Entities.Communication.Message;
import Entities.SchoolOwned.Course;
import Entities.SchoolOwned.ExamForm;
import Entities.SchoolOwned.Subject;
import Entities.Users.Person;
import Entities.Users.Principal;
import Entities.Users.Teacher;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;
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
    void ExamFormClick(MouseEvent event) {

        if (event.getClickCount() == 2)
        {
            // show exam form
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
            }
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
