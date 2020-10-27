package Problem;

import java.util.ArrayList;
import javax.xml.bind.annotation.*;

@XmlRootElement(name="room")
@XmlAccessorType (XmlAccessType.FIELD)
public class Room {

    @XmlAttribute
    private String id;
    @XmlAttribute
    private int capacity;
    @XmlElement(name="travel")
    private ArrayList<Travel> travel;
    @XmlElement(name="unavailable")
    private ArrayList<Time> unavailability;


    public Room(){
        travel = new ArrayList<>();
        unavailability = new ArrayList<>();
    }


    //geters and setters
    public String getId() { return id; }

    public void setId(String id) { this.id = id; }

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

}