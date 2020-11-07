package Problem;

import javax.xml.bind.annotation.*;
import java.util.ArrayList;

@XmlRootElement(name="course")
@XmlAccessorType (XmlAccessType.FIELD)
//@XmlType(propOrder = {"id", "configurations"})
public class Course {
    public Course() {configurations= new ArrayList<Configuration>();}

    @XmlAttribute
    private String id;
    public void setCourse_id(String id) { this.id = id; }
    public String getCourse_id() { return id; }

    @XmlElement(name="config")
    private ArrayList<Configuration> configurations;
    public void addConfig(Configuration config) { configurations.add(config); }
    public ArrayList<Configuration> getConfigurations() { return configurations; }
    public void setConfigurations(ArrayList<Configuration> configurations) { this.configurations = configurations;}


}
