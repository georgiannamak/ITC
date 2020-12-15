package Solution;

import Problem.Room;
import Solution.SolutionStudent;
import Problem.Time;
import Process.PossibleAssignmentsOfClass;
import Process.Registry;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;
import java.util.ArrayList;

public class SolutionClass {
    @XmlAttribute
    private int id;
    @XmlAttribute
    private int start;
    @XmlAttribute
    private String days;
    @XmlAttribute
    private String weeks;
    @XmlAttribute(name = "room")
    private Integer roomId;
    @XmlElement(name = "student")
    private ArrayList<SolutionStudent> students;

    @XmlTransient
    private PossibleAssignmentsOfClass assignmentsOfClass;

    @XmlTransient
    private int end;

    public SolutionClass(PossibleAssignmentsOfClass assignmentsOfClass) {
        this.id = assignmentsOfClass.getId();
        this.assignmentsOfClass=assignmentsOfClass;
        students= new ArrayList<>();
        roomId=0;
    }

    public SolutionClass(int id, int roomId, Time t)
    {
        this.id=id;
        this.roomId=roomId;
        this.start=t.getStart();
        this.days=t.getDays();
        this.weeks=t.getWeeks();
        this.end=t.getEnd();
    }

    public boolean setRoomAndTime(Integer roomid, Time t) {
        //System.out.println(" "+id);

        //int i = 0;
        if (roomid != null && t != null) {
            Room room=Registry.findRoomById(roomid);
            if (room.getAvailability().AssignRoomToClass(id, t)) {
                roomId = roomid;
                days = t.getDays();
                weeks = t.getWeeks();
                start = t.getStart();
                end=t.getEnd();
                assignmentsOfClass.setCurrentRoom(room);
                assignmentsOfClass.setCurrentTime(t);

            } else return false;
        } else {
            roomId = 0;
            weeks = null;
            days = null;
            start = -1;
            return false;
        }
        return true;
    }

    public boolean setRoomAndTimeWithCheck(Integer roomid, Time t) {
        //System.out.println(" "+id);

        if(getAssignmentsOfClass().checkClassforConstraints(this,"REQUIRED")) {
            if (roomid != null && t != null) {
                Room room = Registry.findRoomById(roomid);
                if (room.getAvailability().AssignRoomToClass(id, t)) {
                    roomId = roomid;
                    days = t.getDays();
                    weeks = t.getWeeks();
                    start = t.getStart();
                    end = t.getEnd();
                    assignmentsOfClass.setCurrentRoom(room);
                    assignmentsOfClass.setCurrentTime(t);

                } else return false;
            } else {
                roomId = 0;
                weeks = null;
                days = null;
                start = -1;
                return false;
            }
        }else return false;
        return true;
    }

    ////////////////////////////////////
    @XmlTransient
    public int getStart() {
        return start;
    }

    public void setStart(int start) {
        this.start = start;
    }
    @XmlTransient
    public String getDays() {
        return days;
    }

    public void setDays(String days) {
        this.days = days;
    }
    @XmlTransient
    public String getWeeks() {
        return weeks;
    }

    public void setWeeks(String weeks) {
        this.weeks = weeks;
    }
    @XmlTransient
    public Integer getRoomId() {
        return roomId;
    }

    public void setRoomId(Integer roomId) {
        this.roomId = roomId;
    }
    @XmlTransient
    public ArrayList<SolutionStudent> getStudents() {
        return students;
    }
    @XmlTransient
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setStudents(ArrayList<SolutionStudent> students) {
        this.students = students;
    }

    public void setEnd(int end) {
        this.end = end;
    }

    @XmlTransient
    public int getEnd() {
       // if(end)
        return end;
    }
    @XmlTransient
    public PossibleAssignmentsOfClass getAssignmentsOfClass() {
        return assignmentsOfClass;
    }
}
