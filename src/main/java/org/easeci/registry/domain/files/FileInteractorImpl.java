package org.easeci.registry.domain.files;

import java.util.List;
import java.util.Map;

class FileInteractorImpl implements FileInteractor {

    @Override
    public RegistryStatus persist(FileRepresentation fileRepresentation) {
        return null;
    }

    @Override
    public FileRepresentation get(Map<String, String> params) {
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
