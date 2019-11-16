package org.easeci.registry.domain.files;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Set;

@Getter
@AllArgsConstructor
public class PaginatedSet<T> {
    private Set<T> paginatedSet;
    private int elements;
    private int page;
    private int pages;
}
