package org.easeci.registry.utils;

import org.easeci.registry.domain.api.dto.FileUploadRequest;
import org.easeci.registry.domain.files.FileRepresentation;

public class CommonUtils {

    public final static String scriptExample = "friends = ['john', 'pat', 'gary', 'michael']\n" +
            "for i, name in enumerate(friends):\n" +
            "    print (\"iteration {iteration} is {name}\".format(iteration=i, name=name))";

    public static FileRepresentation factorizeFileRepresentation() {
        return FileRepresentation.builder()
                .meta(FileRepresentation.FileMeta.builder()
                        .performerName("Test")
                        .performerVersion("0.0.1")
                        .build())
                .payload(scriptExample.getBytes())
                .build();
    }

    public static FileRepresentation factorizeFileRepresentationNextVersion() {
        return FileRepresentation.builder()
                .meta(FileRepresentation.FileMeta.builder()
                        .performerName("Test")
                        .performerVersion("0.0.2")
                        .build())
                .payload(scriptExample.getBytes())
                .build();
    }

    public static FileUploadRequest factorizeFileUploadRequest() {
        return FileUploadRequest.builder()
                .authorFullname("John Doe")
                .authorEmail("john.doe@ease.ci")
                .company("Ease CI")
                .performerName("Docker")
                .performerVersion("0.0.1")
                .multipartFile(scriptExample.getBytes())
                .build();
    }

    public static FileUploadRequest factorizeFileUploadRequestNextVersion() {
        return FileUploadRequest.builder()
                .authorFullname("John Doe")
                .authorEmail("john.doe@ease.ci")
                .company("Ease CI")
                .performerName("Docker")
                .performerVersion("0.0.2")
                .multipartFile(scriptExample.getBytes())
                .build();
    }
}
