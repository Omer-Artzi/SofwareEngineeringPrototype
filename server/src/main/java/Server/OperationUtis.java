package Server;

import Entities.ClassExam;
import Entities.StudentExam;

import java.util.List;
import java.util.stream.Collectors;

public class OperationUtis {
    public static ClassExam UpdateClassExamStats(ClassExam classExam) {
        double mean = 0.0;
        double variance = 0.0;
        List<StudentExam> studentExams = classExam.getStudentExams().stream().
                filter(studentExam -> studentExam.getStatus().startsWith("Approved")).collect(Collectors.toList());
        List<Integer> grades = studentExams.stream().map(StudentExam::getGrade).collect(Collectors.toList());
        int approvedExamsNum = grades.size();
        for (int i = 0; i < classExam.getStudentExams().size(); i++)
        {
            System.out.println(classExam.getStudentExams().get(i).getStatus());
        }
        System.out.println(approvedExamsNum);
        System.out.println(studentExams.size());

        for (int i = 0; i < approvedExamsNum; i++)
        {
            mean += grades.get(i);
        }
        mean /= approvedExamsNum;

        for (int i = 0; i < approvedExamsNum; i++)
        {
            variance += Math.pow((grades.get(i) - mean), 2);
        }

        variance = Math.sqrt(variance / approvedExamsNum);

        classExam.setApprovedExamsNum(approvedExamsNum);
        classExam.setGradesMean(mean);
        classExam.setGradesVariance(variance);

        return classExam;
    }
}
