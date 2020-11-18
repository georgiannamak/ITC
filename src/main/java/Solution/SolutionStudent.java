package Solution;

import javax.xml.bind.annotation.*;

@XmlRootElement(name="student")
@XmlAccessorType(XmlAccessType.FIELD)
public class SolutionStudent {
    @XmlAttribute
    int id;

    public SolutionStudent() {
    }

    public SolutionStudent(int id) {
        this.id = id;
    }
}
