package Client.Controllers.MainViews.StaffViews.TeacherViews;

import Client.Controllers.MainViews.SaveBeforeExit;
import Client.Events.*;
import Client.SimpleChatClient;
import Client.SimpleClient;
import Entities.Communication.Message;
import Entities.Enums;
import Entities.SchoolOwned.ClassExam;
import Entities.SchoolOwned.Course;
import Entities.SchoolOwned.ExamForm;
import Entities.SchoolOwned.Subject;
import Entities.Users.Teacher;
import Events.ExamSavedEvent;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.io.IOException;

import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;
import java.util.regex.Pattern;

public class TeacherCreateClassExamController extends SaveBeforeExit {
    @FXML
    private DatePicker endDateTF;

    @FXML
    private ComboBox<Enums.ExamType> typeCB;

    @FXML
    private TableView<ExamForm> ExamFormsTV;
    @FXML
    private TableColumn<?, ?> dateCreatedColumn;
    @FXML
    private TableColumn<?, ?> lastUsedColumn;
    @FXML
    private TableColumn<?, ?> teacherNotesColumn;
    @FXML
    private TableColumn<?, ?> IDColumn;

    @FXML
    private TableColumn<?, ?> codeColumn;

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
    @FXML
    private TableColumn<?,?> subjectColumn;
    @FXML
    private TableColumn<?,?> courseColumn;


    private List<Course> courses;
    private ClassExam classExam;
    private List<Subject> subjects;



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
        if(!EventBus.getDefault().isRegistered(this))
            EventBus.getDefault().register(this);
        ExamFormsTV.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        //IDColumn.setCellValueFactory(new PropertyValueFactory<>("ID"));
        IDColumn.setCellValueFactory(new PropertyValueFactory<>("examFormID"));
        lastUsedColumn.setCellValueFactory(new PropertyValueFactory<>("lastUsed"));
        teacherNotesColumn.setCellValueFactory(new PropertyValueFactory<>("examNotesForTeacher"));
        dateCreatedColumn.setCellValueFactory(new PropertyValueFactory<>("dateCreated"));
        subjectColumn.setCellValueFactory(new PropertyValueFactory<>("subject"));
        courseColumn.setCellValueFactory(new PropertyValueFactory<>("course"));
        typeCB.getItems().addAll(Enums.ExamType.Automatic, Enums.ExamType.Manual);
        courseCB.setDisable(true);
        ExamFormsTV.setDisable(true);
        startDateTF.setDisable(true);
        startTimeTF.setDisable(true);
        endTimeTF.setDisable(true);
        endDateTF.setDisable(true);
        codeTF.setDisable(true);
        examTimeTF.setDisable(true);
        typeCB.setDisable(true);
        saveExamButton.setDisable(true);
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
        courseCB.getItems().clear();
        if(courses != null && !courses.isEmpty())
        {

            for(Course course: courses) {
                System.out.println("Course: " + course.getName() + " Subject: " + course.getSubject().getName() + " selectedSubject: " + selectedSubject.getName() + " equals: " + course.getSubject().getName().equals(selectedSubject.getName()));
                if(course.getSubject().getName().equals(selectedSubject.getName())) {
                    System.out.println("Course: " + course.getName() + " added to courseCB");
                    courseCB.getItems().add(course);
                }
            }
            courseCB.setDisable(false);

        }
        else
        {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("No Courses");
            alert.setHeaderText("No Courses Found");
            alert.setContentText("No Courses Found For Subject: " + selectedSubject.getName());
            alert.showAndWait();
            //JOptionPane.showMessageDialog(null, "You do not teach in any subjects or the database could not retrieve the data", "Database Error", JOptionPane.WARNING_MESSAGE);
        }
        classExam.setSubject(subjectCB.getSelectionModel().getSelectedItem());

    }

    @FXML
    public void onCourseSelection() throws IOException {
        Message message = new Message(1, "Get Exam Forms For Course: " + courseCB.getSelectionModel().getSelectedItem().getName());
        message.setData(courseCB.getSelectionModel().getSelectedItem());
        SimpleClient.getClient().sendToServer(message);
    }
    @FXML
    public void onExamFormChosen()
    {
        try {
            ExamForm examForm = ExamFormsTV.getSelectionModel().getSelectedItem();
            classExam.setSubject(examForm.getSubject());
            classExam.setCourse(examForm.getCourse());
        }
        catch (Exception e)
        {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("No Exam Form");
            alert.setHeaderText("No Exam Form Selected");
            alert.setContentText("No Exam Form Selected");
            alert.showAndWait();
            //JOptionPane.showMessageDialog(null, "You did not choose an exam form", "Error", JOptionPane.WARNING_MESSAGE);
        }
    }
    @FXML
    public void onSaveExam() throws IOException {
        String time =examTimeTF.getText();
        String startTime =examTimeTF.getText();
        String endTime =examTimeTF.getText();
        String code = codeTF.getText();
        if( code.length() == 4 && isValidTimeFormat(time) &&
                isValidTimeFormat(startTime) &&
                isValidTimeFormat(endTime) &&
                typeCB.getSelectionModel().getSelectedItem() != null &&
                ExamFormsTV.getSelectionModel().getSelectedItem() != null &&
                startDateTF.getValue() != null &&
                endDateTF.getValue() != null &&
                courseCB.getSelectionModel().getSelectedItem() != null &&
                subjectCB.getSelectionModel().getSelectedItem() != null &&
                (endDateTF.getValue().isAfter(startDateTF.getValue())||
                        ((endDateTF.getValue().equals(startDateTF.getValue())) &&
                                timeToDouble(endTimeTF.getText())> timeToDouble(startTimeTF.getText()))))
        {


            Date startDate = (Date.from(startDateTF.getValue().atStartOfDay(ZoneId.systemDefault()).toInstant()));
            long startTiming = ((long)timeToDouble(startTimeTF.getText())*1000) + startDate.getTime();

            Date endDate = (Date.from(endDateTF.getValue().atStartOfDay(ZoneId.systemDefault()).toInstant()));
            long endTiming = ((long)timeToDouble(endTimeTF.getText())*1000) + endDate.getTime();


            startDate.setTime(startTiming);
            endDate.setTime(endTiming);

            classExam.setStartDate(startDate);
            classExam.setFinalSubmissionDate(endDate);
            classExam.setAccessCode(codeTF.getText());
            classExam.setExamTime((timeToDouble(examTimeTF.getText()))/60);
            classExam.setExamForm(ExamFormsTV.getSelectionModel().getSelectedItem());
            classExam.setExamType(typeCB.getSelectionModel().getSelectedItem());
            classExam.setTeacher((Teacher)SimpleClient.getUser());
            classExam.setCourse(courseCB.getValue());
            Message message = new Message(1, "Add New Class Exam");
            message.setData(classExam);
            SimpleClient.getClient().sendToServer(message);
        }
        else
        {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Some fields are not filled properly");
            alert.setHeaderText("Some fields are not filled properly");
            alert.setContentText("Please make sure all field are filled properly");
            alert.showAndWait();
            System.out.println("Please make sure all field are filled properly");
            System.out.println("code length: " + code.length());
            System.out.println("time format: " + isValidTimeFormat(time));
            System.out.println("start time format: " + isValidTimeFormat(startTime));
            System.out.println("end time format: " + isValidTimeFormat(endTime));
            System.out.println("type: " + typeCB.getSelectionModel().getSelectedItem());
            System.out.println("exam form: " + ExamFormsTV.getSelectionModel().getSelectedItem());
            System.out.println("start date: " + startDateTF.getValue());
            System.out.println("end date: " + endDateTF.getValue());
            System.out.println("start date is before end date: " + startDateTF.getValue().isBefore(endDateTF.getValue()));
            System.out.println("start date is after today: " + startDateTF.getValue().isAfter(LocalDate.now()));
            System.out.println("end date is after today: " + endDateTF.getValue().isAfter(LocalDate.now()));
           // JOptionPane.showMessageDialog(null, "Please make sure all field are filled properly", "Invalid Input", JOptionPane.WARNING_MESSAGE);

        }
    }

    @Subscribe
    public void displayExamForms(ExamMessageEvent event)
    {
        List<ExamForm> exams = event.getExamForms();
        System.out.println("Num of exams: " + exams.size() + " " + exams);
        if(exams != null && !exams.isEmpty())
        {
            ExamFormsTV.getItems().addAll(exams);
            ExamFormsTV.setDisable(false);
            startDateTF.setDisable(false);
            startTimeTF.setDisable(false);
        }
        else
        {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("No Exam Forms");
            alert.setHeaderText("No Exam Forms Found");
            alert.setContentText("No Exam Forms Found For Course: " + courseCB.getSelectionModel().getSelectedItem().getName());
            alert.showAndWait();
            //JOptionPane.showMessageDialog(null, "There are no exam forms in this course, please create some", "Database Error", JOptionPane.WARNING_MESSAGE);
        }
    }
    @Subscribe
    public void displaySubjects(SubjectsOfTeacherMessageEvent event) throws IOException {
         subjects = event.getSubjects();
        if(subjects != null && !subjects.isEmpty())
        {
            subjectCB.getItems().addAll(subjects);

        }
        else
        {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("No Subjects");
            alert.setHeaderText("No Subjects Found");
            alert.setContentText("No Subjects Found For Teacher: " + SimpleClient.getClient().getUser().getFullName());
            alert.showAndWait();
            //JOptionPane.showMessageDialog(null, "You do not teach in any subjects or the database could not retrieve the data", "Database Error", JOptionPane.WARNING_MESSAGE);
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
        try {
            System.out.println("Loading existing Exam");
            ExamFormsTV.setDisable(false);
            examTimeTF.setDisable(false);
            typeCB.setDisable(false);
            codeTF.setDisable(false);
            startDateTF.setDisable(false);
            endDateTF.setDisable(false);
            startTimeTF.setDisable(false);
            endTimeTF.setDisable(false);
            courseCB.setDisable(false);
            subjectCB.setValue(event.getClassExam().getSubject());
            courseCB.setValue(event.getClassExam().getCourse());
            ExamForm examForm = event.getClassExam().getExamForm();
            ExamFormsTV.getItems().clear();
            ExamFormsTV.getItems().add(examForm);
            classExam = event.getClassExam();
            saveExamButton.setDisable(false);
            Date date = new Date(classExam.getStartDate().getTime() * 1000);
            // Create a SimpleDateFormat object to format the date
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
            // Format the date to HH:mm:ss format
            String formattedTime = sdf.format(date);
            startTimeTF.setText(formattedTime);
            date = new Date(classExam.getFinalSubmissionDate().getTime() * 1000);
            formattedTime = sdf.format(date);
            endTimeTF.setText(formattedTime);
            codeTF.setText(classExam.getAccessCode());
            typeCB.getSelectionModel().select(classExam.getExamType());
            double timeInSeconds = classExam.getExamTime() * 60 ;
            int hours = (int) (timeInSeconds / 3600);
            int minutes = (int) ((timeInSeconds % 3600) / 60);
            int seconds = (int) (timeInSeconds % 60);
            examTimeTF.setText(String.format("%02d:%02d:%02d", hours, minutes, seconds));
            Thread.sleep(1000);
            ExamFormsTV.getSelectionModel().select(classExam.getExamForm());
            classExam = new ClassExam();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
    private double timeToDouble(String time) {
        String[] timeParts = time.split(":");

        if (timeParts.length != 3) {
            throw new IllegalArgumentException("Invalid time format: " + time);
        }

        int hours = Integer.parseInt(timeParts[0]);
        int minutes = Integer.parseInt(timeParts[1]);
        int seconds = Integer.parseInt(timeParts[2]);

        return (hours * 3600) + (minutes * 60) + seconds;
    }
    @Subscribe
    public void examSaved(ExamSavedEvent event) throws IOException {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Exam Saved");
            alert.setHeaderText("Exam Saved Successfully");
            alert.setContentText("Exam Saved Successfully");
            alert.showAndWait();
            try {
                SimpleChatClient.setRoot("TeacherMainScreen");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });

    }
    @FXML
    private void requestExisting() throws IOException {
        EventBus.getDefault().post(new ChooseExamEvent());
        SimpleChatClient.setRoot("TeacherViewLiveExams");
    }
    @FXML
    public void onStartDateSelection()
    {
        if(/*startDateTF.getValue().isAfter(LocalDate.now())*/ startDateTF.getValue() != null)
        {
            endDateTF.setDisable(false);
            endTimeTF.setDisable(false);
        }
        else
        {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Invalid Input");
            alert.setHeaderText("Invalid Input");
            alert.setContentText("Please select a date after today");
            alert.showAndWait();
            //JOptionPane.showMessageDialog(null, "Please select a date after today", "Invalid Input", JOptionPane.WARNING_MESSAGE);
            startDateTF.setValue(null);
        }
    }

    @FXML
    public void onEndDateSelection()
    {
            codeTF.setDisable(false);
            examTimeTF.setDisable(false);
            typeCB.setDisable(false);
    }
    @FXML
    public void onTypeSelection()
    {
        if(typeCB.getSelectionModel().getSelectedItem() != null)
        {
            saveExamButton.setDisable(false);
        }
        else {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Invalid Input");
            alert.setHeaderText("Invalid Input");
            alert.setContentText("Please select an exam type");
            alert.showAndWait();
            //JOptionPane.showMessageDialog(null, "Please select an exam type", "Invalid Input", JOptionPane.WARNING_MESSAGE);
        }
    }
}

