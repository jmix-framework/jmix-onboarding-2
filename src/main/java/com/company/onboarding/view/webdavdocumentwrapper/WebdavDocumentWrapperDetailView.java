package com.company.onboarding.view.webdavdocumentwrapper;

import com.company.onboarding.entity.WebdavDocumentWrapper;

import com.company.onboarding.view.main.MainView;

import com.vaadin.flow.router.Route;
import io.jmix.flowui.view.*;

@Route(value = "webdavDocumentWrappers/:id", layout = MainView.class)
@ViewController("WebdavDocumentWrapper.detail")
@ViewDescriptor("webdav-document-wrapper-detail-view.xml")
@EditedEntityContainer("webdavDocumentWrapperDc")
public class WebdavDocumentWrapperDetailView extends StandardDetailView<WebdavDocumentWrapper> {
}