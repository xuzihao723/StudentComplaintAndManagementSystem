package edu.demo.scfs.util;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class CaseNumberGeneratorTest {

    @Test
    void generatesReadableUniqueCaseNumbers() {
        CaseNumberGenerator generator = new CaseNumberGenerator();

        String first = generator.generate();
        String second = generator.generate();

        assertThat(first).startsWith("SCF-");
        assertThat(first).matches("SCF-\\d{8}-[A-Z0-9]{6}");
        assertThat(second).isNotEqualTo(first);
    }
}

