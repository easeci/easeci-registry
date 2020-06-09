package org.easeci.registry.domain.files;

import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.easeci.registry.domain.files.dto.AddPerformerResponse;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.*;
import java.util.function.Predicate;
import java.util.jar.Attributes;
import java.util.jar.JarFile;
import java.util.stream.Collectors;

import static org.easeci.registry.domain.files.PathBuilder.buildPath;

@Slf4j
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class JarFileValidator {
    private PerformerRepository performerRepository;
    private String temporaryStorage;
    private String fileExtension;

    public static JarFileValidator of(PerformerRepository performerRepository, String temporaryStorage, String fileExtension) {
        return new JarFileValidator(performerRepository, temporaryStorage, fileExtension);
    }

    public AddPerformerResponse check(FileRepresentation fileRepresentation) {
        log.info("Checking validity of incoming file: {}", fileRepresentation.toString());
        Path pathTmp = saveTmp(fileRepresentation.getPayload(), fileRepresentation.getMeta().getPerformerName(), fileRepresentation.getMeta().getPerformerVersion());
//        Attributes attributes = extract(pathTmp.toFile());

        boolean fileNotExistsOnTmpStorage = isFileNotExistsOnTmpStorage(pathTmp);
        boolean extensionNotExists = isExtensionNotExists(fileRepresentation.getMeta().getPerformerName(), fileRepresentation.getMeta().getPerformerVersion());
//        boolean jarAttributesValidity = Pipeline.make(attributes)
//                .join(attributeValue -> attributeValue.startsWith("io.easeci.extension"), "Implements")
//                .join(Objects::nonNull, "Entry-Class")
//                .join(this::checkUuid, "Token")
//                .evaluate()
//                .success();



//        boolean isTmpFileDeleted = deleteTemporaryFile(pathTmp);

//        return fileNotExistsOnTmpStorage && extensionNotExists && jarAttributesValidity && isTmpFileDeleted;
//        return fileNotExistsOnTmpStorage && extensionNotExists && isTmpFileDeleted;
//        return AddPerformerResponse.of(false, List.of(new AddPerformerResponse.ValidationError("Cannot add property")));
        return AddPerformerResponse.of(true, List.of());
    }

    private boolean deleteTemporaryFile(Path pathTmp) {
        try {
            return Files.deleteIfExists(pathTmp);
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    private boolean checkUuid(String attributeValue) {
        try {
            UUID.fromString(attributeValue);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private Path saveTmp(byte[] received, String extensionName, String extensionVersion) {
        Path pathTmp = buildPath(temporaryStorage, fileExtension, extensionName, extensionVersion);
        try {
            Files.createDirectories(pathTmp.getParent());
            Files.write(pathTmp, received, StandardOpenOption.CREATE);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return pathTmp;
    }

    private Attributes extract(File file) {
        try {
            return new JarFile(file).getManifest().getMainAttributes();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private boolean isFileNotExistsOnTmpStorage(Path filePath) {
        return Files.exists(filePath);
    }

    private boolean isExtensionNotExists(String extensionName, String extensionVersion) {
        return performerRepository.isVersionExists(extensionName, extensionVersion) == 0;
    }

    private boolean isMetadataCorrect(FileRepresentation.FileMeta fileMeta) {
        return true;
    }

}

@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
class Pipeline {
    private PipelineSet<Item> itemSet;
    private Attributes attributes;

    static Pipeline make(Attributes attributes) {
        return new Pipeline(new PipelineSet<>(), attributes);
    }

    Pipeline join(Predicate<String> condition, String attribute) {
        this.itemSet.add(new Item(condition, attribute));
        return this;
    }

    PipelineSet<Result> evaluate() {
        return new PipelineSet<>(itemSet.stream()
                .map(this::evaluateItem)
                .collect(Collectors.toSet()));
    }

    private Result evaluateItem(Item item) {
        String attributeValue = attributes.getValue(item.attribute);
        boolean result = item.condition.test(attributeValue);
        return Result.of(result, !result
                ? Optional.of("Manifest's attribute named: " + item.getAttribute() + " is not acceptable with value: " + attributeValue)
                : Optional.empty());
    }

    @Getter
    @ToString
    @EqualsAndHashCode
    @AllArgsConstructor
    private class Item {
        private Predicate<String> condition;
        private String attribute;
    }

    @NoArgsConstructor
    static class PipelineSet<T> extends HashSet<T> {
        public PipelineSet(Collection<? extends T> resultSet) {
            super(resultSet);
        }

        boolean success() {
            return this.stream()
                    .allMatch(t -> {
                        Result result = (Result) t;
                        return result.isSuccess();
                    });
        }
    }
}

@Getter
@ToString
@EqualsAndHashCode
@AllArgsConstructor(staticName = "of")
class Result {
    private boolean isSuccess;
    private Optional<String> message;
}
