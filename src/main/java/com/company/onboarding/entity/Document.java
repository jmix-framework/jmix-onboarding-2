package com.company.onboarding.entity;

import io.jmix.core.FileRef;
import io.jmix.core.entity.annotation.JmixGeneratedValue;
import io.jmix.core.metamodel.annotation.DependsOnProperties;
import io.jmix.core.metamodel.annotation.InstanceName;
import io.jmix.core.metamodel.annotation.JmixEntity;
import io.jmix.core.metamodel.annotation.JmixProperty;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

@JmixEntity
@Table(name = "DOCUMENT", indexes = {
        @Index(name = "IDX_DOCUMENT_USER", columnList = "USER_ID")
})
@Entity
public class Document {
    @JmixGeneratedValue
    @Column(name = "ID", nullable = false)
    @Id
    private UUID id;

    @NotNull
    @InstanceName
    @Column(name = "NAME", nullable = false)
    private String name;

    @NotNull
    @Column(name = "FILE_", nullable = false, length = 1024)
    private FileRef file;

    @Column(name = "SIGNED_FILE", length = 1024)
    private FileRef signedFile;

    @NotNull
    @JoinColumn(name = "USER_ID", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private User user;

    @Column(name = "STATUS", nullable = false)
    @NotNull
    private String status = DocumentStatus.INCOMPLETE.getId();

    public FileRef getSignedFile() {
        return signedFile;
    }

    public void setSignedFile(FileRef signedFile) {
        this.signedFile = signedFile;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public DocumentStatus getStatus() {
        return status == null ? null : DocumentStatus.fromId(status);
    }

    public void setStatus(DocumentStatus status) {
        this.status = status == null ? null : status.getId();
    }

    @DependsOnProperties({"signedFile"})
    @JmixProperty
    public Boolean getDateSigned() {
        return signedFile != null;
    }

    public FileRef getFile() {
        return file;
    }

    public void setFile(FileRef file) {
        this.file = file;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }
}