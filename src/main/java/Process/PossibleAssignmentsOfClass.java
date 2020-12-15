package Process;

import Problem.Class;
import Problem.Constraint;
import Problem.Room;
import Problem.Time;
import Solution.Solution;
import Solution.SolutionClass;

import javax.swing.text.html.Option;
import java.util.*;
import java.util.stream.Collectors;

public class PossibleAssignmentsOfClass {
    private int id;
    private ArrayList<Time> times;
    private ArrayList<Room> rooms;
    private Set<Constraint> constraints;
    private Set<Constraint> requiredConstraints;
    private Set<Constraint> softConstraints;
    private Set<Class> otherClassesEvolvedInConstraints;
    private SolutionClass solutionClass;
    private Time currentTime;
    private Room currentRoom;
    private int weight;

    public PossibleAssignmentsOfClass(Class c) {
        id = c.getClassId();
        times = new ArrayList<>(c.getTime());
        rooms = new ArrayList<>(c.getRooms());
        solutionClass = new SolutionClass(this);
        currentRoom = null;
        currentTime = null;
        constraints = new HashSet<>();
        requiredConstraints = new HashSet<>();
        softConstraints=new HashSet<>();
        otherClassesEvolvedInConstraints = new HashSet<>();
        if (rooms.size() == 0) {
            currentRoom = null;
            solutionClass.setRoomId(-5);
        }
        // times.sort(Comparator.comparing(Time::getPenalty));
        //rooms.sort(Comparator.comparing(Room::getPenalty));
        //weight=id;
        //weight=Integer.MAX_VALUE;

    }

    public void addConstraint(Constraint constraint) {
        constraints.add(constraint);
        //otherClassesEvolvedInConstraints.addAll(constraint.getClasses());
       // otherClassesEvolvedInConstraints.remove(Registry.findClassById(id));
    }

    public void addSoftConstraint(Constraint constraint) {
        softConstraints.add(constraint);
        //otherClassesEvolvedInConstraints.addAll(constraint.getClasses());
        //otherClassesEvolvedInConstraints.remove(Registry.findClassById(id));
    }

    public void addRequiredConstraint(Constraint constraint) {
        requiredConstraints.add(constraint);
        otherClassesEvolvedInConstraints.addAll(constraint.getClasses());
        otherClassesEvolvedInConstraints.remove(Registry.findClassById(id));
    }

    public SolutionClass getSolutionClass() {
        return solutionClass;
    }

    public boolean assignRandomTimeAndRoom() {
        if ((!(solutionClass.getRoomId() > 0))) {//&& weight>0) {
            //System.out.println("id=" + id);
            //times = new ArrayList<>(Registry.findClassById(id).getTime());
            // rooms = new ArrayList<>(Registry.findClassById(id).getRooms());
            Collections.shuffle(rooms);
            Collections.shuffle(times);
            int i = 0;
            while (currentRoom == null && currentTime == null && i < rooms.size()) {

                Room randRoom = rooms.get(i);
                long start = System.currentTimeMillis();
                //solutionClass.setRoomAndTimeWithCheck(randRoom.getId(),t)
                Time randomTime = times.parallelStream().filter((t) -> randRoom.getAvailability().isRoomFreeThisTime(t) && checkClassforConstraints(new SolutionClass(id, randRoom.getId(), t),"REQUIRED"))
                        //.filter((Time t)->checkClassforConstraints(new SolutionClass(id, randRoom.getId(), t)))
                        .findAny().orElse(null);
                //.collect(Collectors.toList());


                long finish = System.currentTimeMillis();
                // System.out.println("Filtering took " +(finish - start) +" millis");

                if (randomTime != null) {// && checkClassforConstraints(new SolutionClass(id, randRoom.getId(), randomTime))) {
                    currentTime = randomTime;
                    currentRoom = randRoom;
                    solutionClass.setRoomAndTime(currentRoom.getId(), currentTime);
                    System.out.println("Success id= " + id + "weights: " + rooms.size() * times.size());
                    return true;
                }
                i++;
            }
        }
        if (currentTime == null)
            System.out.println("Can't find assignment for id " + id);
        return false;
    }


    public void AssignRandomRoomAndTime() {
        //System.out.println("id=" +id);
        //times=new ArrayList<>(Registry.findClassById(id).getTime());
        // rooms=new ArrayList<>(Registry.findClassById(id).getRooms());

        Random rand = new Random();
        Collections.shuffle(times);
        while ((currentTime == null || currentRoom == null)) {
            currentTime = times.get(rand.nextInt(times.size()));
            // currentTime=times.get(0);
            findRandomRoomforClass();
            if (times.isEmpty()) {
                //System.out.println("Can't find assignment for id " +id );
                break;
            }
            if (currentRoom != null && currentTime != null) {
                if (!solutionClass.setRoomAndTime(currentRoom.getId(), currentTime)) {
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
        if (currentTime != null) {
            Random rand = new Random();
            Room randRoom;
            while (rooms.size() > 0) {
                randRoom = rooms.get(rand.nextInt(rooms.size()));
                // randRoom=rooms.get(0);
                //System.out.println("SC35 " +rooms.size() +times.size() +currentTime +id +randRoom.getAvailability().isRoomFreeThisTime(currentTime) );
                if (randRoom.getAvailability().isRoomFreeThisTime(currentTime) && checkClassforConstraints(new SolutionClass(id, randRoom.getId(), currentTime),"REQUIRED")) {

                    currentRoom = randRoom;
                    // solutionClass.setRoom(currentRoom, currentTime);
                    break;
                } else {
                    rooms.remove(randRoom);
                }
            }
            if (rooms.isEmpty()) {
                // System.out.println("There is no room available for class " +id +" at time " + currentTime.getWeeks() + " " + currentTime.getDays() +" " +currentTime.getStart());
                times.remove(currentTime);
                currentRoom = null;
                currentTime = null;
                rooms = new ArrayList<>(Registry.findClassById(id).getRooms());
                // System.out.println("SC3 " +rooms.size());
                // Main.registry.getPenalty().getClassesWithoutRoom().add(solutionClass.getId());

            }

        }

    }

    public boolean checkClassforConstraints(SolutionClass newSolutionClass,String method) {
        ArrayList<Class> otherClasses;
        ArrayList<Constraint> constraintsToCheck= new ArrayList<>();
        if(method.equals("REQUIRED"))
            constraintsToCheck=new ArrayList<>(requiredConstraints);
        else if (method.equals("ALL"))
            constraintsToCheck=new ArrayList<>(constraints);
        for (Constraint c : constraintsToCheck) {
            if (c.getType().contains("MaxDayLoad")) {
                ArrayList<SolutionClass> solutionClasses = new ArrayList<>();
                String clean = c.getType().replaceAll("\\D+", ""); //remove non-digits
                int S = Integer.parseInt(clean);//System.out.println(S);
                List<Class> classes = c.getClasses().stream().filter(constraintClass -> constraintClass.getAssignments().getSolutionClass().getWeeks() != null)
                        .collect(Collectors.toList());
                solutionClasses.add(newSolutionClass);
                for (Class otherClass : classes) {
                    solutionClasses.add(otherClass.getAssignments().getSolutionClass());
                }
                for (int week = 0; week < Registry.getProblem().getNrWeeks(); week++) {
                    int finalWeek = week;
                    List<SolutionClass> weeklyClasses = solutionClasses.stream().filter(sc -> sc.getWeeks().charAt(finalWeek) == '1')
                            .collect(Collectors.toList());
                    for (int day = 0; day < Registry.getProblem().getNrDays(); day++) {
                        int sum = 0;
                        for (SolutionClass sc : weeklyClasses) {
                            if (sc.getDays().charAt(day) == '1')
                                sum += sc.getEnd() - sc.getStart();
                        }
                        if (sum > S )
                            return false;
                    }
                }

            } else if (c.getType().contains("MaxDays")) {
                int sum = 0;
                ArrayList<SolutionClass> solutionClasses = new ArrayList<>();
                String clean = c.getType().replaceAll("\\D+", ""); //remove non-digits
                int D = Integer.parseInt(clean);//System.out.println(S);
                List<Class> classes = c.getClasses().stream().filter(constraintClass -> constraintClass.getAssignments().getSolutionClass().getWeeks() != null)
                        .collect(Collectors.toList());
                for(Class classroom:classes)
                    solutionClasses.add(classroom.getAssignments().getSolutionClass());
                solutionClasses.add(newSolutionClass);
                StringBuilder str = new StringBuilder();
                for (int i = 0; i < Registry.getProblem().getNrDays(); i++)
                    str.append('0');
                String currentOr = str.toString();
                for (SolutionClass sc : solutionClasses) {
                    currentOr = Registry.timeOrTime(currentOr, sc.getDays());
                }
                for (int i = 0; i < currentOr.length(); i++) {
                    if (currentOr.charAt(i) == '1')
                        sum++;
                }
                if (sum > D)
                    return false;
            } else if(c.getType().contains("MaxBlock"))
            {
                ArrayList<SolutionClass> solutionClasses = new ArrayList<>();
                int sum=0;
                String[] types=c.getType().split(",");
                types[0] = types[0].replaceAll("\\D+", ""); //remove non-digits
                types[1]=types[1].replaceAll("\\D+", "");
                int M=Integer.parseInt(types[0]);
                int S=Integer.parseInt(types[1]);
               // System.out.println("s="+S +" m="+M);

                List<Class> classes = c.getClasses().stream().filter(constraintClass -> constraintClass.getAssignments().getSolutionClass().getWeeks() != null)
                        .collect(Collectors.toList());
                solutionClasses.add(newSolutionClass);
                for (Class otherClass : classes) {
                    solutionClasses.add(otherClass.getAssignments().getSolutionClass());
                }
                for (int week = 0; week < Registry.getProblem().getNrWeeks(); week++) {
                    int finalWeek = week;
                    List<SolutionClass> weeklyClasses = solutionClasses.stream().filter(sc -> sc.getWeeks().charAt(finalWeek) == '1')
                            .collect(Collectors.toList());
                    if(weeklyClasses.size()>1) {
                        for (int day = 0; day < Registry.getProblem().getNrDays(); day++) {
                            int finalDay = day;
                            List<SolutionClass> dailyClasses = weeklyClasses.stream().filter(sc -> sc.getDays().charAt(finalDay) == '1')
                                    .collect(Collectors.toList());
                            dailyClasses.sort(Comparator.comparing(SolutionClass::getStart));
                            boolean dailyFlag;
                            Set<SolutionClass> Block = new HashSet<>();
                            if (dailyClasses.size() > 1) {
                                for (int k = 0; k < dailyClasses.size() - 1; k++) {
                                    if (dailyClasses.get(k + 1).getStart() - dailyClasses.get(k).getEnd() <= S) {
                                        Block.add(dailyClasses.get(k));
                                        Block.add(dailyClasses.get(k + 1));
                                    }
                                }
                                for (SolutionClass sc : Block)
                                    sum += sc.getEnd() - sc.getStart();

                                if (sum > M)
                                    return false;
                            }
                        }
                    }
                }
                return true;

            }else if(c.getType().contains("MaxBreaks")){
                ArrayList<SolutionClass> solutionClasses = new ArrayList<>();
                int sum=0;
                String[] types=c.getType().split(",");
                types[0] = types[0].replaceAll("\\D+", ""); //remove non-digits
                types[1]=types[1].replaceAll("\\D+", "");
                int R=Integer.parseInt(types[0]);
                int S=Integer.parseInt(types[1]);
               // System.out.println("s="+S +" r="+R);

                List<Class> classes = c.getClasses().stream().filter(constraintClass -> constraintClass.getAssignments().getSolutionClass().getWeeks() != null)
                        .collect(Collectors.toList());
                solutionClasses.add(newSolutionClass);
                for (Class otherClass : classes) {
                    solutionClasses.add(otherClass.getAssignments().getSolutionClass());
                }
                for (int week = 0; week < Registry.getProblem().getNrWeeks(); week++) {
                    int finalWeek = week;
                    List<SolutionClass> weeklyClasses = solutionClasses.stream().filter(sc -> sc.getWeeks().charAt(finalWeek) == '1')
                            .collect(Collectors.toList());
                    if(weeklyClasses.size()>1) {
                        for (int day = 0; day < Registry.getProblem().getNrDays(); day++) {
                            int finalDay = day;
                            List<SolutionClass> dailyClasses = weeklyClasses.stream().filter(sc -> sc.getDays().charAt(finalDay) == '1')
                                    .collect(Collectors.toList());
                            dailyClasses.sort(Comparator.comparing(SolutionClass::getStart));
                            boolean dailyFlag;
                            Set<SolutionClass> Block = new HashSet<>();
                            if (dailyClasses.size() > 1) {
                                for (int k = 0; k < dailyClasses.size() - 1; k++) {
                                    if (dailyClasses.get(k + 1).getStart() - dailyClasses.get(k).getEnd() <= S) {
                                        Block.add(dailyClasses.get(k));
                                        Block.add(dailyClasses.get(k + 1));
                                    }
                                }

                                if (Block.size() - 1 > R)
                                    return false;
                            }
                        }
                    }
                }
                return true;
            }
            else {
                otherClasses = new ArrayList<>(c.getClasses()); //so that it doesn't take the actual list but a copy of it
                otherClasses.remove(Registry.findClassById(id));
                for (Class courseClass : otherClasses) {

                    if (courseClass.getAssignments().getSolutionClass().getWeeks() != null) {
                        if (!c.valideConstraintForTheseClasses(courseClass.getAssignments().getSolutionClass(), newSolutionClass))
                            return false;//courseClass.getAssignments().getId();
                    }
                }
            }
        }
        return true;
    }

    public boolean findAlternativeTimeForCurrentRoom(boolean remove) {
        Room olrCurrentRoom = currentRoom;
        if (currentRoom != null && !remove) {
            currentRoom.getAvailability().removeRoomfromClassThisTime(id, currentTime);
            times.sort(Comparator.comparing(Time::getPenalty));
            for (Time t : times) {
                /*boolean overlapped = false;
                if (Registry.FareTimesOverlaped(t.getWeeks(), currentTime.getWeeks())) {
                    if (Registry.areTimesOverlaped(t.getDays(), currentTime.getDays())) {
                        if (!(t.getEnd() < currentTime.getStart() || currentTime.getEnd() < t.getStart())) {
                            overlapped = true;
                        }
                    }
                }
                */
                if (/*currentRoom != null &&*/  currentRoom.getAvailability().isRoomFreeThisTime(t) && t.IsDifeerentTime(currentTime)) {// && !overlapped) {
                    Time oldCurrent = currentTime;
                    currentRoom.getAvailability().IsAssignedTo(oldCurrent).forEach(System.out::println);

                    SolutionClass tempSolutionClass = new SolutionClass(id, currentRoom.getId(), t);
                    if (checkClassforConstraints(tempSolutionClass,"REQUIRED")) {

                        if (solutionClass.setRoomAndTime(currentRoom.getId(), t)) {
                            // currentRoom.getAvailability().removeRoomfromClassThisTime(id,oldCurrent);
                            System.out.println("Just changed the time to id " + id + " to " + currentTime.getDays() + " " + currentTime.getStart());
                            //times.remove(oldCurrent);
                            //times.set(times.size()-1,oldCurrent);
                            return true;

                        } else currentTime = oldCurrent;
                    } else currentTime = oldCurrent;
                }


            }
            // if (currentRoom != null) {
            //Random rand = new Random();
               /* if(constraints.size()>0) {
                    ArrayList<Constraint> constraintsList = new ArrayList<>(constraints);
                    Constraint randomConstraint = constraintsList.get(rand.nextInt(constraintsList.size()));

                    System.out.println("Removed everything from : " + id);
                    for (Class c : randomConstraint.getClasses()) {
                        if (c.getAssignments().getCurrentRoom() != null)
                            c.getAssignments().getCurrentRoom().getAvailability().removeRoomfromClass(c.getClassId());
                    }
                }*/
            rooms.remove(currentRoom);
            rooms.add(rooms.size(), currentRoom);
            currentRoom.getAvailability().removeRoomfromClass(id);
            System.out.println("Removed everything from : " + id);


            //for (Class otherClass : otherClassesEvolvedInConstraints) {

            //if (otherClass.getAssignments().getCurrentRoom() != null)
            // otherClass.getAssignments().getCurrentRoom().getAvailability().removeRoomfromClass(otherClass.getClassId());
            //}
            //times = new ArrayList<>(Registry.findClassById(id).getTime());
            //rooms = new ArrayList<>(Registry.findClassById(id).getRooms());
            //     }
        }
        // rooms.remove(olrCurrentRoom);//new
        //  System.out.println("Rooms size for class " +id +" is " +rooms.size());//new
        return false;
    }

    public void fixRequiredConstarintsIfPossible() {
        for (Constraint c : requiredConstraints) {
            ArrayList<SolutionClass> removed = new ArrayList<>();
            for (SolutionClass sc : c.findProblematicClasses()) {
                Random rand = new Random();
                int n = 0;
                while (sc.getAssignmentsOfClass().getCurrentRoom() == null && n < 10 && sc.getAssignmentsOfClass().getTimes().size() > 0) {
                    //System.out.println("SC52");
                    Room randRoom = sc.getAssignmentsOfClass().getRooms().get(rand.nextInt(sc.getAssignmentsOfClass().getRooms().size()));//System.out.println("randRoom " +randRoom.getId());
                    ArrayList<Time> timesByCasualties = randRoom.getAvailability().sortTimesByCasualties(sc.getAssignmentsOfClass().getTimes());
                    //System.out.println("Exchanged with " +sc.getId());
                    Time minT = null;
                    for (Time t : timesByCasualties) {
                        SolutionClass solutionClass = new SolutionClass(sc.getId(), randRoom.getId(), t);
                        if (sc.getAssignmentsOfClass().checkClassforConstraints(solutionClass,"REQUIRED")) {
                            minT = t;
                            //System.out.println("Constarint valide for id " +solutionClass.getId());
                            break;
                        }
                    }
                    if (minT != null) { //else not valide for constraint
                        for (Integer i : randRoom.getAvailability().IsAssignedTo(minT))
                            Registry.findClassById(i).getAssignments().findAlternativeTimeForCurrentRoom(false);
                        if (sc.setRoomAndTime(randRoom.getId(), minT)) {
                            //System.out.println("Exchanged " +sc.getId());
                            break;
                        } else {
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

    public boolean AssignBestPossibleRoomAndTime2() {
        ArrayList<Time> TempTimes = new ArrayList<>(times); //new
        ArrayList<Room> TempRooms = new ArrayList<>(rooms); //new
        times.sort(Comparator.comparing(Time::getPenalty));
        //rooms.so
        int i = 0;
        if (rooms.size() == 0) {
           // currentTime=times.stream().filter(t -> checkClassforConstraints(new SolutionClass(id, -5, t),"ALL"))
                                       // .findAny().orElse(null);
            //if(currentTime==null)
                 currentTime = times.stream()
                    .filter(t -> checkClassforConstraints(new SolutionClass(id, -5, t),"REQUIRED"))
                    .findAny().orElse(null);
            if (currentTime != null) {
                solutionClass.setDays(currentTime.getDays());
                solutionClass.setWeeks(currentTime.getWeeks());
                solutionClass.setStart(currentTime.getStart());
                solutionClass.setEnd(currentTime.getEnd());
                System.out.println("Done without room: " + id);
                return true;
                //System.out.println("done with " + i + "/85");

            } //else
        } else {
            while (currentRoom == null && currentTime == null && i < rooms.size()) {

                //Time randTime = times.get(i);
                Room randRoom = rooms.get(i);
                // System.out.println("Room= "+randRoom.getId());
                for (Time t : times) {
                    if (randRoom.getAvailability().isRoomFreeThisTime(t)) {
                        /*if (checkClassforConstraints(new SolutionClass(id, randRoom.getId(), t),"ALL")) {
                            solutionClass.setRoomAndTime(randRoom.getId(), t);
                            currentTime = t;
                            currentRoom = randRoom;
                            //System.out.println("Success id= "+id);
                            return true;
                        }else*/ if(checkClassforConstraints(new SolutionClass(id,randRoom.getId(),t),"REQUIRED"))
                        {
                            solutionClass.setRoomAndTime(randRoom.getId(), t);
                            currentTime = t;
                            currentRoom = randRoom;
                            //System.out.println("Success id= "+id);
                            return true;
                        }
                        //System.out.println("Failed on constraint");
                    }

               /* else {
                    System.out.println("Failed on availability");
                    System.out.println(randRoom.getAvailability().IsAssignedTo(t));
                }*/
                }

                //Time randTime = times.parallelStream().filter((t) -> randRoom.getAvailability().isRoomFreeThisTime(t) &&checkClassforConstraints(new SolutionClass(id, randRoom.getId(), t)))
                //.filter((Time t)->checkClassforConstraints(new SolutionClass(id, randRoom.getId(), t)))
                // .findAny().orElse(null);
           /* if (randTime != null){// && checkClassforConstraints(new SolutionClass(id, randRoom.getId(), randomTime))) {
                currentTime = randTime;
                currentRoom = randRoom;
                solutionClass.setRoomAndTime(currentRoom.getId(), currentTime);
                System.out.println("Success id= "+id);
                return true;
            }*/
                i++;
            }
        }
        if (currentRoom == null && rooms.size() > 0)
            System.out.println("Can't find assignment for id " + id);
        return false;

    }

    public boolean AssignBestPossibleRoomAndTime() {
        ArrayList<Time> TempTimes = new ArrayList<>(times); //new
        ArrayList<Room> TempRooms = new ArrayList<>(rooms); //new
        // if(weight>0) {
        times.sort(Comparator.comparing(Time::getPenalty));
        // System.out.println("times size =" + times.size() + " for class=" + id);
        while ((currentTime == null || currentRoom == null) && solutionClass.getRoomId() == 0 && times.size() > 0) {
            currentTime = times.get(0);
            findRandomRoomforClass();
            if (times.isEmpty()) {
                times = TempTimes;
                break;
                //Main.registry.getPenalty().addClass(solutionClass);
            }
            if (currentRoom != null && currentTime != null) {
                if (!solutionClass.setRoomAndTime(currentRoom.getId(), currentTime)) {
                    rooms.remove(currentRoom);
                    currentRoom = null;
                } else {
                    times = TempTimes;
                    rooms = TempRooms;
                    return true;
                }
            }
            //i++;
        }
        times = TempTimes;
        rooms = TempRooms;
        return false;


    }

    public void calculateWeight() {
        ArrayList<Time> toBeRemoved = new ArrayList<>();
        weight = 0;
        times = new ArrayList<>(Registry.findClassById(id).getTime());
        rooms = new ArrayList<>(Registry.findClassById(id).getRooms());
        for (Time t : times) {
            boolean removeTime = true;
            for (Room r : rooms) {
                if (r.getAvailability().isRoomFreeThisTime(t)) {
                    weight++;
                    removeTime = false;
                }

            }
            if (removeTime)
                toBeRemoved.add(t);
        }
        times.removeAll(toBeRemoved);
        if (toBeRemoved.size() > 0)
            System.out.println("removed " + toBeRemoved.size() + "times from class " + id);
        Class c = Registry.findClassById(id);
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

    public boolean fixConstraintForOtherClass() {
        Class[] otherClasses = otherClassesEvolvedInConstraints.toArray(new Class[0]);
        Class otherClass = otherClasses[0];
        if (otherClass.getAssignments().getSolutionClass().getRoomId() > 0) {
            otherClass.getAssignments().getCurrentRoom().getAvailability().removeRoomfromClass(otherClass.getClassId());

            for (Room r : rooms) {
                for (Time t : times) {
                    if (r.getAvailability().isRoomFreeThisTime(t)) {
                        solutionClass.setRoomAndTime(r.getId(), t);
                        for (Room r2 : otherClass.getRooms()) {
                            for (Time t2 : otherClass.getTime()) {
                                boolean flag = true;
                                if (r2.getAvailability().isRoomFreeThisTime(t2)) {
                                    //boolean flag=true;
                                    for (Constraint c : constraints) {
                                        if (!c.valideConstraintForTheseClasses(new SolutionClass(this.getId(), r.getId(), t), new SolutionClass(otherClass.getClassId(), r2.getId(), t2)))
                                            flag = false;
                                    }
                                } else flag = false;
                                if (flag) {
                                    otherClass.getAssignments().getSolutionClass().setRoomAndTime(r2.getId(), t2);
                                    System.out.println("You did it");
                                    return true;
                                }
                            }
                        }
                        currentRoom.getAvailability().removeRoomfromClass(id);
                    }

                }
            }
        } else if(otherClass.getAssignments().getSolutionClass().getRoomId()==-5)
        {
            for (Room r : rooms) {
                for (Time t : times) {
                    if (r.getAvailability().isRoomFreeThisTime(t)) {
                        solutionClass.setRoomAndTime(r.getId(), t);

                           // boolean flag = true;
                                    //boolean flag=true;
                        if(otherClass.getAssignments().findAlternativeTimeForCurrentRoom(false)){
                            System.out.println("You did it");
                            return true;
                        }

                        currentRoom.getAvailability().removeRoomfromClass(id);
                    }

                }


            }
        }
        return false;
    }

    public boolean findAlternativeRoomForCurrentTime(boolean remove) {

       // Time oldCurrentTime = currentTime;
        if (currentRoom != null && !remove) {
            currentRoom.getAvailability().removeRoomfromClassThisTime(id, currentTime);
            rooms.sort(Comparator.comparing((Room r)->r.getRoomPenaltyForClass(id)));
            for (Room room : rooms) {

                if (/*currentRoom != null &&*/  room.getAvailability().isRoomFreeThisTime(currentTime) && room!=currentRoom) {// && !overlapped) {
                    Room oldCurrent = currentRoom;
                    //currentRoom.getAvailability().IsAssignedTo(oldCurrent).forEach(System.out::println);

                    SolutionClass tempSolutionClass = new SolutionClass(id, room.getId(), currentTime);
                    if (checkClassforConstraints(tempSolutionClass,"REQUIRED")) {

                        if (solutionClass.setRoomAndTime(room.getId(), currentTime)) {
                            // currentRoom.getAvailability().removeRoomfromClassThisTime(id,oldCurrent);
                            System.out.println("Just changed the room to id " + id + " to " + currentRoom.getId());
                            //times.remove(oldCurrent);
                            //times.set(times.size()-1,oldCurrent);
                            return true;

                        } else currentRoom = oldCurrent;
                    } else currentRoom = oldCurrent;
                }
               // else
                   // room.getAvailability().IsAssignedTo(currentTime).forEach(System.out::println);


            }

            /*times.remove(currentRoom);
            rooms.add(rooms.size(), currentRoom);*/
            currentRoom.getAvailability().removeRoomfromClass(id);
            System.out.println("Removed everything from : " + id);


        }
        return false;
    }

    public Set<Constraint> getSoftConstraints() {
        return softConstraints;
    }

    public void setSoftConstraints(Set<Constraint> softConstraints) {
        this.softConstraints = softConstraints;
    }

    public int calculateDistributionPenalty() {
        int sum=0;
        for(Constraint constraint:softConstraints)
        {
            if(!constraint.isValid())
                sum+=constraint.getPenalty();
        }
        return sum;
    }

    public void findAlternativeRoomForCurrentTimeIncludingPenalty() {
    }
}

