package Problem;

import javax.xml.bind.annotation.*;
import java.util.ArrayList;

import static java.lang.Integer.max;
import static java.lang.Integer.min;

@XmlRootElement(name="distribution")
@XmlAccessorType(XmlAccessType.FIELD)
public class Constraint {
    @XmlAttribute
    private String type;
    @XmlAttribute
    private int penalty;
    @XmlAttribute
    private boolean required;
    @XmlElement(name = "class")
    private ArrayList<Class> classes = new ArrayList<>();
}