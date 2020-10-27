package Problem;

import javax.xml.bind.annotation.*;
import java.util.ArrayList;

@XmlRootElement(name="config")
@XmlAccessorType (XmlAccessType.FIELD)
public class Configuration {

    public Configuration() { subparts=new ArrayList<Subpart>(); }

    @XmlAttribute
    private String id;
    public void setConfigId(String id) { this.id = id; }
    public String getConfigId() { return id; }

    @XmlElement(name="subpart")
    private ArrayList<Subpart> subparts;
    public void addSubpart(Subpart sub) {
        subparts.add(sub);
    }

    public ArrayList<Subpart> getSubparts() {
        return subparts;
    }
    public void setSubparts(ArrayList<Subpart> subparts) {
        this.subparts = subparts;
    }



}
