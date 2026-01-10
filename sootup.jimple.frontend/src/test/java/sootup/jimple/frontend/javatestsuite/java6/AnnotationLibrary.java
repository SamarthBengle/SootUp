package sootup.jimple.frontend.javatestsuite.java6;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Disabled;
import sootup.core.model.ClassModifier;
import sootup.core.model.SootClass;
import sootup.jimple.frontend.javatestsuite.JimpleTestSuiteBaseIT;

/**
 * @author Kaustubh Kelkar
 */
public class AnnotationLibrary extends JimpleTestSuiteBaseIT {

  @Disabled
  public void testAnnotation() {
    // TODO: annotations are not supported yet
    System.out.println(getDeclaredClassSignature());
    SootClass sootClass = loadClass(getDeclaredClassSignature());
    assertTrue(ClassModifier.isAnnotation(sootClass.getModifiers()));
  }
}
