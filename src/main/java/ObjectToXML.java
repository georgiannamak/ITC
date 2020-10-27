import Problem.Problem;

import java.io.FileOutputStream;
import java.io.OutputStream;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;


public class ObjectToXML {

    public ObjectToXML(Problem prob) {

        try {
            JAXBContext contextObj = JAXBContext.newInstance(Problem.class);
            Marshaller marshallerObj = contextObj.createMarshaller();
            marshallerObj.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            OutputStream os = new FileOutputStream("pu-cs-fal07.xml");
            marshallerObj.marshal(prob, os);

            //prob.print();
            System.out.println("XML file created successfully!");
            os.close();
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(0);

        }
    }
}
