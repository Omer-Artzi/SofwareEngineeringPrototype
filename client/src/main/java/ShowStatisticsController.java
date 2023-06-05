import Entities.*;
import Events.ClassExamGradeEvent;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.util.Callback;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.io.IOException;
import java.text.SimpleDateFormat;

import java.util.ArrayList;
import java.util.Date;
import java.util.Formatter;
import java.util.List;
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

    Teacher clientTeacher;

    @Subscribe
    public void stub(ClassExamGradeEvent event) {
    }


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


    private BarChart GetHistogram(List<Integer> grades)
    {
        // Create BarChart with Axis
        CategoryAxis xAxis = new CategoryAxis();
        NumberAxis yAxis = new NumberAxis();
        BarChart<String, Number> gradeHistogramChart = new BarChart<>(xAxis, yAxis);

        // Create a Series which will contain the columns values of the Histogram
        XYChart.Series<String, Number> series = new XYChart.Series<>();

        // Iterate through the grade buckets and insert the grades that fits to the bucket
        for (int i = 0; i < 100; i += 10) {
            int bucketStart = i;
            int bucketEnd = i + 10;
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

            // Create a column in a bar chart
            XYChart.Data<String, Number> dataPoint = new XYChart.Data<>(
                    bucketStart + "-" + bucketEnd, bucketSize);
            // Insert the column to series
            series.getData().add(dataPoint);
        }

        // Add the Series to the BarChart
        gradeHistogramChart.getData().add(series);
        return gradeHistogramChart;
    }

    @FXML
    void initialize() throws IOException {
        EventBus.getDefault().register(this);

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
        ExamIDCol.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getExamForm().getExamFormID()));
        TesterCol.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getTeacher().getFullName()));
        DateCol.setCellValueFactory(param -> new SimpleStringProperty(FormatDate(param.getValue().getStartDate()).toString()));
        ExamineeCol.setCellValueFactory(param -> new SimpleStringProperty(Integer.toString(param.getValue().getStudentExams().size())));
        PassedCol.setCellValueFactory(param -> new SimpleStringProperty(Integer.toString(param.getValue().
                getStudentExams().stream().filter(item->item.getGrade() >= 50).
                collect(Collectors.toList()).size())));
        MeanCol.setCellValueFactory(param -> new SimpleStringProperty(FormatDouble(param.getValue().getGradesMean())));
        StandardDeviationCol.setCellValueFactory(param -> new SimpleStringProperty(FormatDouble(Math.sqrt(param.getValue().
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
                    private final Button button = new Button("View");

                    {
                        button.setOnAction(event ->
                        {
                            Stage histStage = new Stage();
                            AnchorPane root = new AnchorPane();
                            Scene histScene = new Scene(root, 500, 400);
                            List<StudentExam> exams = getTableView().getItems().get(getIndex()).getStudentExams();
                            System.out.println("number of exams: " + exams.size());
                            List<Integer> grades = exams.stream().map(exam -> exam.getGrade()).collect(Collectors.toList());
                            System.out.println("number of grades: " + grades.size());
                            root.getChildren().add(GetHistogram(grades));
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

}
