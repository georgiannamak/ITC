package Problem;

import Solution.SolutionClass;
import Process.Registry;
import com.sun.xml.bind.v2.runtime.reflect.opt.Const;

import javax.xml.bind.annotation.*;
import java.util.*;
import java.util.stream.Collectors;

import static java.lang.Integer.max;
import static java.lang.Integer.min;

@XmlRootElement(name="distribution")
@XmlAccessorType(XmlAccessType.FIELD)
public class Constraint {
    @XmlAttribute
    private String type;
    @XmlAttribute
    private int penalty;
    @XmlAttribute
    private boolean required;
    @XmlElement(name = "class")
    private ArrayList<Class> classes = new ArrayList<>();
    @XmlTransient
    boolean respected;
    public ArrayList<Class> getClasses() {
        return classes;
    }
    public Constraint(){}

    public Constraint(String type) {
        this.type = type;
    }

    public void setClasses(ArrayList<Class> newClasses) {
        classes = newClasses;
    }

    public boolean isRequired() {
        return required;
    }

    public boolean valideConstraintForTheseClasses(SolutionClass c1, SolutionClass c2) {
        int S;
        if (type.contains("WorkDay")) {
            String clean = type.replaceAll("\\D+", ""); //remove non-digits
            S = Integer.parseInt(clean);//System.out.println(S);
            if (!Registry.areTimesOverlaped(c1.getDays(), c2.getDays()))
                return true;
            else if (!Registry.areTimesOverlaped(c1.getWeeks(), c2.getWeeks()))
                    return true;
            else return max(c1.getEnd(), c2.getEnd()) - min(c1.getStart(), c2.getStart()) <= S;
        } else if (type.contains("MinGap")) {
            String clean = type.replaceAll("\\D+", ""); //remove non-digits
            S = Integer.parseInt(clean);//System.out.println(S);
            if (!Registry.areTimesOverlaped(c1.getDays(), c2.getDays()) || !Registry.areTimesOverlaped(c1.getWeeks(), c2.getWeeks()))
                return true;
            else return c1.getEnd() + S <= c2.getStart() || c2.getEnd() + S <= c1.getStart();}
        else if(type.contains("MaxBlock")) {
           String[] types=type.split(",");
           types[0] = types[0].replaceAll("\\D+", ""); //remove non-digits
            types[1]=types[1].replaceAll("\\D+", "");
            int M=Integer.parseInt(types[0]);
             S=Integer.parseInt(types[1]);
             ArrayList<SolutionClass> solutionClasses= new ArrayList<>();
             solutionClasses.add(c1);
             solutionClasses.add(c2);
            for (int week = 0; week < Registry.getProblem().getNrWeeks(); week++) {
                int finalWeek = week;
                List<SolutionClass> weeklyClasses = solutionClasses.stream().filter(sc -> sc.getWeeks().charAt(finalWeek) == '1')
                        .collect(Collectors.toList());
                if (weeklyClasses.size() > 1) {
                    for (int day = 0; day < Registry.getProblem().getNrDays(); day++) {
                        int finalDay = day;
                        List<SolutionClass> dailyClasses = weeklyClasses.stream().filter(sc -> sc.getDays().charAt(finalDay) == '1')
                                .collect(Collectors.toList());
                        dailyClasses.sort(Comparator.comparing(SolutionClass::getStart));

                        Set<SolutionClass> Block = new HashSet<>();
                        if (dailyClasses.size() > 1) {
                            for (int k = 0; k < dailyClasses.size() - 1; k++) {
                                if (dailyClasses.get(k + 1).getStart() - dailyClasses.get(k).getEnd() <= S) {
                                    Block.add(dailyClasses.get(k));
                                    Block.add(dailyClasses.get(k + 1));
                                }
                            }
                            int sum = 0;
                            for (SolutionClass sc : Block)
                                sum += sc.getEnd() - sc.getStart();

                            if (sum > M)
                                return false;
                        }
                    }
                }
            }
            return true;
        }
        else if(type.contains("MaxDayLoad")) {

                ArrayList<SolutionClass> solutionClasses = new ArrayList<>();
                String clean = type.replaceAll("\\D+", ""); //remove non-digits
                 S = Integer.parseInt(clean);//System.out.println(S);

                solutionClasses.add(c1);
                solutionClasses.add(c2);
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
                        if (sum > S)
                            return false;
                    }
                }

            return true;
        }
        else if(type.contains("MaxDays")) {
            int sum = 0;
            ArrayList<SolutionClass> solutionClasses = new ArrayList<>();
            String clean = type.replaceAll("\\D+", ""); //remove non-digits
            int D = Integer.parseInt(clean);//System.out.println(S);
            //List<Class> classes = c.getClasses().stream().filter(constraintClass -> constraintClass.getAssignments().getSolutionClass().getWeeks() != null)
                    //.collect(Collectors.toList());
            //for(Class classroom:classes)
                ///solutionClasses.add(classroom.getAssignments().getSolutionClass());
            solutionClasses.add(c1);
            solutionClasses.add(c2);
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
            return sum <= D;
        }
        else {
            switch (type) {
                case "SameAttendees":
                   // if(c1.getId()==303 || c2.getId()==303)
                      //  System.out.println("Here");
                    //System.out.println("Same attendees fom" +c1.getId() +"and " +c2.getId());
                    if ((c1.getEnd() + (Registry.findTravelBetweenRooms(c1.getRoomId(), c2.getRoomId())) <= c2.getStart()))
                        return true;
                    else if ((c2.getEnd() + (Registry.findTravelBetweenRooms(c1.getRoomId(), c2.getRoomId()))) <= c1.getStart())
                        return true;
                    else if (!(Registry.areTimesOverlaped(c1.getDays(), c2.getDays())))
                        return true;
                    else return !(Registry.areTimesOverlaped(c1.getWeeks(), c2.getWeeks()));
                    //System.out.println("not satisfied!");
                case "SameStart":
                    return c1.getStart() == c2.getStart();
                case "SameRoom":
                    return c1.getRoomId().equals(c2.getRoomId());
                case "DifferentDays":
                    return !Registry.areTimesOverlaped(c1.getDays(), c2.getDays());
                case "DifferentWeeks":
                    return !Registry.areTimesOverlaped(c1.getWeeks(), c2.getWeeks());
                case "Overlap":
                    if(Registry.areTimesOverlaped(c1.getDays(), c2.getDays()) && Registry.areTimesOverlaped(c1.getWeeks(), c2.getWeeks()))
                    {
                        if(c1.getStart()>=c2.getStart() && c1.getEnd()<=c2.getStart())
                            return true;
                        else return c2.getStart() >= c1.getStart() && c1.getStart() <= c2.getEnd();
                    }
                    return false;
                case "NotOverlap":
                    if (c1.getEnd() <= c2.getStart() || c2.getEnd() <= c1.getStart())
                        return true;
                    else
                        return ((!Registry.areTimesOverlaped(c1.getDays(), c2.getDays())) || (!Registry.areTimesOverlaped(c1.getWeeks(), c2.getWeeks())));
                case "SameDays":
                    if (Registry.timeOrTime(c1.getDays(), c2.getDays()).equals(c1.getDays()))
                        return true;
                    else return Registry.timeOrTime(c1.getDays(), c2.getDays()).equals(c2.getDays());
                case "SameTime":
                    if (c1.getEnd()-c1.getStart() == c2.getEnd()-c2.getStart()) {
                        type = "SameStart";
                         boolean flag = this.valideConstraintForTheseClasses(c1, c2);
                        type = "SameTime";
                        return flag;
                    }
                    if (c1.getStart() <= c2.getStart() && c2.getEnd() <= c1.getEnd())
                        return true;
                    else return c2.getStart() <= c1.getStart() && c1.getEnd() <= c2.getEnd();
                case "DifferentTime":
                    if (c1.getEnd() <= c2.getStart())
                        return true;
                    else return c2.getEnd() <= c2.getStart();
                case "Precedence":
                    SolutionClass temp;
                    int indexOfC1=classes.indexOf(Registry.findClassById(c1.getId()));
                    int indexOfC2=classes.indexOf(Registry.findClassById(c2.getId()));
                    if (indexOfC2< indexOfC1) {
                        temp = c1;
                        c1 = c2;
                        c2 = temp;
                    }
                    if (first(c1.getWeeks()) < first(c2.getWeeks()))
                        return true;
                    else if (first(c1.getWeeks()) == first(c2.getWeeks()) && first(c1.getDays()) < first(c2.getDays()))
                        return true;
                    else
                        return first(c1.getWeeks()) == first(c2.getWeeks()) && first(c1.getDays()) == first(c2.getDays()) && c1.getEnd() <= c2.getStart();
                case "SameWeeks":
                    if(Registry.timeOrTime(c1.getWeeks(),c2.getWeeks()).equals(c1.getWeeks()))
                        return true;
                    else return Registry.timeOrTime(c1.getWeeks(),c2.getWeeks()).equals(c2.getWeeks());
                default:
                    System.out.println("There was error with constraints " + type);
                    return false;
            }
        }
    }

    public int first(String s) {
        for (int i = 0; i <= s.length(); i++) {
            if (s.charAt(i) == '1')
                return i;

        }
        return -1;
    }

    public Set<SolutionClass> findProblematicClasses() {
        Set<SolutionClass> problematic= new HashSet<>();
        if (haveAllClassesAssignments()) {
            for (Class c : classes) {
                for (Class c2 : classes)
                    if (c != c2 && valideConstraintForTheseClasses(c.getAssignments().getSolutionClass(), c2.getAssignments().getSolutionClass()))
                        problematic.add(c.getAssignments().getSolutionClass());
            }
        } else {
            for (Class c : classes) {
                if (c.getAssignments().getCurrentRoom() == null || c.getAssignments().getCurrentTime() == null) {
                    c.getAssignments().AssignRandomRoomAndTime();
                    /*if (!valideConstraintForTheseClasses(ac))
                        System.out.println("Not able to assign");*/
                }
            }
        }
        return problematic;
    }

    private boolean haveAllClassesAssignments() {
        for(Class c: classes)
        {
            if(!(c.getAssignments().getSolutionClass().getRoomId()!=0 && c.getAssignments().getSolutionClass().getWeeks()!=null))
                return false;
        }
        return true;
    }

    public Set<SolutionClass> isConstraintValideSoFar()
    {
        Set<SolutionClass> problematic= new HashSet<>();
        for(Class c:classes)
        {
            if(c.getAssignments().getSolutionClass().getRoomId()==0 && !problematic.contains(c.getAssignments().getSolutionClass()))
            {
                for(Class c2:classes)
                {
                    if(c2!=c && c2.getAssignments().getSolutionClass().getRoomId()!=0) {
                        if (!valideConstraintForTheseClasses(c.getAssignments().getSolutionClass(), c2.getAssignments().getSolutionClass()))
                            problematic.add(c2.getAssignments().getSolutionClass());
                    }
                }
            }
        }
        return problematic;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getPenalty() {
        return penalty;
    }

    public void setPenalty(int penalty) {
        this.penalty = penalty;
    }

    public int violatedPairs() //returnsviolatedPairs
    {
        int violatedPairs=0;
        if (type.contains("MaxDayLoad")) {
            ArrayList<SolutionClass> solutionClasses = new ArrayList<>();
            String clean = type.replaceAll("\\D+", ""); //remove non-digits
            int S = Integer.parseInt(clean);//System.out.println(S);
            //List<Class> classes = this.getClasses().stream().filter(constraintClass -> constraintClass.getAssignments().getSolutionClass().getWeeks() != null)
                 //   .collect(Collectors.toList());
           // solutionClasses.add(newSolutionClass);
            for (Class otherClass : this.getClasses()) {
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
                        violatedPairs+= (sum-S);
                }
            }
            return  violatedPairs%Registry.getProblem().getNrWeeks();

        } else if (type.contains("MaxDays")) {
            int sum = 0;
            ArrayList<SolutionClass> solutionClasses = new ArrayList<>();
            String clean = type.replaceAll("\\D+", ""); //remove non-digits
            int D = Integer.parseInt(clean);//System.out.println(S);
           // List<Class> classes = c.getClasses().stream().filter(constraintClass -> constraintClass.getAssignments().getSolutionClass().getWeeks() != null)
                   // .collect(Collectors.toList());
            //solutionClasses.add(newSolutionClass);
            for(Class c:this.getClasses())
                solutionClasses.add(c.getAssignments().getSolutionClass());
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
            if(sum>D)
                return sum-D;
            else return 0;
        } else if(type.contains("MaxBlock"))
        {
            ArrayList<SolutionClass> solutionClasses = new ArrayList<>();
            int sum=0;
            String[] types=type.split(",");
            types[0] = types[0].replaceAll("\\D+", ""); //remove non-digits
            types[1]=types[1].replaceAll("\\D+", "");
            int M=Integer.parseInt(types[0]);
            int S=Integer.parseInt(types[1]);
            //System.out.println("s="+S +" m="+M);

            //List<Class> classes = c.getClasses().stream().filter(constraintClass -> constraintClass.getAssignments().getSolutionClass().getWeeks() != null)
                //    .collect(Collectors.toList());
            //solutionClasses.add(newSolutionClass);
            for (Class otherClass : this.getClasses()) {
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
                        if (dailyClasses.size() > 1) {
                            Set<SolutionClass> Block = new HashSet<>();
                            for (int k = 0; k < dailyClasses.size() - 1; k++) {
                                if (dailyClasses.get(k + 1).getStart() - dailyClasses.get(k).getEnd() <= S) {
                                    Block.add(dailyClasses.get(k));
                                    Block.add(dailyClasses.get(k + 1));
                                }
                            }
                            sum=0;
                            for (SolutionClass sc : Block)
                                sum += sc.getEnd() - sc.getStart();

                            if (sum > M)
                                violatedPairs+=sum-M;
                        }
                    }
                }
            }
            return violatedPairs%Registry.getProblem().getNrWeeks();

        }else if(type.contains("MaxBreaks")){
            ArrayList<SolutionClass> solutionClasses = new ArrayList<>();
            int sum=0;
            String[] types=type.split(",");
            types[0] = types[0].replaceAll("\\D+", ""); //remove non-digits
            types[1]=types[1].replaceAll("\\D+", "");
            int R=Integer.parseInt(types[0]);
            int S=Integer.parseInt(types[1]);
            //System.out.println("s="+S +" r="+R);

            //List<Class> classes = c.getClasses().stream().filter(constraintClass -> constraintClass.getAssignments().getSolutionClass().getWeeks() != null)
                    //.collect(Collectors.toList());
            //solutionClasses.add(newSolutionClass);
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
                        //boolean dailyFlag;
                        Set<SolutionClass> Block = new HashSet<>();
                        if(dailyClasses.size()>1) {
                            for (int k = 0; k < dailyClasses.size() - 1; k++) {
                                if (dailyClasses.get(k + 1).getStart() - dailyClasses.get(k).getEnd() <= S) {
                                    Block.add(dailyClasses.get(k));
                                    Block.add(dailyClasses.get(k + 1));
                                }
                            }

                            if (Block.size() - 1 > R)
                                violatedPairs+=Block.size()-1-R;
                        }
                    }
                }
            }
            return violatedPairs%Registry.getProblem().getNrWeeks();
        }
        else {

            for (int i=0;i<this.getClasses().size()-1;i++) {
                for(int j=i+1;j<this.getClasses().size();j++) {
                    if (!valideConstraintForTheseClasses(classes.get(j).getAssignments().getSolutionClass(), classes.get(i).getAssignments().getSolutionClass()))
                        violatedPairs++;
                }

            }
        }

        return violatedPairs;
    }

    public boolean isRespected() {
        return respected;
    }

    public void setRespected(boolean respected) {
        this.respected = respected;
    }
}