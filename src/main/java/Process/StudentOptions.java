package Process;

import Problem.*;
import Problem.Class;
import Solution.SolutionClass;
import Solution.SolutionStudent;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Random;

public class StudentOptions {
    private SolutionStudent solutionStudent;
    private int id;
    private ArrayList<Course> courses;
    private ArrayList<Class> classes;

    public StudentOptions(SolutionStudent solutionStudent, int id, ArrayList<Course> courses) {
        this.solutionStudent = solutionStudent;
        this.id = id;
        this.courses = new ArrayList<>(courses);
        classes=new ArrayList<>();
    }

    public void enrollToCourses() {
        Random random=new Random();
        for(Course c:courses) {


                Configuration configuration = c.getConfigurations().get(random.nextInt(c.getConfigurations().size()));
                configuration.getSubparts().sort(Comparator.comparing(Subpart::getSubpartId));
                for (Subpart subpart : configuration.getSubparts()) {
                    Class sameSub=classes.stream().filter((Class classroom)->classroom.getSubpart()==subpart).findAny().orElse(null);
                    if(sameSub==null) {
                        Class previous= new Class();
                        for (int i = 0; i < subpart.getClasses().size(); i++) {
                            Class courseClass=subpart.getClasses().stream().filter((Class classroom)-> !isThereConflict(classroom)).
                                    findAny().orElse(null);
                            if(courseClass==null || courseClass==previous)
                                courseClass = subpart.getClasses().get(random.nextInt(subpart.getClasses().size()));
                            if (courseClass.getAssignments().getSolutionClass().getStudents().size() < courseClass.getLimit()) {
                                if (courseClass.getParent() == 0) {

                                    if (courseClass.getChild() != null) {
                                       if (courseClass.getChild().getAssignments().getSolutionClass().getStudents().size() < courseClass.getChild().getLimit()) {
                                           courseClass.getAssignments().getSolutionClass().getStudents().add(solutionStudent);
                                           classes.add(courseClass);
                                           classes.add(courseClass.getChild());
                                           courseClass.getChild().getAssignments().getSolutionClass().getStudents().add(solutionStudent);
                                           System.out.println("Enrolling student "+id +" to " +" class" +courseClass.getClassId() +"without parent" +"child=" +courseClass.getChild().getClassId());
                                       }
                                    }
                                    else
                                    {
                                        courseClass.getAssignments().getSolutionClass().getStudents().add(solutionStudent);
                                        classes.add(courseClass);
                                        System.out.println("Enrolling student "+id +" to " +" class" +courseClass.getClassId() +"without parent");
                                    }
                                    break;
                                } else {
                                    if (classes.contains(Registry.findClassById(courseClass.getParent()))) {
                                        courseClass.getAssignments().getSolutionClass().getStudents().add(solutionStudent);
                                        classes.add(courseClass);
                                        System.out.println("Enrolling student "+id +" to " +" class" +courseClass.getClassId() +"with parent allready");
                                        break;
                                    }else
                                    {
                                        Class parentClass=Registry.findClassById(courseClass.getParent());
                                        if(parentClass.getAssignments().getSolutionClass().getStudents().size()<parentClass.getLimit()){
                                            Class otherClassFromParentsSub=classes.stream().filter((Class c2)->c2.getSubpart()==parentClass.getSubpart()).findAny().orElse(null);
                                            if(otherClassFromParentsSub!=null) {
                                                classes.remove(otherClassFromParentsSub);
                                                otherClassFromParentsSub.getAssignments().getSolutionClass().getStudents().remove(solutionStudent);
                                            }
                                            parentClass.getAssignments().getSolutionClass().getStudents().add(solutionStudent);
                                            classes.add(parentClass);
                                            courseClass.getAssignments().getSolutionClass().getStudents().add(solutionStudent);
                                            classes.add(courseClass);
                                            System.out.println("Enrolling sudent "+id +" to " +" class" +courseClass.getClassId() +"and parent "+parentClass.getClassId());

                                            break;
                                        }//den me noiazei an exw allon apo auto to subpart giati tha hmoun hdh kai sta 2

                                    }
                                }
                            }
                            previous=courseClass;
                        }
                    }
                }


        }

    }

   public boolean isThereConflict(Class c)
   {
       for(Class classroom:classes)
       {
           if(!new Constraint("SameAttendees").valideConstraintForTheseClasses(c.getAssignments().getSolutionClass(),classroom.getAssignments().getSolutionClass()))
               return true;
       }
       return false;
   }
}
