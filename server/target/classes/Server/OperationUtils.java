package Server;

import Entities.ClassExam;
import Entities.HSTS_Enums;
import Entities.StudentExam;

import java.util.List;
import java.util.stream.Collectors;

public class OperationUtils {

    // Calculate ean and variance and store them in class exam
    public static ClassExam UpdateClassExamStats(ClassExam classExam) {
        double mean = 0.0;
        double variance = 0.0;

        // Extract exam's grades which were approved by the teacher
        List<StudentExam> studentExams = classExam.getStudentExams().stream().
                filter(studentExam -> studentExam.getStatus() == HSTS_Enums.StatusEnum.Approved).collect(Collectors.toList());
        List<Integer> grades = studentExams.stream().map(StudentExam::getGrade).collect(Collectors.toList());
        int approvedExamsNum = grades.size();

        // Mean calculation
        for (int i = 0; i < approvedExamsNum; i++)
        {
            mean += grades.get(i);
        }
        mean /= approvedExamsNum;

        // variance calculation
        if (approvedExamsNum != 1)
        {
            for (int i = 0; i < approvedExamsNum; i++)
            {
                variance += Math.pow((grades.get(i) - mean), 2);
            }
            variance = variance / (approvedExamsNum-1);
        }

        // set and return result
        classExam.setApprovedExamsNum(approvedExamsNum);
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