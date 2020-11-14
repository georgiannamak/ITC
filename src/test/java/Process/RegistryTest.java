package Process;

import Problem.Class;
import junit.framework.TestCase;

public class RegistryTest extends TestCase {

    public void testFindClassById() {

        Class c= Registry.findClassById(2);

        for (Class c1:Registry.getProblem().getClasses()) {
            if (c1.getClassId() == 2)
                assertEquals(c,c1);

        }

    }
}