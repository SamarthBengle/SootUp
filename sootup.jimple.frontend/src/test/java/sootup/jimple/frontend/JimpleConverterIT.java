package sootup.jimple.frontend;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.junit.jupiter.api.Test;
import sootup.core.frontend.ResolveException;
import sootup.core.inputlocation.EagerInputLocation;
import sootup.core.jimple.common.Trap;
import sootup.core.model.*;
import sootup.core.types.PrimitiveType;
import sootup.core.util.printer.BriefStmtPrinter;
import sootup.java.core.JavaSootClass;
import sootup.java.core.OverridingJavaClassSource;
import sootup.java.core.views.JavaView;

public class JimpleConverterIT {

  private JavaSootClass parseJimpleClass(CharStream cs) throws ResolveException {
    JimpleConverter jimpleVisitor = new JimpleConverter();
    EagerInputLocation eagerInputLocation = new EagerInputLocation();
    final OverridingJavaClassSource scs =
        jimpleVisitor.run(
            cs,
            eagerInputLocation,
            Paths.get(""),
            Collections.emptyList(),
            new JavaView(eagerInputLocation));
    return new JavaSootClass(scs, SourceType.Application);
  }

  @Test
  public void testQuotedTypeParsing() throws IOException {
    SootClass clazz =
        parseJimpleClass(
            CharStreams.fromFileName("src/test/java/resources/jimple/SubTypeValidator.jimple"));
    Set<? extends SootMethod> methods = clazz.getMethods();
    SootMethod method = methods.iterator().next();
    Body body = method.getBody();
    assertEquals(3, body.getLocalCount());
  }

  @Test
  public void testEdgeCaseDoubleParsing() throws IOException {
    SootClass clazz =
        parseJimpleClass(
            CharStreams.fromFileName("src/test/java/resources/jimple/EdgeCaseDoubleNumber.jimple"));
    Set<? extends SootField> fields = clazz.getFields();
    for (SootField field : fields) {
      assertEquals(PrimitiveType.DoubleType.getInstance(), field.getType());
    }
  }

  @Test
  public void testLegacyTransientMethodModifier() throws IOException {
    SootClass clazz =
        parseJimpleClass(
            CharStreams.fromFileName(
                "src/test/java/resources/jimple/LegacyTransientMethodModifier.jimple"));
    Set<? extends SootMethod> methods = clazz.getMethods();
    SootMethod method = methods.iterator().next();
    Set<MethodModifier> modifiers = method.getModifiers();
    assertEquals(1, modifiers.size());
    MethodModifier modifier = modifiers.iterator().next();
    assertEquals(MethodModifier.VARARGS, modifier);
  }

  @Test
  public void testRedundantTrapHandler() throws IOException {
    SootClass clazz =
        parseJimpleClass(
            CharStreams.fromFileName("src/test/java/resources/jimple/RedundantTrapHandler.jimple"));
    Set<? extends SootMethod> methods = clazz.getMethods();
    SootMethod method = methods.iterator().next();

    BriefStmtPrinter stmtPrinter = new BriefStmtPrinter();
    stmtPrinter.buildTraps(method.getBody().getStmtGraph());
    List<Trap> traps = stmtPrinter.getTraps();
    assertEquals(0, traps.size());
  }
}
