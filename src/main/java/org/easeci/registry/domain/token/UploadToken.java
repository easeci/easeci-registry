package org.easeci.registry.domain.token;

import lombok.*;

import javax.persistence.*;
import java.util.Date;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "upload_token")
public class UploadToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private boolean reserved;
    private String reservedBy;
    private boolean isInUse;
    private Date releaseDate;
    private Date useDate;
    private Long releasedForVersionId;
    private String token;
}
