package com.company.onboarding.security;

import com.company.onboarding.entity.Document;
import com.company.onboarding.entity.User;
import io.jmix.security.role.annotation.JpqlRowLevelPolicy;
import io.jmix.security.role.annotation.RowLevelRole;

@RowLevelRole(name = "EmployeeRlRole", code = EmployeeRlRole.CODE)
public interface EmployeeRlRole {
    String CODE = "employee-rl-role";

    @JpqlRowLevelPolicy(
            entityClass = Document.class,
            where = "{E}.user.id = :current_user_id")
    void department();
}