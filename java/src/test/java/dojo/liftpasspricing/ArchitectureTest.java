package dojo.liftpasspricing;

import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;
import dojo.liftpasspricing.infrastructure.LiftPassServer;

import java.sql.SQLException;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noMethods;
import static com.tngtech.archunit.library.Architectures.layeredArchitecture;

@AnalyzeClasses(packages = "dojo.liftpasspricing")
public class ArchitectureTest {

    @ArchTest
    static ArchRule services_should_be_prefixed =
            classes()
                    .that().resideInAPackage("..service..")
                    .and().areNotNestedClasses()
                    .should().haveSimpleNameContaining("Service");

    @ArchTest
    static final ArchRule layer_dependencies_are_respected = layeredArchitecture()
            .layer("Infrastructure").definedBy("dojo.liftpasspricing.infrastructure..")
            .layer("Service").definedBy("dojo.liftpasspricing.service..")
            .whereLayer("Infrastructure").mayOnlyBeAccessedByLayers("Service")
            .ignoreDependency(Main.class, LiftPassServer.class)
            .ignoreDependency(ArchitectureTest.class, LiftPassServer.class)
            .because("Of clean architecture")
            ;

    @ArchTest
    static final ArchRule classes_other_than_repositories_must_not_throw_SQLExceptions =
            noMethods().that().areDeclaredInClassesThat().haveNameMatching(".*Repository")
                    .should().declareThrowableOfType(SQLException.class);
}
