package com.company.onboarding.security;

import com.company.onboarding.entity.*;
import io.jmix.security.model.EntityAttributePolicyAction;
import io.jmix.security.model.EntityPolicyAction;
import io.jmix.security.role.annotation.EntityAttributePolicy;
import io.jmix.security.role.annotation.EntityPolicy;
import io.jmix.security.role.annotation.ResourceRole;
import io.jmix.securityflowui.role.annotation.MenuPolicy;
import io.jmix.securityflowui.role.annotation.ViewPolicy;
import io.jmix.webdavflowui.role.WebdavMinimalRole;

@ResourceRole(name = "Employee", code = "employee", scope = "UI")
public interface EmployeeRole {
    @MenuPolicy(menuIds = {"MyOnboardingView", "KnowledgeBaseView"})
    @ViewPolicy(viewIds = {"MyOnboardingView", "KnowledgeBaseView"})
    void screens();

    @EntityAttributePolicy(entityClass = User.class,
            attributes = "*",
            action = EntityAttributePolicyAction.VIEW)
    @EntityPolicy(entityClass = User.class,
            actions = {EntityPolicyAction.READ, EntityPolicyAction.UPDATE})
    void user();

    @EntityAttributePolicy(entityClass = UserStep.class,
            attributes = "*",
            action = EntityAttributePolicyAction.VIEW)
    @EntityPolicy(entityClass = UserStep.class,
            actions = {EntityPolicyAction.READ, EntityPolicyAction.UPDATE})
    void userStep();

    @EntityAttributePolicy(entityClass = Step.class,
            attributes = "*",
            action = EntityAttributePolicyAction.VIEW)
    @EntityPolicy(entityClass = Step.class,
            actions = EntityPolicyAction.READ)
    void step();

    @EntityAttributePolicy(entityClass = WebdavFolder.class,
            attributes = "*",
            action = EntityAttributePolicyAction.VIEW)
    @EntityPolicy(entityClass = WebdavFolder.class, actions = EntityPolicyAction.READ)
    void webdavFolder();

    @EntityAttributePolicy(entityClass = WebdavDocumentWrapper.class,
            attributes = "*",
            action = EntityAttributePolicyAction.VIEW)
    @EntityPolicy(entityClass = WebdavDocumentWrapper.class, actions = EntityPolicyAction.READ)
    void webdavDocumentWrapper();
}