package Process;

import Problem.Problem;
import Problem.Constraint;
import Problem.Room;
import Problem.Class;
import Solution.Solution;
import Solution.SolutionClass;
import Problem.Time;
import com.sun.xml.bind.v2.runtime.reflect.Lister;

import java.util.*;

public class Registry {
    public static Problem problem;
    public static Solution solution;
    public static XMLToObject obj;
    private static Set<SolutionClass> classesWithoutRoom;
    public static int penalty=Integer.MAX_VALUE;
    public static int bestPenalty=Integer.MAX_VALUE;

    public static void main(String[] args) {
        int n=0;
        //ObjectToXML xml2;
        while(n<5 && penalty>0) {

            obj = new XMLToObject();
            problem = obj.getProblem();
            problem.findAllClasses();

            solution = new Solution(problem.getName());
           // ObjectToXML xml = new ObjectToXML(problem);
            createAvailabilitiesOfRooms();
            connectRoomsWithRoomsAvailableForClass();
            //problem.getClasses().get(0).getRooms().get(0).getAvailability().printAvailability();
            createClassPossibleAssignments();
            handleConstraints();
            findClassesForRooms();
            //ObjectToXML xml2;
            int k = 0;
            assignRandomRoomToClasses();
            calculatePenalty();
           while (k <5  && penalty > 0) {
               System.out.println("k= " + k);
               handleClassesWithoutRoom();
               calculatePenalty();

               assignClassToRooms();
               calculatePenalty();

               handleClassesWithHardConstraints();
               calculatePenalty();


               assignBestPossibleRoomToClasses();
                calculatePenalty();

               randomUnassign();
               calculatePenalty();

                k++;
            }
           n++;
        }

       //System.out.println(obj.findTravelBetweenRooms(-1,1));
        //solution=new Solution(problem.getName());

    }

    private static void randomUnassign() {
        Random rand= new Random();
        ArrayList<SolutionClass> classesWithoutRoomList = new ArrayList<SolutionClass>(classesWithoutRoom);
        SolutionClass randomSC=classesWithoutRoomList.get(rand.nextInt(classesWithoutRoomList.size()));
        for(Class c:randomSC.getAssignmentsOfClass().getOtherClassesEvolvedInConstraints()) {
            if(c.getAssignments().getCurrentRoom()!=null)
                c.getAssignments().getCurrentRoom().getAvailability().removeRoomfromClass(c.getClassId());
        }
    }

    private static void handleClassesWithHardConstraints() {
        int i=0;
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
        for(SolutionClass sc: solution.getClasses())
            sc.getAssignmentsOfClass().AssignBestPossibleRoomAndTime();
    }

    private static void assignClassToRooms() {
        //System.out.println("Rooms size " +problem.getRooms().size());
        Collections.shuffle(problem.getRooms());
        for(Room room: problem.getRooms())
            room.getAvailability().AssignRandomClassToRoom();
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
            ObjectToXML xml2 = new ObjectToXML(solution);
            bestPenalty = penalty;
            System.out.println("New best: " + bestPenalty);
        }

        System.out.println("Classes without Room: " +penalty);
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
                if(constraint.isRequired())
                    c.getAssignments().addRequiredConstraint(constraint);
            }
        }
    }

    public static void createClassPossibleAssignments() {
        for(Class c:problem.getClasses())
        {
            c.setAssignments(new PossibleAssignmentsOfClass(c));
            solution.addClass(c.getAssignments().getSolutionClass());
        }
    }
    public static Class findClassById(int Id)
    {
        for (Class c:problem.getClasses()) {
            if (c.getClassId() == Id)
                return c;

        }return null;
    }
    public static void createAvailabilitiesOfRooms() {
        for (Room room:problem.getRooms()
             ) {
            room.setAvailability(new AvailabiltyOfRoom(room));
        }

    }

    public static void assignRandomRoomToClasses()
    {
        int i=0;
        for(Class c: problem.getClasses())
            c.getAssignments().calculateWeight();
        problem.getClasses().sort(Comparator.comparing((Class c)->c.getAssignments().getWeight()));
       for(Class c: problem.getClasses()) {
           c.getAssignments().AssignRandomRoomAndTime();
          // System.out.println(i++);
       }
    }


    public static boolean areTimesOverlaped(String s1 , String s2)
    {
        for(int i=0; i<s1.length() ; i++)
        {
            if(s1.charAt(i)==s2.charAt(i))
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

    public static int findTravelBetweenRooms(int r1, int r2 )
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
                problemRooms.add(findRoomById(r.getId()));
            }
            c.setRooms(problemRooms);
        }
    }

    public static Room findRoomById(int id)
    {
        ArrayList<Room> rooms = problem.getRooms();
        try {
            for(Room r : rooms)
            {
                if(r.getId()==id)
                    return r;
            }
            //System.out.println("Can't find room with id " +id);
        }catch(Exception e) {
            e.printStackTrace();
            System.exit(0);
        }
        return null;
    }

    public static void handleClassesWithoutRoom(){

        ArrayList<SolutionClass> removed = new ArrayList<>();
        ArrayList<SolutionClass> sortedArray= new ArrayList<SolutionClass>(classesWithoutRoom);
        for(SolutionClass sc: solution.getClasses())
            sc.getAssignmentsOfClass().calculateWeight();
       sortedArray.sort(Comparator.comparing(
               (SolutionClass sc)->sc.getAssignmentsOfClass().getWeight())
               .reversed()
               .thenComparing((SolutionClass c)->c.getAssignmentsOfClass().getRequiredConstraints().size())
               .thenComparing((SolutionClass c)->c.getAssignmentsOfClass().getOtherClassesEvolvedInConstraints().size()));

        for (SolutionClass sc: classesWithoutRoom)
        {
            Random rand=new Random();

            for(int i=0; i<sc.getAssignmentsOfClass().getRooms().size() ; i++)
            {
                Room randRoom = sc.getAssignmentsOfClass().getRooms().get(rand.nextInt(sc.getAssignmentsOfClass().getRooms().size()));//System.out.println("randRoom " +randRoom.getId() );
                ArrayList<Time> timesSortedByCasualties=randRoom.getAvailability().sortTimesByCasualties(sc.getAssignmentsOfClass().getTimes());
                Time minT= null;
                for(Time t: timesSortedByCasualties) {

                    if (sc.getAssignmentsOfClass().checkClassforConstraints(new SolutionClass(sc.getId(), randRoom,t))) {
                        minT=t;
                        System.out.println("Constarint valide for id " +sc.getId());
                        break;
                    }
                }
                if(minT!=null) { //else not valide for constraint
                    System.out.println(randRoom.getAvailability().IsAssignedTo(minT).get(0));
                    for (Integer c : randRoom.getAvailability().IsAssignedTo(minT))
                        findClassById(c).getAssignments().findAlternativeTimeForCurrentRoom();
                    if (sc.setRoomAndTime(randRoom, minT)) {
                        System.out.println("Exchanged " +sc.getId());
                        break;
                    }
                    else {
                         System.out.println("something went wrong!!!");
                        for(int j:randRoom.getAvailability().IsAssignedTo(minT))
                            System.out.println("Assigned to " +j);
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
///
    //Phase2

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
