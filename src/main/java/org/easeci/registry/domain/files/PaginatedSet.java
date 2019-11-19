package org.easeci.registry.domain.files;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Collection;

@Getter
@AllArgsConstructor
public class PaginatedSet<T> {
    private Collection<T> paginatedCollection;
    private int elementsOnPage;
    private int page;
    private int allPages;
}
