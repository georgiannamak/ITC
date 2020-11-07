package Problem;
import java.util.ArrayList;

import javax.xml.bind.annotation.*;

@XmlRootElement(name="subpart")
@XmlAccessorType (XmlAccessType.FIELD)
public class Subpart  {

    public Subpart() { classes = new ArrayList<Class>();  }

    @XmlAttribute
    private String id;
    public String getSubpartId() { return id; }
    public void setSubpartId(String id) { this.id = id; }

    @XmlElement(name="class")
    private ArrayList<Class> classes;
    public void addClass(Class c) { classes.add(c); }
    public ArrayList<Class> getClasses() { return classes; }
    public void setClasses(ArrayList<Class> classes) { this.classes = classes; }


    public void printClasses()
    {
        //System.out.println("Subpart id" +id);
        for(Class c: classes)
            System.out.print(" " +c.getClassId());

    }



}
