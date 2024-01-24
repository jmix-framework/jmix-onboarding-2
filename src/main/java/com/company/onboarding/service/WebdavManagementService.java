package com.company.onboarding.service;

import com.company.onboarding.entity.WebdavDocumentWrapper;
import com.company.onboarding.entity.WebdavFolder;
import io.jmix.core.DataManager;
import io.jmix.core.EntitySet;
import io.jmix.core.SaveContext;
import io.jmix.core.common.util.Preconditions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Component
public class WebdavManagementService {

    @Autowired
    private DataManager dataManager;

    @Transactional
    public void removeWebdavFolder(WebdavFolder folder) {
        Preconditions.checkNotNullArgument(folder, "Folder is null");

        List<WebdavFolder> foldersHierarchy = loadWebdavFoldersHierarchy(folder);

        List<WebdavDocumentWrapper> documentWrappers = foldersHierarchy.stream().flatMap(fld -> {
                    List<WebdavDocumentWrapper> docs = dataManager.load(WebdavDocumentWrapper.class)
                            .query("e.folder = :folder")
                            .parameter("folder", fld)
                            .list();
                    return docs.stream();
                })
                .toList();

        SaveContext saveContext = new SaveContext();
        saveContext.removing(documentWrappers);
        saveContext.removing(foldersHierarchy);
        dataManager.save(saveContext);
    }

    protected List<WebdavFolder> loadWebdavFoldersHierarchy(WebdavFolder parentFolder) {
        List<WebdavFolder> result = new ArrayList<>();

        List<WebdavFolder> nestedFolders = dataManager.load(WebdavFolder.class)
                .query("e.parent = :parent")
                .parameter("parent", parentFolder)
                .list();

        if (!nestedFolders.isEmpty()) {
            for (WebdavFolder nestedFolder : nestedFolders) {
                result.addAll(loadWebdavFoldersHierarchy(nestedFolder));
            }
        }
        result.add(parentFolder);
        return result;
    }
}
