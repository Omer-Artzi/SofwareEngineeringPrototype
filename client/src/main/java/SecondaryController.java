import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import Entities.Grade;
import Entities.Message;
import Entities.Student;
import com.github.javafaker.Faker;
import Events.GradeMessageEvent;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;


public class SecondaryController {
    @FXML
    private TableColumn<Grade, SimpleStringProperty> CourseColumn;

    @FXML
    private TableColumn<Grade, SimpleStringProperty> SubjectColumn;

    @FXML
    private Button backButton;

    @FXML
    private TableColumn<Grade, Integer> gradeColumn;

    @FXML
    private TableView<Grade> gradesTV;

    @FXML
    private Label studentNameTF;
    public static Student student;
    @FXML
    private Button editGradeButton;
    @FXML
    private TextField newGrade;
    private int messageID = 0;
    @FXML
    private Label statsLabel;
    @FXML
    private Button addGradeButton;
    private List<String> courseOptions;
    private List<String> subjectOptions;


    @FXML
    private void switchToPrimary() throws IOException {
        Message message = new Message(1, "Get Students");
        SimpleClient.getClient().sendToServer(message);
        SimpleChatClient.setRoot("primary");

    }

    @FXML
    void edit() {
        try {
            int newGradeNum = Integer.parseInt(newGrade.getText());
            if (newGradeNum >= 0 && newGradeNum <= 100) {
                Grade editedGrade = gradesTV.getSelectionModel().getSelectedItem();
                if (editedGrade != null) {
                    editedGrade.setGrade(newGradeNum);
                    sendNewGrade(editedGrade);
                    gradesTV.getSelectionModel().getSelectedItem().setGrade(newGradeNum);
                    gradesTV.refresh();
                } else {
                    Alert alert = new Alert(Alert.AlertType.ERROR, "No Entities.Grade Was Chosen");
                    alert.setTitle("Error!");
                    alert.setHeaderText("Error:");
                    alert.show();
                }
            } else {
                newGrade.setText("Invalid Entities.Grade");
            }
        } catch (NumberFormatException e) {
            newGrade.setText("Invalid number");
        }
    }

    @Subscribe
    public void DisplayGrades(GradeMessageEvent event) {
        try {
            studentNameTF.setText(event.getStudent().getFullName() + "'s Grades");
            studentNameTF.setAlignment(Pos.TOP_CENTER);
        }
        catch (IllegalStateException e)
        {

        }
        student = event.getStudent();
        gradesTV.getItems().clear();
        List<Grade> grades = event.getGrades();
        if(grades != null && !grades.isEmpty()) {
            gradesTV.getItems().addAll(grades);
        }
        calcStats(grades);
        gradesTV.setRowFactory(tv -> new TableRow<>() {
            @Override
            protected void updateItem(Grade item, boolean empty) {
                super.updateItem(item, empty);
                if (item == null || empty) {
                    // Set the default background color for empty rows
                    setStyle("");
                } else {
                    // Set the background color based on some condition
                    if (item.getGrade() < 51) {
                        setStyle("-fx-background-color: red;");
                    } else {
                        setStyle("-fx-background-color: green;");
                    }
                }
            }
        });


    }

    private void calcStats(List<Grade> grades) {
        if(grades != null) {
            int passes = 0, fails = 0, sum = 0, size = grades.size();
            double average = 0;
            for (Grade grade : grades) {
                if (grade.getGrade() >= 51) {
                    passes++;
                } else {
                    fails++;
                }
                sum += grade.getGrade();
            }
            if (size != 0) {
                average = sum / size;
            }

            statsLabel.setText("Average: " + average + " ,num of grades: " + size + ", passes: " + passes + " , fails: " + fails);
            statsLabel.setAlignment(Pos.BASELINE_CENTER);
        }
    }

    @FXML
    void initialize() throws IOException {
        EventBus.getDefault().register(this);
        CourseColumn.setCellValueFactory(new PropertyValueFactory<>("course"));
        SubjectColumn.setCellValueFactory(new PropertyValueFactory<>("subject"));
        gradeColumn.setCellValueFactory(new PropertyValueFactory<>("grade"));
        gradesTV.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        courseOptions = new ArrayList<>();
        subjectOptions = new ArrayList<>();
        Faker faker = new Faker();
        for (int i = 0; i < 100; i++) {
            String course = faker.educator().course();
            if (!courseOptions.contains(course)) {
                courseOptions.add(course);
            }
            String subject = faker.pokemon().name();
            if (!subjectOptions.contains(subject)) {
                subjectOptions.add(subject);
            }
        }
    }

    public void sendNewGrade(Grade grade) {
        try {
            Message message = new Message(messageID++, "Change Entities.Grade");
            message.setData(grade);
            calcStats(gradesTV.getItems());
            SimpleClient.getClient().sendToServer(message);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    @FXML
    public void addGrade() {
        Faker faker = new Faker();
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Entities.Course, Entities.Subject, and Entities.Grade");
        dialog.setHeaderText("Enter the course, subject, and grade:");

        GridPane gridPane = new GridPane();
        gridPane.setHgap(10);
        gridPane.setVgap(10);

        ComboBox<String> courseComboBox = new ComboBox<>();
        courseComboBox.getItems().addAll(courseOptions);
        ComboBox<String> subjectComboBox = new ComboBox<>();
        subjectComboBox.getItems().addAll(subjectOptions);
        TextField gradeTF = new TextField();

        gridPane.add(new Label("Entities.Course:"), 0, 0);
        gridPane.add(courseComboBox, 1, 0);
        gridPane.add(new Label("Entities.Subject:"), 0, 1);
        gridPane.add(subjectComboBox, 1, 1);
        gridPane.add(new Label("Entities.Grade:"), 0, 2);
        gridPane.add(gradeTF, 1, 2);

        dialog.getDialogPane().setContent(gridPane);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == ButtonType.OK) {
                String course = courseComboBox.getValue();
                String subject = subjectComboBox.getValue();
                String grade = gradeTF.getText();
                try {
                    int igrade = Integer.parseInt(grade);
                    if (igrade < 0 || igrade > 100) {
                        Alert alert = new Alert(Alert.AlertType.ERROR, "");
                        alert.setTitle("Error!");
                        alert.setHeaderText("Error: Invalid Entities.Grade, The Entities.Grade Was Not Saved");
                        alert.show();
                        return null;
                    }
                    Message message = new Message(1, "Add Entities.Grade to Entities.Student ID: " + student.getID());
                    Grade newgrade = new Grade(igrade, course, subject, student);
                    message.setData(newgrade);
                    gradesTV.getItems().add(newgrade);
                    gradesTV.refresh();
                    calcStats(gradesTV.getItems());
                    SimpleClient.getClient().sendToServer(message);
                } catch (NumberFormatException e) {


                } catch (IOException e) {
                    throw new RuntimeException(e);
                }


            }
            return null;
        });

        dialog.showAndWait();
    }


    public void refreshGrades() throws IOException {
        Message message = new Message(1, "Get Grades: " + student.getID());
        SimpleClient.getClient().sendToServer(message);
    }
}