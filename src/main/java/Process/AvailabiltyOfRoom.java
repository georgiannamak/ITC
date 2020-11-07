package Process;

import Problem.Class;
import Problem.Room;
import Problem.Time;
import Solution.SolutionClass;

import java.util.*;

public class AvailabiltyOfRoom {

    private int Id;
    private int[][][] availability;
    private Set<Class> classes = new HashSet<>();

    public AvailabiltyOfRoom(Room room){
        //classes=new ArrayList<>();
        //unavailability= r.getUnavailability();
        Id=room.getId();
        availability = new int[Registry.getProblem().getNrWeeks()+1][Registry.getProblem().getNrDays()+1][Registry.getProblem().getSlotsPerDay()+1];

        for(Time t: room.getUnavailability()) {
            String weeks=t.getWeeks();
            String days=t.getDays();
            int start=t.getStart();
            int end=t.getEnd();
            for (int i=0; i < weeks.length(); i++) {
                if(weeks.charAt(i)=='1')
                {
                    for (int j=0; j < days.length(); j++){
                        if(days.charAt(j)=='1') {
                            for (int k = start; k <= end; k++) {
                                availability[i][j][k] = -404;
                                 //System.out.println(+i +" " +j +" " +k +" " +" " +availability[i][j][k]);
                            }
                        }
                    }
                }

            }
        }

    }

    public boolean isRoomFreeThisTime(Time t)
    {
        String weeks=t.getWeeks();
        String days=t.getDays();
        int start=t.getStart();
        int end=t.getEnd();
        for (int i=0; i < weeks.length(); i++) {
            if (weeks.charAt(i) == '1') {
                for (int j = 0; j < days.length(); j++)
                {
                    if (days.charAt(j) == '1') {
                        for (int k = start; k <= end; k++) {
                            if (availability[i][j][k] != 0){
                                //System.out.println("unavailable by " +availability[i][j][k]);
                                return false;
                            }

                        }
                    }
                }

            }

        }
        return true;

    }

    public boolean AssignRoomToClass(int id,Time t)
    {
        boolean flag=isRoomFreeThisTime(t);
        if(flag) {
            String weeks = t.getWeeks();
            String days = t.getDays();
            int start = t.getStart();
            int end = t.getEnd();
            for (int i = 0; i < weeks.length(); i++) {
                if (weeks.charAt(i) == '1') {
                    for (int j = 0; j < days.length(); j++){
                        if (days.charAt(j) == '1') {
                            for (int k = start; k <= end; k++) {
                                availability[i][j][k] = id;
                                // if(room.getId().equals("3"))
                                //   System.out.println("availability of room " +room.getId() +"changed successfuly" +i +" " +j +" " +k +" " +availability[i][j][k]);
                            }

                        }
                    }
                }
            }
            return true;
        }
        else
            //System.out.println("Room was unavailable");
            return false;
    }


    public void printAvailability()
    {
        for(int i = 0; i< Registry.getProblem().getNrWeeks(); i++)
        {
            System.out.println("\n----------Week " +i +"----------");
            for(int j = 0; j< Registry.getProblem().getNrDays(); j++)
            {
                System.out.println("\nDay " +j);
                for(int k = 0; k< Registry.getProblem().getSlotsPerDay(); k++) {
                    System.out.print(" " +availability[i][j][k]);
                }

            }
        }
    }

    public void AssignRandomClassToRoom()
    {
     //   System.out.println("Handling Room: " +Id);

        for(Class c: classes)
        {
            // System.out.println("handlind class " +c.getClass_id());
            if(c.getAssignments().getSolutionClass().getRoomId()==0)
            {
                for(Time t: c.getAssignments().getTimes())
                {
                    if(isRoomFreeThisTime(t)){
                        SolutionClass tempSolutionClass= new SolutionClass(c.getClassId(),Registry.findRoomById(Id),t);
                        if(c.getAssignments().checkClassforConstraints(tempSolutionClass)) {
                             if (c.getAssignments().getSolutionClass().setRoomAndTime(Registry.findRoomById(Id), t)) {
                                System.out.println("Done with Rooms");
                                break;
                            }
                        }
                    }

                }
            }
        }

    }

    public void addClass(Class c)
    {
        classes.add(c);
    }

    public ArrayList<Time> sortTimesByCasualties(ArrayList<Time> times)
    {
        ArrayList<Class> classes =new ArrayList<>();
        ArrayList<Time> removedTimes= new ArrayList<>();
        for(Time t:times)
        {
            ArrayList<Integer> listOdIds= this.IsAssignedTo(t);
            if(listOdIds.contains(-404))//room unavailable
                removedTimes.add(t);
            //ArrayList<Class> classes =new ArrayList<>();
            for(Integer i:listOdIds)
            {
                if(i!=-404) {
                    Class c = Registry.findClassById(i);
                    //System.out.println(c.getClassId());
                    if (!classes.contains(c))
                        classes.add(c);
                }
            }

        }
        times.removeAll(removedTimes);
        if(!classes.isEmpty()) {
            //for(Class c:classes)
               // System.out.println(c.toString());
            //System.out.println("Classes " +classes.size());
            classes.sort(Comparator.comparing((Class c) -> c.getRooms().size() * c.getTime().size())
                    .reversed()
                    .thenComparing((Class c) -> c.getAssignments().getRequiredConstraints().size())
                    .thenComparing((Class c) -> c.getAssignments().getOtherClassesEvolvedInConstraints().size()));
        }
        ArrayList<Time> sortedTimes=new ArrayList<>();
        for(int i=0 ; i<classes.size() ; i++) sortedTimes.add(null);
        for (Time t:times)
        {
            ArrayList<Integer> listOdIds= this.IsAssignedTo(t);
            for (int i:listOdIds)
            {
                if(!classes.isEmpty() && classes.contains(Registry.findClassById(i)) && i!=-404)
                    sortedTimes.set(classes.indexOf(Registry.findClassById(i)),t);

            }
        }
        while(sortedTimes.remove(null))
          //  System.out.println("Hey!");
       // System.out.println(times.size() +" " +sortedTimes.size());
        if(sortedTimes.isEmpty())
            return times;
        return sortedTimes;

    }


    public ArrayList<Integer> IsAssignedTo(Time t)
    {
        ArrayList<Integer> list = new ArrayList<>();
        String weeks=t.getWeeks();
        String days=t.getDays();
        int start=t.getStart();
        int end=t.getEnd();
        for (int i=0; i < weeks.length(); i++) {
            if (weeks.charAt(i) == '1') {
                for (int j = 0; j < days.length(); j++)
                {
                    if (days.charAt(j) == '1') {
                        for (int k = start; k <= end; k++) {
                            if (availability[i][j][k] != 0 ) {
                                if (!(list.contains(availability[i][j][k]))) {
                                    list.add(availability[i][j][k]);
                                    //System.out.println("unavailable by " +availability[i][j][k]);
                                }
                            }

                        }

                    }
                }
            }
        }
        return list;
    }

    public void removeRoomfromClass(int id)
    {
        Time t = Registry.findClassById(id).getAssignments().getCurrentTime(); //System.out.println(" SC900 " +Main.registry.getClassById(id).getAvailabilitiesforClass().getCurrentTime());
        String weeks=t.getWeeks();
        String days=t.getDays();
        int start=t.getStart();
        int end=t.getEnd();
        for (int i=0; i < weeks.length(); i++) {
            if (weeks.charAt(i) == '1') {
                for (int j = 0; j < days.length(); j++){
                    if (days.charAt(j) == '1') {
                        for (int k = start; k <= end; k++) {
                            availability[i][j][k] = 0;
                            // if(room.getId().equals("3"))
                            // System.out.println("availability of room " +room.getId() +"changed successfuly" +i +" " +j +" " +k +" " +availability[i][j][k]);
                        }

                    }
                }
            }
        }
        Class c=Registry.findClassById(id);
        c.getAssignments().setCurrentRoom(null);
        c.getAssignments().setCurrentTime(null);
        c.getAssignments().getSolutionClass().setRoomAndTime(null,null);
        c.getAssignments().setRooms(new ArrayList<>(c.getRooms()));
        c.getAssignments().setTimes(new ArrayList<>(c.getTime()));
    }

    public void removeRoomfromClassThisTime(int id,Time t)
    {
        //Time t = Registry.findClassById(id).getAssignments().getCurrentTime(); //System.out.println(" SC900 " +Main.registry.getClassById(id).getAvailabilitiesforClass().getCurrentTime());
        String weeks=t.getWeeks();
        String days=t.getDays();
        int start=t.getStart();
        int end=t.getEnd();
        for (int i=0; i < weeks.length(); i++) {
            if (weeks.charAt(i) == '1') {
                for (int j = 0; j < days.length(); j++){
                    if (days.charAt(j) == '1') {
                        for (int k = start; k <= end; k++) {
                            availability[i][j][k] = 0;
                            // if(room.getId().equals("3"))
                            // System.out.println("availability of room " +room.getId() +"changed successfuly" +i +" " +j +" " +k +" " +availability[i][j][k]);
                        }

                    }
                }
            }
        }
        Class c=Registry.findClassById(id);
       // c.getAssignments().setCurrentRoom(null);
       // c.getAssignments().setCurrentTime(null);
        c.getAssignments().getSolutionClass().setRoomAndTime(null,null);
        c.getAssignments().setRooms(new ArrayList<>(c.getRooms()));
        c.getAssignments().setTimes(new ArrayList<>(c.getTime()));
    }

    public void sortClasses() {

        ArrayList<Class> sortedList = new ArrayList<>(classes);
        sortedList.sort(Comparator.comparing(
                (Class c)->c.getTime().size()*c.getRooms().size())
                .reversed()
                .thenComparing((Class c)->c.getAssignments().getRequiredConstraints().size())
                .thenComparing((Class c)->c.getAssignments().getOtherClassesEvolvedInConstraints().size()));
        classes=new HashSet<>(sortedList);
    }
}
