package org.easeci.registry.domain.files;

import lombok.AllArgsConstructor;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.jar.Attributes;
import java.util.jar.JarFile;
import java.util.zip.ZipException;

import static org.easeci.registry.domain.files.PathBuilder.buildPath;

@AllArgsConstructor
public class JarFileValidatorHelper {
    private String temporaryStorage;
    private String fileExtension;

    boolean deleteTemporaryFile(Path pathTmp) {
        try {
            return Files.deleteIfExists(pathTmp);
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    Path saveTmp(byte[] received, String extensionName, String extensionVersion) {
        Path pathTmp = buildPath(temporaryStorage, fileExtension, extensionName, extensionVersion);
        try {
            Files.createDirectories(pathTmp.getParent());
            Files.write(pathTmp, received, StandardOpenOption.CREATE);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return pathTmp;
    }

    Attributes extract(File file) {
        try {
            return new JarFile(file).getManifest().getMainAttributes();
        } catch (ZipException e) {
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
