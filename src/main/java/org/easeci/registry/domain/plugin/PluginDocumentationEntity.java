package org.easeci.registry.domain.plugin;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity
@Table(name = "plugin_doc")
public class PluginDocumentationEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String pluginName;
    private String pluginVersion;
}
