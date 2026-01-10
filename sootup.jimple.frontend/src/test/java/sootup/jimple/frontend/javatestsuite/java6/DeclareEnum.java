package sootup.jimple.frontend.javatestsuite.java6;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import sootup.core.model.SootClass;
import sootup.jimple.frontend.javatestsuite.JimpleTestSuiteBaseIT;

/**
 * @author Kaustubh Kelkar
 */
public class DeclareEnum extends JimpleTestSuiteBaseIT {

  @Test
  public void test() {
    SootClass sc =
        loadClass(
            identifierFactory.getClassType(
                getDeclaredClassSignature().getFullyQualifiedName() + "$Type"));
    assertTrue(sc.isEnum());
  }
}
