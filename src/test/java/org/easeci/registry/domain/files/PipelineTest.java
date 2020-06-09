package org.easeci.registry.domain.files;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Objects;
import java.util.UUID;
import java.util.jar.Attributes;

import static org.junit.jupiter.api.Assertions.*;

class PipelineTest {
    Attributes attributes = new Attributes();

    @BeforeEach
    void setup() {
        attributes.put(new Attributes.Name("Implements"), "io.easeci.extension.Standalone");
        attributes.put(new Attributes.Name("Entry-Class"), "com.meksula.WelcomePage");
        attributes.put(new Attributes.Name("Token"), "1ead1bd8-9b9f-11ea-bb37-0242ac130002");
    }

    @Test
    void pipelineSuccessTest() {
        boolean isSuccess = Pipeline.make(attributes)
                .join(attributeValue -> attributeValue.startsWith("io.easeci.extension"), "Implements")
                .join(Objects::nonNull, "Entry-Class")
                .join(attributeValue -> {
                    try {
                        UUID.fromString(attributeValue);
                        return true;
                    } catch (Exception e) {
                        return false;
                    }
                }, "Token")
                .evaluate()
                .success();

        assertTrue(isSuccess);
    }

    @Test
    void pipelineFailTest() {
        attributes.put(new Attributes.Name("Token"), "1ead1bd8-9b9f-11 ea-bb37-0242ac130002");

        boolean isSuccess = Pipeline.make(attributes)
                .join(attributeValue -> attributeValue.startsWith("io.easeci.extension"), "Implements")
                .join(Objects::nonNull, "Entry-Class")
                .join(attributeValue -> {
                    try {
                        UUID.fromString(attributeValue);
                        return true;
                    } catch (Exception e) {
                        return false;
                    }
                }, "Token")
                .evaluate()
                .success();

        assertFalse(isSuccess);
    }

    @Test
    void pipelineRetrieveErrorMessage() {
        attributes.put(new Attributes.Name("Token"), "31ead1bd8-9b9f-11 ea-bb37-0242ac130002");

        Pipeline.PipelineSet<Result> evaluated = Pipeline.make(attributes)
                .join(attributeValue -> attributeValue.startsWith("io.easeci.extension"), "Implements")
                .join(Objects::nonNull, "Entry-Class")
                .join(attributeValue -> {
                    try {
                        UUID.fromString(attributeValue);
                        return true;
                    } catch (Exception e) {
                        return false;
                    }
                }, "Token")
                .evaluate();

        evaluated.stream()
                .filter(result -> !result.isSuccess())
                .findFirst()
                .ifPresent(message ->
                        assertEquals("Manifest's attribute named: Token is not acceptable with value: 31ead1bd8-9b9f-11 ea-bb37-0242ac130002",
                                message.getMessage().get()));
    }
}