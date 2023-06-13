package Server;
import Entities.SchoolOwned.ClassExam;
import Entities.Enums;
import Entities.StudentOwned.StudentExam;

import java.util.List;
import java.util.stream.Collectors;

public class OperationUtils {

    // Calculate ean and variance and store them in class exam
    public static ClassExam UpdateClassExamStats(ClassExam classExam) {
        double mean = 0.0;
        double variance = 0.0;

        // Extract exam's grades which were approved by the teacher
        List<StudentExam> studentExams = classExam.getStudentExams().stream().
                filter(studentExam -> studentExam.getStatus() == Enums.submissionStatus.Approved).collect(Collectors.toList());
        List<Integer> grades = studentExams.stream().map(StudentExam::getGrade).collect(Collectors.toList());
        int approvedExamsNum = grades.size();

        // Mean calculation
        for (int i = 0; i < approvedExamsNum; i++)
        {
            mean += grades.get(i);
        }

        // variance calculation
        if (approvedExamsNum != 1 || approvedExamsNum == 0)
        {
            mean /= approvedExamsNum;
            for (int i = 0; i < approvedExamsNum; i++)
            {
                variance += Math.pow((grades.get(i) - mean), 2);
            }
            variance = variance / (approvedExamsNum-1);
        }

        // set and return result
        classExam.setApprovedExamsNum(approvedExamsNum);
        classExam.setExamToEvaluate(classExam.getStudentExams().stream().filter(studentExam ->
                studentExam.getStatus() == Enums.submissionStatus.ToEvaluate).collect(Collectors.toList()).size());
        classExam.setGradesMean(mean);
        classExam.setGradesVariance(variance);
        return classExam;
    }


    // Useful when building the code for ExamForm and Question
    static public String IDZeroPadding(String ID, int requiredLength)
    {
        while (ID.length() < requiredLength)
        {
            ID = '0' + ID;
        }
        return ID;
    }


}
