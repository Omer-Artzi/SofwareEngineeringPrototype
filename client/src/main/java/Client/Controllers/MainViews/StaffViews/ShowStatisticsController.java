package Client.Controllers.MainViews.StaffViews;

import Client.Controllers.MainViews.SaveBeforeExit;
import Client.Events.ChangeMainSceneEvent;
import Client.Events.GeneralEvent;
import Client.Events.LiveExamsEvent;
import Client.SimpleChatClient;
import Client.SimpleClient;
import Entities.Enums;
import Entities.SchoolOwned.ClassExam;
import Entities.Communication.Message;
import Entities.SchoolOwned.ExamForm;
import Entities.SchoolOwned.Course;
import Entities.StudentOwned.StudentExam;
import Entities.Users.Student;
import Entities.Users.Teacher;
import javafx.beans.property.SimpleStringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Callback;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.stream.Collectors;

public class ShowStatisticsController extends SaveBeforeExit {
    @FXML
    private AnchorPane controllerRoot;
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

    // Student table attributes

    @FXML
    private TableView<StudentExam> StExamStatsTv;

    @FXML
    private TableColumn<StudentExam, String> StDateCol;

    @FXML
    private TableColumn<StudentExam, String> StExamIDCol;

    @FXML
    private TableColumn<StudentExam, String> StGradeCol;

    @FXML
    private TableColumn<StudentExam, String> StPassedCol;

    @FXML
    private TableColumn<StudentExam, String> StTesterCol;

    @FXML
    // The combo box that chooses which type of exam filtering will be used: By teacher, by course or by student
    private ComboBox<String> chooseReportTypeCombo;

    @FXML
    // The combo box that fills with names of Teachers/Courses/Students, according to the other box's choice
    private ComboBox<String> chooseStatsForCombo;
    @FXML
    private AnchorPane SummaryRoot;
    @FXML
    private ScrollPane BarScrollPane;
    @FXML
    private Label TitleLabel;
    @FXML
    private Label MeanLabel;

    @FXML
    private Label SDLabel;

    @FXML
    private Label MedianLabel;

    @FXML
    private VBox TablesVBOX;

    Stage histStage;
    Teacher clientTeacher;
    String chosenCriteriaType;
    String chosenReport;

    List<ClassExam> allClassExams = new ArrayList<>();
    List<Teacher> allTeachers = new ArrayList<>();
    List<Course> allCourses = new ArrayList<>();
    List<Student> allStudents = new ArrayList<>();
    private boolean HistType;


    // TODO: move it to global utils file
    // Recursive function which disable all the Tree of nodes
    public static void DisableAllNodes(Node node, boolean toDisable) {
        node.setDisable(toDisable);
        if (node instanceof Parent) {
            Parent parent = (Parent) node;
            for (Node child : parent.getChildrenUnmodifiable()) {
                DisableAllNodes(child, toDisable);
            }
        }
    }

    public static void SetVisibleAllNodes(Node node, boolean toSetVisible) {
        try {
            node.setVisible(toSetVisible);
            if (node instanceof Parent) {
                Parent parent = (Parent) node;
                for (Node child : parent.getChildrenUnmodifiable()) {
                    SetVisibleAllNodes(child, toSetVisible);
                }
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public static String FormatDate(Date date)
    {
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        return formatter.format(date);
    }

    public static String FormatDouble(Double number)
    {
        Formatter formatter = new Formatter();
        formatter.format("%.2f", number);
        return formatter.toString();
    }


    // TODO use this function when it'll be moved from server to entities
    private static Date ConvertToDate(LocalDateTime localDate) {
        ZoneId zoneId = ZoneId.systemDefault();
        ZonedDateTime zonedDateTime = localDate.atZone(zoneId);
        return Date.from(zonedDateTime.toInstant());
    }

    public static double FindMedian(List<Double> numbers) {
        // Sort the list in ascending order
        Collections.sort(numbers);

        int size = numbers.size();
        int middleIndex = size / 2;

        if (size % 2 == 1) {
            // List size is odd
            return numbers.get(middleIndex);
        } else {
            // List size is even
            double middleElement1 = numbers.get(middleIndex - 1);
            double middleElement2 = numbers.get(middleIndex);
            return (middleElement1 + middleElement2) / 2.0;
        }
    }

    // TODO move it to utils files, and maybe create class for the return value
    public static double[] CalculateStats(List<Double> grades) {
        double mean = 0.0;
        double variance = 0.0;

        int approvedExamsNum = grades.size();

        // Mean calculation
        for (Double grade : grades) {
            mean += grade;
        }
        mean /= approvedExamsNum;

        // variance calculation
        if (approvedExamsNum != 1)
        {
            for (Double grade : grades) {
                variance += Math.pow((grade - mean), 2);
            }
            variance = variance / (approvedExamsNum-1);
        }
        double[] returnVals = {mean, variance};

        return returnVals;
    }

    @Subscribe
    // The function that receives the event that has ALL the class exams in it, and then populates all the lists we need.
    public void receiveDataFromServer(LiveExamsEvent event)
    {
        allClassExams = event.getLiveExams();
        // This is the part where we try to filter out any exams that aren't over yet by now.
        Date currentTime = ConvertToDate(LocalDateTime.now());
        allClassExams = allClassExams.stream().filter(classExam ->
                currentTime.after(classExam.getFinalSubmissionDate()) && classExam.getGradesMean() != -1).collect(Collectors.toList());
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
                if (!(allStudents.contains(tempStudentExam.getStudent()))
                        && tempStudentExam.getStatus() == Enums.submissionStatus.Approved)
                    allStudents.add(tempStudentExam.getStudent());
            }
        }

        // Only now, when we got the exams and lists populated can we safely enable the first combo box.
        chooseReportTypeCombo.setDisable(false);
    }


    @FXML
    void ChangeHistType(ActionEvent event) {
        HistType = !HistType;
        setSecondCombo();
    }

    @FXML
    // The selected string should represent the type of filtering. We'll need a 2nd combo box before we can get the list.
    void reportTypeChosen(ActionEvent event) {
        // Get the chosen criteria in a string.
        chosenCriteriaType = chooseReportTypeCombo.getValue();
        // With this we should now initialize the second combo box appropriately.
        initializeSecondComboBox(chosenCriteriaType);
        SetVisibleAllNodes(SummaryRoot, false);

    }

    // The initialization is called exclusively by making a choice to filter reports by which criteria type
    void initializeSecondComboBox(String whichCriteriaType)
    {
        // First of all, delete any existing items in the combo box and tables, we're going to overwrite anyway.
        chooseStatsForCombo.getItems().clear();
        StExamStatsTv.getItems().clear();
        ClassExamStatsTv.getItems().clear();


        switch(whichCriteriaType)
        {
            case "Filter by Teacher":
                chooseStatsForCombo.getItems().addAll(allTeachers.stream().map(teacher ->
                        teacher.getFullName()).collect(Collectors.toList()));
                chooseStatsForCombo.setPromptText("Choose teacher");
                break;

            case "Filter by Course":
                chooseStatsForCombo.getItems().addAll(allCourses.stream().map(course ->
                        course.getName()).collect(Collectors.toList()));
                chooseStatsForCombo.setPromptText("Choose Course");
                break;

            case "Filter by Student":
                chooseStatsForCombo.getItems().addAll(allStudents.stream().map(student ->
                        student.getFullName()).collect(Collectors.toList()));
                chooseStatsForCombo.setPromptText("Choose Student");
                break;
        }
        // Enable the combo box, the choice can now be made.
        chooseStatsForCombo.setDisable(false);
    }


    @FXML
    // The selected string should represent the name of the filter, be it Teacher, Course or Student.
    void reportNameChosen(ActionEvent event) {
        chosenReport = chooseStatsForCombo.getValue();
        if (chosenReport != null)
            setSecondCombo();
    }

    void setSecondCombo()
    {
        // Clear tables before refill
        StExamStatsTv.getItems().clear();
        ClassExamStatsTv.getItems().clear();

        List<ClassExam> classExams;
        List<Double> allGrades = new ArrayList<>();
        List<Date> allDates = new ArrayList<>();
        switch(chosenCriteriaType)
        {
            case "Filter by Teacher":
                classExams = allClassExams.stream().filter(classExam ->
                        classExam.getExamForm().getCreator().getFullName().startsWith(chosenReport)).collect(Collectors.toList());
                ClassExamStatsTv.getItems().addAll(classExams);
                ClassExamStatsTv.sort();
                allGrades = classExams.stream().map(ClassExam::getGradesMean).collect(Collectors.toList());
                allDates = classExams.stream().map(ClassExam::getStartDate).sorted().collect(Collectors.toList());
                ClassExamStatsTv.setVisible(true);
                ClassExamStatsTv.setPrefHeight(300);
                StExamStatsTv.setVisible(false);
                StExamStatsTv.setPrefHeight(0);
                TablesVBOX.getChildren().remove(ClassExamStatsTv);
                TablesVBOX.getChildren().add(0, ClassExamStatsTv);


                break;

            case "Filter by Course":
                classExams = allClassExams.stream().filter(classExam ->
                        classExam.getExamForm().getCourse().getName().startsWith(chosenReport)).collect(Collectors.toList());
                ClassExamStatsTv.getItems().addAll(classExams);
                ClassExamStatsTv.sort();
                allGrades = classExams.stream().map(ClassExam::getGradesMean).collect(Collectors.toList());
                allDates = classExams.stream().map(ClassExam::getStartDate).sorted().collect(Collectors.toList());
                ClassExamStatsTv.setVisible(true);
                ClassExamStatsTv.setPrefHeight(300);
                StExamStatsTv.setVisible(false);
                StExamStatsTv.setPrefHeight(0);
                TablesVBOX.getChildren().remove(ClassExamStatsTv);
                TablesVBOX.getChildren().add(0, ClassExamStatsTv);


                break;

            case "Filter by Student":
                List<StudentExam> studentExams = new ArrayList<>();

                for(ClassExam classExam : allClassExams)
                {
                    for(StudentExam studentExam : classExam.getStudentExams())
                    {
                        if(studentExam.getStudent().getFullName().startsWith(chosenReport)
                        && studentExam.getStatus() == Enums.submissionStatus.Approved)
                        {
                            studentExams.add(studentExam);
                        }
                    }
                }

                StExamStatsTv.getItems().addAll(studentExams);


                allGrades = studentExams.stream().sorted(Comparator.comparing(a -> a.getClassExam().getStartDate()))
                        .map(studentExam -> (double)studentExam.getGrade()).collect(Collectors.toList());
                allDates = studentExams.stream().map(studentExam -> studentExam.getClassExam().getStartDate()).
                        sorted().collect(Collectors.toList());

                StExamStatsTv.setVisible(true);
                StExamStatsTv.setPrefHeight(300);
                ClassExamStatsTv.setVisible(false);
                ClassExamStatsTv.setPrefHeight(0);
                TablesVBOX.getChildren().remove(StExamStatsTv);
                TablesVBOX.getChildren().add(0, StExamStatsTv);

        }

        // Calculate mean and variance from the list of grades
        double[] ExamStats = CalculateStats(allGrades);

        // Set labels according to the new stats
        MeanLabel.setText(FormatDouble(ExamStats[0]));
        SDLabel.setText(FormatDouble(Math.sqrt(ExamStats[1])));
        MedianLabel.setText(FormatDouble(FindMedian(new ArrayList<>(allGrades))));
        BarChart barChart = GetHistogram(allGrades, allDates, 0, 0);
        barChart.setMaxHeight(300);
        barChart.setMaxWidth(600);
        BarScrollPane.setContent(barChart);

        SetVisibleAllNodes(SummaryRoot, true);
        DisableAllNodes(SummaryRoot, false);
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

    private BarChart GetHistogram(List<Double> grades, List<Date> dates, int width, int height)
    {

        // Create a CategoryAxis for the X-axis
        CategoryAxis xAxis = new CategoryAxis();

        // Create a NumberAxis for the Y-axis
        NumberAxis yAxis = new NumberAxis();

        BarChart<String, Number> gradeHistogramChart = new BarChart<>(xAxis, yAxis);
        if (height != 0)
            gradeHistogramChart.setMaxHeight(300);
        if (width != 0)
            gradeHistogramChart.setMaxWidth(600);

        // Create a Series for the grade histogram data
        XYChart.Series<String, Number> series = new XYChart.Series<>();

        int steps;
        int upperBound;
        if (HistType)
        {
            steps = 10;
            upperBound = 100;
        }
        else
        {
            steps = 1;
            upperBound = grades.size();
        }

        for (int i = 0; i < upperBound; i+= steps) {
            int bucketStart = i;
            int bucketEnd = i + 10;
            // Create a data point for the bucket
            int bucketSize = 0;
            for (Double grade : grades)
            {
                if (grade < bucketEnd && grade >= bucketStart)
                    bucketSize += 1;

                if (i == 90 && grade == 100)
                {
                    bucketSize += 1;
                }
            }

            XYChart.Data<String, Number> dataPoint;
            if (HistType)
            {
                dataPoint = new XYChart.Data<>(
                        bucketStart + "-" + bucketEnd, bucketSize);
            }
            else
            {
                dataPoint = new XYChart.Data<>(
                        FormatDate(dates.get(i), false), grades.get(i));
            }

            // Add the data point to the series
            series.getData().add(dataPoint);
        }

        // Add the Series to the BarChart
        gradeHistogramChart.getData().add(series);

        return gradeHistogramChart;
    }

    private void initClassExamTable()
    {
        // Define class Exams table columns
        ExamIDCol.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getExamForm().getExamFormID()));
        TesterCol.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getTeacher().getFullName()));
        DateCol.setCellValueFactory(param -> new SimpleStringProperty(FormatDate(param.getValue().getStartDate(), true)));
        ExamineeCol.setCellValueFactory(param -> new SimpleStringProperty(Integer.toString(param.getValue().getStudentExams().size())));
        PassedCol.setCellValueFactory(param -> new SimpleStringProperty(Integer.toString(param.getValue().
                getStudentExams().stream().filter(item->item.getGrade() >= 50).
                collect(Collectors.toList()).size())));
        MeanCol.setCellValueFactory(param -> new SimpleStringProperty(FormatDouble(param.getValue().getGradesMean())));
        StandardDeviationCol.setCellValueFactory(param -> new SimpleStringProperty(FormatDouble(Math.sqrt(param.getValue().
                getGradesVariance()))));
        HistogramCol.setCellFactory(generateButtonCellFactory());
        ClassExamStatsTv.getSortOrder().add(ExamIDCol);

        // Center column content
        ExamIDCol.setStyle( "-fx-alignment: CENTER;");
        TesterCol.setStyle( "-fx-alignment: CENTER;");
        DateCol.setStyle( "-fx-alignment: CENTER;");
        ExamineeCol.setStyle( "-fx-alignment: CENTER;");
        PassedCol.setStyle( "-fx-alignment: CENTER;");
        MeanCol.setStyle( "-fx-alignment: CENTER;");
        StandardDeviationCol.setStyle( "-fx-alignment: CENTER;");
        HistogramCol.setStyle( "-fx-alignment: CENTER;");
    }


    private void initStudentExamTable()
    {
        // Define student Exams table columns
        StExamIDCol.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().
                getClassExam().getExamForm().getExamFormID()));
        StTesterCol.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getClassExam().getTeacher().getFullName()));
        StDateCol.setCellValueFactory(param -> new SimpleStringProperty(FormatDate(param.getValue().getClassExam().getStartDate(), true)));
        StPassedCol.setCellValueFactory(param -> new SimpleStringProperty(Boolean.toString(param.getValue().
                getGrade() >=50 )));
        StGradeCol.setCellValueFactory(param -> new SimpleStringProperty(Integer.toString(param.getValue().
                getGrade())));
        StExamStatsTv.getSortOrder().add(StExamIDCol);

        // Center column content
        StExamIDCol.setStyle( "-fx-alignment: CENTER;");
        StTesterCol.setStyle( "-fx-alignment: CENTER;");
        StDateCol.setStyle( "-fx-alignment: CENTER;");
        StPassedCol.setStyle( "-fx-alignment: CENTER;");
        StGradeCol.setStyle( "-fx-alignment: CENTER;");
        StExamStatsTv.setStyle( "-fx-alignment: CENTER;");
    }


    @FXML
    void initialize() throws IOException {
        EventBus.getDefault().register(this);
        
        // Teacher initialization
        if (SimpleClient.getUser() instanceof Teacher)
        {
            TitleLabel.setText("Exams Statistics");
            TitleLabel.setPrefWidth(Region.USE_COMPUTED_SIZE);
            clientTeacher = (Teacher) SimpleClient.getUser();
            
            // Extract exams
            List<ExamForm> examFormList = clientTeacher.getExamForm();
            List<ClassExam> classExamList = new ArrayList<>();

            // gets all the class exam the teacher belongs to the exam forms the teacher created
            for (ExamForm examForm : examFormList)
            {
                classExamList.addAll(examForm.getClassExam());
            }
            Date currentTime = ConvertToDate(LocalDateTime.now());
            classExamList = classExamList.stream().filter(classExam ->
                    currentTime.after(classExam.getFinalSubmissionDate()) && classExam.getGradesMean() != -1).collect(Collectors.toList());
            
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
            
            initClassExamTable();
            
            ClassExamStatsTv.getItems().addAll(classExamList);
            ClassExamStatsTv.sort();

            ClassExamStatsTv.setVisible(true);
            TablesVBOX.setMaxHeight(550);
            TablesVBOX.setPrefHeight(550);
            ClassExamStatsTv.setPrefHeight(550);
            StExamStatsTv.setVisible(false);
            StExamStatsTv.setPrefHeight(0);

            chooseReportTypeCombo.setVisible(false);
            chooseReportTypeCombo.setDisable(true);
            chooseStatsForCombo.setVisible(false);
            chooseStatsForCombo.setDisable(true);
            SetVisibleAllNodes(SummaryRoot, false);
            DisableAllNodes(SummaryRoot, true);
            HistType = true;
        }
        // Principal initialization
        else
        {
            // Set report to invisible in initialization
            SetVisibleAllNodes(SummaryRoot, false);
            // Until we get all the class exams from the server, disable even the first combo box.
            chooseReportTypeCombo.setDisable(true);
            // Disable the second combo box on startup, as it requires the first's choice.
            chooseStatsForCombo.setDisable(true);


            SimpleClient.getClient().sendToServer(new Message(0, "Retrieve all class exams"));

            // Populate the first combo box with the big three choices.
            chooseReportTypeCombo.getItems().addAll("Filter by Teacher", "Filter by Course", "Filter by Student");
            initClassExamTable();
            initStudentExamTable();

            ClassExamStatsTv.setVisible(false);
            ClassExamStatsTv.setPrefHeight(0);
            StExamStatsTv.setVisible(false);
            StExamStatsTv.setPrefHeight(0);

            HistType = false;
        }
    }

    // Generate buttons for histogram column
    private Callback<TableColumn<ClassExam, String>, TableCell<ClassExam, String>> generateButtonCellFactory() {
        return new Callback<>() {
            @Override
            public TableCell<ClassExam, String> call(final TableColumn<ClassExam, String> param) {
                final TableCell<ClassExam, String> cell = new TableCell<>() {
                    private final Button button = new Button("View");

                    {
                        button.setOnAction(event ->
                        {
                            histStage = new Stage();

                            histStage.setOnCloseRequest(closeEvent -> {
                                // Close the other stage if it exists
                                if (histStage != null) {
                                    histStage.close();
                                }
                            });

                            AnchorPane root = new AnchorPane();
                            Scene histScene = new Scene(root, 500, 400);
                            List<StudentExam> exams = getTableView().getItems().get(getIndex()).getStudentExams();
                            List<Double> grades = exams.stream().map(exam -> (double)exam.getGrade()).collect(Collectors.toList());
                            boolean tempBool = HistType;
                            HistType = true;
                            root.getChildren().add(GetHistogram(grades, null, 0, 0));
                            HistType = tempBool;
                            histStage.setScene(histScene);
                            DisableAllNodes(controllerRoot, true);
                            histStage.showAndWait();
                            DisableAllNodes(controllerRoot, false);
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

    @Subscribe
    public void CleanUp(GeneralEvent event) {
        if (event.getMessage().getMessage().startsWith("Exit") &&
                histStage != null)
            histStage.close();
    }

    @Override
    @Subscribe
    public void TriggerDataCheck(ChangeMainSceneEvent event) {
        if (histStage != null)
            histStage.close();

        boolean unsavedData = CheckForUnsavedData();
        if (unsavedData) {
            boolean changeScreen = PromptUserToSaveData(event.getSceneName());
        }
        try {
            EventBus.getDefault().unregister(this);
            SimpleChatClient.setRoot(event.getSceneName());
            System.out.println("TriggerDataCheck changing scene");
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }




}
