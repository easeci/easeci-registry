package org.easeci.registry.domain.files;

import lombok.Getter;

@Getter
public class PageRequest {
    private final int PAGE_SIZE = 10;
    private long page;

    private PageRequest(long page) {
        this.page = page;
    }

    public static PageRequest of(long page) {
        return new PageRequest(page);
    }
}