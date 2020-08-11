package org.easeci.registry.domain.plugin;

import lombok.*;

import javax.persistence.*;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = "documentationTextBytes")
@Table(name = "plugin_doc")
public class PluginDocumentationEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String lastModifiedBy;
    private String pluginName;
    private String pluginVersion;

    private byte[] documentationTextBytes;
}
