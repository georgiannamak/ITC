package Problem;

import Solution.SolutionClass;
import Process.Registry;

import javax.xml.bind.annotation.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

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

    public ArrayList<Class> getClasses() {
        return classes;
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
            if (!Registry.areTimesOverlaped(c1.getDays(), c2.getDays()) || !Registry.areTimesOverlaped(c1.getWeeks(), c2.getWeeks()))
                return true;
            else return max(c1.getEnd(), c2.getEnd()) - min(c1.getStart(), c2.getStart()) <= S;
        } else if (type.contains("MinGap")) {
            String clean = type.replaceAll("\\D+", ""); //remove non-digits
            S = Integer.parseInt(clean);//System.out.println(S);
            if (!Registry.areTimesOverlaped(c1.getDays(), c2.getDays()) || !Registry.areTimesOverlaped(c1.getWeeks(), c2.getWeeks()))
                return true;
            else return c1.getEnd() + S <= c2.getStart() || c2.getEnd() + S <= c2.getStart();
        } else {
            switch (type) {
                case "SameAttendees":
                    //System.out.println("Same attendees fom" +c1.getId() +"and " +c2.getId());
                    if ((c1.getEnd() + Registry.findTravelBetweenRooms(c1.getRoomId(), c2.getRoomId())) <= c2.getStart())
                        return true;
                    else if ((c2.getEnd() + Registry.findTravelBetweenRooms(c1.getRoomId(), c2.getRoomId())) <= c1.getStart())
                        return true;
                    else if (!(Registry.areTimesOverlaped(c1.getDays(), c2.getDays())))
                        return true;
                    else return !(Registry.areTimesOverlaped(c1.getWeeks(), c2.getWeeks()));
                    //System.out.println("not satisfied!");
                case "SameStart":
                    return c1.getStart() == c2.getStart();
                case "SameRoom":
                    return c1.getRoomId() == c2.getRoomId();
                case "DifferentDays":
                    return !Registry.areTimesOverlaped(c1.getDays(), c2.getDays());
                case "NotOverlap":
                    if (c1.getEnd() <= c2.getStart() || c2.getEnd() <= c1.getStart())
                        return true;
                    else
                        return !Registry.areTimesOverlaped(c1.getDays(), c2.getDays()) || !Registry.areTimesOverlaped(c1.getWeeks(), c2.getWeeks());
                case "SameDays":
                    if (Registry.timeOrTime(c1.getDays(), c2.getDays()).equals(c1.getDays()))
                        return true;
                    else return Registry.timeOrTime(c1.getDays(), c2.getDays()).equals(c2.getDays());
                case "SameTime":
                    if ((c1.getEnd() - c1.getStart()) == (c2.getEnd() - c2.getStart())) {
                        type = "SameStart";
                        boolean flag = this.valideConstraintForTheseClasses(c1, c2);
                        type = "SameTime";
                        return flag;
                    }
                    if (c1.getStart() <= c2.getStart() || c2.getEnd() <= c1.getEnd())
                        return true;
                    else return c2.getStart() <= c1.getStart() || c1.getEnd() <= c2.getEnd();
                case "DifferentTime":
                    if (c1.getEnd() <= c2.getStart())
                        return true;
                    else return c2.getEnd() <= c2.getStart();
                case "Precedence":
                    SolutionClass temp;
                    if (c2.getId() < c1.getId()) {
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

}