package edu.demo.scfs.service;

import edu.demo.scfs.domain.Category;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CategoryPolicyTest {

    @Test
    void rejectsAnonymousSubmissionWhenCategoryDoesNotAllowIt() {
        Category category = new Category();
        category.setName("Academic Services");
        category.setAnonymousAllowed(false);

        assertThatThrownBy(() -> CategoryPolicy.validateAnonymous(category, true))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("anonymous");
    }
}

