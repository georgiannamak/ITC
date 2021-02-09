package Process;

import Problem.*;
import Problem.Class;
import Solution.SolutionClass;
import Solution.SolutionStudent;
//import jdk.internal.loader.AbstractClassLoaderValue;

import java.util.*;

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

    /*public void enrollToCourses() {
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

    }*/

    public int  enrollToCourses2() {

        for(Course course:courses)
        {
            Collections.shuffle(course.getConfigurations());
            boolean enrolledToAllClasses=false;
            int i=0;
            while(i<course.getConfigurations().size() && !enrolledToAllClasses) {
                Configuration configuration = course.getConfigurations().get(i);
                if(!enrollToClassesOfConfiguration(configuration)) {
                    i++;
                    System.out.println("Changing config at course " +course.getCourse_id());
                    //for (Class classroom : classes) {
                       // classroom.getAssignments().getSolutionClass().getStudents().remove(solutionStudent);
                   // }
                    for(Subpart subpart:configuration.getSubparts())
                    {
                        for(Class aClass:subpart.getClasses()) {
                            classes.remove(aClass);
                            aClass.getAssignments().getSolutionClass().getStudents().remove(solutionStudent);
                        }
                    }
                }else
                    enrolledToAllClasses=true;
            }
            if(!enrolledToAllClasses)
                return course.getCourse_id();
        }
        return -1;
    }

    private boolean enrollToClassesOfConfiguration(Configuration configuration) {
        for(Subpart subpart:configuration.getSubparts()) {
            Class sameSub = classes.stream().filter(classroom -> classroom.getSubpart() == subpart).findAny().orElse(null);
            if (sameSub == null) {
                ArrayList<Class> notChecked = new ArrayList<Class>(subpart.getClasses());
                Collections.shuffle(notChecked);
                boolean foundClass = false;
                while (!foundClass && notChecked.size() > 0) {
                    Class courseClass = null;
                    for (Class c : notChecked) {
                        if (classes.contains(Registry.findClassById(c.getParent())) && c.getAssignments().getSolutionClass().getStudents().size() < c.getLimit()) {
                            courseClass = c;
                            break;
                        }
                    }
                    if (courseClass == null) {
                        courseClass = notChecked.stream()
                                .filter((Class classroom) -> !isThereConflict(classroom))
                                .filter((Class classroom) -> classroom.getAssignments().getSolutionClass().getStudents().size() < classroom.getLimit())
                                .filter((Class classroom) -> classroom.getParent() == 0)
                                .findAny().orElse(null);
                    }
                    if (courseClass != null) {
                        courseClass.getAssignments().getSolutionClass().addStudent(solutionStudent);
                        classes.add(courseClass);
                        foundClass = true;
                    } else {
                        courseClass=notChecked.get(0);
                        if (courseClass.getAssignments().getSolutionClass().getStudents().size()<courseClass.getLimit()) {
                            if (courseClass.getParent() == 0) {
                                courseClass.getAssignments().getSolutionClass().addStudent(solutionStudent);
                                classes.add(courseClass);
                                foundClass = true;
                            } else {
                                Class parentClass = Registry.findClassById(courseClass.getParent());
                                 sameSub = classes.stream().filter(classroom -> classroom.getSubpart() == parentClass.getSubpart()).findAny().orElse(null);
                                if (sameSub == null && parentClass.getAssignments().getSolutionClass().getStudents().size() < parentClass.getLimit() &&parentClass.getParent()==0) {
                                    courseClass.getAssignments().getSolutionClass().addStudent(solutionStudent);
                                    classes.add(courseClass);
                                    foundClass = true;
                                    parentClass.getAssignments().getSolutionClass().addStudent(solutionStudent);
                                    classes.add(parentClass);
                                }
                            }
                        }
                        //if(courseClass==null)
                        // return false;
                    }
                    if (!foundClass)
                        notChecked.remove(courseClass);
                }
                if (notChecked.size() == 0)
                    return false;
            }
        }
        return true;
    }

   /* public boolean findAlternativeClassFromSubpart(Subpart subpart)
    {
        Class currentClass = classes.stream().filter(c->c.getSubpart()==subpart).findAny().orElse(null);
        if(currentClass!=null){
            Class courseClass=subpart.getClasses().stream().filter(c->c!=currentClass)
                    .filter((Class classroom) -> !isThereConflict(classroom))
                    .filter((Class classroom) -> classroom.getAssignments().getSolutionClass().getStudents().size() < classroom.getLimit())
                    .findAny().orElse(null);
            if (courseClass != null) {
                courseClass.getAssignments().getSolutionClass().getStudents().add(solutionStudent);
                classes.add(courseClass);
                classes.remove(currentClass);
                currentClass.getAssignments().getSolutionClass().getStudents().remove(solutionStudent);
                return true;
            } else {

                courseClass = subpart.getClasses().stream()
                        .filter((Class classroom) -> classroom.getAssignments().getSolutionClass().getStudents().size() < classroom.getLimit())
                        .findAny().orElse(null);
                if (courseClass != null && courseClass.getParent() == 0) {
                    courseClass.getAssignments().getSolutionClass().getStudents().add(solutionStudent);
                    classes.add(courseClass);
                    classes.remove(currentClass);
                    currentClass.getAssignments().getSolutionClass().getStudents().remove(solutionStudent);
                    return true;
                }
            }
        }
        return false;
    }*/


   public boolean isThereConflict(Class c)
   {
       for(Class classroom:classes)
       {
           if(!new Constraint("SameAttendees").valideConstraintForTheseClasses(c.getAssignments().getSolutionClass(),classroom.getAssignments().getSolutionClass()))
               return true;
       }
       return false;
   }

    public SolutionStudent getSolutionStudent() {
        return solutionStudent;
    }

    public void setSolutionStudent(SolutionStudent solutionStudent) {
        this.solutionStudent = solutionStudent;
    }

    public ArrayList<Class> getClasses() {
        return classes;
    }

    public void setClasses(ArrayList<Class> classes) {
        this.classes = classes;
    }
}
