package org.easeci.registry.domain.files;

import org.junit.jupiter.api.Test;

import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

class PathBuilderTest {

    @Test
    void buildFileNameTest() {
        String localStorage = "/tmp/easeci/registry";
        String fileExtension = ".jar";
        String extensionName = "git";
        String extensionVersion = "0.0.1";

        Path path = PathBuilder.buildPath(localStorage, fileExtension, extensionName, extensionVersion);

        assertEquals(path.toString(), "/tmp/easeci/registry/git/0.0.1/git.jar");
    }
}