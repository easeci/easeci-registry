package org.easeci.registry.domain.files;

import io.vavr.Function2;
import io.vavr.Function7;
import io.vavr.collection.Seq;
import io.vavr.control.Validation;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.easeci.registry.domain.files.dto.AddPerformerResponse;
import org.easeci.registry.domain.token.UploadTokenService;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.jar.Attributes;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.Objects.nonNull;

@Slf4j
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class JarFileValidator {
    private PerformerRepository performerRepository;
    private UploadTokenService tokenService;
    private JarFileValidatorHelper jarFileValidatorHelper;

    public static JarFileValidator of(UploadTokenService tokenService, PerformerRepository performerRepository, JarFileValidatorHelper jarFileValidatorHelper) {
        return new JarFileValidator(performerRepository, tokenService, jarFileValidatorHelper);
    }

    public AddPerformerResponse check(FileRepresentation fileRepresentation) {
        log.info("Checking validity of incoming file: {}", fileRepresentation.toString());
        Path pathTmp = jarFileValidatorHelper.saveTmp(fileRepresentation.getPayload(), fileRepresentation.getMeta().getPerformerName(), fileRepresentation.getMeta().getPerformerVersion());
        Attributes attributes = jarFileValidatorHelper.extract(pathTmp.toFile());

        Validation<Seq<String>, Boolean> preValidation =
                Validation.combine(isAttributesNull(attributes), isAttributesHasMinSize(attributes))
                        .ap((Function2<Boolean, Boolean, Boolean>) Boolean::logicalAnd);

        Validation<Seq<String>, Boolean> validationSequence =
                Validation.combine(
                        isFileNotExistsOnTmpStorage(pathTmp),
                        isExtensionNotExists(fileRepresentation.getMeta().getPerformerName(), fileRepresentation.getMeta().getPerformerVersion()),
                        isMetadataCorrect(fileRepresentation.getMeta()),
                        isValidTokenContains(attributes),
                        isValidImplementsProperty(attributes),
                        isValidEntryClassProperty(attributes),
                        jarFileValidatorHelper.deleteTemporaryFile(pathTmp)
                                ? Validation.valid(true)
                                : Validation.invalid("Error occurred while deleting temporary files")
                ).ap((Function7<Boolean, Boolean, Boolean, Boolean, Boolean, Boolean, Boolean, Boolean>)
                        (conditionA, conditionB, conditionC, conditionD, conditionE, conditionF, conditionG)
                                -> conditionA && conditionB && conditionC && conditionD && conditionE && conditionF && conditionG);

        return Stream.of(preValidation, validationSequence)
                .map(this::transformSequence)
                .reduce((a, b) -> merge(Set.of(a, b)))
                .orElse(AddPerformerResponse.of(false, List.of(new AddPerformerResponse.ValidationError("Internal validation error occurred"))));
    }

    public Validation<String, Boolean> isAttributesNull(Attributes attributes) {
        return nonNull(attributes)
                ? Validation.valid(true)
                : Validation.invalid("Cannot read Attributes of manifest that should be placed in your .jar file");
    }

    public Validation<String, Boolean> isAttributesHasMinSize(Attributes attributes) {
        final int MIN_SIZE = 4;
        return nonNull(attributes) && attributes.size() >= MIN_SIZE
                ? Validation.valid(true)
                : Validation.invalid("Manifest has less than " + MIN_SIZE + " attributes");
    }

    public Validation<String, Boolean> isFileNotExistsOnTmpStorage(Path filePath) {
        return Files.notExists(filePath)
                ? Validation.valid(true)
                : Validation.invalid("Temporary file for validation exists. Maybe you download same plugin twice and process not ends yet?");
    }

    public Validation<String, Boolean> isExtensionNotExists(String extensionName, String extensionVersion) {
        return !(performerRepository.isVersionExists(extensionName, extensionVersion) > 0)
                ? Validation.valid(true)
                : Validation.invalid("Plugin just exists in database");
    }

    public Validation<String, Boolean> isMetadataCorrect(FileRepresentation.FileMeta fileMeta) {
        return (nonNull(fileMeta.getAuthorFullname()) && !fileMeta.getAuthorFullname().isEmpty()) &&
                (nonNull(fileMeta.getAuthorEmail()) && !fileMeta.getAuthorEmail().isEmpty()) &&
                (nonNull(fileMeta.getPerformerName()) && !fileMeta.getPerformerName().isEmpty()) &&
                (nonNull(fileMeta.getPerformerVersion()) && !fileMeta.getPerformerVersion().isEmpty())
                ? Validation.valid(true)
                : Validation.invalid("Some of metadata of file uploading object is missing");
    }

    public Validation<String, Boolean> isValidTokenContains(Attributes attributes) {
        final String ATTR_NAME = "Token";
        return (nonNull(attributes.getValue(ATTR_NAME)) &&
                tokenService.isTokenAvailable(attributes.getValue(ATTR_NAME)))
                ? Validation.valid(true)
                : Validation.invalid("Your 'Token' is invalid. Cannot match it with available tokens");
    }

    public Validation<String, Boolean> isValidImplementsProperty(Attributes attributes) {
        final String ATTR_NAME = "Implements";
        return (nonNull(attributes.getValue(ATTR_NAME)) && !attributes.getValue(ATTR_NAME).isEmpty())
                ? Validation.valid(true)
                : Validation.invalid("'Implements' property not exists in MANIFEST.md");
    }

    public Validation<String, Boolean> isValidEntryClassProperty(Attributes attributes) {
        final String ATTR_NAME = "Entry-Class";
        return (nonNull(attributes.getValue(ATTR_NAME)) && !attributes.getValue(ATTR_NAME).isEmpty())
                ? Validation.valid(true)
                : Validation.invalid("'Entry-Class' property not exists in MANIFEST.md");
    }

    private AddPerformerResponse merge(Set<AddPerformerResponse> addPerformerResponses) {
        boolean isAddedCorrectly = addPerformerResponses.stream()
                .anyMatch(addPerformerResponse -> !addPerformerResponse.isAddedCorrectly());
        List<AddPerformerResponse.ValidationError> validationErrors = addPerformerResponses.stream()
                .filter(Objects::nonNull)
                .flatMap(addPerformerResponse -> addPerformerResponse.getValidationErrorList().stream()).collect(Collectors.toList());
        return AddPerformerResponse.of(isAddedCorrectly, validationErrors);
    }

    private AddPerformerResponse transform(Validation<String, Boolean> validation) {
        Optional<String> error = Optional.ofNullable(validation.isInvalid() ? validation.getError() : null);
        List<AddPerformerResponse.ValidationError> validationErrors = new ArrayList<>();
        error.map(AddPerformerResponse.ValidationError::new)
                .ifPresent(validationErrors::add);
        return AddPerformerResponse.of(validation.isValid(), validationErrors);
    }

    private AddPerformerResponse transformSequence(Validation<Seq<String>, Boolean> validationSequence) {
        List<String> errors = validationSequence.isInvalid() ? validationSequence.getError().toJavaList() : Collections.emptyList();
        return AddPerformerResponse.of(
                validationSequence.isValid(),
                errors.stream()
                        .map(AddPerformerResponse.ValidationError::new)
                        .collect(Collectors.toList())
        );
    }
}