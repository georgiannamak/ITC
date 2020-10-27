package Problem;

import javax.xml.bind.annotation.*;
import java.util.ArrayList;

@XmlRootElement(name="class")
@XmlAccessorType (XmlAccessType.FIELD)
public class Class {

    @XmlAttribute
    private int id;
    @XmlAttribute
    private int limit;
    @XmlTransient
    private Class parent;
    @XmlElement(name = "room")
    private ArrayList<Room> rooms;
    @XmlElement(name = "time")
    private ArrayList<Time> time;


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

    public Class getParent() {
        return parent;
    }

    public void setParent(Class parent) {
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

}