package Client.Controllers.MainViews.StaffViews.TeacherViews;

import Client.Controllers.MainViews.SaveBeforeExit;
import Client.Controllers.MainViews.StaffViews.ShowStatisticsController;
import Client.Events.ClassExamGradeEvent;
import Client.Events.StudentExamEvent;
import Client.SimpleChatClient;
import Client.SimpleClient;
import Entities.Enums;
import Entities.SchoolOwned.ClassExam;
import Entities.SchoolOwned.ExamForm;
import Entities.SchoolOwned.Course;
import Entities.SchoolOwned.Subject;
import Entities.StudentOwned.StudentExam;
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
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class TeacherExamGradeController extends SaveBeforeExit {


    // Class Exam Table
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

    // Exam Form Table
    @FXML
    private TableView<ClassExam> ExamFormTv;
    @FXML
    private TableColumn<ClassExam, String> StartDateColumn;
    @FXML
    private TableColumn<ClassExam, String> EndDateColumn;
    @FXML
    private TableColumn<ClassExam, String> ExamineeNumberColumn;
    @FXML
    private TableColumn<ClassExam, String> ToCheckColumn;


    // Combos
    @FXML
    private ComboBox<String> SubjectCombo;
    @FXML
    private ComboBox<String> CourseCombo;
    @FXML
    private ComboBox<String> ExamIDCombo;


    Teacher clientTeacher;
    String chosenCourseStr;
    String chosenExamFormIDStr;
    String chosenSubjectStr;
    ClassExam chosenExam;
    boolean initDone = false;


    @FXML
    void ClassExamTvClicked(MouseEvent event) throws IOException {
        if(event.getClickCount() == 2)
        {
            if(ClassExamTv.getSelectionModel().getSelectedItem() != null) {
                SimpleChatClient.setRoot("TeacherGradeStudentExam");
                EventBus.getDefault().post(new StudentExamEvent(ClassExamTv.getSelectionModel().getSelectedItem()));
                EventBus.getDefault().unregister(this);
            }
            else
            {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error!");
                alert.setHeaderText("Error: No Student Was Chosen");
                alert.show();
            }
        }
    }

    @FXML
    void ExamIDComboAct(ActionEvent event) {
        chosenExamFormIDStr = ExamIDCombo.getSelectionModel().getSelectedItem();
        if (chosenExamFormIDStr != null)
            SetExamFormTv();
    }

    void SetExamFormTv()
    {
        ExamFormTv.getItems().clear();
        List<ClassExam> selectedClassExams = new ArrayList<>();
        List<ClassExam> classExams = clientTeacher.getClassExam();

        // Fill table with the class exam which used the selected exam form
        for(ClassExam classExam : classExams)
        {
            if(classExam.getExamForm().getExamFormID().startsWith(chosenExamFormIDStr))
            {
                selectedClassExams.add(classExam);
            }
        }


        ExamFormTv.getItems().addAll(selectedClassExams);
        ExamFormTv.sort();

    }

    @FXML
    void ExamFormTvClicked(MouseEvent event)
    {
        if(event.getClickCount() == 2)
        {
            chosenExam = ExamFormTv.getSelectionModel().getSelectedItem();
            if (chosenExam != null)
                SetClassExamTv();
        }
    }

    void SetClassExamTv()
    {
        ClassExamTv.getItems().clear();


        // Loading the data to the table
        if(chosenExam != null)
        {
            ClassExamTv.getItems().addAll(chosenExam.getStudentExams().stream().filter(studentExam ->
                    studentExam.getStatus() != Enums.submissionStatus.NotTaken).collect(Collectors.toList()));
            ClassExamTv.sort();
        }
    }

    @FXML
    void CourseComboAct(ActionEvent event)
    {
        chosenCourseStr = CourseCombo.getSelectionModel().getSelectedItem();
        if (chosenCourseStr != null)
            SetExamIDCombo();
    }

    void SetExamIDCombo()
    {
        ClassExamTv.getItems().clear();
        ExamIDCombo.getItems().clear();

        // select Exam
        // collect the courses of the subject
        Date currentTime = Date.from((LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant()));
        List<ClassExam> teacherExams = clientTeacher.getClassExam();

        teacherExams = teacherExams.stream().filter(classExam ->
                currentTime.after(classExam.getFinalSubmissionDate()) && classExam.getGradesMean() != -1).collect(Collectors.toList());

        List<ExamForm> selectedExamForms = new ArrayList<>();
        for(ClassExam classExam : teacherExams)
        {
            ExamForm examForm = classExam.getExamForm();
            if(examForm.getCourse().getName().startsWith(chosenCourseStr) &&
            !selectedExamForms.contains(examForm))
            {
                selectedExamForms.add(examForm);
            }
        }

        if (selectedExamForms.isEmpty())
            return;

        // Sort Combo
        Collections.sort(selectedExamForms, Comparator.comparing(examForm -> examForm.getExamFormID()));
        ExamIDCombo.getItems().addAll(selectedExamForms.stream().map(examForm ->
                examForm.getExamFormID()).collect(Collectors.toList()));

    }

    @FXML
    void SubjectComboAct(ActionEvent event) {
        chosenSubjectStr = SubjectCombo.getSelectionModel().getSelectedItem();
        SetCourseCombo();
    }

    void SetCourseCombo()
    {
        if(!ClassExamTv.getItems().isEmpty()) {
            ClassExamTv.getItems().clear();
        }
        if(!ExamIDCombo.getItems().isEmpty()) {
            ExamIDCombo.getItems().clear();
        }
        if(!CourseCombo.getItems().isEmpty()) {
            CourseCombo.getItems().clear();
        }



        // collect the courses of the subject
        List<Course> teacherCourses = clientTeacher.getCourses();
        List<Course> subjectCourses = teacherCourses.stream().filter(item-> item.getSubject().getName() == chosenSubjectStr)
                .collect(Collectors.toList());

        // Sort Combo
        Collections.sort(subjectCourses, Comparator.comparing(Course::getName));
        for (int i = 0; i < subjectCourses.size(); i++)
        {
            CourseCombo.getItems().add(subjectCourses.get(i).getName());
        }
    }


    @Subscribe
    public void ReturnFromStudentGrade(ClassExamGradeEvent event) {
        // reselect previous items
        Platform.runLater(() -> {
            // wait until the window is initialized
            while (!initDone){
                try {
                    TimeUnit.MILLISECONDS.sleep(50);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
            SubjectCombo.getSelectionModel().select(event.getSubjectStr());
            CourseCombo.getSelectionModel().select(event.getCourseStr());
            ExamIDCombo.getSelectionModel().select(event.getExamIDStr());
            ExamFormTv.getSelectionModel().select(ExamFormTv.getItems().stream().filter(classExam ->
                    classExam.getID() == event.getExamFormID()).collect(Collectors.toList()).get(0));
            chosenExam = ExamFormTv.getSelectionModel().getSelectedItem();
            SetClassExamTv();
        });
    }

    @FXML
    void initialize() throws IOException {
        EventBus.getDefault().register(this);


        // Get teacher courses and return if the teacher is not assigned to any course
        clientTeacher = (Teacher) SimpleClient.getClient().getUser();
        List<Course> teacherCourses = clientTeacher.getCourses();
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

        // Fill subject combo
        for (int i = 0; i < teacherSubjects.size(); i++)
        {
            SubjectCombo.getItems().add(teacherSubjects.get(i).getName());
        }


        // Exam Form config
        StartDateColumn.setCellValueFactory(exam ->
                new SimpleStringProperty(ShowStatisticsController.FormatDate(exam.getValue().getStartDate())));
        EndDateColumn.setCellValueFactory(exam ->
                new SimpleStringProperty(ShowStatisticsController.FormatDate(exam.getValue().getFinalSubmissionDate())));
        ExamineeNumberColumn.setCellValueFactory(exam ->
                new SimpleStringProperty(Integer.toString(exam.getValue().getStudentExams().size())));
        ToCheckColumn.setCellValueFactory(exam ->
                new SimpleStringProperty(Integer.toString(exam.getValue().getExamToEvaluate())));
        ExamFormTv.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);


        StartDateColumn.setStyle( "-fx-alignment: CENTER;");
        EndDateColumn.setStyle( "-fx-alignment: CENTER;");
        ExamineeNumberColumn.setStyle( "-fx-alignment: CENTER;");
        ToCheckColumn.setStyle( "-fx-alignment: CENTER;");
        ExamFormTv.setStyle( "-fx-alignment: CENTER;");


        // Class Exam config
        NameColumn.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getStudent().getFullName()));
        IDColumn.setCellValueFactory(param -> new SimpleStringProperty(Long.toString(param.getValue().getStudent().getID())));
        GradeColumn.setCellValueFactory(new PropertyValueFactory<>("grade"));
        StatusColumn.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().TranslateStatus()));
        ClassExamTv.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);


        NameColumn.setStyle( "-fx-alignment: CENTER;");
        IDColumn.setStyle( "-fx-alignment: CENTER;");
        GradeColumn.setStyle( "-fx-alignment: CENTER;");
        StatusColumn.setStyle( "-fx-alignment: CENTER;");
        ClassExamTv.setStyle( "-fx-alignment: CENTER;");


        // Initialize sort mechanic
        StatusColumn.setComparator(StatusColumn.getComparator().reversed());
        ClassExamTv.getSortOrder().add(StatusColumn);
        ExamFormTv.getSortOrder().add(StartDateColumn);

        initDone = true;
    }

}
