package com.company.onboarding.listener;

import com.company.onboarding.entity.WebdavDocumentWrapper;
import io.jmix.core.Id;
import io.jmix.core.event.EntityChangedEvent;
import io.jmix.webdav.entity.WebdavDocument;
import io.jmix.webdav.service.WebdavDocumentsManagementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class WebdavDocumentWrapperEventListener {

    @Autowired
    private WebdavDocumentsManagementService webdavService;

    @EventListener
    public void onWebdavDocumentWrapperChangedBeforeCommit(final EntityChangedEvent<WebdavDocumentWrapper> event) {
        if (event.getType() == EntityChangedEvent.Type.DELETED) {
            Id<WebdavDocument> webdavDocumentId = event.getChanges().getOldReferenceId("webdavDocument");
            if (webdavDocumentId != null) {
                webdavService.removeDocumentByWebdavDocumentId(((UUID) webdavDocumentId.getValue()));
            }
        }
    }
}