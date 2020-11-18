package Problem;
import java.util.ArrayList;
import Process.StudentOptions;
import javax.xml.bind.annotation.*;
@XmlRootElement(name="student")
@XmlAccessorType (XmlAccessType.FIELD)
public class Student {

    public Student() {

    }

    @XmlAttribute
    private int id;
    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }

    @XmlElement(name="course")
    private ArrayList<Course> courses;
    public ArrayList<Course> getCourses() { return courses; }
    public void setCourses(ArrayList<Course> courses) { this.courses = courses; }
    public void  addCourse(Course c) { courses.add(c); }

    @XmlTransient
    private StudentOptions options;

    public StudentOptions getOptions() {
        return options;
    }

    public void setOptions(StudentOptions options) {
        this.options = options;
    }
}
