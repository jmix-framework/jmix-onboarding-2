package com.company.onboarding.entity;

import io.jmix.core.DeletePolicy;
import io.jmix.core.entity.annotation.JmixGeneratedValue;
import io.jmix.core.entity.annotation.OnDeleteInverse;
import io.jmix.core.metamodel.annotation.InstanceName;
import io.jmix.core.metamodel.annotation.JmixEntity;
import jakarta.persistence.*;

import java.util.UUID;

@JmixEntity
@Table(name = "WEBDAV_FOLDER", indexes = {
        @Index(name = "IDX_WEBDAV_FOLDER_DEPARTMENT", columnList = "DEPARTMENT_ID"),
        @Index(name = "IDX_WEBDAV_FOLDER_PARENT", columnList = "PARENT_ID")
})
@Entity
public class WebdavFolder {
    @JmixGeneratedValue
    @Column(name = "ID", nullable = false)
    @Id
    private UUID id;

    @InstanceName
    @Column(name = "NAME")
    private String name;

    @Column(name = "CLASSIFICATION")
    private String classification;

    @OnDeleteInverse(DeletePolicy.DENY)
    @JoinColumn(name = "DEPARTMENT_ID")
    @ManyToOne(fetch = FetchType.LAZY)
    private Department department;

    @JoinColumn(name = "PARENT_ID")
    @ManyToOne(fetch = FetchType.LAZY)
    private WebdavFolder parent;

    public WebdavFolder getParent() {
        return parent;
    }

    public void setParent(WebdavFolder parent) {
        this.parent = parent;
    }

    public Department getDepartment() {
        return department;
    }

    public void setDepartment(Department department) {
        this.department = department;
    }

    public Classification getClassification() {
        return classification == null ? null : Classification.fromId(classification);
    }

    public void setClassification(Classification classification) {
        this.classification = classification == null ? null : classification.getId();
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