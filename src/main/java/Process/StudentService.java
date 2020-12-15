package Process;

import Problem.Configuration;
import Problem.Course;
import Problem.Student;
import Solution.SolutionStudent;

import java.util.ArrayList;
import java.util.Comparator;

public class StudentService {

    public void connectStudentCoursesWithProblemCourses()
    {
        for(Student student: Registry.problem.getStudents())
        {
            ArrayList<Course> newCourses = new ArrayList<>();
            for(Course course:student.getCourses())
            {
                newCourses.add(Registry.findCoursebyId(course.getCourse_id()));
            }
            student.setCourses(newCourses);
        }
    }

    public void assignStudentsToCourses()
    {
        Registry.problem.getStudents().sort(Comparator.comparing((Student student)->student.getCourses().size()));
               int i=0;                                        // .thenComparing((Course course)->course.getConfigurations().size()));
        for (Student student: Registry.problem.getStudents()
             ) {
            student.setOptions(new StudentOptions(new SolutionStudent(student.getId()),student.getId(),student.getCourses()));
            student.getOptions().enrollToCourses();
            System.out.println(i++);
        }
    }
}
