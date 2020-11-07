package Solution;

import javax.xml.bind.annotation.*;
import java.util.HashSet;
import java.util.Set;


@XmlRootElement(name ="solution")
@XmlAccessorType(XmlAccessType.FIELD)
public class Solution {

    @XmlAttribute
    private String name;
    @XmlAttribute
    private int runtime;
    @XmlAttribute
    private int cores;
    @XmlAttribute
    private String technique;
    @XmlAttribute
    private String author;
    @XmlAttribute
    private String institurion;
    @XmlAttribute
    private String country;

    @XmlElement(name="class")
    private Set<SolutionClass> classes;

    public Solution(String name){
        this.name=name;
        technique="Anarixisi";
        author="Georgianna2";
        institurion="Uom";
        country="Greece";
        classes= new HashSet<SolutionClass>();
    }

    public Solution() {
    }

    public void addClass(SolutionClass solutionClass)
    {
        classes.add(solutionClass);
    }

    public Set<SolutionClass> getClasses() {
        return classes;
    }
}
