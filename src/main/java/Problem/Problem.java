package Problem;
import javax.xml.bind.annotation.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

@XmlRootElement(name ="problem")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(propOrder = {"optimization", "rooms", "courses" ,"distributions", "students"})
public class Problem {

    public Problem() {
        rooms=new ArrayList<Room>();
        students=new ArrayList<Student>();
        courses=new ArrayList<Course>();
        distributions= new ArrayList<Constraint>();
        classes=new ArrayList<Class>();
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

    @XmlTransient
    private ArrayList<Class> classes;
    @XmlTransient
    private Set<Constraint> softConstraints= new HashSet<>();

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

    public void findAllClasses()
    {
        for(Course course:courses)
        {
            for (Configuration configuration: course.getConfigurations())
            {
                for (Subpart subpart: configuration.getSubparts())
                {
                    classes.addAll(subpart.getClasses());
                }
            }
        }
        for(Room room:rooms)
        {
            room.createRoomPenalty(classes.size());
        }
    }
    public ArrayList<Class> getClasses() {
        return classes;
    }
    @XmlTransient
    public Set<Constraint> getSoftConstraints() {
        return softConstraints;
    }

    public void setSoftConstraints(Set<Constraint> softConstraints) {
        this.softConstraints = softConstraints;
    }
}