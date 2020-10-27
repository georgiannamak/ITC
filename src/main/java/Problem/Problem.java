package Problem;
import javax.xml.bind.annotation.*;
import java.util.ArrayList;

@XmlRootElement(name ="problem")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(propOrder = {"optimization", "rooms", "courses" ,"distributions", "students"})
public class Problem {

    public Problem() {
        rooms=new ArrayList<Room>();
        students=new ArrayList<Student>();
        courses=new ArrayList<Course>();
        distributions= new ArrayList<Constraint>();
    }

    @XmlElementWrapper(name="rooms")
    @XmlElement(name="room")
    private ArrayList<Room> rooms;
    @XmlElementWrapper(name="courses")
    @XmlElement(name="course")
    private ArrayList<Course> courses;
    @XmlElementWrapper(name="students")
    @XmlElement(name="student")
    private ArrayList<Student> students;

    @XmlElementWrapper(name="distributions")
    @XmlElement(name="distribution")
    private ArrayList<Constraint> distributions;

    @XmlElement(name="optimization")
    private Optimization optimization;

    @XmlAttribute
    private String name;
    @XmlAttribute
    private int nrDays;
    @XmlAttribute
    private int slotsPerDay;
    @XmlAttribute
    private int nrWeeks;



    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ArrayList<Room> getRooms() {
        return rooms;
    }

    public void setRooms(ArrayList<Room> rooms) {
        this.rooms = rooms;
    }

    public ArrayList<Course> getCourses() {
        return courses;
    }

    public void setCourses(ArrayList<Course> courses) {
        this.courses = courses;
    }

    public ArrayList<Student> getStudents() {
        return students;
    }

    public void setStudents(ArrayList<Student> students) {
        this.students = students;
    }

    public ArrayList<Constraint> getDistributions() {
        return distributions;
    }

    public void setDistributions(ArrayList<Constraint> distributions) {
        this.distributions = distributions;
    }

    public Optimization getOptimization() {
        return optimization;
    }

    public void setOptimization(Optimization optimization) {
        this.optimization = optimization;
    }

    public int getNrDays() {
        return nrDays;
    }

    public void setNrDays(int nrDays) {
        this.nrDays = nrDays;
    }

    public int getSlotsPerDay() {
        return slotsPerDay;
    }

    public void setSlotsPerDay(int slotsPerDay) {
        this.slotsPerDay = slotsPerDay;
    }

    public int getNrWeeks() {
        return nrWeeks;
    }

    public void setNrWeeks(int nrWeeks) {
        this.nrWeeks = nrWeeks;
    }


}