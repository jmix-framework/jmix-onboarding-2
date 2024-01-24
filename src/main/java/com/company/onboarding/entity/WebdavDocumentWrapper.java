package com.company.onboarding.entity;

import io.jmix.core.DeletePolicy;
import io.jmix.core.entity.annotation.JmixGeneratedValue;
import io.jmix.core.entity.annotation.OnDeleteInverse;
import io.jmix.core.metamodel.annotation.InstanceName;
import io.jmix.core.metamodel.annotation.JmixEntity;
import io.jmix.webdav.entity.WebdavDocument;
import jakarta.persistence.*;

import java.time.OffsetDateTime;
import java.util.UUID;

@JmixEntity
@Table(name = "WEBDAV_DOCUMENT_WRAPPER", indexes = {
        @Index(name = "IDX_WEBDAV_DOCUMENT_WRAPPER_WEBDAV_DOCUMENT", columnList = "WEBDAV_DOCUMENT_ID"),
        @Index(name = "IDX_WEBDAV_DOCUMENT_WRAPPER_FOLDER", columnList = "FOLDER_ID")
})
@Entity
public class WebdavDocumentWrapper {
    @JmixGeneratedValue
    @Column(name = "ID", nullable = false)
    @Id
    private UUID id;

    @JoinColumn(name = "WEBDAV_DOCUMENT_ID")
    @OneToOne(fetch = FetchType.LAZY)
    private WebdavDocument webdavDocument;

    @InstanceName
    @Column(name = "DESCRIPTION")
    private String description;

    @Column(name = "LAST_MODIFIED_BY")
    private String lastModifiedBy;

    @Column(name = "LAST_MODIFIED_DATE")
    private OffsetDateTime lastModifiedDate;

    @OnDeleteInverse(DeletePolicy.CASCADE)
    @JoinColumn(name = "FOLDER_ID")
    @ManyToOne(fetch = FetchType.LAZY)
    private WebdavFolder folder;

    public WebdavFolder getFolder() {
        return folder;
    }

    public void setFolder(WebdavFolder folder) {
        this.folder = folder;
    }

    public OffsetDateTime getLastModifiedDate() {
        return lastModifiedDate;
    }

    public void setLastModifiedDate(OffsetDateTime lastModifiedDate) {
        this.lastModifiedDate = lastModifiedDate;
    }

    public String getLastModifiedBy() {
        return lastModifiedBy;
    }

    public void setLastModifiedBy(String lastModifiedBy) {
        this.lastModifiedBy = lastModifiedBy;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public WebdavDocument getWebdavDocument() {
        return webdavDocument;
    }

    public void setWebdavDocument(WebdavDocument webdavDocument) {
        this.webdavDocument = webdavDocument;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }
}