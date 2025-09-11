package com.xavier.sigasaasapi;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.modulith.core.ApplicationModules;
import org.springframework.modulith.docs.Documenter;

import static org.assertj.core.api.Assertions.assertThat;

public class ModularityTest {
    ApplicationModules modules = ApplicationModules.of(SigaSaasApiApplication.class);

    @Test
    @DisplayName("Verify modular structure")
    void verifyModularStructure() {
        modules.verify();
    }

    /**
     * Tests that the common module is properly configured and accessible to other modules.
     */
    @Test
    void verifyCommonModuleStructure() {
        ApplicationModules modules = ApplicationModules.of(SigaSaasApiApplication.class);

        // Verify common module exists
        assertThat(modules.getModuleByName("common")).isPresent();

        var commonModule = modules.getModuleByName("common").get();

        // Verify common module has the expected base packages
        assertThat(commonModule.getBasePackage().getName())
                .isEqualTo("com.xavier.sigasaasapi.common");
    }

    @Test
    @DisplayName("Generate modulith documentation")
    void generateModulithDocumentation() {
        new Documenter(modules)
                .writeDocumentation()
                .writeModulesAsPlantUml()
                .writeIndividualModulesAsPlantUml();
    }
}
