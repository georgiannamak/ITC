package Process;

import Problem.Problem;
import Problem.Constraint;
import Problem.Room;
import Problem.Class;
import Problem.Course;
import Problem.Subpart;
import Problem.Configuration;
import Solution.Solution;
import Solution.SolutionClass;
import Problem.Student;
import Problem.Time;
import com.sun.xml.bind.v2.runtime.reflect.opt.Const;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

public class Registry {
    public static Problem problem,bestProblem;
    public static Solution solution,bestSolution;
    public static XMLToObject obj;
    private static Set<SolutionClass> classesWithoutRoom,bestClassesWithoutRoom;
    public static int penalty=Integer.MAX_VALUE;
    public static int bestPenalty=Integer.MAX_VALUE;

    public static void main(String[] args) {

        int n = 0;
        //ObjectToXML xml2;

        System.out.println("n= " + n);
        obj = new XMLToObject();
        problem = obj.getProblem();
        System.out.println("problem = " + problem.getName());
        problem.findAllClasses();

        solution = new Solution(problem.getName());
        //ObjectToXML xml4 = new ObjectToXML(problem);
        createAvailabilitiesOfRooms();
        connectRoomsWithRoomsAvailableForClass();
       // findChildClass();
        //problem.getClasses().get(0).getRooms().get(0).getAvailability().printAvailability();
        createClassPossibleAssignments();
        handleConstraints();
        findClassesForRooms();

        problem.getClasses().sort(Comparator.comparing((Class c) -> c.getAssignments().getRooms().size() * c.getAssignments().getTimes().size())
                .thenComparing((Class c) -> c.getAssignments().getRequiredConstraints().size()).reversed()
                .thenComparing((Class c) -> c.getAssignments().getOtherClassesEvolvedInConstraints().size()).reversed());
        //problem.getClasses().sort(Comparator.comparing((Class c) -> c.getAssignments().getWeight()));
        for (Class c : problem.getClasses())
            c.getAssignments().setWeight(problem.getClasses().indexOf(c));
        problem.getClasses().sort(Comparator.comparing((Class c) -> c.getAssignments().getWeight()));

        Class c = new Class();
        int i = 0, errorIndex = 0;
        int j = 0;
        while (penalty > 0) {

            boolean flag = false;
            System.out.println("New Try!");
            //problem.getClasses().sort(Comparator.comparing((Class c) -> c.getAssignments().getWeight()));

            //Class c = new Class();
            //int i = 0, errorIndex = 0;
            //int j = 0;

            while (i < problem.getClasses().size()) {
                c = problem.getClasses().get(i);
                System.out.println("Handling classId " + c.getClassId() +" at "+(i+1));
                // if (c.getRooms().size() > 0 && c.getAssignments().getSolutionClass().getRoomId()<=0)  {
                if (!c.getAssignments().AssignBestPossibleRoomAndTime2()) {
                    List<Class> otherClasses = c.getAssignments().getOtherClassesEvolvedInConstraints().stream().
                            filter((Class c2) -> c2.getAssignments().getSolutionClass().getStart() > 0)
                            .collect(Collectors.toList());

                    if (c.getRooms().size() > 0) {
                        if (otherClasses.size() == 0) {
                            System.out.println("Exiting ");
                            j = 0;
                            errorIndex = i;
                            i = problem.getClasses().size();
                        } else if (otherClasses.size() == 1) {
                            // if(c.getClassId()==261 || c.getClassId()==718)
                            //System.out.println("");
                            if (!c.getAssignments().fixConstraintForOtherClass(otherClasses.get(0))) {
                                System.out.println("Exiting ");
                                j = 0;
                                errorIndex = i;
                                i = problem.getClasses().size();
                                for (Class otherClass : otherClasses)
                                    otherClass.getAssignments().setWeight(problem.getClasses().get(0).getAssignments().getWeight() - 1);//ta vazw prwta gia na meinoun anephrreasta apo ta alla
                            } else i++;
                        } else {
                            Random rand = new Random();
                            Class otherClass = otherClasses.get(rand.nextInt(otherClasses.size()));
                            int p = new Random().nextInt(100);
                            //for (Class otherClass : OtherClasses) {
                            if (j >= 7 || j >= otherClass.getTime().size())
                                flag = true;
                            if (otherClass.getAssignments().getTimes().size() == 1 || (p < 30 && otherClass.getAssignments().getRooms().size() > 1)) {
                                if (!otherClass.getAssignments().findAlternativeRoomForCurrentTime(flag)) {
                                    System.out.println("Exiting ");
                                    j = 0;
                                    errorIndex = i;
                                    i = problem.getClasses().size();
                                    break;
                                }
                            } else if (!otherClass.getAssignments().findAlternativeTimeForCurrentRoom(flag)) {// || j==otherClass.getTime().size()) {

                                System.out.println("Exiting ");
                                j = 0;
                                errorIndex = i;
                                i = problem.getClasses().size();
                                break;
                            }

                            //}
                        }


                        j++;
                        System.out.println(j);
                    } else {
                        System.out.println("Exiting ");
                        j = 0;
                        errorIndex = i;
                        i = problem.getClasses().size();
                        break;
                    }
                } else {
                    if (c.getRooms().size() > 0)
                        System.out.println("Sucess classId=" + c.getClassId() + " RoomPenalty=" + c.getAssignments().getCurrentRoom().getRoomPenaltyForClass(c.getClassId()) + " TimePenalty=" + c.getAssignments().getCurrentTime().getPenalty());
                    else
                        System.out.println("Sucess classId=" + c.getClassId() + " TimePenalty=" + c.getAssignments().getCurrentTime().getPenalty());
                    j = 0;
                    i++;
                }

                //}
                // else i++;

            }
            calculatePenalty();
            if (c.equals(problem.getClasses().get(problem.getClasses().size() - 1)) && penalty > 0) {
                for (SolutionClass sc : classesWithoutRoom) {
                    if (!sc.getAssignmentsOfClass().AssignBestPossibleRoomAndTime2())
                        errorIndex = problem.getClasses().indexOf(findClassById(sc.getId()));
                }
                calculatePenalty();
            }
            if (penalty > 0) {
                // ObjectToXML sol= new ObjectToXML(solution);

                //System.out.println(index);
                c = problem.getClasses().get(errorIndex);
                int minIndex = findOtherClassesMinIndex(c);
                Random random = new Random();
                int p = random.nextInt(100);

                if (minIndex < errorIndex && p > 30) {
                    System.out.println("p=" + p + " min=" + problem.getClasses().get(minIndex).getClassId() + " minIndex=" + minIndex);
                    c.getAssignments().setWeight(problem.getClasses().get(minIndex).getAssignments().getWeight());
                    for (int k = minIndex; k < errorIndex; k++)
                        problem.getClasses().get(k).getAssignments().setWeight(problem.getClasses().get(k).getAssignments().getWeight() + 1);
                } else {
                    System.out.println("p=" + p + " min=" + problem.getClasses().get(0).getClassId() + " minIndex=" + minIndex);
                    c.getAssignments().setWeight(problem.getClasses().get(0).getAssignments().getWeight() - 1);
                }

                problem.getClasses().sort(Comparator.comparing((Class problemClass) -> problemClass.getAssignments().getWeight()));
                int newIndex = problem.getClasses().indexOf(c);
                for (int q = newIndex; q < problem.getClasses().size(); q++) {
                    SolutionClass sc = problem.getClasses().get(q).getAssignments().getSolutionClass();
                    if (sc.getRoomId() > 0)
                        sc.getAssignmentsOfClass().getCurrentRoom().getAvailability().removeRoomfromClass(sc.getId());
                    else if (sc.getRoomId() == -5) {
                        sc.getAssignmentsOfClass().setCurrentTime(null);
                        sc.getAssignmentsOfClass().getSolutionClass().setStart(0);
                        sc.getAssignmentsOfClass().getSolutionClass().setWeeks(null);
                        sc.getAssignmentsOfClass().getSolutionClass().setDays(null);
                    }
                    //sc.getAssignmentsOfClass().getCurrentRoom()
                }
                i = newIndex;
                errorIndex = 0;
                j = 0;
            }

        }

        CreateXmlSolutionFile(solution,"_Final");


        //PHASE 2
        int sum=0;
        for(Constraint constraint:problem.getSoftConstraints())
        {
            int violatedPairs=constraint.violatedPairs();
            if(violatedPairs!=0) {
                constraint.setRespected(false);
                sum+=(constraint.getPenalty()*problem.getOptimization().getDistribution()*violatedPairs);
            }
        }

        List<SolutionClass> bestSolutionCLasses =bestSolution.getClasses().stream().
                sorted(Comparator.comparing(sc->sc.getAssignmentsOfClass().getSoftConstraints().size())).
                collect(Collectors.toList());
        System.out.println("Dist Penalty= " +sum);
        for(SolutionClass sc:bestSolutionCLasses)
        {
            sum+=sc.getAssignmentsOfClass().getCurrentTime().getPenalty()*problem.getOptimization().getTime();
            if(sc.getRoomId()!=-5)
                sum+=sc.getAssignmentsOfClass().getCurrentRoom().getRoomPenaltyForClass(sc.getId())*problem.getOptimization().getRoom();
        }
        System.out.println("Final Penalty = " +sum );

        int times=0;
        int currentSum=0;
        int oldSum=sum;
        while(times<50 && currentSum<oldSum) {
            if(times!=0) {
                oldSum = currentSum;
                CreateXmlSolutionFile(bestSolution,"_Final");
                currentSum = 0;
            }
            System.out.println("tines="+times);
            for (SolutionClass sc : bestSolutionCLasses) {
               // int k = 0;
                Time oldCurrentTime;
                Room oldCurrentRoom;
                int currentRoomPenalty = 0;
                boolean flag = true;
                //while (flag && k < Integer.max(sc.getAssignmentsOfClass().getRooms().size(), sc.getAssignmentsOfClass().getTimes().size())) {
                    oldCurrentTime = sc.getAssignmentsOfClass().getCurrentTime();
                    oldCurrentRoom = sc.getAssignmentsOfClass().getCurrentRoom();
                    int currentDistributionPenalty = sc.getAssignmentsOfClass().calculateDistributionPenalty() * problem.getOptimization().getDistribution();

                    int currentTimePenalty = oldCurrentTime.getPenalty() * problem.getOptimization().getTime();
                    if (oldCurrentRoom != null)
                        currentRoomPenalty = oldCurrentRoom.getRoomPenaltyForClass(sc.getId()) * problem.getOptimization().getRoom();
                    System.out.println("current Time + Room + Dist =" + currentTimePenalty + " " + currentRoomPenalty + " " + currentDistributionPenalty + " for id= " + sc.getId());
                    if (currentTimePenalty == 0 && currentRoomPenalty == 0 && currentDistributionPenalty == 0)
                        flag = false;
                    else {
                        if (currentTimePenalty < currentRoomPenalty && oldCurrentRoom != null) {

                            if (!sc.getAssignmentsOfClass().findAlternativeRoomForCurrentTimeIncludingPenalty(false)) {
                                sc.setRoomAndTime(oldCurrentRoom.getId(), oldCurrentTime);
                                if (!sc.getAssignmentsOfClass().findAlternativeTimeForCurrentRoomIncludingPenalty(false))
                                    flag = false;
                            }

                        } else {
                            if (!sc.getAssignmentsOfClass().findAlternativeTimeForCurrentRoomIncludingPenalty(false)) {
                                if (oldCurrentRoom != null) {
                                    sc.setRoomAndTime(oldCurrentRoom.getId(), oldCurrentTime);
                                    if (!sc.getAssignmentsOfClass().findAlternativeRoomForCurrentTimeIncludingPenalty(false))
                                        flag = false;
                                } else {
                                    sc.setDays(oldCurrentTime.getDays());
                                    sc.setWeeks(oldCurrentTime.getWeeks());
                                    sc.setStart(oldCurrentTime.getStart());
                                    sc.setEnd(oldCurrentTime.getEnd());
                                    flag = false;
                                }

                            }
                        }
                        int newTineRoomPenalty;
                        if (flag) {
                            int newDistPenalty = 0;
                            for (Constraint constraint : sc.getAssignmentsOfClass().getSoftConstraints()) {
                                int violatedPairs=constraint.violatedPairs();
                                if (violatedPairs!=0)
                                    newDistPenalty += constraint.getPenalty()*violatedPairs;
                            }
                            if (sc.getAssignmentsOfClass().getCurrentRoom() != null)
                                newTineRoomPenalty = sc.getAssignmentsOfClass().getCurrentTime().getPenalty() * problem.getOptimization().getTime() + sc.getAssignmentsOfClass().getCurrentRoom().getRoomPenaltyForClass(sc.getId()) * problem.getOptimization().getRoom();
                            else
                                newTineRoomPenalty = sc.getAssignmentsOfClass().getCurrentTime().getPenalty() * problem.getOptimization().getTime();
                            int p = new Random().nextInt(100);
                            if (newDistPenalty * problem.getOptimization().getDistribution() + newTineRoomPenalty >= currentDistributionPenalty + currentRoomPenalty + currentTimePenalty && p <= 80)
                                flag = false;
                            System.out.println("p=" + p + " current dist=" + currentDistributionPenalty + " currentRoom= " + currentRoomPenalty + " currentTime= " + currentTimePenalty + " new dist= " + newDistPenalty * problem.getOptimization().getDistribution() + " newTimeAndRoompen= " + newTineRoomPenalty);
                            if (flag)
                                System.out.println("You saved " + (currentDistributionPenalty + currentRoomPenalty + currentTimePenalty - newDistPenalty* problem.getOptimization().getDistribution() - newTineRoomPenalty));
                        }
                        //k++;
                        if (!flag && oldCurrentRoom != null)
                            sc.setRoomAndTime(oldCurrentRoom.getId(), oldCurrentTime);
                        else if (oldCurrentRoom == null) {
                            sc.setDays(oldCurrentTime.getDays());
                            sc.setWeeks(oldCurrentTime.getWeeks());
                            sc.setStart(oldCurrentTime.getStart());
                            sc.setEnd(oldCurrentTime.getEnd());
                        }
                    //}
                }
            }
            for(Constraint constraint:problem.getSoftConstraints())
            {
                int violatedPairs=constraint.violatedPairs();
                if(violatedPairs!=0) {
                    constraint.setRespected(false);
                    currentSum+=(constraint.getPenalty()*problem.getOptimization().getDistribution()*violatedPairs);
                }
            }
            System.out.println("Dist Penalty= " +currentSum);
            for(Class c1:problem.getClasses())
            {
                currentSum+=c1.getAssignments().getCurrentTime().getPenalty()*problem.getOptimization().getTime();
                if(c1.getAssignments().getCurrentRoom()!=null)
                    currentSum+=c1.getAssignments().getCurrentRoom().getRoomPenaltyForClass(c1.getClassId())*problem.getOptimization().getRoom();
            }
            System.out.println("Final Penalty = " +currentSum );
            times++;

        }
        if(times==50)
            CreateXmlSolutionFile(solution,"_Final");
        else
        {
            XMLToObject obj= new XMLToObject("solution_"+problem.getName()+"_Final.xml");
            Solution bestSolutionFound=obj.getSolution();
            for(SolutionClass solutionClass:bestSolutionCLasses)//bestSolutionCLasses)
            {
                for(SolutionClass solutionClassofNew:bestSolutionFound.getClasses()){
                    if(solutionClass.getId()==solutionClassofNew.getId())
                    {
                        solutionClass.setRoomId(solutionClassofNew.getRoomId());
                        solutionClass.setStart(solutionClassofNew.getStart());
                        solutionClass.setDays(solutionClassofNew.getDays());
                        solutionClass.setWeeks(solutionClassofNew.getWeeks());
                        solutionClass.setEnd(solutionClassofNew.getEnd());
                    }
                }
            }
        }
       // XMLToObject object = new XMLToObject()
        StudentService studentService = new StudentService(Registry.getProblem().getStudents());
        studentService.connectStudentCoursesWithProblemCourses();
        studentService.assignStudentsToCourses();
        ObjectToXML xml = new ObjectToXML(solution,"_Final");
    }

    public static void CreateXmlSolutionFile(Solution solution,String type) {
        for (SolutionClass sc : solution.getClasses()) {
            if (sc.getRoomId() == -5)
                sc.setRoomId(null);
        }
        ObjectToXML xml2 = new ObjectToXML(solution,type);
        for (SolutionClass sc : solution.getClasses()) {
            if (sc.getRoomId() == null)
                sc.setRoomId(-5);
        }
    }

    private static void findChildClass() {
        for(Course course:problem.getCourses())
        {
            for(Configuration config:course.getConfigurations())
            {
                for(Subpart subpart:config.getSubparts())
                {
                    for(Class c:subpart.getClasses())
                    {
                        c.setSubpart(subpart);
                        if(c.getParent()!=0)
                        {
                           findClassById(c.getParent()).setChild(c);
                        }

                    }


                }
            }
        }
    }

    private static int findOtherClassesMinIndex(Class c) {
        int min=Integer.MAX_VALUE;
        for(Class otherClass:c.getAssignments().getOtherClassesEvolvedInConstraints())
        {
            int index=problem.getClasses().indexOf(otherClass);
            if(index<min)
                min=index;
        }
        return min;
    }

    private static void assignRandomRoomToClasses2() {

        ArrayList<Class> classesToHandle= new ArrayList<Class>(problem.getClasses());
        classesToHandle.removeIf((Class c)->c.getRooms().size()==0);
        classesToHandle.sort(Comparator.comparing((Class c)->c.getTime().size()*c.getRooms().size()));
        ArrayList<Room> rooms = new ArrayList<>(problem.getRooms());
        rooms.sort(Comparator.comparing((Room room)->room.getAvailability().getClasses().size()));
        while(!classesToHandle.isEmpty()) {
            Date date = new Date();
            SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
            System.out.println(formatter.format(date));
            //System.out.println("Classes");
            long start =System.currentTimeMillis();
            int i=0;
            while (i<problem.getClasses().size()/20 && (!classesToHandle.isEmpty())) {
                //System.out.println("id=");
                classesToHandle.get(0).getAssignments().assignRandomTimeAndRoom();
                classesToHandle.remove(0);
                i++;
            }
            long finish =System.currentTimeMillis();
            System.out.println("Filtering took " +i +" classes " +(finish - start) +" millis");
            System.out.println("Rooms");
            int j=0;
            while (j<rooms.size()/20 && !rooms.isEmpty()) {
                rooms.get(0).getAvailability().AssignRandomClassToRoom();
                rooms.remove(0);
                j++;
            }
            classesToHandle.forEach((Class c)->c.getAssignments().calculateWeight());
            classesToHandle.sort(Comparator.comparing((Class c)->c.getAssignments().getWeight()));
            System.out.println("classes to handle : "+classesToHandle.size());
        }

    }




    private static void handleHardConstraints() {
        int i=0;
        for(Constraint constraint:problem.getDistributions())
        {
            System.out.println(i++ +"/" +problem.getDistributions().size());
            Set<SolutionClass> problematic = constraint.isConstraintValideSoFar();
            for(SolutionClass sc:problematic){
                System.out.println("Removing from " +sc.getId());
                sc.getAssignmentsOfClass().getCurrentRoom().getAvailability().removeRoomfromClass(sc.getId());

            }
        }
    }


    private static void randomUnassign() {
        Random rand= new Random();
        ArrayList<SolutionClass> classesWithoutRoomList = new ArrayList<SolutionClass>(classesWithoutRoom);
        SolutionClass randomSC=classesWithoutRoomList.get(rand.nextInt(classesWithoutRoomList.size()));
        for(Class c:randomSC.getAssignmentsOfClass().getOtherClassesEvolvedInConstraints()) {
            if(c.getAssignments().getCurrentRoom()!=null) {
                c.getAssignments().getCurrentRoom().getAvailability().removeRoomfromClass(c.getClassId());
                c.getAssignments().setWeight(Integer.MAX_VALUE);
            }
        }
    }

    private static void handleClassesWithHardConstraints() {
        for(SolutionClass sc: classesWithoutRoom)
            sc.getAssignmentsOfClass().calculateWeight();
        ArrayList<SolutionClass> sortedWithoutRoom= new ArrayList<SolutionClass>(classesWithoutRoom);
        sortedWithoutRoom.sort(Comparator.comparing((SolutionClass sc)->sc.getAssignmentsOfClass().getWeight()));
        for( SolutionClass sc: sortedWithoutRoom) {
            sc.getAssignmentsOfClass().fixRequiredConstarintsIfPossible();
            //System.out.println(i++);
        }
    }

    private static void assignBestPossibleRoomToClasses() {
        //Collections.shuffle(solution.getClasses());
        ArrayList<SolutionClass> sortedWithoutRoom= new ArrayList<SolutionClass>(classesWithoutRoom);
        sortedWithoutRoom.forEach(sc->sc.getAssignmentsOfClass().calculateWeight());
        sortedWithoutRoom.sort(Comparator.comparing((SolutionClass sc)->sc.getAssignmentsOfClass().getWeight())
                                            .thenComparing((SolutionClass sc)->sc.getAssignmentsOfClass().getRequiredConstraints().size())
                                            .thenComparing((SolutionClass sc)->sc.getAssignmentsOfClass().getOtherClassesEvolvedInConstraints().size()));
        for(SolutionClass sc: sortedWithoutRoom)
            sc.getAssignmentsOfClass().AssignBestPossibleRoomAndTime();
    }

    private static void assignClassToRooms() {
        //System.out.println("Rooms size " +problem.getRooms().size());

        Collections.shuffle(problem.getRooms());
        problem.getRooms().forEach((Room r)->r.getAvailability().AssignRandomClassToRoom());
        /*for(Room room: problem.getRooms())
            room.getAvailability().AssignRandomClassToRoom();*/
    }

    public static void findClassesForRooms()
    {
        for(Class c: problem.getClasses())
        {
            for(Room room: c.getRooms())
                findRoomById(room.getId()).getAvailability().addClass(c);
        }
        for(Room room:problem.getRooms())
            room.getAvailability().sortClasses();
    }


    private static void calculatePenalty() {
        penalty=0;
        classesWithoutRoom=new HashSet<SolutionClass>();
        for (SolutionClass sc:solution.getClasses())
        {
            if(sc.getRoomId()==0) {
                classesWithoutRoom.add(sc);
                penalty++;
            }
        }
        if (penalty < bestPenalty) {

            bestPenalty = penalty;
            System.out.println("New best: " + bestPenalty);
            bestProblem=problem;
            bestSolution=solution;
            for(SolutionClass sc:bestSolution.getClasses())
            {
                if(sc.getRoomId()==-5)
                    sc.setRoomId(null);
            }
            ObjectToXML xml2 = new ObjectToXML(solution,"_SWO");
            for(SolutionClass sc:bestSolution.getClasses())
            {
                if(sc.getRoomId()==null)
                    sc.setRoomId(-5);
            }
            bestClassesWithoutRoom=classesWithoutRoom;
        }

        System.out.println("Classes without Room: " +penalty);
        Date date = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        System.out.println(formatter.format(date));
    }

    public static void handleConstraints() {

        for(Constraint constraint: problem.getDistributions())
        {
            ArrayList<Class> classesToReplace=new ArrayList<Class>();
            for(Class c :constraint.getClasses()){
                Class problemClass=findClassById(c.getClassId());
                classesToReplace.add(problemClass);
            }
            constraint.setClasses(classesToReplace);
            for(Class c :constraint.getClasses())
            {
                c.getAssignments().addConstraint(constraint);
                if(constraint.isRequired()) {
                    c.getAssignments().addRequiredConstraint(constraint);
                }
                else
                    c.getAssignments().addSoftConstraint(constraint);
            }
        }
        for(Class c:problem.getClasses())
            problem.getSoftConstraints().addAll(c.getAssignments().getSoftConstraints());
    }

    public static void createClassPossibleAssignments() {
        problem.getClasses().forEach((Class c)->c.setAssignments(new PossibleAssignmentsOfClass(c)));
        problem.getClasses().forEach((Class c)->solution.addClass(c.getAssignments().getSolutionClass()));
        for(Course course:problem.getCourses())
        {
            for(Configuration config: course.getConfigurations())
            {
                for(Subpart subpart:config.getSubparts())
                {
                    subpart.getClasses().forEach(classroom->classroom.setSubpart(subpart));
                }
            }
        }
    }
    public static Class findClassById(int Id)
    {

        return problem.getClasses().stream().filter(c -> c.getClassId() == Id).findFirst().orElse(null);
       // return matchningClass.
       /* for (Class c:problem.getClasses()) {
            if (c.getClassId() == Id)
                return c;

        }return null;*/
    }

    public static Course findCourseById(int Id)
    {

        return problem.getCourses().stream().filter(c -> c.getCourse_id() == Id).findFirst().orElse(null);
        // return matchningClass.
       /* for (Class c:problem.getClasses()) {
            if (c.getClassId() == Id)
                return c;

        }return null;*/
    }


    public static Course findCoursebyId(int Id)
    {
        Optional<Course> first = problem.getCourses().stream().filter((Course c) -> c.getCourse_id() == Id).findFirst();
        return first.get();
    }
    public static void createAvailabilitiesOfRooms() {
        problem.getRooms().forEach((Room room)->room.setAvailability(new AvailabiltyOfRoom(room)));
        /*for (Room room:problem.getRooms()
             ) {
            room.setAvailability(new AvailabiltyOfRoom(room));
        }*/

    }

    public static void assignRandomRoomToClasses()
    {
       // int i=0;
        ArrayList<Class> classesToHandle= new ArrayList<Class>(problem.getClasses());
        classesToHandle.sort(Comparator.comparing((Class c)->c.getTime().size()*c.getRooms().size()));
        //System.out.println(problem.getClasses().size());
        boolean flag=classesToHandle.removeIf((Class c)->c.getRooms().size()==0);
        //System.out.println(flag+"" +problem.getClasses().size());
        classesToHandle.forEach((Class c)->c.getAssignments().AssignRandomRoomAndTime());

    }


    public static boolean areTimesOverlaped(String s1 , String s2)
    {
        for(int i=0; i<s1.length() ; i++)
        {
            if(s1.charAt(i)==s2.charAt(i) && s1.charAt(i)=='1')
                return true;
        }
        return false;
    }

    public static String timeOrTime(String s1, String s2)
    {
        StringBuilder str = new StringBuilder(s1);
        for(int i=0 ; i<s1.length(); i++)
        {
            if(s1.charAt(i)=='1' || s2.charAt(i)=='1')
                str.setCharAt(i,'1');
            else
                str.setCharAt(i,'0');
        }

        return str.toString();
    }

    public static int findTravelBetweenRooms(Integer r1, Integer r2 )
    {
        return obj.findTravelBetweenRooms(r1,r2);
    }

    public static void connectRoomsWithRoomsAvailableForClass()
    {
        for(Class c:problem.getClasses())
        {
            ArrayList<Room> problemRooms = new ArrayList<>();
            for(Room r:c.getRooms())
            {
                Room problemRoom=findRoomById(r.getId());
                problemRoom.addRoomPenalty(c.getClassId(),r.getPenalty());
                problemRooms.add(problemRoom);
            }
            c.setRooms(problemRooms);
        }
    }

    public static Room findRoomById(int id)
    {
        Optional<Room> first = problem.getRooms().stream().filter((Room r) -> r.getId() == id).findFirst();
        return first.get();
    }

    public static void handleClassesWithoutRoom(){

        ArrayList<SolutionClass> removed = new ArrayList<>();
        ArrayList<SolutionClass> sortedArray= new ArrayList<SolutionClass>(classesWithoutRoom);
       // for(SolutionClass sc: classesWithoutRoom)
          //  sc.getAssignmentsOfClass().calculateWeight();
       sortedArray.sort(Comparator.comparing(
               (SolutionClass sc)->sc.getAssignmentsOfClass().getTimes().size()*sc.getAssignmentsOfClass().getRooms().size())
               .thenComparing((SolutionClass c)->c.getAssignmentsOfClass().getRequiredConstraints().size()).reversed()
               .thenComparing((SolutionClass c)->c.getAssignmentsOfClass().getOtherClassesEvolvedInConstraints().size()).reversed());

        for (SolutionClass sc: sortedArray)
        {
            //sc.getAssignmentsOfClass().setTimes(findClassById(sc.getId()).getTime());
            //sc.getAssignmentsOfClass().setRooms(findClassById(sc.getId()).getRooms());
            Random rand=new Random();

            for(int i=0; i<sc.getAssignmentsOfClass().getRooms().size() ; i++)
            {
                Room randRoom = sc.getAssignmentsOfClass().getRooms().get(rand.nextInt(sc.getAssignmentsOfClass().getRooms().size()));//System.out.println("randRoom " +randRoom.getId() );
                ArrayList<Time> timesSortedByCasualties=randRoom.getAvailability().sortTimesByCasualties(sc.getAssignmentsOfClass().getTimes());
                Time minT= null;
                for(Time t: timesSortedByCasualties) {

                    if (sc.getAssignmentsOfClass().checkClassforConstraints(new SolutionClass(sc.getId(), randRoom.getId(),t),"REQUIRED")) {
                        minT=t;
                        System.out.println("Constraint valide for id " +sc.getId());
                        break;
                    }
                }
                if(minT!=null) { //else not valide for constraint
                   // System.out.println(randRoom.getAvailability().IsAssignedTo(minT).get(0));
                    for (Integer c : randRoom.getAvailability().IsAssignedTo(minT))
                        findClassById(c).getAssignments().findAlternativeTimeForCurrentRoom(false);
                    if (sc.setRoomAndTime(randRoom.getId(), minT)) {
                       System.out.println("Exchanged " +sc.getId());
                        break;
                    }
                    else {
                        System.out.println("something went wrong!!!");
                        //for(int j:randRoom.getAvailability().IsAssignedTo(minT))
                           // System.out.println("Assigned to " +j);
                    }
                }



                //sc.getAssignmentsOfClass().setCurrentTime(timesSortedByCasualties.get(0));
                //sc.getAssignmentsOfClass().AssignRoomToClassCurrentTime(randRoom);
                /*if(sc.getAvailabilities().getCurrentRoom()!=null)
                {
                    removed.add(sc);
                    break;
                    //System.out.println("SC55");
                }*/

            }

        }
        //penalty.setClassesWithoutRoom(new ArrayList<SolutionClass>(tempClassesWithputRoom));
        //classesWithoutRoom .removeAll(removed);
        //System.out.println("SC2 " +penalty.getClassesWithoutRoom().size());
    }


    /////////////////////////////////////////

    public static Problem getProblem() {
        return problem;
    }

    public static void setProblem(Problem problem) {
        Registry.problem = problem;
    }

    public static Solution getSolution() {
        return solution;
    }

    public static void setSolution(Solution solution) {
        Registry.solution = solution;
    }
}
