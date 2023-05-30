import Entities.*;
import Events.SubjectMessageEvent;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import javax.swing.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class addQuestionController {
    int correct;
    private  List<Course>CourseOfTeacher;
    private  List<Subject>SubjectOfTeacher;
    private List<Course>courses;
    private List<Subject>subjects;
    private List<String>notes;
    @FXML
    private int msgId;
    private static int counter=1;
    @FXML
    private TableColumn<Answer, String> AnswerColumn;
    @FXML
    private TableView<Answer> AnswerTable;
    @FXML
    private TableColumn<Answer, String> NumberColumn;
    @FXML
    private TextArea QuestionDataTF;
    @FXML
    private TextArea StudentNote;
    @FXML
    private TextArea TeacherNote;
    @FXML
    private ChoiceBox<String> addCourseChoiceBox; /////////////////
    @FXML
    private Button addQuestionButtom;
    @FXML
    private ChoiceBox<String> addSubjectChoiceBox;
    @FXML
    private Button correctBT;
    @FXML
    private Button previewButtom;

    //@Subscribe
    //public void editQuestion(editQuestionEvent event)
    //{
    //    Question question=event.getQuestion();
    //    TeacherNote.setText(question.getTeacherNote());
    //   StudentNote.setText(question.getStudentNote());
    //    QuestionDataTF.setText(question.getQuestionData());
    //    addCourseChoiceBox.setValue(question.getCourse().getName());
    //    addSubjectChoiceBox.setValue(question.getCourse().getName());
    //    List<String>answers=question.getAnswers();
    //    for(int i=1;i<5;i++)
    //    {
    //        Answer answer=new Answer(String.valueOf(i),answers.get(i-1));
    //        AnswerTable.getItems().add(answer);
    //    }
    ///***** need to add the correct answer choiceBox ******/////
    // }

    @Subscribe
    public void updateData1(SubjectMessageEvent eventSUB) throws IOException {
        Person person=(Teacher)SimpleClient.getClient().getUser();
        if(person instanceof Teacher){
            Teacher teacher=(Teacher) person;
            CourseOfTeacher=teacher.getCourseList();
            System.out.println(teacher.getFullName());
        }
        subjects=eventSUB.getSubjects();
        List<String>subject_name=new ArrayList<>();
        for(Subject item: subjects)
            subject_name.add(item.getName());
        /*CourseOfTeacher=teacher.getCourseList();
        SubjectOfTeacher=teacher.getSubjectList();
        List<String>subject_name=new ArrayList<>();
        for(Subject item: SubjectOfTeacher)
            subject_name.add(item.getName());*/
        addSubjectChoiceBox.setItems(FXCollections.observableArrayList(subject_name));
    }

    @FXML
    void initialize() throws IOException {             //initialize the page. update the data and the elemnts
        EventBus.getDefault().register(this);
        Answer ans=new Answer();
        ObservableList<Answer> data = AnswerTable.getItems();
        StudentNote.setDisable(true);
        TeacherNote.setDisable(true);
        QuestionDataTF.setDisable(true);
        previewButtom.setDisable(true);
        addCourseChoiceBox.setDisable(true);
        correctBT.setDisable(true);
        addQuestionButtom.setDisable(true);
        notes = new ArrayList<String>();
        NumberColumn.setCellValueFactory(new PropertyValueFactory<>("number"));
        AnswerColumn.setCellValueFactory(new PropertyValueFactory<>("answer"));

        addSubjectChoiceBox.setOnAction(event -> {
            if (addSubjectChoiceBox.getSelectionModel().isEmpty() || addSubjectChoiceBox.getSelectionModel() == null) {
            } else {
                addCourseChoiceBox.setDisable(false);
                List<String> course_name = new ArrayList<>(); //new course list to put in choiceBox
                Subject subject=null;
                for(Subject item: subjects)
                {
                    if(item.getName().equals(addSubjectChoiceBox.getValue()))
                        subject=item;
                }
                for(Course item: CourseOfTeacher) // TODO: change to all courses in the specific Subject (not only the courses that teacher teaches)
                {
                    if(item.getSubject().equals(addSubjectChoiceBox.getValue()))
                        course_name.add(item.getName());
                }
                addCourseChoiceBox.setItems(FXCollections.observableArrayList(course_name));
            }
        });

        addCourseChoiceBox.setOnAction(event1 -> {
            if (addCourseChoiceBox.getSelectionModel().isEmpty() || addCourseChoiceBox.getSelectionModel() == null) {
            } else {
                StudentNote.setDisable(false);
                TeacherNote.setDisable(false);
                QuestionDataTF.setDisable(false);
                previewButtom.setDisable(false);
                correctBT.setDisable(false);
                addQuestionButtom.setDisable(false);
                List<Answer> a = new ArrayList<>();
                Answer ans1 = new Answer();
                a.add(ans1);
                Answer ans2 = new Answer();
                a.add(ans2);
                Answer ans3 = new Answer();
                a.add(ans3);
                Answer ans4 = new Answer();
                a.add(ans4);
                for (int i = 0; i < 4; i++) {
                    a.get(i).setNumber(String.valueOf(i + 1));
                    data.add(a.get(i));
                }
            }
        });
        /*make the table editable function */
        AnswerTable.setEditable(true);

        NumberColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        NumberColumn.setOnEditCommit(event -> {
            Answer item = event.getRowValue();
            item.setNumber(event.getNewValue());
        });

        AnswerColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        AnswerColumn.setOnEditCommit(event -> {
            Answer item = event.getRowValue();
            item.setAnswer(event.getNewValue());
            counter++;
        });

    }

    @FXML
    void correctAnswer(ActionEvent event) { ///////////////////
        // correctBT.setOnAction(event1 -> {
        Answer selectedAnswer = AnswerTable.getSelectionModel().getSelectedItem();
        if (selectedAnswer != null) {
            AnswerTable.setRowFactory(tv -> {
                TableRow<Answer> row = new TableRow<>();
                row.styleProperty().bind(
                        javafx.beans.binding.Bindings.when(
                                        row.itemProperty().isEqualTo(selectedAnswer))
                                .then("-fx-background-color: green;")
                                .otherwise("")
                );
                return row;
            });
        }
        //  });
    }

    @FXML
    void addQuestion(ActionEvent event) throws IOException {
        /* check if the input is valid */
        String errorMSG="";
        String data=QuestionDataTF.getText();
        //String correctAnswer=correctAnswerCB.getValue();
        //if(correctAnswerCB.getSelectionModel().isEmpty()||correctAnswerCB.getSelectionModel()==null){
        //    errorMSG="Error: Please choose correct answer";
        /// }
        if(data=="")
            errorMSG="Error: Please fiil question's information";
        // else if(counter!=5)
        // errorMSG="Error: Please fiil question's answers";
        if(errorMSG!=""){
            JOptionPane.showMessageDialog(null,errorMSG,"Invalid Input",JOptionPane.ERROR_MESSAGE);
            return;
        }
        /* get all the data and create a new Question object, updata the data base */
        //Course course=(Course) addCourseChoiceBox.getItems(); //wait for data base
        String selectedSubject=addSubjectChoiceBox.getValue();
        Subject subject;
        for(Subject item: subjects)
        {
            if (item.getName()==selectedSubject)
                subject=item;

        }
        String studentNote=StudentNote.getText();
        String teacherNote= TeacherNote.getText();
        //Question question=new Question(course,subject, data,answers,Integer.valueOf(correctAnswer),teacherNote,studentNote);

        /* switch to 'Question saved window*/
        // SimpleChatClient.setRoot("QuestionSaved");
    }

    @FXML
    void seePreview(ActionEvent event) throws IOException {
        /*loading the FXML file to open the page */
        FXMLLoader fxmlLoader=new FXMLLoader(getClass().getResource("seePreview.fxml"));
        Parent root1=(Parent) fxmlLoader.load();
        Stage stage=new Stage();
        stage.setTitle("Preview of question:");
        stage.setScene(new Scene(root1));

        /*get the data for Question which can present in an exam ,make it to Text object and present it on the page */
        VBox container = (VBox) fxmlLoader.getNamespace().get("vBox");
        Text text = new Text();
        TableColumn<Answer, String> column = (TableColumn<Answer, String>) AnswerTable.getColumns().get(1);
        ObservableList<Answer> items = AnswerTable.getItems();
        String string="";
        for (Answer item : items) {
            String answer = AnswerColumn.getCellData(item);
            if(answer==null)
                answer="";
            String number = NumberColumn.getCellData(item);
            string=string+number+". "+answer+"\n";
        }
        String studentNote=StudentNote.getText();
        if(studentNote=="")
            text.setText(QuestionDataTF.getText()+"\n"+string);
        else
            text.setText(QuestionDataTF.getText()+"\n"+"note: "+studentNote+"\n"+string);
        text.setFont(Font.font("Arial", 15));
        container.getChildren().add(text);
        stage.show();
}
}