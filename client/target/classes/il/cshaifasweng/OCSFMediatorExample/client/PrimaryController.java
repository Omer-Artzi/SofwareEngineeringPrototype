package il.cshaifasweng.OCSFMediatorExample.client;


import java.awt.event.ActionEvent;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

import il.cshaifasweng.OCSFMediatorExample.entities.*;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import javax.swing.*;


public class PrimaryController {
	@FXML
	private int msgId;

	@FXML
	private ComboBox<Course> CoursesCB;

	@FXML
	private TableView<Exam> ExamsTV;

	@FXML
	private TableColumn<Exam, Integer> IDColumn;

	@FXML
	private Button ViewDIgitalButton;

	@FXML
	private Button ViewManualButton;

	@FXML
	private Button addExamButton;

	@FXML
	private Button backButton;

	@FXML
	private TableColumn<Exam, SimpleStringProperty> codeColumn;

	@FXML
	private TableColumn<Exam, SimpleStringProperty> createdColumn;

	@FXML
	private TableColumn<Exam, SimpleStringProperty> creatorColumn;

	@FXML
	private TableColumn<Exam, SimpleStringProperty> lastUsedColumn;

	@FXML
	private Button modifyExamButton;

	@FXML
	private Button statsButton;

	@FXML
	private ComboBox<Subject> subjectsCB;

	@FXML
	private TableColumn<Exam, SimpleStringProperty> timeColumm;



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
			codeColumn.setCellValueFactory(new PropertyValueFactory<>("studentName"));
			createdColumn.setCellValueFactory(new PropertyValueFactory<>(""));
			ExamsTV.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}


	@FXML
	public void refreshStudents() throws IOException {
		Message message = new Message(1, "Get Students");
		SimpleClient.getClient().sendToServer(message);
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
			Message message = new Message(++msgId, "Get Exams Forms for Subject: "  + CoursesCB.getSelectionModel().getSelectedItem() );
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


}
