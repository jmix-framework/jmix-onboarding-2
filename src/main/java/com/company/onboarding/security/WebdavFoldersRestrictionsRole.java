package com.company.onboarding.security;

import com.company.onboarding.entity.Classification;
import com.company.onboarding.entity.Department;
import com.company.onboarding.entity.User;
import com.company.onboarding.entity.WebdavFolder;
import io.jmix.core.usersubstitution.CurrentUserSubstitution;
import io.jmix.security.model.RowLevelBiPredicate;
import io.jmix.security.model.RowLevelPolicyAction;
import io.jmix.security.role.annotation.PredicateRowLevelPolicy;
import io.jmix.security.role.annotation.RowLevelRole;
import org.springframework.context.ApplicationContext;
import org.springframework.security.core.userdetails.UserDetails;

@RowLevelRole(name = "Webdav folders restrictions", code = WebdavFoldersRestrictionsRole.CODE)
public interface WebdavFoldersRestrictionsRole {
    String CODE = "webdav-folders-restrictions";


    @PredicateRowLevelPolicy(entityClass = WebdavFolder.class, actions = RowLevelPolicyAction.READ)
    default RowLevelBiPredicate<WebdavFolder, ApplicationContext> webdavFolderReadPredicate() {
        return (webdavFolder, applicationContext) -> {
            Classification classification = webdavFolder.getClassification();
            if (classification.equals(Classification.PUBLIC)) {
                return true;
            }

            CurrentUserSubstitution currentUserSubstitution = applicationContext.getBean(CurrentUserSubstitution.class);
            UserDetails effectiveUser = currentUserSubstitution.getEffectiveUser();

            boolean isAdmin = effectiveUser.getAuthorities().stream()
                    .anyMatch(grantedAuthority -> {
                        String authority = grantedAuthority.getAuthority();
                        return authority.equals("hr-manager") || authority.equals("system-full-access");
                    });
            if (isAdmin) {
                return true;
            }

            boolean isEmployee = effectiveUser.getAuthorities().stream()
                    .anyMatch(grantedAuthority -> {
                        String authority = grantedAuthority.getAuthority();
                        return authority.equals("employee");
                    });
            if (isEmployee) {
                Department userDepartment = ((User) effectiveUser).getDepartment();
                Department folderDepartment = webdavFolder.getDepartment();
                return userDepartment.equals(folderDepartment);
            }

            return false;
        };
    }


    @PredicateRowLevelPolicy(entityClass = WebdavFolder.class, actions = {RowLevelPolicyAction.CREATE, RowLevelPolicyAction.UPDATE, RowLevelPolicyAction.DELETE})
    default RowLevelBiPredicate<WebdavFolder, ApplicationContext> webdavFolderModifyPredicate() {
        return (webdavFolder, applicationContext) -> {
            CurrentUserSubstitution currentUserSubstitution = applicationContext.getBean(CurrentUserSubstitution.class);
            UserDetails effectiveUser = currentUserSubstitution.getEffectiveUser();

            return effectiveUser.getAuthorities().stream()
                    .anyMatch(grantedAuthority -> {
                        String authority = grantedAuthority.getAuthority();
                        return authority.equals("hr-manager") || authority.equals("system-full-access");
                    });
        };
    }
}