package com.company.onboarding.view.document;

import com.company.onboarding.entity.Document;
import com.company.onboarding.view.main.MainView;
import com.vaadin.componentfactory.pdfviewer.PdfViewer;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.IFrame;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.StreamResource;
import io.jmix.core.FileRef;
import io.jmix.core.FileStorage;
import io.jmix.core.FileStorageLocator;
import io.jmix.flowui.view.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.InputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Route(value = "documents/:id", layout = MainView.class)
@ViewController("Document.detail")
@ViewDescriptor("document-detail-view.xml")
@EditedEntityContainer("documentDc")
public class DocumentDetailView extends StandardDetailView<Document> {

    @ViewComponent
    private Div documentFrame;

    @Autowired
    private FileStorage fileStorage;

    @Subscribe
    public void onReady(final ReadyEvent event) {
        Document document = getEditedEntity();
        FileRef file = document.getFile();

        InputStream inputStream = fileStorage.openStream(file);

        PdfViewer pdfViewer = new PdfViewer();
        StreamResource resource = new StreamResource(file.getFileName(), () -> inputStream);
        pdfViewer.setSrc(resource);
        pdfViewer.openThumbnailsView();
        pdfViewer.setHeight("100%");

        documentFrame.add(pdfViewer);
    }
}