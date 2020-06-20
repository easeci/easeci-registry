package org.easeci.registry.domain.files;

import io.vavr.control.Validation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.NoSuchElementException;
import java.util.jar.Attributes;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;

class JarFileValidatorTest {
    private PerformerRepository performerRepository;
    private JarFileValidator jarFileValidator;

    private final static String TEMPORARY_STORAGE = "/tmp/",
                                   FILE_EXTENSION = ".jar";

    @BeforeEach
    void setup() {
        this.performerRepository = Mockito.mock(PerformerRepository.class);
        this.jarFileValidator = JarFileValidator.of(performerRepository, TEMPORARY_STORAGE, FILE_EXTENSION);
    }

    @Test
    @DisplayName("Should not pass validation when Attributes.class is null")
    void isAttributesNullNegativeTest() {
        Attributes attributes = null;

        Validation<String, Boolean> validation = jarFileValidator.isAttributesNull(attributes);

        assertAll(() -> assertNotNull(validation),
                () -> assertFalse(validation.isValid()),
                () -> assertEquals("Cannot read Attributes of manifest that should be placed in your .jar file", validation.getError()));
    }

    @Test
    @DisplayName("Should pass validation when Attributes.class is not null")
    void isAttributesNullPositiveTest() {
        Attributes attributes = new Attributes();

        Validation<String, Boolean> validation = jarFileValidator.isAttributesNull(attributes);

        assertAll(() -> assertNotNull(validation),
                () -> assertTrue(validation.isValid()),
                () -> assertThrows(NoSuchElementException.class, validation::getError));
    }

    @Test
    @DisplayName("Should not pass validation when Attributes.class has less than 4 size")
    void isAttributesHasMinSizeNegativeTest() {
        Attributes attributes = new Attributes();
        attributes.put(new Attributes.Name("a"), "");
        attributes.put(new Attributes.Name("b"), "");
        attributes.put(new Attributes.Name("c"), "");

        Validation<String, Boolean> validation = jarFileValidator.isAttributesHasMinSize(attributes);

        assertAll(() -> assertNotNull(validation),
                () -> assertFalse(validation.isValid()),
                () -> assertEquals("Manifest has less than 4 attributes", validation.getError()));
    }

    @Test
    @DisplayName("Should pass validation when Attributes.class has equal or more than 4 size")
    void isAttributesHasMinSizePositiveTest() {
        Attributes attributes = new Attributes();
        attributes.put(new Attributes.Name("a"), "");
        attributes.put(new Attributes.Name("b"), "");
        attributes.put(new Attributes.Name("c"), "");
        attributes.put(new Attributes.Name("d"), "");

        Validation<String, Boolean> validation = jarFileValidator.isAttributesHasMinSize(attributes);

        assertAll(() -> assertNotNull(validation),
                () -> assertTrue(validation.isValid()),
                () -> assertThrows(NoSuchElementException.class, validation::getError));
    }

    @Test
    @DisplayName("Should pass validation when file not exists on passed path")
    void isFileNotExistsOnTmpStoragePositiveTest() {
        final Path PATH = Paths.get("/tmp/test-file.txt");

        Validation<String, Boolean> validation = jarFileValidator.isFileNotExistsOnTmpStorage(PATH);

        assertAll(() -> assertNotNull(validation),
                () -> assertTrue(validation.isValid()),
                () -> assertThrows(NoSuchElementException.class, validation::getError));
    }

    @Test
    @DisplayName("Should not pass validation when file just exists on passed path")
    void isFileNotExistsOnTmpStorageNegativeTest() throws IOException {
        final Path PATH = Paths.get("/tmp/test-file.txt");
        Files.createFile(PATH);

        Validation<String, Boolean> validation = jarFileValidator.isFileNotExistsOnTmpStorage(PATH);

        assertAll(() -> assertNotNull(validation),
                () -> assertFalse(validation.isValid()),
                () -> assertEquals("Temporary file for validation exists. Maybe you download same plugin twice and process not ends yet?", validation.getError()));

        Files.deleteIfExists(PATH);
    }

    @Test
    @DisplayName("Should pass validation when extension not exists yet in system")
    void isExtensionNotExistsPositiveTest() {
        Mockito.when(performerRepository.isVersionExists(any(), any())).thenReturn(0);

        Validation<String, Boolean> validation = jarFileValidator.isExtensionNotExists("any", "0.0.1");

        assertAll(() -> assertNotNull(validation),
                () -> assertTrue(validation.isValid()),
                () -> assertThrows(NoSuchElementException.class, validation::getError));
    }

    @Test
    @DisplayName("Should not pass validation when extension just exits")
    void isExtensionNotExistsNegativeTest() {
        Mockito.when(performerRepository.isVersionExists(any(), any())).thenReturn(1);

        Validation<String, Boolean> validation = jarFileValidator.isExtensionNotExists("any", "0.0.1");

        assertAll(() -> assertNotNull(validation),
                () -> assertFalse(validation.isValid()),
                () -> assertEquals("Plugin just exists in database", validation.getError()));
    }
}