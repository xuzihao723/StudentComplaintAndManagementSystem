package edu.demo.scfs.service;

import edu.demo.scfs.domain.Category;

public final class CategoryPolicy {
    private CategoryPolicy() {
    }

    public static void validateAnonymous(Category category, boolean anonymous) {
        if (anonymous && !category.isAnonymousAllowed()) {
            throw new IllegalArgumentException("This category does not allow anonymous submissions.");
        }
    }
}

