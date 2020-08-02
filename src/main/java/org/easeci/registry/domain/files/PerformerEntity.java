package org.easeci.registry.domain.files;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Set;

@Getter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "performer")
class PerformerEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long performerId;

    private String uploaderPrincipalName;
    private String authorFullname;
    private String authorEmail;
    private String company;
    private LocalDateTime creationDate;
    private String performerName;

    @Column(columnDefinition = "TEXT", length = 2000)
    private String description;

    @Setter
    @OneToMany(fetch = FetchType.EAGER, mappedBy = "performer", cascade = CascadeType.ALL)
    private Set<PerformerVersionEntity> performerVersions;
}

@Getter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "performer_version")
class PerformerVersionEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long versionId;

    @ManyToOne
    @JoinColumn(name = "performerId", nullable = false)
    private PerformerEntity performer;

    private String performerVersion;
    private long performerScriptBytes;
    private Boolean validated;
    private LocalDateTime releaseDate;
    private String documentationUrl;
}