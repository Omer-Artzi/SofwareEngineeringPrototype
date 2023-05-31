import Entities.*;
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

public class AddQuestionController {
    private List<Course> courseOfTeacher;
    private List<Course>courses;
    private List<Subject> subjectOfTeacher;
    private List<String>notes;
    @FXML
    private int msgId;
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
    private ChoiceBox<String> addCourseChoiceBox;
    @FXML
    private Button addQuestionButtom;
    @FXML
    private ChoiceBox<String> addSubjectChoiceBox;
    @FXML
    private ChoiceBox<String> CorrectAnswerCB;
    @FXML
    private Button previewButtom;

    @Subscribe
    public void editQuestion(EditQuestionEvent event)
    {
        Question question=event.getQuestion();
        TeacherNote.setText(question.getTeacherNote());
        StudentNote.setText(question.getStudentNote());
        QuestionDataTF.setText(question.getQuestionData());
        addCourseChoiceBox.setValue(question.getCourse().getName());
        addSubjectChoiceBox.setValue(question.getCourse().getName());
        List<String>answers=question.getAnswers();
        for(int i=1;i<5;i++)
        {
            Answer answer=new Answer(String.valueOf(i),answers.get(i-1));
            AnswerTable.getItems().add(answer);
        }
        CorrectAnswerCB.setValue(String.valueOf(question.getCorrectAnswer()));
        ///***** need to add the correct answer choiceBox ******/////
    }

    /*
    @Subscribe
    public void updateData1(SubjectMessageEvent eventSUB) throws IOException {
        Person person=(Teacher)SimpleClient.getClient().getUser();
        if(person instanceof Teacher){
            Teacher teacher=(Teacher) person;
            CourseOfTeacher=teacher.getCourseList();
          //  SubjectOfTeacher=teacher.getSubjectList();
            //System.out.println(teacher.getFullName());
        }
        ///*** need to change, after the changes I can delete this function //
        List<String>subject_name=new ArrayList<>();
        for(Subject item: SubjectOfTeacher)
            subject_name.add(item.getName());
        addSubjectChoiceBox.setItems(FXCollections.observableArrayList(subject_name));
    }
*/
    public void Elements(boolean b)
    {
        StudentNote.setDisable(b);
        TeacherNote.setDisable(b);
        QuestionDataTF.setDisable(b);
        previewButtom.setDisable(b);
        CorrectAnswerCB.setDisable(b);
        addQuestionButtom.setDisable(b);
    }

    @FXML
    void initialize() throws IOException {             //initialize the page. update the data and the elements
        EventBus.getDefault().register(this);
        Teacher teacher=((Teacher)(SimpleClient.getClient().getUser()));
        courseOfTeacher = teacher.getCourseList();
        subjectOfTeacher=teacher.getSubject();
        List<String>subject_name=new ArrayList<>();
        for(Subject item: subjectOfTeacher)
            subject_name.add(item.getName());
        addSubjectChoiceBox.setItems(FXCollections.observableArrayList(subject_name));

        Answer ans=new Answer("");
        ObservableList<Answer> data = AnswerTable.getItems();
        addCourseChoiceBox.setDisable(true);
        Elements(true);
        notes = new ArrayList<String>();
        NumberColumn.setCellValueFactory(new PropertyValueFactory<>("number"));
        AnswerColumn.setCellValueFactory(new PropertyValueFactory<>("answer"));

        //** choose Subject in ChoiceBox **//
        addSubjectChoiceBox.setOnAction(event -> {
            if (addSubjectChoiceBox.getSelectionModel().isEmpty() || addSubjectChoiceBox.getSelectionModel() == null) {
            } else {
                addCourseChoiceBox.setDisable(false);
                List<String>course_name=new ArrayList<>();
                for(Course item: courseOfTeacher) {
                    if (item.getSubject().getName().equals(addSubjectChoiceBox.getValue()))
                        course_name.add(item.getName());
                }
                addCourseChoiceBox.setItems(FXCollections.observableArrayList(course_name));
            }
        });

        //** choose Course in ChoiceBox **//
        addCourseChoiceBox.setOnAction(event1 -> {
            if (addCourseChoiceBox.getSelectionModel().isEmpty() || addCourseChoiceBox.getSelectionModel() == null) {
            } else {
                String [] str={"1","2","3","4"};
                Elements(false);
                List<Answer> a = new ArrayList<>();
                Answer ans1 = new Answer("");
                a.add(ans1);
                Answer ans2 = new Answer("");
                a.add(ans2);
                Answer ans3 = new Answer("");
                a.add(ans3);
                Answer ans4 = new Answer("");
                a.add(ans4);
                for (int i = 0; i < 4; i++) {
                    a.get(i).setNumber(String.valueOf(i + 1));
                    data.add(a.get(i));
                    CorrectAnswerCB.setItems(FXCollections.observableArrayList(str));
                }
            }
        });

       //**make the table editable function **//
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
        });
    }


    @FXML
    void addQuestion(ActionEvent event) throws IOException {
        /* check if the input is valid */
        int counter=0;
        String errorMSG="";
        String data=QuestionDataTF.getText();
        String correctAnswer=CorrectAnswerCB.getValue();
        if(CorrectAnswerCB.getSelectionModel().isEmpty()||CorrectAnswerCB.getSelectionModel()==null)
            errorMSG="Error: Please choose correct answer";
        else if(data=="")
            errorMSG="Error: Please fiil question's information";
        String CorrectAnswer=CorrectAnswerCB.getValue();
        String correct="";
        List<String> answers= new ArrayList<>();
        List<Answer> dataList = AnswerTable.getItems();
        for(Answer item: dataList)
        {
            System.out.println("answer" +item.getAnswer());
        }
        int i=0;
        for (Answer answer : dataList) {
            answers.add(answer.getAnswer());
            if(String.valueOf(i+1).equals(correctAnswer)) {
                correct = answer.getAnswer();
            }
            if(answer.getAnswer()!="") {
                counter++;
            }
            i++;
        }
        if(counter!=4)
         errorMSG="Error: Please fiil question's answers";
        if(errorMSG!=""){
            JOptionPane.showMessageDialog(null,errorMSG,"Invalid Input",JOptionPane.ERROR_MESSAGE);
            return;
        }

        /* get all the data and create a new Question object, update the database */
        String selectedSubject=addSubjectChoiceBox.getValue();
        Subject subject = null;
        for(Subject item: subjectOfTeacher)
        {
            if (item.getName().equals(selectedSubject))
                subject=item;
        }
        String selectedCourse=addCourseChoiceBox.getValue();
        Course course = null;
        for(Course item: courseOfTeacher)
        {
            if (item.getName().equals(selectedCourse)) {
                course = item;
            }
        }
        String studentNote=StudentNote.getText();
        String teacherNote= TeacherNote.getText();
        Question question=new Question(course, data,answers,correct,teacherNote,studentNote);
        Message message=new Message(1,"Save Question");
        message.setData(question);
        SimpleClient.getClient().sendToServer(message);
        //SimpleChatClient.setRoot("QuestionSaved");
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
