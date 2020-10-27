package Problem;

import javax.xml.bind.annotation.*;
@XmlRootElement(name ="travel")
@XmlAccessorType(XmlAccessType.FIELD)
public class Travel {
    public Travel(){}

    @XmlAttribute
    private String room;
    public String getRoom() { return room; }
    public void setRoom(Room room) { this.room = room.getId(); }


    @XmlAttribute
    private int value;
    public int getValue() { return value; }
    public void setValue(int value) { this.value = value; }
}