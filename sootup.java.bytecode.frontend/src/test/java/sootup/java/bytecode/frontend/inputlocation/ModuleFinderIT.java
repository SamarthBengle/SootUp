package sootup.java.bytecode.frontend.inputlocation;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Paths;
import java.util.Collection;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import sootup.java.core.JavaModuleIdentifierFactory;
import sootup.java.core.signatures.ModuleSignature;

/**
 * @author Kaustubh Kelkar
 */
public class ModuleFinderIT extends AnalysisInputLocation {

  @Test
  public void discoverJarModuleByName() {
    ModuleFinderIT moduleFinderIT = new ModuleFinderIT(jar);
    sootup.core.inputlocation.AnalysisInputLocation inputLocation =
        moduleFinderIT.getModule(JavaModuleIdentifierFactory.getModuleSignature("MiniApp"));
    assertTrue(inputLocation instanceof PathBasedAnalysisInputLocationIT);
  }

  @Test
  public void discoverJarModuleInAllModules() {
    ModuleFinderIT moduleFinderIT = new ModuleFinderIT(jar);
    Collection<ModuleSignature> modules = moduleFinderIT.getAllModules();
    assertTrue(modules.contains(JavaModuleIdentifierFactory.getModuleSignature("MiniApp")));
  }

  @Test
  public void discoverWarModuleByName() {
    ModuleFinderIT moduleFinderIT = new ModuleFinderIT(war);
    sootup.core.inputlocation.AnalysisInputLocation inputLocation =
        moduleFinderIT.getModule(JavaModuleIdentifierFactory.getModuleSignature("dummyWarApp"));
    assertTrue(inputLocation instanceof PathBasedAnalysisInputLocationIT);
  }

  @Test
  public void discoverWarModuleInAllModules() {
    ModuleFinderIT moduleFinderIT = new ModuleFinderIT(war);
    Collection<ModuleSignature> modules = moduleFinderIT.getAllModules();
    assertTrue(modules.contains(JavaModuleIdentifierFactory.getModuleSignature("dummyWarApp")));
  }

  @Test
  public void testModuleJar() {
    ModuleFinderIT moduleFinderIT =
        new ModuleFinderIT(
            Paths.get("../shared-test-resources/java9-target/de/upb/soot/namespaces/modules/"));
    Collection<ModuleSignature> discoveredModules = moduleFinderIT.getAllModules();
    assertTrue(
        discoveredModules.contains(JavaModuleIdentifierFactory.getModuleSignature("de.upb.mod")));
  }

  @Test
  public void testModuleExploded() {
    ModuleFinderIT moduleFinderIT =
        new ModuleFinderIT(
            Paths.get("../shared-test-resources/java9-target/de/upb/soot/namespaces/modules/"));
    Collection<ModuleSignature> discoveredModules = moduleFinderIT.getAllModules();
    assertTrue(
        discoveredModules.contains(JavaModuleIdentifierFactory.getModuleSignature("fancyMod")));
  }

  @Test
  public void testAutomaticModuleNaming() {
    Assertions.assertEquals(
        "foo.bar", ModuleFinderIT.createModuleNameForAutomaticModule(Paths.get("foo-bar.jar")));
    Assertions.assertEquals(
        "foo",
        ModuleFinderIT.createModuleNameForAutomaticModule(Paths.get("foo-1.2.3-SNAPSHOT.jar")));
  }

  @Test
  public void testAutomaticModuleNamingViaManifest() {

    ModuleFinderIT moduleFinderIT =
        new ModuleFinderIT(
            Paths.get(
                "../shared-test-resources/java9-target/de/upb/soot/namespaces/modules/automaticModuleWithManifest"));

    assertNotNull(
        moduleFinderIT.getModule(
            JavaModuleIdentifierFactory.getModuleSignature(
                "automaticmoduleWithNamingViaManifestModuleName")));

    assertNull(
        moduleFinderIT.getModule(
            JavaModuleIdentifierFactory.getModuleSignature("AutomaticmoduleWithManifest")));
  }
}
