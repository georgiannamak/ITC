package Problem;

import java.util.ArrayList;
import javax.xml.bind.annotation.*;
import Process.AvailabiltyOfRoom;

@XmlRootElement(name="room")
@XmlAccessorType (XmlAccessType.FIELD)
public class Room {

    @XmlAttribute
    private int id;
    @XmlAttribute
    private int capacity;
    @XmlElement(name="travel")
    private ArrayList<Travel> travel;
    @XmlElement(name="unavailable")
    private ArrayList<Time> unavailability;

    @XmlTransient
    private AvailabiltyOfRoom availability;


    public Room(){
        travel = new ArrayList<>();
        unavailability = new ArrayList<>();
    }


    //geters and setters
    public int getId() { return id; }

    public void setId(int id) { this.id = id; }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public ArrayList<Travel> getTravel() {
        //for(Travel t: travel)
        //System.out.println(travel.size() +"+ " +t.getRoom() +" " +t.getValue());
        return travel;
    }

    public void setTravel(ArrayList<Travel> travel) {
        this.travel = travel;
    }
    public void addTravel(Travel t){
        travel.add(t);
    }

    public ArrayList<Time> getUnavailability() {
        return unavailability;
    }

    public AvailabiltyOfRoom getAvailability() {
        return availability;
    }

    public void setAvailability(AvailabiltyOfRoom availability) {
        //System.out.println(toString());
        this.availability = availability;
    }

    public void setUnavailability(ArrayList<Time> unavailability) {
        this.unavailability = unavailability;
    }

    @Override
    public String toString() {
        return "Room{" +
                "id=" + id +
                ", capacity=" + capacity +
                ", travel=" + travel +
                ", unavailability=" + unavailability +
                ", availability=" + availability +
                '}';
    }


}