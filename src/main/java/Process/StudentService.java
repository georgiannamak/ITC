package Process;

import Problem.Configuration;
import Problem.Course;
import Problem.Problem;
import Problem.Student;
import Solution.SolutionClass;
import Solution.SolutionStudent;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

public class StudentService {

    private ArrayList<Student> allStudents;

    public StudentService(ArrayList<Student> allStudents) {
        this.allStudents = allStudents;
    }

    public void connectStudentCoursesWithProblemCourses()
    {
        for(Student student: allStudents)
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
       // ArrayList<Student> allStudents= Registry.getProblem().getStudents();
        allStudents.sort(Comparator.comparing((Student student)->student.getCourses().size()).reversed().thenComparing((Student student) -> student.getId()));
        int i=0;
        allStudents.forEach(student->student.setWeight(allStudents.indexOf(student)));
        while(i<allStudents.size())
        {
            Student student=allStudents.get(i);
            System.out.println("Student " +i +" with id " +student.getId());
            student.setOptions(new StudentOptions(new SolutionStudent(student.getId()),student.getId(),student.getCourses()));
            int problemCourseId=student.getOptions().enrollToCourses2();
            int minIndex=-1;
            if(problemCourseId!=-1) {
                Registry.CreateXmlSolutionFile(Registry.getSolution(),"_Students");
                minIndex = findMinIndexOfStudentEnrolledInCourse(problemCourseId);
                for (int j=0;j<student.getOptions().getClasses().size();j++)
                {
                    student.getOptions().getClasses().get(j).getAssignments().getSolutionClass().getStudents().remove(student.getOptions().getSolutionStudent());
                }
                student.getOptions().setClasses(new ArrayList<>());
                if (minIndex < allStudents.indexOf(student) && new Random().nextInt(100)<=90) {
                    student.setWeight(allStudents.get(minIndex).getWeight());
                } else {
                    minIndex=0;
                    student.setWeight(allStudents.get(0).getWeight() - 1);
                }

                for (int k = minIndex; k < allStudents.indexOf(student); k++) {
                    Student otherStudent=allStudents.get(k);
                    otherStudent.setWeight(otherStudent.getWeight() + 1);
                    //
                    for (int j=0;j<otherStudent.getOptions().getClasses().size();j++)
                    {
                        otherStudent.getOptions().getClasses().get(j).getAssignments().getSolutionClass().getStudents().remove(otherStudent.getOptions().getSolutionStudent());
                    }
                    otherStudent.getOptions().setClasses(new ArrayList<>());
                }
                allStudents.sort(Comparator.comparing(Student::getWeight));
                i=allStudents.indexOf(student);
                System.out.println("New try for students");

            }else i++;

        }
    }

    private int findMinIndexOfStudentEnrolledInCourse(int problemCourseId) {
        Course course=Registry.findCoursebyId(problemCourseId);
        List<Student> studentsEnrolledInCourse=Registry.getProblem().getStudents().stream().filter(student->student.getCourses().contains(course)).collect(Collectors.toList());
        int min=Integer.MAX_VALUE;
        for(Student student:studentsEnrolledInCourse)
        {
            int index=allStudents.indexOf(student);
            if(index<min)
                min=index;
        }
        return min;
    }

    public ArrayList<Student> getAllStudents() {
        return allStudents;
    }

}
