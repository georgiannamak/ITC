package Process;

import Problem.Class;
import Problem.Configuration;
import Problem.Course;
import Problem.Subpart;
import Solution.SolutionStudent;

import java.util.ArrayList;
import java.util.Random;

public class StudentOptions {
    private SolutionStudent solutionStudent;
    private int id;
    private ArrayList<Course> courses;

    public StudentOptions(SolutionStudent solutionStudent, int id, ArrayList<Course> courses) {
        this.solutionStudent = solutionStudent;
        this.id = id;
        this.courses = new ArrayList<>(courses);
    }

    public void enrollToCourses() {
        Random random=new Random();
        for(Course c:courses){
            if(c.getConfigurations().size()==1)
            {
                for (Subpart subpart:c.getConfigurations().get(0).getSubparts())
                {
                    for(int i=0 ; i<subpart.getClasses().size() ;i++){
                        Class courseClass = subpart.getClasses().get(random.nextInt(subpart.getClasses().size()));
                        if (courseClass.getAssignments().getSolutionClass().getStudents().size() < courseClass.getLimit() ) {
                            courseClass.getAssignments().getSolutionClass().getStudents().add(solutionStudent);
                            break;
                        }
                    }
                }
            }
            else
            {
                for(int i=0;i<c.getConfigurations().size();i++)
                {
                    Configuration configuration= c.getConfigurations().get(random.nextInt(c.getConfigurations().size()));
                    for (Subpart subpart:c.getConfigurations().get(0).getSubparts())
                    {
                        for(int j=0 ; j<subpart.getClasses().size() ;j++){
                            Class courseClass = subpart.getClasses().get(new Random().nextInt(subpart.getClasses().size()));
                            if (courseClass.getAssignments().getSolutionClass().getStudents().size() < courseClass.getLimit()) {
                                courseClass.getAssignments().getSolutionClass().getStudents().add(solutionStudent);
                                break;
                            }
                        }
                    }

                }
            }
        }

    }
}
