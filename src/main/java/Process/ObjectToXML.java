package Process;

import Problem.Problem;
import Solution.Solution;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import java.io.FileOutputStream;
import java.io.OutputStream;

public class ObjectToXML {

    public ObjectToXML(Problem prob) {

        try {
            JAXBContext contextObj = JAXBContext.newInstance(Problem.class);
            Marshaller marshallerObj = contextObj.createMarshaller();
            marshallerObj.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            OutputStream os = new FileOutputStream( "src\\main\\resources\\student.xml");
            marshallerObj.marshal(prob, os );

            //prob.print();
            System.out.println("XML file created successfully!" );
            os.close();
        }catch(Exception e) {
            e.printStackTrace();
            System.exit(0);

        }

    }

    public ObjectToXML(Solution solution) {

        try {
            JAXBContext contextObj = JAXBContext.newInstance(Solution.class);
            Marshaller marshallerObj = contextObj.createMarshaller();
            marshallerObj.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            OutputStream os = new FileOutputStream( "src\\main\\resources\\solution.xml");
            marshallerObj.marshal(solution, os );

            //prob.print();
            System.out.println("XML file created successfully!" );
            os.close();
        }catch(Exception e) {
            e.printStackTrace();
            System.exit(0);

        }

    }


}
