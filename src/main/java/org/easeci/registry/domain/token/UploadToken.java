package org.easeci.registry.domain.token;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class UploadToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


}
