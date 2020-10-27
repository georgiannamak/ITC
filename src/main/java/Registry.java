import Problem.Problem;

public class Registry {

    public static void main(String[] args) {
        XMLToObject obj = new XMLToObject();
        Problem problem = obj.getProblem();
        ObjectToXML xml= new ObjectToXML(problem);
    }
}
