package org.easeci.registry.domain.files;

public class ExtensionNotExistsException extends Exception {

    public ExtensionNotExistsException(String performerName, String performerVersion) {
        super("Cannot find extension: " + performerName + ", with version: " + performerVersion);
    }
}
