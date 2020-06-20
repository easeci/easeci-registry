package org.easeci.registry.domain.files;

import io.vavr.Function2;
import io.vavr.Function8;
import io.vavr.collection.Seq;
import io.vavr.control.Validation;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.easeci.registry.domain.files.dto.AddPerformerResponse;

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
    private JarFileValidatorHelper jarFileValidatorHelper;

    public static JarFileValidator of(PerformerRepository performerRepository, String temporaryStorage, String fileExtension) {
        return new JarFileValidator(performerRepository, new JarFileValidatorHelper(temporaryStorage, fileExtension));
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
                        isJarFileNameMatchesMetadata(fileRepresentation.getMeta(), attributes),
                        isValidTokenContains(attributes),
                        isValidImplementsProperty(attributes),
                        isValidEntryClassProperty(attributes),
                        jarFileValidatorHelper.deleteTemporaryFile(pathTmp)
                                ? Validation.valid(true)
                                : Validation.invalid("Error occurred while deleting temporary files")
                ).ap((Function8<Boolean, Boolean, Boolean, Boolean, Boolean, Boolean, Boolean, Boolean, Boolean>)
                        (conditionA, conditionB, conditionC, conditionD, conditionE, conditionF, conditionG, conditionH)
                                -> conditionA && conditionB && conditionC && conditionD && conditionE && conditionF && conditionG && conditionH);

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
        return null;
    }

    public Validation<String, Boolean> isJarFileNameMatchesMetadata(FileRepresentation.FileMeta fileMeta, Attributes attributes) {
        return null;
    }

    public Validation<String, Boolean> isValidTokenContains(Attributes attributes) {
        return null;
    }

    public Validation<String, Boolean> isValidImplementsProperty(Attributes attributes) {
        return null;
    }

    public Validation<String, Boolean> isValidEntryClassProperty(Attributes attributes) {
        return null;
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