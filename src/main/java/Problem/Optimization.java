package Problem;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlTransient;

public class Optimization {
    @XmlAttribute
    int time;
    @XmlAttribute
    int  room;
    @XmlAttribute
    int  distribution;
    @XmlAttribute
    int  student;
    @XmlTransient
    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
    }
    @XmlTransient
    public int getRoom() {
        return room;
    }

    public void setRoom(int room) {
        this.room = room;
    }
    @XmlTransient
    public int getDistribution() {
        return distribution;
    }

    public void setDistribution(int distribution) {
        this.distribution = distribution;
    }
    @XmlTransient
    public int getStudent() {
        return student;
    }

    public void setStudent(int student) {
        this.student = student;
    }

    public Optimization() {
    }
}
