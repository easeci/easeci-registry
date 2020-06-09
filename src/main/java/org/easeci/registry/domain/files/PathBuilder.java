package org.easeci.registry.domain.files;

import java.nio.file.Path;

public class PathBuilder {

    public static Path buildPath(final String storagePath, final String fileExtension, final String performerName, final String performerVersion) {
        return Path.of(storagePath.concat("/")
                .concat(performerName)
                .concat("/")
                .concat(performerVersion)
                .concat("/")
                .concat(performerName.toLowerCase())
                .concat(fileExtension));
    }
}
