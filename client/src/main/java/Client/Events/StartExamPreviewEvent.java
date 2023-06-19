package Client.Events;

import Entities.SchoolOwned.ExamForm;

public class StartExamPreviewEvent {
    private static ExamForm examForm;
    public StartExamPreviewEvent(ExamForm selectedExam) {
        examForm = selectedExam;
    }

    public static ExamForm getExamForm() {
        return examForm;
    }

    public void setExamForm(ExamForm examForm) {
        this.examForm = examForm;
    }
}
