import Entities.*;
import Events.*;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.ComboBox;
import javafx.event.ActionEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.util.Callback;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import javax.persistence.criteria.CriteriaBuilder;
import java.io.IOException;
import java.text.SimpleDateFormat;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

public class ShowStatsAndReportsController extends SaveBeforeExit {

    @FXML
    private TableView<ClassExam> ClassExamStatsTv;

    @FXML
    private TableColumn<ClassExam, String> DateCol;

    @FXML
    private TableColumn<ClassExam, String> ExamIDCol;

    @FXML
    private TableColumn<ClassExam, String> ExamineeCol;

    @FXML
    private TableColumn<ClassExam, String> MeanCol;

    @FXML
    private TableColumn<ClassExam, String> PassedCol;

    @FXML
    private TableColumn<ClassExam, String> StandardDeviationCol;

    @FXML
    private TableColumn<ClassExam, String> TesterCol;

    @FXML
    private TableColumn<ClassExam, String> HistogramCol;

    @FXML
    // The combo box that chooses which type of exam filtering will be used: By teacher, by course or by student
    private ComboBox<String> chooseReportTypeCombo;

    @FXML
    // The combo box that fills with names of Teachers/Courses/Students, according to the other box's choice
    private ComboBox<String> chooseStatsForCombo;

    Teacher clientTeacher;

    String chosenCriteriaType;

    List<ClassExam> allClassExams = new ArrayList<>();
    List<Teacher> allTeachers = new ArrayList<>();
    List<Course> allCourses = new ArrayList<>();
    List<Student> allStudents = new ArrayList<>();

    Teacher chosenTeacher;
    Course chosenCourse;
    Student chosenStudent;

    // TODO use this function when it'll be moved from server to entities
    private static Date ConvertToDate(LocalDateTime localDate) {
        ZoneId zoneId = ZoneId.systemDefault();
        ZonedDateTime zonedDateTime = localDate.atZone(zoneId);
        return Date.from(zonedDateTime.toInstant());
    }

    @Subscribe
    // The function that receives the event that has ALL the class exams in it, and then populates all the lists we need.
    public void receiveDataFromServer(LiveExamsEvent event)
    {
        System.out.println("Attempting to receive class exams from the server in the stats screen.");
        allClassExams = event.getLiveExams();
        // This is the part where we try to filter out any exams that aren't over yet by now.
        Date currentTime = ConvertToDate(LocalDateTime.now());
        allClassExams = allClassExams.stream().filter(classExam ->
                classExam.getFinalDate().after(currentTime)).collect(Collectors.toList());

        // It's population time... via a loop.
        for (int i = 0; i < allClassExams.size(); i++)
        {
            Teacher tempTeacher = allClassExams.get(i).getTeacher();
            if (!(allTeachers.contains(tempTeacher)))
                allTeachers.add(tempTeacher);
            Course tempCourse = allClassExams.get(i).getCourse();
            if (!(allCourses.contains(tempCourse)))
                allCourses.add(tempCourse);

            // For this... I'm REALLY sorry :(
            for (StudentExam tempStudentExam : allClassExams.get(i).getStudentExams())
            {
                if (!(allStudents.contains(tempStudentExam.getStudent())))
                    allStudents.add(tempStudentExam.getStudent());
            }
        }

        // Only now, when we got the exams and lists populated can we safely enable the first combo box.
        chooseReportTypeCombo.setDisable(false);
    }

    @FXML
    // The selected string should represent the type of filtering. We'll need a 2nd combo box before we can get the list.
    void reportTypeChosen(ActionEvent event) {
        // Get the chosen criteria in a string.
        chosenCriteriaType = chooseReportTypeCombo.getValue();
        // With this we should now initialize the second combo box appropriately.
        initializeSecondComboBox(chosenCriteriaType);

    }

    // The initialization is called exclusively by making a choice to filter reports by which criteria type
    void initializeSecondComboBox(String whichCriteriaType)
    {
        // First of all, delete any existing items in the combo box, we're gonna overwrite anyway.
        chooseStatsForCombo.getItems().clear();

        switch(whichCriteriaType)
        {
            case "Filter by Teacher":
                chooseStatsForCombo.getItems().addAll(allTeachers.stream().map(teacher ->
                        teacher.getFullName()).collect(Collectors.toList()));
                break;

            case "Filter by Course":
                chooseStatsForCombo.getItems().addAll(allCourses.stream().map(course ->
                        course.getName()).collect(Collectors.toList()));
                break;

            case "Filter by Student":
                chooseStatsForCombo.getItems().addAll(allStudents.stream().map(student ->
                        student.getFullName()).collect(Collectors.toList()));
                break;
        }
        // Enable the combo box, the choice can now be made.
        chooseStatsForCombo.setDisable(false);
    }

    @FXML
    // The selected string should represent the name of the filter, be it Teacher, Course or Student.
    void reportNameChosen(ActionEvent event) {
        // TODO Code that, according to first comboBox's choice, chooses the correct filter and then initialized the stats table.
    }

    private String FormatDate(Date date)
    {
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        return formatter.format(date);
    }

    private BarChart GetHistogram(List<Integer> grades)
    {
        //System.out.println("in hist func");
        // Create a CategoryAxis for the X-axis
        CategoryAxis xAxis = new CategoryAxis();

        // Create a NumberAxis for the Y-axis
        NumberAxis yAxis = new NumberAxis();

        BarChart<String, Number> gradeHistogramChart = new BarChart<>(xAxis, yAxis);

        // Create a Series for the grade histogram data
        XYChart.Series<String, Number> series = new XYChart.Series<>();

        // Iterate through the grade buckets (0 to 100 with a range of 10)
        for (int i = 0; i < 100; i += 10) {
            int bucketStart = i;
            int bucketEnd = i + 10;
            // Create a data point for the bucket
            int bucketSize = 0;
            for (Integer grade : grades)
            {
                if (grade < bucketEnd && grade >= bucketStart)
                    bucketSize += 1;

                if (i == 90 && grade == 100)
                {
                    bucketSize += 1;
                }
            }

            XYChart.Data<String, Number> dataPoint = new XYChart.Data<>(
                    bucketStart + "-" + bucketEnd, bucketSize);

            // Add the data point to the series
            series.getData().add(dataPoint);
        }

        // Add the Series to the BarChart
        gradeHistogramChart.getData().add(series);

        return gradeHistogramChart;
    }

    @FXML
    void initialize() throws IOException {
        System.out.println("Attempting to initialize ShowStatsAndReports.");
        // Until we get all the class exams from the server, disable even the first combo box.
        chooseReportTypeCombo.setDisable(true);
        // Disable the second combo box on startup, as it requires the first's choice.
        chooseStatsForCombo.setDisable(true);

        EventBus.getDefault().register(this);
        SimpleClient.getClient().sendToServer(new Message(0, "Retrieve all class exams"));

        // Populate the first combo box with the big three choices.
        chooseReportTypeCombo.getItems().addAll("Filter by Teacher", "Filter by Course", "Filter by Student");

        // TODO Have the table populate according to the chosen criteria from the combo boxes.
        // populateTable();

    }

    // This function will populate the table with the exams and their stats.
    void populateTable() throws IOException {
        // Extract teacher entity to controller
        clientTeacher = (Teacher)SimpleClient.getClient().getUser();

        List<ExamForm> examFormList = clientTeacher.getExamForm();
        List<ClassExam> classExamList = new ArrayList<>();

        // gets all the class exam the teacher belongs to the exam forms the teacher created
        for (ExamForm examForm : examFormList)
        {
            for (ClassExam classExam : examForm.getClassExam())
            {
                classExamList.add(classExam);
            }
        }


        if (examFormList.isEmpty())
        {
            System.out.println("The teacher " + clientTeacher.getFullName() + " Didn't create any exam");
            return;
        }
        if (classExamList.isEmpty())
        {
            System.out.println("The teacher " + clientTeacher.getFullName() + " Didn't tested any exam");
            return;
        }

        // define column insertion mechanics
        ExamIDCol.setCellValueFactory(param -> new SimpleStringProperty(Long.toString(param.getValue().getExamForm().getID())));
        TesterCol.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getTeacher().getFullName()));
        DateCol.setCellValueFactory(param -> new SimpleStringProperty(FormatDate(param.getValue().getStartDate()).toString()));
        ExamineeCol.setCellValueFactory(param -> new SimpleStringProperty(Integer.toString(param.getValue().getStudentExams().size())));
        PassedCol.setCellValueFactory(param -> new SimpleStringProperty(Integer.toString(param.getValue().
                getStudentExams().stream().filter(item->item.getGrade() >= 50).
                collect(Collectors.toList()).size())));
        MeanCol.setCellValueFactory(param -> new SimpleStringProperty(Double.toString(param.getValue().getGradesMean())));
        StandardDeviationCol.setCellValueFactory(param -> new SimpleStringProperty(Double.toString(Math.sqrt(param.getValue().
                getGradesVariance()))));

        // Set cell factory for the action column
        HistogramCol.setCellFactory(generateButtonCellFactory());

        // insert content to table
        ClassExamStatsTv.getItems().addAll(classExamList);

        ExamIDCol.setStyle( "-fx-alignment: CENTER;");
        TesterCol.setStyle( "-fx-alignment: CENTER;");
        DateCol.setStyle( "-fx-alignment: CENTER;");
        ExamineeCol.setStyle( "-fx-alignment: CENTER;");
        PassedCol.setStyle( "-fx-alignment: CENTER;");
        MeanCol.setStyle( "-fx-alignment: CENTER;");
        StandardDeviationCol.setStyle( "-fx-alignment: CENTER;");
        HistogramCol.setStyle( "-fx-alignment: CENTER;");


        ClassExamStatsTv.getSortOrder().add(ExamIDCol);

        ClassExamStatsTv.sort();
    }


    // Generate buttons for histogram column
    private Callback<TableColumn<ClassExam, String>, TableCell<ClassExam, String>> generateButtonCellFactory() {
        return new Callback<>() {
            @Override
            public TableCell<ClassExam, String> call(final TableColumn<ClassExam, String> param) {
                final TableCell<ClassExam, String> cell = new TableCell<>() {
                    private final Button button = new Button("Click");

                    {
                        button.setOnAction(event ->
                        {
                            Stage histStage = new Stage();
                            AnchorPane root = new AnchorPane();
                            Scene histScene = new Scene(root, 400, 400);
                            List<StudentExam> exams = getTableView().getItems().get(getIndex()).getStudentExams();
                            System.out.println("number of exams: " + exams.size());
                            List<Integer> grades = exams.stream().map(exam -> exam.getGrade()).collect(Collectors.toList());
                            System.out.println("number of grades: " + grades.size());
                            root.getChildren().add(GetHistogram(grades));
                            histStage.setScene(histScene);

                            histStage.showAndWait();

                        });
                    }

                    @Override
                    protected void updateItem(String item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setGraphic(null);
                        } else {
                            setGraphic(button);
                        }
                    }
                };
                return cell;
            }
        };
    }

}
