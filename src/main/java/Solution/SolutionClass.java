package Solution;

import Problem.Room;
import Problem.Student;
import Problem.Time;
import Process.PossibleAssignmentsOfClass;
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
    private int roomId;
    @XmlElement(name = "student")
    private ArrayList<Student> students;

    @XmlTransient
    private PossibleAssignmentsOfClass assignmentsOfClass;
    @XmlTransient
    private int end;

    public SolutionClass(PossibleAssignmentsOfClass assignmentsOfClass) {
        this.id = assignmentsOfClass.getId();
        this.assignmentsOfClass=assignmentsOfClass;
    }

    public SolutionClass(int id, Room room, Time t)
    {
        this.id=id;
        this.roomId=room.getId();
        this.start=t.getStart();
        this.days=t.getDays();
        this.weeks=t.getWeeks();
        this.end=t.getEnd();
    }

    public boolean setRoomAndTime(Room room, Time t) {
        //System.out.println(" "+id);
        int i = 0;
        if (room != null && t != null) {
            if (room.getAvailability().AssignRoomToClass(id, t)) {
                roomId = room.getId();
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
    public int getRoomId() {
        return roomId;
    }

    public void setRoomId(int roomId) {
        this.roomId = roomId;
    }
    @XmlTransient
    public ArrayList<Student> getStudents() {
        return students;
    }
    @XmlTransient
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setStudents(ArrayList<Student> students) {
        this.students = students;
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
