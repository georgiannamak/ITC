import Problem.Problem;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;

import java.io.FileInputStream;
import java.io.InputStream;

public class XMLToObject {
    Problem problem = new Problem();
    public XMLToObject(){

        try {

            JAXBContext context= JAXBContext.newInstance(Problem.class);
            Unmarshaller unmarshaller = context.createUnmarshaller();
            //URL url = getClass().getResource("lums-sum17.xml");
            InputStream inStream = new FileInputStream( "src\\main\\resources\\pu-cs-fal07.xml");
            //Registry reg = (Registry) unmarshaller.unmarshal( inStream );
            problem = (Problem) unmarshaller.unmarshal( inStream );
        }catch(Exception e)
        {
            e.printStackTrace();
            System.exit(0);
        }

    }

    public Problem getProblem() {
        return problem;
    }
}

