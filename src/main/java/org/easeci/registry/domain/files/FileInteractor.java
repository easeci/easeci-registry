package org.easeci.registry.domain.files;

import java.util.List;
import java.util.Map;

interface FileInteractor {

    /**
     * This method can persist Performer developed and uploaded by
     * programmer with active account in EaseCI Registry
     * */
    RegistryStatus persist(FileRepresentation fileRepresentation);

    /**
     * Helps to this method we can download complete Performer script
     * As arguments this method gets map of parameters with such keys:
     * - Performer's name as `name` key
     * - Performer's version as `version`
     * */
    FileRepresentation get(Map<String, String> params);

    /**
     * This method is created for fetching and presenting data on frontend
     * By `performerName` you can get details about Performer.
     * */
    List<FileRepresentation.FileMeta> get(String performerName);

    /**
     * This method lists all available versions of Performer specified
     * by name provided as method argument
     * */
    List<FileRepresentation.FileMeta> listByName(String performer);

    /**
     * This method allows to paginate all available Performers in disk.
     * As argument this method pass PageRequest - object that hold info
     * about elements amount, elements in page whole pages and page numbers.
     * */
    PaginatedSet<FileRepresentation.FileMeta> listAll(PageRequest pageRequest);
}
