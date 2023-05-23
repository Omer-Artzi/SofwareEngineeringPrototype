package il.cshaifasweng.OCSFMediatorExample.client;


import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.List;

import il.cshaifasweng.OCSFMediatorExample.entities.*;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import org.apache.poi.xwpf.usermodel.*;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import javax.swing.*;


public class PrimaryController {
	@FXML
	private int msgId;

	@FXML
	private ComboBox<Course> CoursesCB;

	@FXML
	private TableView<ExamForm> ExamsTV;

	@FXML
	private TableColumn<ExamForm, Integer> IDColumn;

	@FXML
	private Button ViewDIgitalButton;

	@FXML
	private Button ViewManualButton;

	@FXML
	private Button addExamButton;

	@FXML
	private Button backButton;

	@FXML
	private TableColumn<ExamForm, SimpleStringProperty> codeColumn;

	@FXML
	private TableColumn<ExamForm, SimpleStringProperty> createdColumn;

	@FXML
	private TableColumn<ExamForm, SimpleStringProperty> creatorColumn;

	@FXML
	private TableColumn<ExamForm, SimpleStringProperty> lastUsedColumn;

	@FXML
	private Button modifyExamButton;

	@FXML
	private Button statsButton;

	@FXML
	private ComboBox<Subject> subjectsCB;

	@FXML
	private TableColumn<ExamForm, SimpleStringProperty> timeColumm;



	@Subscribe
	public void errorEvent(ErrorEvent event){
		DateTimeFormatter dtf = DateTimeFormatter.ofPattern("HH:mm:ss");
		Platform.runLater(() -> {
			Alert alert = new Alert(Alert.AlertType.ERROR,
					String.format("Message:\nId: %d\nData: %s\nTimestamp: %s\n",
							event.getMessage().getId(),
							event.getMessage().getMessage(),
							event.getMessage().getTimeStamp().format(dtf))
			);
			alert.setTitle("Error!");
			alert.setHeaderText("Error:");
			alert.show();
		});
	}

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
			//Simulate Input
			ExamForm sample = new ExamForm();
			sample.setID(1);
			sample.setCode("Code1");
			sample.setCourse(new Course("Infi 1"));
			sample.setSubject(new Subject("Mathematics"));
			sample.setCreator(new Teacher());
			List<Question> sampleQuestions = new ArrayList<>();
			Question question1 = new Question("What is a differential","Ami's favorite snack","I like this question","The answer is A");
			List<String> answers = new ArrayList<>();
			answers.add("Omer");
			answers.add("Ilan");
			answers.add("Edan");
			answers.add("Alon");
			question1.setAnswers(answers);
			sampleQuestions.add(question1);
			sample.setQuestionList(sampleQuestions);
			LocalDate localDate = LocalDate.now();
			// Convert LocalDate to Date
			Date date = Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
			sample.setDateCreated(date);
			ExamsTV.getItems().add(sample);
			ExamsTV.refresh();
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
			JOptionPane.showMessageDialog(null, "Could not Retrieve any subjects", "DataBase Erroe", JOptionPane.WARNING_MESSAGE);
		}

	}
	@FXML
	public void onSubjectSelection() throws IOException {
		List<Course> courses = subjectsCB.getSelectionModel().getSelectedItem().getCourses();
		if(courses != null)
		{
			CoursesCB.getItems().addAll(courses);
			Message message = new Message(++msgId, "Get Exams Forms for Subject: "  + subjectsCB.getSelectionModel().getSelectedItem() );
			message.setData(subjectsCB.getSelectionModel().getSelectedItem());
			SimpleClient.getClient().sendToServer(message);
		}
		else {
			JOptionPane.showMessageDialog(null, "This subject does not contain any courses", "DataBase Erroe", JOptionPane.WARNING_MESSAGE);
		}
	}
	@FXML
	public void onCourseSelection() throws IOException {
		Message message = new Message(++msgId, "Get Exams Forms for Course: "  + CoursesCB.getSelectionModel().getSelectedItem() );
		message.setData(CoursesCB.getSelectionModel().getSelectedItem());
		SimpleClient.getClient().sendToServer(message);
	}
	@FXML
	public void viewDigital()
	{
		ExamForm selectedForm = ExamsTV.getSelectionModel().getSelectedItem();
		//TODO : add digital view after meeting on tuesday

	}
	@FXML
	public void viewManual()
	{
		int questionID = 1;
		Random random = new Random();
		ExamForm selectedForm = ExamsTV.getSelectionModel().getSelectedItem();
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
			int randPlace = random.nextInt(3);
			answers.add(randPlace,question.getCorrectAnswer());
			questionBody.setText(questionID++ +". " + question.getQuestionData());
			questionBody.addBreak();
			questionBody.addBreak();
			questionBody.setText(" A." + answers.get(0),questionBody.getTextPosition());
			questionBody.addBreak();
			questionBody.setText(" B." + answers.get(1),questionBody.getTextPosition());
			questionBody.addBreak();
			questionBody.setText(" C." + answers.get(2),questionBody.getTextPosition());
			questionBody.addBreak();
			questionBody.setText(" D." + answers.get(3),questionBody.getTextPosition());
			questionBody.addBreak();
			questionBody.addBreak();
			questionBody.setText("Student Notes: " + question.getStudentNote(),questionBody.getTextPosition());
			questionBody.addBreak();
			questionBody.addBreak();
			questionBody.setText("Teacher Notes: " + question.getTeacherNote(),questionBody.getTextPosition());
			questionBody.addBreak();
			questionBody.addBreak();

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

	private void openExam(String fileName) {
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
		ExamForm selectedForm = ExamsTV.getSelectionModel().getSelectedItem();
		if(selectedForm != null) {
			System.out.println("Move to Add Exam page");
			EventBus.getDefault().post(new LoadExamEvent(selectedForm,"Modify Exam"));
			SimpleChatClient.setRoot("AddExam");
		}
		else {
			JOptionPane.showMessageDialog(null,"No exam selected","Error!",JOptionPane.ERROR_MESSAGE);
		}
	}
	@FXML
	public void moveToViewStats() throws IOException {
		ExamForm selectedForm = ExamsTV.getSelectionModel().getSelectedItem();
		if(selectedForm != null) {
			System.out.println("Move to Stats page");
			EventBus.getDefault().post(new LoadExamEvent(selectedForm,"View Stats"));
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
		ExamsTV.getItems().addAll(event.getExamForms());
		ExamsTV.refresh();
	}


}
