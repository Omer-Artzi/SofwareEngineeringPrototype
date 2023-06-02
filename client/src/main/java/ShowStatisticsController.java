import Entities.*;
import Events.ClassExamGradeEvent;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import javax.persistence.criteria.CriteriaBuilder;
import java.io.IOException;
import java.text.SimpleDateFormat;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

public class ShowStatisticsController {

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

    Teacher clientTeacher;

    @Subscribe
    public void stub(ClassExamGradeEvent event) {
    }



    private String FormatDate(Date date)
    {
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        return formatter.format(date);
    }

    // not working
    private void DisplayHistogram(List<Integer> grades)
    {
        // Create a CategoryAxis for the X-axis
        CategoryAxis xAxis = new CategoryAxis();

        // Create a NumberAxis for the Y-axis
        NumberAxis yAxis = new NumberAxis();

        BarChart<String, Number> gradeHistogramChart = new BarChart<>(xAxis, yAxis);

        // Create a Series for the grade histogram data
        XYChart.Series<String, Number> series = new XYChart.Series<>();

        // Iterate through the grade buckets (0 to 100 with a range of 10)
        for (int i = 0; i <= 100; i += 10) {
            int bucketStart = i;
            int bucketEnd = i + 10;
            //grades.removeIf(grade-> grade <= (i+1)*10);
            // Create a data point for the bucket
            XYChart.Data<String, Number> dataPoint = new XYChart.Data<>(
                    bucketStart + "-" + bucketEnd, 0);

            // Add the data point to the series
            series.getData().add(dataPoint);
        }

        // Add the Series to the BarChart
        gradeHistogramChart.getData().add(series);
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

        // insert content to table
        ClassExamStatsTv.getItems().addAll(classExamList);

        ExamIDCol.setStyle( "-fx-alignment: CENTER;");
        TesterCol.setStyle( "-fx-alignment: CENTER;");
        DateCol.setStyle( "-fx-alignment: CENTER;");
        ExamineeCol.setStyle( "-fx-alignment: CENTER;");
        PassedCol.setStyle( "-fx-alignment: CENTER;");
        MeanCol.setStyle( "-fx-alignment: CENTER;");
        StandardDeviationCol.setStyle( "-fx-alignment: CENTER;");


        ClassExamStatsTv.getSortOrder().add(ExamIDCol);

        ClassExamStatsTv.sort();


    }

}
