package org.easeci.registry.domain.files;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PageRequest {
    private int elements;
    private int page;
    private SortDirection sortDirection;
}

enum SortDirection {
    ASC,
    DESC
}