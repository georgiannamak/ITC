package Problem;

import java.util.ArrayList;
import javax.xml.bind.annotation.*;
import Process.AvailabiltyOfRoom;
import  Process.Registry;

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
    @XmlAttribute
    private  int penalty;

    @XmlTransient
    private AvailabiltyOfRoom availability;
    @XmlTransient
    private ArrayList<Integer> penaltyForClass;

    public Room(){
        travel = new ArrayList<>();
        unavailability = new ArrayList<>();
        //penaltyForClass=new ArrayList<>(Registry.getProblem().getClasses().size());
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

    public int getPenalty() {
        return penalty;
    }

    public void setPenalty(int penalty) {
        this.penalty = penalty;
    }

    public int getRoomPenaltyForClass(int id)
    {
        return penaltyForClass.get(id-1);
    }

    public void addRoomPenalty(int id,int penalty)
    {
        penaltyForClass.set(id-1,penalty);
    }

    public void createRoomPenalty(int size) {
        penaltyForClass=new ArrayList<>();
        for(int i=0;i<size;i++)
            penaltyForClass.add(0);
    }
    // @Override
   /* public String toString() {
        return "Room{" +
                "id=" + id +
                ", capacity=" + capacity +
                ", travel=" + travel +
                ", unavailability=" + unavailability +
                ", availability=" + availability +
                '}';
    }*/


}