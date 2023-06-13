package Client.Controllers.MainViews;

import Client.Events.ExamMessageEvent;
import Client.Events.LoadExamEvent;
import Client.Events.SubjectMessageEvent;
import Client.SimpleChatClient;
import Client.SimpleClient;
import Entities.SchoolOwned.ClassExam;
import Entities.Communication.Message;
import Entities.SchoolOwned.ExamForm;
import Entities.SchoolOwned.Course;
import Entities.SchoolOwned.Question;
import Entities.SchoolOwned.Subject;
import Entities.Users.Student;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.poi.xwpf.usermodel.*;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Random;


public class ViewExamController {
	@FXML
	private int msgId;

	@FXML
	private ComboBox<Course> CoursesCB;

	@FXML
	private TableView<ClassExam> ExamsTV;

	@FXML
	private TableColumn<ClassExam, Integer> IDColumn;

	@FXML
	private Button ViewDIgitalButton;

	@FXML
	private Button ViewManualButton;

	@FXML
	private Button addExamButton;

	@FXML
	private Button backButton;

	@FXML
	private TableColumn<ClassExam, SimpleStringProperty> codeColumn;

	@FXML
	private TableColumn<ClassExam, SimpleStringProperty> createdColumn;

	@FXML
	private TableColumn<ClassExam, SimpleStringProperty> creatorColumn;

	@FXML
	private TableColumn<ClassExam, SimpleStringProperty> lastUsedColumn;

	@FXML
	private Button modifyExamButton;

	@FXML
	private Button statsButton;

	@FXML
	private ComboBox<Subject> subjectsCB;

	@FXML
	private TableColumn<ClassExam, SimpleStringProperty> timeColumn;


	@FXML
	void initialize() {
		EventBus.getDefault().register(this);
		try {
			Message message = new Message(msgId, "Get Subjects");
			SimpleClient.getClient().sendToServer(message);
			IDColumn.setCellValueFactory(new PropertyValueFactory<>("ID"));
			codeColumn.setCellValueFactory(new PropertyValueFactory<>("Code"));
			createdColumn.setCellValueFactory(new PropertyValueFactory<>("dateCreated"));
			ExamsTV.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
			ExamsTV.getItems().addAll(((Student)(SimpleClient.getUser())).getClassExams());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	@Subscribe
	public void displaySubjects(SubjectMessageEvent event)
	{
		if(event.getSubjects() != null)
		{
			subjectsCB.getItems().addAll(event.getSubjects());
		}
		else {
			JOptionPane.showMessageDialog(null, "Could not Retrieve any subjects", "DataBase Error", JOptionPane.WARNING_MESSAGE);
		}

	}
	@FXML
	public void onSubjectSelection() throws IOException {
		List<Course> courses = subjectsCB.getSelectionModel().getSelectedItem().getCourses();
		if(courses != null)
		{
			CoursesCB.getItems().addAll(courses);
			Message message = new Message(++msgId, "Get Class Exams for Subject: "  + subjectsCB.getSelectionModel().getSelectedItem() );
			message.setData(subjectsCB.getSelectionModel().getSelectedItem());
			SimpleClient.getClient().sendToServer(message);
		}
		else {
			JOptionPane.showMessageDialog(null, "This subject does not contain any courses", "DataBase Error", JOptionPane.WARNING_MESSAGE);
		}
	}
	@FXML
	public void onCourseSelection() throws IOException {
		Message message = new Message(++msgId, "Get Class Exams for Course: "  + CoursesCB.getSelectionModel().getSelectedItem() );
		message.setData(CoursesCB.getSelectionModel().getSelectedItem());
		SimpleClient.getClient().sendToServer(message);
	}
	@FXML
	public void viewDigital()
	{
		ClassExam selectedForm = ExamsTV.getSelectionModel().getSelectedItem();
		//TODO : add digital view after meeting on tuesday

	}
	@FXML
	public void viewManual()
	{
		ClassExam selectedForm = ExamsTV.getSelectionModel().getSelectedItem();
		createManualExam(selectedForm);
	}
	public static void createManualExam(ClassExam selectedExam)
	{
		ExamForm selectedForm = selectedExam.getExamForm();
		int questionID = 1;
		Random random = new Random();

		XWPFDocument document = new XWPFDocument();
		XWPFParagraph titleParagraph = document.createParagraph();
		titleParagraph.setAlignment(ParagraphAlignment.CENTER);
		XWPFRun title = titleParagraph.createRun();
		title.setBold(true);
		title.setUnderline(UnderlinePatterns.DASH_LONG);
		title.setText("Exam in " + selectedForm.getSubject().getName() + " - " + selectedForm.getCourse().getName());
		title.addBreak();
		title.setText("Exam Code: " + selectedForm.getCode(),title.getTextPosition());
		title.addBreak();
		title.setText("Created By: " + selectedForm.getCreator().getFullName() + " in " + selectedForm.getDateCreated(),title.getTextPosition());
		title.addBreak();

		for(Question question : selectedForm.getQuestionList())
		{
			XWPFParagraph questionParagraph = document.createParagraph();
			XWPFRun questionBody = questionParagraph.createRun();
			List<String> answers = question.getAnswers();
			questionBody.setText(questionID++ +". " + StringEscapeUtils.unescapeHtml4(question.getQuestionData()));
			questionBody.addBreak();
			questionBody.addBreak();
			questionBody.setText(" A." + StringEscapeUtils.unescapeHtml4(answers.get(0)),questionBody.getTextPosition());
			questionBody.addBreak();
			questionBody.setText(" B." + StringEscapeUtils.unescapeHtml4(answers.get(1)),questionBody.getTextPosition());
			questionBody.addBreak();
			questionBody.setText(" C." +StringEscapeUtils.unescapeHtml4(answers.get(2)),questionBody.getTextPosition());
			questionBody.addBreak();
			questionBody.setText(" D." +StringEscapeUtils.unescapeHtml4(answers.get(3)),questionBody.getTextPosition());
			questionBody.addBreak();
			questionBody.addBreak();
			if(question.getStudentNote() != null) {
				questionBody.setText("Student Notes: " + question.getStudentNote(), questionBody.getTextPosition());
				questionBody.addBreak();
				questionBody.addBreak();
			}
			if(question.getTeacherNote() != null) {
				questionBody.setText("Teacher Notes: " + question.getTeacherNote(), questionBody.getTextPosition());
				questionBody.addBreak();
				questionBody.addBreak();
			}

		}
		try
		{
			String fileName = "Exam_"+ selectedForm.getCode() + "_" + selectedForm.getCourse().getName()+".docx";
			FileOutputStream outputStream = new FileOutputStream(fileName);
			document.write(outputStream);
			System.out.println("Document created successfully.");
			openExam(fileName);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}


	private static void openExam(String fileName) {
		if (Desktop.isDesktopSupported()) {
			Desktop desktop = Desktop.getDesktop();
			try {
				File file = new File(fileName);
				desktop.open(file);
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else {
			System.out.println("Opening documents is not supported on this platform.");
		}
	}
	@FXML
	public void backToHomePage() throws IOException {
		System.out.println("Move to Home Page");
		SimpleChatClient.setRoot("HomePage");
	}
	@FXML
	public void moveToAddExams() throws IOException {
		System.out.println("Move to Add Exam Page");
		SimpleChatClient.setRoot("AddExam");
	}
	@FXML
	public void MoveToModifyExams() throws IOException {
		ClassExam selectedForm = ExamsTV.getSelectionModel().getSelectedItem();
		if(selectedForm != null) {
			System.out.println("Move to Add Exam page");
			EventBus.getDefault().post(new LoadExamEvent(selectedForm.getExamForm(),"Modify Exam"));
			SimpleChatClient.setRoot("AddExam");
		}
		else {
			JOptionPane.showMessageDialog(null,"No exam selected","Error!",JOptionPane.ERROR_MESSAGE);
		}
	}
	@FXML
	public void moveToViewStats() throws IOException {
		ClassExam selectedForm = ExamsTV.getSelectionModel().getSelectedItem();
		if(selectedForm != null) {
			System.out.println("Move to Stats page");
			EventBus.getDefault().post(new LoadExamEvent(selectedForm.getExamForm(),"View Stats"));
			SimpleChatClient.setRoot("ViewStats");
		}
		else {
			JOptionPane.showMessageDialog(null,"No exam selected","Error!",JOptionPane.ERROR_MESSAGE);
		}
	}
	@Subscribe
	public void DisplayExam(ExamMessageEvent event)
	{
		ExamsTV.getItems().clear();
		ExamsTV.getItems().addAll(event.getClassExams());
		ExamsTV.refresh();
	}


}
