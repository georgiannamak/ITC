package Problem;
import java.util.ArrayList;

import javax.xml.bind.annotation.*;
@XmlRootElement(name="student")
@XmlAccessorType (XmlAccessType.FIELD)
public class Student {

    public Student() {

    }

    @XmlAttribute
    private String id;
    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }

    @XmlElement(name="course")
    private ArrayList<Course> courses;
    public ArrayList<Course> getCourses() { return courses; }
    public void setCourses(ArrayList<Course> courses) { this.courses = courses; }
    public void  addCourse(Course c) { courses.add(c); }

}
