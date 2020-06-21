package org.easeci.registry.domain.files;

import io.vavr.control.Validation;
import org.easeci.registry.domain.token.UploadTokenService;
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

import static org.easeci.registry.utils.CommonUtils.factorizeCompleteFileRepresentation;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;

class JarFileValidatorTest {
    private PerformerRepository performerRepository;
    private UploadTokenService tokenService;
    private JarFileValidator jarFileValidator;

    private final static String TEMPORARY_STORAGE = "/tmp/",
                                   FILE_EXTENSION = ".jar";

    @BeforeEach
    void setup() {
        this.performerRepository = Mockito.mock(PerformerRepository.class);
        this.tokenService = Mockito.mock(UploadTokenService.class);
        this.jarFileValidator = JarFileValidator.of(tokenService, performerRepository, new JarFileValidatorHelper(TEMPORARY_STORAGE, FILE_EXTENSION));
    }

    @Test
    @DisplayName("Should pass validation when uploaded file has more than zero bytes")
    void isUploadedFileNotEmptyPositiveTest() {
        byte[] bytes = new byte[] {1, 5, 4};

        Validation<String, Boolean> validation = jarFileValidator.isUploadedFileNotEmpty(bytes);

        assertAll(() -> assertNotNull(validation),
                () -> assertTrue(validation.isValid()),
                () -> assertThrows(NoSuchElementException.class, validation::getError));
    }

    @Test
    @DisplayName("Should not pass validation when uploaded file is empty")
    void isUploadedFileNotEmptyNegativeTest() {
        byte[] bytes = new byte[] {};

        Validation<String, Boolean> validation = jarFileValidator.isUploadedFileNotEmpty(bytes);

        assertAll(() -> assertNotNull(validation),
                () -> assertFalse(validation.isValid()),
                () -> assertEquals("No file detected in your request or file is malformed", validation.getError()));
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
    @DisplayName("Should pass validation when file exists on passed path")
    void isFileExistsOnTmpStoragePositiveTest() throws IOException {
        final Path PATH = Paths.get("/tmp/test-file.txt");
        Files.createFile(PATH);

        Validation<String, Boolean> validation = jarFileValidator.isFileExistsOnTmpStorage(PATH);

        assertAll(() -> assertNotNull(validation),
                () -> assertTrue(validation.isValid()),
                () -> assertThrows(NoSuchElementException.class, validation::getError));

        Files.deleteIfExists(PATH);
    }

    @Test
    @DisplayName("Should not pass validation when file not exists on passed path")
    void isFileExistsOnTmpStorageNegativeTest() throws IOException {
        final Path PATH = Paths.get("/tmp/test-file.txt");

        Validation<String, Boolean> validation = jarFileValidator.isFileExistsOnTmpStorage(PATH);

        assertAll(() -> assertNotNull(validation),
                () -> assertFalse(validation.isValid()),
                () -> assertEquals("Temporary file for validation exists. Maybe you download same plugin twice and process not ends yet?", validation.getError()));
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

    @Test
    @DisplayName("Should pass validation when metadata is correct")
    void isMetadataCorrectPositiveTest() {
        FileRepresentation fileRepresentation = factorizeCompleteFileRepresentation();

        Validation<String, Boolean> validation = jarFileValidator.isMetadataCorrect(fileRepresentation.getMeta());

        assertAll(() -> assertNotNull(validation),
                () -> assertTrue(validation.isValid()),
                () -> assertThrows(NoSuchElementException.class, validation::getError));
    }

    @Test
    @DisplayName("Should not pass validation when some metadata is missing")
    void isMetadataCorrectNegativeTest() {
        FileRepresentation fileRepresentation = factorizeCompleteFileRepresentation();
        fileRepresentation.getMeta().setAuthorEmail(null);

        Validation<String, Boolean> validation = jarFileValidator.isMetadataCorrect(fileRepresentation.getMeta());

        assertAll(() -> assertNotNull(validation),
                () -> assertFalse(validation.isValid()),
                () -> assertEquals("Some of metadata of file uploading object is missing", validation.getError()));
    }

    @Test
    @DisplayName("Should pass validation when token in jar file is available in database")
    void isValidTokenContainsPositiveTest() {
        final String ATTR_NAME = "Token";
        final String TOKEN = "i2mub7bydvtf67ybbkjn880m";
        Mockito.when(tokenService.isTokenAvailable(TOKEN)).thenReturn(true);

        Attributes attributes = new Attributes();
        attributes.put(new Attributes.Name(ATTR_NAME), TOKEN);

        Validation<String, Boolean> validation = jarFileValidator.isValidTokenContains(attributes);

        assertAll(() -> assertNotNull(validation),
                () -> assertTrue(validation.isValid()),
                () -> assertThrows(NoSuchElementException.class, validation::getError));
    }

    @Test
    @DisplayName("Should not pass validation when token in jar file is not available in database - another plugin is assigned to this one")
    void isValidTokenContainsNegativeTest() {
        final String ATTR_NAME = "Token";
        final String TOKEN = "i2mub7bydvtf67ybbkjn880m";
        Mockito.when(tokenService.isTokenAvailable(TOKEN)).thenReturn(false);

        Attributes attributes = new Attributes();
        attributes.put(new Attributes.Name(ATTR_NAME), TOKEN);

        Validation<String, Boolean> validation = jarFileValidator.isValidTokenContains(attributes);

        assertAll(() -> assertNotNull(validation),
                () -> assertFalse(validation.isValid()),
                () -> assertEquals("Your 'Token' is invalid. Cannot match it with available tokens", validation.getError()));
    }

    @Test
    @DisplayName("Should pass validation when 'Implements' property exists in MANIFEST.md")
    void isValidImplementsPropertyPositiveTest() {
        Attributes attributes = new Attributes();
        attributes.put(new Attributes.Name("Implements"), "...");

        Validation<String, Boolean> validation = jarFileValidator.isValidImplementsProperty(attributes);

        assertAll(() -> assertNotNull(validation),
                () -> assertTrue(validation.isValid()),
                () -> assertThrows(NoSuchElementException.class, validation::getError));
    }

    @Test
    @DisplayName("Should not pass validation when 'Implements' property not exists in MANIFEST.md")
    void isValidImplementsPropertyNegativeTest() {
        Attributes attributes = new Attributes();

        Validation<String, Boolean> validation = jarFileValidator.isValidImplementsProperty(attributes);

        assertAll(() -> assertNotNull(validation),
                () -> assertFalse(validation.isValid()),
                () -> assertEquals("'Implements' property not exists in MANIFEST.md", validation.getError()));
    }

    @Test
    @DisplayName("Should pass validation when 'Entry-Class' property exists in MANIFEST.md")
    void isValidEntryClassPropertyPositiveTest() {
        Attributes attributes = new Attributes();
        attributes.put(new Attributes.Name("Entry-Class"), "...");

        Validation<String, Boolean> validation = jarFileValidator.isValidEntryClassProperty(attributes);

        assertAll(() -> assertNotNull(validation),
                () -> assertTrue(validation.isValid()),
                () -> assertThrows(NoSuchElementException.class, validation::getError));
    }

    @Test
    @DisplayName("Should not pass validation when 'Entry-Class' property not exists in MANIFEST.md")
    void isValidEntryClassPropertyNegativeTest() {
        Attributes attributes = new Attributes();

        Validation<String, Boolean> validation = jarFileValidator.isValidEntryClassProperty(attributes);

        assertAll(() -> assertNotNull(validation),
                () -> assertFalse(validation.isValid()),
                () -> assertEquals("'Entry-Class' property not exists in MANIFEST.md", validation.getError()));
    }
}