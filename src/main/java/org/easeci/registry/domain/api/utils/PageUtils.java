package org.easeci.registry.domain.api.utils;

import static java.util.Objects.isNull;

public class PageUtils {

    public static int transformPageNum(String page) {
        int pageNum = 0;
        if (isNull(page) || page.isEmpty()) {
            return pageNum;
        }
        return Integer.parseInt(page) - 1;
    }
}
