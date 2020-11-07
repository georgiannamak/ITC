package Process;

import Problem.Problem;
import org.xml.sax.InputSource;
//import jdk.internal.org.xml.sax.InputSource;
//import org.graalvm.compiler.graph.Node;
//import org.graalvm.compiler.graph.NodeList;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import java.io.FileInputStream;
import java.io.InputStream;

import static java.lang.Double.NaN;

public class XMLToObject {
    Problem problem = new Problem();
    public XMLToObject(){

        try {

            JAXBContext context= JAXBContext.newInstance(Problem.class);
            Unmarshaller unmarshaller = context.createUnmarshaller();
            InputStream inStream = new FileInputStream( "src\\main\\resources\\pu-cs-fal07.xml");
            problem = (Problem) unmarshaller.unmarshal( inStream );
        }catch(Exception e)
        {
            e.printStackTrace();
            System.exit(0);
        }

    }

    public int findTravelBetweenRooms(int room1, int room2 )
    {
        Double travel = null;
        XPath xpath = XPathFactory.newInstance().newXPath();
        String expression = "/problem/rooms/room[@id=" + "'" + room1 + "'" + "]"+"/travel[@room=" +"'" +room2 +"'" +"]" +"/@value";
        InputSource inputSource = new InputSource("src\\main\\resources\\pu-cs-fal07.xml");
        try {
             travel = (Double) xpath.evaluate(expression, inputSource, XPathConstants.NUMBER);
        } catch (XPathExpressionException e) {
            e.printStackTrace();
        }
        if(travel.isNaN())
        {
            expression = "/problem/rooms/room[@id=" + "'" + room2 + "'" + "]"+"/travel[@room=" +"'" +room1 +"'" +"]" +"/@value";
            try {
                travel = (Double) xpath.evaluate(expression, inputSource, XPathConstants.NUMBER);
            } catch (XPathExpressionException e) {
                e.printStackTrace();
            }
        }
        return travel.intValue();
    }

    public Problem getProblem() {
        return problem;
    }
}

