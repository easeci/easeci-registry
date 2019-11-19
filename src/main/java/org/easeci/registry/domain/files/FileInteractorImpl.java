package org.easeci.registry.domain.files;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.*;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
class FileInteractorImpl implements FileInteractor {

    @Value("${storage.dir}")
    private String storage;

    @Override
    public RegistryStatus persist(FileRepresentation fileRepresentation) {
        final String performerName = fileRepresentation.getMeta().getPerformerName();
        final String performerPath = storage.concat("/").concat(performerName);
        if (!Files.exists(Path.of(performerPath))) {
            RegistryStatus registryStatus = makeDir(performerName);
            if (!registryStatus.equals(RegistryStatus.CREATED)) {
                log.error("Cannot create directory for performer with name '" + performerName + "'");
                return RegistryStatus.SAVE_FAILED;
            }
        }

        String performerVersion = fileRepresentation.getMeta().getPerformerVersion();
        RegistryStatus registryStatus = makeDir(performerName.concat("/").concat(performerVersion));
        if (!registryStatus.equals(RegistryStatus.CREATED)) {
            if (Files.exists(Path.of(trim(storage).concat("/").concat(performerName.concat("/").concat(performerVersion))))) {
                log.error("Cannot override script that just exists!");
                return RegistryStatus.FILE_JUST_EXISTS;
            } else {
                log.error("Cannot create directory for performer's version with name '" + performerName + "', and version: " + fileRepresentation.getMeta().getPerformerVersion());
            }
            return RegistryStatus.SAVE_FAILED;
        }
        final String completePath = performerPath.concat("/").concat(performerVersion);
        Path scriptPath = Path.of(completePath.concat("/").concat(performerName.toLowerCase().concat(".py")));
        return placeContent(scriptPath, fileRepresentation.getPayload());
    }

    private RegistryStatus makeDir(String nameDir) {
        nameDir = trim(nameDir);
        try {
            Files.createDirectory(Path.of(storage.concat("/").concat(nameDir)));
            return RegistryStatus.CREATED;
        } catch (IOException e) {
            return RegistryStatus.CREATION_FAILED;
        }
    }

    private String trim(String path) {
        if (path.endsWith("/")) {
            return path.substring(0, path.length() - 1);
        }
        return path;
    }

    private RegistryStatus placeContent(Path filePath, byte[] scriptAsBytes) {
        try {
            Files.write(filePath, scriptAsBytes, StandardOpenOption.CREATE_NEW);
            return RegistryStatus.SAVED;
        } catch (IOException e) {
            return RegistryStatus.SAVE_FAILED;
        }
    }

    @Override
    public FileRepresentation get(Map<String, String> params) {
        return null;
    }

    @Override
    public List<FileRepresentation.FileMeta> get(String performerName) {
        return null;
    }

    @Override
    public List<FileRepresentation.FileMeta> listByName(String performer) {
        return null;
    }

    @Override
    public PaginatedSet<FileRepresentation.FileMeta> listAll(PageRequest pageRequest) {
        return null;
    }
}
