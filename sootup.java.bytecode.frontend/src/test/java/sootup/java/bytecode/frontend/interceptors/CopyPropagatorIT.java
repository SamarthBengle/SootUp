package sootup.java.bytecode.frontend.interceptors;

import static org.junit.jupiter.api.Assertions.*;

import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import sootup.core.graph.MutableStmtGraph;
import sootup.core.inputlocation.AnalysisInputLocation;
import sootup.core.jimple.basic.NoPositionInformation;
import sootup.core.jimple.basic.StmtPositionInfo;
import sootup.core.jimple.common.Local;
import sootup.core.jimple.common.constant.IntConstant;
import sootup.core.jimple.common.constant.LongConstant;
import sootup.core.jimple.common.constant.NullConstant;
import sootup.core.jimple.common.expr.AbstractConditionExpr;
import sootup.core.jimple.common.expr.Expr;
import sootup.core.jimple.common.expr.JCastExpr;
import sootup.core.jimple.common.ref.IdentityRef;
import sootup.core.jimple.common.stmt.*;
import sootup.core.model.Body;
import sootup.core.model.SootMethod;
import sootup.core.model.SourceType;
import sootup.core.signatures.MethodSignature;
import sootup.core.types.VoidType;
import sootup.core.util.ImmutableUtils;
import sootup.core.util.Utils;
import sootup.core.views.View;
import sootup.interceptors.CopyPropagator;
import sootup.java.bytecode.frontend.inputlocation.ClassFileBasedAnalysisInputLocation;
import sootup.java.bytecode.frontend.inputlocation.JavaClassPathAnalysisInputLocation;
import sootup.java.core.JavaIdentifierFactory;
import sootup.java.core.language.JavaJimple;
import sootup.java.core.types.JavaClassType;
import sootup.java.core.views.JavaView;

/**
 * @author Zun Wang
 */
public class CopyPropagatorIT {

  public View setUp() {
    String baseDir = "../shared-test-resources/interceptors/";
    JavaClassPathAnalysisInputLocation inputLocation =
        new JavaClassPathAnalysisInputLocation(
            baseDir, SourceType.Library, Collections.emptyList());
    final JavaView view = new JavaView(Arrays.asList(inputLocation));
    return view;
  }

  @Test
  public void testCopyPropagationWithRedefinition() {
    View view = setUp();
    final MethodSignature methodSignature =
        view.getIdentifierFactory()
            .getMethodSignature(
                "CopyPropagatorTest", "tc1", "void", Collections.singletonList("int"));
    Body bodyBefore = view.getMethod(methodSignature).get().getBody();
    final Body.BodyBuilder builder = Body.builder(bodyBefore, Collections.emptySet());
    new CopyPropagator().interceptBody(builder, view);
    Body bodyAfter = builder.build();
    assertEquals(
        Stream.of(
                "CopyPropagatorTest this",
                "int l1",
                "unknown l2, l3, l4",
                "this := @this: CopyPropagatorTest",
                "l1 := @parameter0: int",
                "l3 = 0",
                "l2 = l1",
                "l1 = 10",
                "l3 = 20",
                // l2 should not be replaced with l1 as l1 gets redefined
                "l4 = l2 + 20",
                "return")
            .collect(Collectors.toList()),
        Utils.filterJimple(bodyAfter.toString()));
  }

  @Test
  void testBigInput() {
    AnalysisInputLocation inputLocation =
        new ClassFileBasedAnalysisInputLocation(
            Paths.get("../shared-test-resources/bugfixes/SlowCopyPropagator.class"),
            "",
            SourceType.Application,
            Collections.singletonList(new CopyPropagator()));

    JavaView view = new JavaView(inputLocation);
    final SootMethod sootMethod =
        view.getMethod(
                view.getIdentifierFactory()
                    .parseMethodSignature("<SlowCopyPropagator: void foo()>"))
            .get();

    Body body = sootMethod.getBody();
    assertFalse(body.toString().isEmpty());
  }
}
