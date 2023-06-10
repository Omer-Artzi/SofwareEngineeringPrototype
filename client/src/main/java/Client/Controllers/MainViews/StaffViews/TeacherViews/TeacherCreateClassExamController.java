package Client.Controllers.MainViews.StaffViews.TeacherViews;

import Client.Controllers.MainViews.SaveBeforeExit;
import Client.Events.CoursesOfTeacherEvent;
import Client.Events.ExamMessageEvent;
import Client.Events.LoadExamEvent;
import Client.Events.SubjectsOfTeacherMessageEvent;
import Client.SimpleClient;
import Entities.Communication.Message;
import Entities.SchoolOwned.ClassExam;
import Entities.SchoolOwned.Course;
import Entities.SchoolOwned.ExamForm;
import Entities.SchoolOwned.Subject;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import javax.swing.*;
import java.io.IOException;
import java.sql.Date;
import java.time.ZoneId;
import java.util.List;
import java.util.regex.Pattern;

public class TeacherCreateClassExamController extends SaveBeforeExit {
    @FXML
    private DatePicker endDateTF;

    @FXML
    private TableView<ExamForm> ExamFormsTV;

    @FXML
    private TextField codeTF;

    @FXML
    private ComboBox<Course> courseCB;

    @FXML
    private TextField endTimeTF;

    @FXML
    private TextField examTimeTF;

    @FXML
    private Button saveExamButton;

    @FXML
    private DatePicker startDateTF;

    @FXML
    private TextField startTimeTF;

    @FXML
    private ComboBox<Subject> subjectCB;


    private List<Course> courses;
    private ClassExam classExam;



    @FXML
    void initialize() throws IOException {
        assert endDateTF != null : "fx:id=\"EndDateTF\" was not injected: check your FXML file 'TeacherCreateClassExam.fxml'.";
        assert ExamFormsTV != null : "fx:id=\"ExamFormsTV\" was not injected: check your FXML file 'TeacherCreateClassExam.fxml'.";
        assert codeTF != null : "fx:id=\"codeTF\" was not injected: check your FXML file 'TeacherCreateClassExam.fxml'.";
        assert courseCB != null : "fx:id=\"courseCB\" was not injected: check your FXML file 'TeacherCreateClassExam.fxml'.";
        assert endTimeTF != null : "fx:id=\"endTimeTF\" was not injected: check your FXML file 'TeacherCreateClassExam.fxml'.";
        assert examTimeTF != null : "fx:id=\"examTimeTF\" was not injected: check your FXML file 'TeacherCreateClassExam.fxml'.";
        assert saveExamButton != null : "fx:id=\"saveExamButton\" was not injected: check your FXML file 'TeacherCreateClassExam.fxml'.";
        assert startDateTF != null : "fx:id=\"startDateTF\" was not injected: check your FXML file 'TeacherCreateClassExam.fxml'.";
        assert startTimeTF != null : "fx:id=\"startTimeTF\" was not injected: check your FXML file 'TeacherCreateClassExam.fxml'.";
        assert subjectCB != null : "fx:id=\"subjectCB\" was not injected: check your FXML file 'TeacherCreateClassExam.fxml'.";
        EventBus.getDefault().register(this);
        ExamFormsTV.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        courseCB.setDisable(true);
        ExamFormsTV.setDisable(true);
        startDateTF.setDisable(true);
        startTimeTF.setDisable(true);
        endTimeTF.setDisable(true);
        endDateTF.setDisable(true);
        codeTF.setDisable(true);
        examTimeTF.setDisable(true);
        Message subjectMessage = new Message(1, "1Get Subjects of Teacher: " + SimpleClient.getUser().getID());
        Message courseMessage = new Message(1, "1Get Courses of Teacher: " + SimpleClient.getUser().getID());
        subjectMessage.setData(SimpleClient.getUser());
        courseMessage.setData(SimpleClient.getUser());
         SimpleClient.getClient().sendToServer(subjectMessage);
        SimpleClient.getClient().sendToServer(courseMessage);
        classExam = new ClassExam();
    }

    @FXML
    public void onSubjectSelection()
    {
        System.out.println("Subject Selected");
        Subject selectedSubject = subjectCB.getSelectionModel().getSelectedItem();
        if(courses != null /*&& !courses.isEmpty()*/)
        {
            for(Course course: courses) {
                if(course.getSubject() == selectedSubject) {
                    courseCB.getItems().add(course);
                }
            }
            courseCB.setDisable(false);

        }
        else
        {
            JOptionPane.showMessageDialog(null, "You do not teach in any subjects or the database could not retrieve the data", "Database Error", JOptionPane.WARNING_MESSAGE);
        }
        classExam.setSubject(subjectCB.getSelectionModel().getSelectedItem());

    }

    @FXML
    public void onCourseSelection() throws IOException {
        Message message = new Message(1, "Get Exams For Course: " + courseCB.getSelectionModel().getSelectedItem().getName());
        message.setData(courseCB.getSelectionModel().getSelectedItem());
        SimpleClient.getClient().sendToServer(message);

        classExam.setCourse(courseCB.getSelectionModel().getSelectedItem());
    }
    @FXML
    public void onExamFormChosen()
    {
        startDateTF.setDisable(false);
        startTimeTF.setDisable(false);
        endTimeTF.setDisable(false);
        endDateTF.setDisable(false);
        codeTF.setDisable(false);
        examTimeTF.setDisable(false);
    }
    @FXML
    public void onSaveExam() throws IOException {
        String time =examTimeTF.getText();
        String startTime =examTimeTF.getText();
        String endTime =examTimeTF.getText();
        String code = codeTF.getText();
        if( code.length() == 4 && isValidTimeFormat(time) && isValidTimeFormat(startTime) && isValidTimeFormat(endTime))
        {
            String[] timeParts = time.split(":");

            if (timeParts.length != 3) {
                throw new IllegalArgumentException("Invalid time format: " + time);
            }

            int hours = Integer.parseInt(timeParts[0]);
            int minutes = Integer.parseInt(timeParts[1]);
            int seconds = Integer.parseInt(timeParts[2]);

            int totalSeconds = (hours * 3600) + (minutes * 60) + seconds;
            Date startDate = (Date.valueOf(startDateTF.getValue()));
            startDate.setTime(Long.parseLong(startTimeTF.getText()));
            Date endDate = (Date.valueOf(endDateTF.getValue()));
            endDate.setTime(Long.parseLong(endTimeTF.getText()));
            classExam.setStartDate(startDate);
            classExam.setFinalDate(endDate);
            classExam.setCode(codeTF.getText());
            classExam.setExamTime(Double.parseDouble(examTimeTF.getText()));
            classExam.setExamForm(ExamFormsTV.getSelectionModel().getSelectedItem());
            classExam.setExamTime(totalSeconds);
            Message message = new Message(1, "Add New Class Exam");
            message.setData(classExam);
            SimpleClient.getClient().sendToServer(message);
        }
        else
        {
            JOptionPane.showMessageDialog(null, "Please make sure all field are filled properly", "Invalid Input", JOptionPane.WARNING_MESSAGE);
        }
    }

    @Subscribe
    public void displayExamForms(ExamMessageEvent event)
    {
        List<ExamForm> exams = event.getExamForms();
        if(exams != null && exams.isEmpty())
        {
            ExamFormsTV.getItems().addAll(exams);
            ExamFormsTV.setDisable(false);
        }
        else
        {
            JOptionPane.showMessageDialog(null, "There are no exam forms in this course, please create some", "Database Error", JOptionPane.WARNING_MESSAGE);
        }
    }
    @Subscribe
    public void displaySubjects(SubjectsOfTeacherMessageEvent event){
        List<Subject> subjects = event.getSubjects();
        if(subjects != null && !subjects.isEmpty())
        {
            subjectCB.getItems().addAll(subjects);

        }
        else
        {
            JOptionPane.showMessageDialog(null, "You do not teach in any subjects or the database could not retrieve the data", "Database Error", JOptionPane.WARNING_MESSAGE);
        }
    }
    @Subscribe
    public void displayCourses(CoursesOfTeacherEvent event){
       courses = event.getCourses();
       if(courses != null && !courses.isEmpty())
        {
            courseCB.getItems().addAll(courses);
            System.out.println("Courses: " + courses);
        }

    }
    public static boolean isValidTimeFormat(String input) {
        String pattern = "^(?:[01]\\d|2[0-3]):[0-5]\\d:[0-5]\\d$";
        return Pattern.matches(pattern, input);
    }

    @Subscribe
    public void loadExam(LoadExamEvent event)
    {
        classExam = event.getClassExam();
        startDateTF.setValue(classExam.getStartDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate());
        startTimeTF.setText(String.valueOf(classExam.getStartDate().getTime()));
        endDateTF.setValue(classExam.getFinalSubmissionDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate());
        endTimeTF.setText(String.valueOf(classExam.getFinalDate().getTime()));
        codeTF.setText(classExam.getCode());
        double timeInSeconds = classExam.getExamTime();
        int hours = (int) (timeInSeconds / 3600);
        int minutes = (int) ((timeInSeconds % 3600) / 60);
        int seconds = (int) (timeInSeconds % 60);
        examTimeTF.setText(String.format("%02d:%02d:%02d", hours, minutes, seconds));
        ExamFormsTV.getSelectionModel().select(classExam.getExamForm());
        classExam = new ClassExam();
    }
}

