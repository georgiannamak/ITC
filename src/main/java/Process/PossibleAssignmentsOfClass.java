package Process;

import Problem.Class;
import Problem.Constraint;
import Problem.Room;
import Problem.Time;
import Solution.Solution;
import Solution.SolutionClass;

import javax.swing.text.html.Option;
import java.util.*;

public class PossibleAssignmentsOfClass {
    private int id;
    private ArrayList<Time> times;
    private ArrayList<Room> rooms;
    private Set<Constraint> constraints;
    private Set<Constraint> requiredConstraints;
    private Set<Class> otherClassesEvolvedInConstraints;
    private SolutionClass solutionClass;
    private Time currentTime;
    private Room currentRoom;
    private int weight;

    public PossibleAssignmentsOfClass(Class c)
    {
        id=c.getClassId();
        times=new ArrayList<>(c.getTime());
        rooms=new ArrayList<>(c.getRooms());
        solutionClass=new SolutionClass(this);
        currentRoom=null;
        currentTime=null;
        constraints= new HashSet<>();
        requiredConstraints = new HashSet<>();
        otherClassesEvolvedInConstraints=new HashSet<>();
        if(rooms.size()==0) {
            currentRoom = null;
            solutionClass.setRoomId(-5);
            currentTime=times.stream().filter(t->checkClassforConstraints(new SolutionClass(id,-5,t))).findAny().orElse(null);
            if(currentTime!=null) {
                solutionClass.setDays(currentTime.getDays());
                solutionClass.setWeeks(currentTime.getWeeks());
                solutionClass.setStart(currentTime.getStart());
            }
        }
        weight=Integer.MAX_VALUE;

    }

    public void addConstraint(Constraint constraint)
    {
        constraints.add(constraint);
        otherClassesEvolvedInConstraints.addAll(constraint.getClasses());
    }
    public void addRequiredConstraint(Constraint constraint)
    {
        requiredConstraints.add(constraint);
    }

    public SolutionClass getSolutionClass() {
        return solutionClass;
    }

    public void assignRandomTimeAndRoom()
    {
        if((!(solutionClass.getRoomId()>0)) && weight>0) {
           // System.out.println("id=" + id);
            //times = new ArrayList<>(Registry.findClassById(id).getTime());
           // rooms = new ArrayList<>(Registry.findClassById(id).getRooms());
            Collections.shuffle(rooms);
            Collections.shuffle(times);
            int i = 0;
            while (currentRoom == null && currentTime == null && i < rooms.size()) {
                Room randRoom = rooms.get(i);
                long start =System.currentTimeMillis();

                Time randomTime = times.stream().filter((t) -> randRoom.getAvailability().isRoomFreeThisTime(t) &&checkClassforConstraints(new SolutionClass(id, randRoom.getId(), t)))
                        //.filter((Time t)->checkClassforConstraints(new SolutionClass(id, randRoom.getId(), t)))
                        .findAny().orElse(null);
                long finish =System.currentTimeMillis();
               // System.out.println("Filtering took " +(finish - start) +" millis");

                if (randomTime != null) {
                    currentTime = randomTime;
                    currentRoom = randRoom;
                    solutionClass.setRoomAndTime(currentRoom.getId(), currentTime);
                    //System.out.println("Success id= "+id +"weights: "+rooms.size()*times.size());
                }
                i++;
            }
        }
        //if(currentTime==null)
            //System.out.println("Can't find assignment for id " +id);
        }


    public void AssignRandomRoomAndTime() {

        times=new ArrayList<>(Registry.findClassById(id).getTime());
        rooms=new ArrayList<>(Registry.findClassById(id).getRooms());

        Random rand= new Random();
        Collections.shuffle(times);
        while((currentTime==null || currentRoom==null))
        {
           currentTime=times.get(rand.nextInt(times.size()));
           // currentTime=times.get(0);
            findRandomRoomforClass();
            if(times.isEmpty())
            {
                //System.out.println("Can't find assignment for id " +id );
                break;
            }
            if(currentRoom!=null && currentTime!=null)
            {
                    if (!solutionClass.setRoomAndTime(currentRoom.getId(), currentTime) ) {
                        rooms.remove(currentRoom);
                        currentRoom = null;
                    } //else System.out.println("Success id "+id+" weight= " +times.size()*rooms.size());
            }
          //  i++;
        }
        times = new ArrayList<>(Registry.findClassById(id).getTime());
        rooms = new ArrayList<>(Registry.findClassById(id).getRooms());
    }

    public void findRandomRoomforClass() {
        Collections.shuffle(rooms);
        if(currentTime!=null ) {
            Random rand = new Random();
            Room randRoom;
            while (rooms.size()>0){
                randRoom=rooms.get(rand.nextInt(rooms.size()));
               // randRoom=rooms.get(0);
                //System.out.println("SC35 " +rooms.size() +times.size() +currentTime +id +randRoom.getAvailability().isRoomFreeThisTime(currentTime) );
                if (randRoom.getAvailability().isRoomFreeThisTime(currentTime) &&  checkClassforConstraints(new SolutionClass(id,randRoom.getId(),currentTime))) {

                        currentRoom = randRoom;
                        // solutionClass.setRoom(currentRoom, currentTime);
                        break;
                }
                else
                {
                    rooms.remove(randRoom);
                }
            }
            if(rooms.isEmpty())
            {
               // System.out.println("There is no room available for class " +id +" at time " + currentTime.getWeeks() + " " + currentTime.getDays() +" " +currentTime.getStart());
                times.remove(currentTime);
                currentRoom=null;
                currentTime=null;
                rooms= new ArrayList<>(Registry.findClassById(id).getRooms());
                // System.out.println("SC3 " +rooms.size());
                // Main.registry.getPenalty().getClassesWithoutRoom().add(solutionClass.getId());

            }

        }

    }

    public boolean checkClassforConstraints(SolutionClass solutionClass)
    {
        ArrayList<Class> otherClasses;
        for(Constraint c:requiredConstraints)
        {
            otherClasses=new ArrayList<>(c.getClasses()); //so that it doesn't take the actual list but a copy of it
            otherClasses.remove(Registry.findClassById(id));
            for(Class courseClass: otherClasses)
            {
                if(courseClass.getAssignments().getSolutionClass().getRoomId()!=0)
                {
                    if(!c.valideConstraintForTheseClasses(courseClass.getAssignments().getSolutionClass(),solutionClass))
                        return false;//courseClass.getAssignments().getId();
                }
            }
        }
        return true;
    }

    public boolean findAlternativeTimeForCurrentRoom()
    {
        if (currentRoom!=null) {
            currentRoom.getAvailability().removeRoomfromClassThisTime(id, currentTime);
            for (Time t : times) {
                boolean overlapped=false;
                if (Registry.areTimesOverlaped(t.getWeeks(), currentTime.getWeeks())) {
                    if (Registry.areTimesOverlaped(t.getDays(), currentTime.getDays())) {
                        if (!(t.getEnd() < currentTime.getStart() || currentTime.getEnd() < t.getStart())) {
                            overlapped=true;
                        }
                    }
                }

                if (currentRoom != null && currentRoom.getAvailability().isRoomFreeThisTime(t) && t.IsDifeerentTime(currentTime) && !overlapped) {
                    Time oldCurrent = currentTime;

                    SolutionClass tempSolutionClass = new SolutionClass(id, currentRoom.getId(), t);
                    if (checkClassforConstraints(tempSolutionClass)) {

                        if (solutionClass.setRoomAndTime(currentRoom.getId(), t)) {
                            // currentRoom.getAvailability().removeRoomfromClassThisTime(id,oldCurrent);
                            System.out.println("Just changed the time to id " + id + " ");
                            return true;

                        } else currentTime = oldCurrent;
                    }else currentTime=oldCurrent;
                }


            }
           // if (currentRoom != null) {
                Random rand =new Random();
               /* if(constraints.size()>0) {
                    ArrayList<Constraint> constraintsList = new ArrayList<>(constraints);
                    Constraint randomConstraint = constraintsList.get(rand.nextInt(constraintsList.size()));

                    System.out.println("Removed everything from : " + id);
                    for (Class c : randomConstraint.getClasses()) {
                        if (c.getAssignments().getCurrentRoom() != null)
                            c.getAssignments().getCurrentRoom().getAvailability().removeRoomfromClass(c.getClassId());
                    }
                }*/
                  System.out.println("Removed everything from : " + id);

                 for (Class otherClass : otherClassesEvolvedInConstraints) {

                   if (otherClass.getAssignments().getCurrentRoom() != null)
                        otherClass.getAssignments().getCurrentRoom().getAvailability().removeRoomfromClass(otherClass.getClassId());
                }
                times = new ArrayList<>(Registry.findClassById(id).getTime());
                rooms = new ArrayList<>(Registry.findClassById(id).getRooms());
       //     }
       }
        return false;
    }

    public void fixRequiredConstarintsIfPossible()
    {
        for(Constraint c:requiredConstraints)
        {
            ArrayList<SolutionClass> removed= new ArrayList<>();
            for(SolutionClass sc :c.findProblematicClasses())
            {
                Random rand=new Random();
                int n=0;
                while(sc.getAssignmentsOfClass().getCurrentRoom()==null && n<10 && sc.getAssignmentsOfClass().getTimes().size()>0)
                {
                    //System.out.println("SC52");
                    Room randRoom = sc.getAssignmentsOfClass().getRooms().get(rand.nextInt(sc.getAssignmentsOfClass().getRooms().size()));//System.out.println("randRoom " +randRoom.getId());
                    ArrayList<Time>  timesByCasualties=randRoom.getAvailability().sortTimesByCasualties(sc.getAssignmentsOfClass().getTimes());
                    //System.out.println("Exchanged with " +sc.getId());
                    Time minT= null;
                    for(Time t: timesByCasualties) {
                        SolutionClass solutionClass = new SolutionClass(sc.getId(), randRoom.getId(),t);
                        if (sc.getAssignmentsOfClass().checkClassforConstraints(solutionClass)) {
                            minT=t;
                            //System.out.println("Constarint valide for id " +solutionClass.getId());
                            break;
                        }
                    }
                    if(minT!=null) { //else not valide for constraint
                        for (Integer i : randRoom.getAvailability().IsAssignedTo(minT))
                            Registry.findClassById(i).getAssignments().findAlternativeTimeForCurrentRoom();
                        if (sc.setRoomAndTime(randRoom.getId(), minT)) {
                            //System.out.println("Exchanged " +sc.getId());
                            break;
                        }
                        else {
                            System.out.println("something went wrong!!!");
                           // for(int j:randRoom.getAvailability().IsAssignedTo(minT))
                                //System.out.println("Assigned to " +j);
                        }
                    }

                    n++;
                }


            }


        }
    }

    public void AssignBestPossibleRoomAndTime() {
        times=new ArrayList<>(Registry.findClassById(id).getTime());
        rooms=new ArrayList<>(Registry.findClassById(id).getRooms());
        times.sort(Comparator.comparing(Time::getPenalty));
        //int i=0;
        while((currentTime==null || currentRoom==null) && solutionClass.getRoomId()==0 )
        {
            currentTime=times.get(0);
            findRandomRoomforClass();
            if(times.isEmpty())
            {
                times=new ArrayList<>(Registry.findClassById(id).getTime());
                break;
                //Main.registry.getPenalty().addClass(solutionClass);
            }
            if(currentRoom!=null && currentTime!=null)
            {
                if (!solutionClass.setRoomAndTime(currentRoom.getId(), currentTime)) {
                        rooms.remove(currentRoom);
                        currentRoom = null;
                }
            }
            //i++;
        }
        times = new ArrayList<>(Registry.findClassById(id).getTime());
        rooms = new ArrayList<>(Registry.findClassById(id).getRooms());

    }

    public void calculateWeight()
    {
        ArrayList<Room> toBeRemoved= new ArrayList<>();
        weight=0;
        for(Room r: rooms)
        {
            boolean removeRoom=true;
            for(Time t: times)
            {
                if(r.getAvailability().isRoomFreeThisTime(t)) {
                    weight++;
                   removeRoom=false;
                }

            }
            if(removeRoom)
                 toBeRemoved.add(r);
        }
        rooms.removeAll(toBeRemoved);
        //if(toBeRemoved.size()>0)
          //  System.out.println("removed " +toBeRemoved.size() +"rooms from class " +id);
    }


    public Time getCurrentTime() {
        return currentTime;
    }

    public void setCurrentTime(Time currentTime) {
        this.currentTime = currentTime;
    }

    public Room getCurrentRoom() {
        return currentRoom;
    }

    public void setCurrentRoom(Room currentRoom) {
        this.currentRoom = currentRoom;
    }

    public int getId() {
        return id;
    }

    public ArrayList<Time> getTimes() {
        return times;
    }

    public Set<Constraint> getRequiredConstraints() {
        return requiredConstraints;
    }

    public Set<Class> getOtherClassesEvolvedInConstraints() {
        return otherClassesEvolvedInConstraints;
    }

    public ArrayList<Room> getRooms() {
        return rooms;
    }

    public void setTimes(ArrayList<Time> times) {
        this.times = times;
    }

    public void setRooms(ArrayList<Room> rooms) {
        this.rooms = rooms;
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }
}
