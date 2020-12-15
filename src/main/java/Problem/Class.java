package Problem;

import javax.xml.bind.annotation.*;
import java.util.ArrayList;
import Process.PossibleAssignmentsOfClass;

@XmlRootElement(name="class")
@XmlAccessorType (XmlAccessType.FIELD)
public class Class {

    @XmlAttribute
    private int id;
    @XmlAttribute
    private int limit;
    @XmlAttribute
    private int parent;
    @XmlElement(name = "room")
    private ArrayList<Room> rooms;
    @XmlElement(name = "time")
    private ArrayList<Time> time;

    @XmlTransient
    private PossibleAssignmentsOfClass assignments;

    @XmlTransient
    private Class child;
    @XmlTransient
    private Subpart subpart;


    public Class() {
        rooms = new ArrayList<Room>();
        time = new ArrayList<Time>();
    }


    //getters and setters
    public int getClassId() {
        return id;
    }

    public void setClassId(int class_id) {
        this.id = class_id;
    }

    public int getLimit() {
        return limit;
    }

    public void setLimit(int limit) {
        this.limit = limit;
    }

    public int getParent() {
        return parent;
    }

    public void setParent(int parent) {
        this.parent = parent;
    }

    public ArrayList<Room> getRooms() {
        return rooms;
    }

    public void setRooms(ArrayList<Room> rooms) {
        this.rooms = rooms;
    }

    public ArrayList<Time> getTime() {
        return time;
    }

    public void setTime(ArrayList<Time> time) {
        this.time = time;
    }

    public PossibleAssignmentsOfClass getAssignments() {
        return assignments;
    }

    public void setAssignments(PossibleAssignmentsOfClass assignments) {
        this.assignments = assignments;
    }

    public Class getChild() {
        return child;
    }

    public void setChild(Class child) {
        this.child = child;
    }

    public Subpart getSubpart() {
        return subpart;
    }

    public void setSubpart(Subpart subpart) {
        this.subpart = subpart;
    }
}