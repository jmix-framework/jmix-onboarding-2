package com.company.onboarding.view.document;

import com.company.onboarding.entity.Document;
import com.company.onboarding.view.main.MainView;
import com.vaadin.flow.component.html.IFrame;
import com.vaadin.flow.router.Route;
import io.jmix.core.FileRef;
import io.jmix.flowui.view.*;

import java.net.URLEncoder;
import java.nio.charset.Charset;

@Route(value = "documents/:id", layout = MainView.class)
@ViewController("Document.detail")
@ViewDescriptor("document-detail-view.xml")
@EditedEntityContainer("documentDc")
public class DocumentDetailView extends StandardDetailView<Document> {

    @ViewComponent
    private IFrame documentFrame;

    @Subscribe
    public void onReady(final ReadyEvent event) {
        Document document = getEditedEntity();
        FileRef file = document.getFile();

        if (file != null) {
            documentFrame.setSrc("http://localhost:8080/rest/files?fileRef=" + URLEncoder.encode(file.toString()));
        }
    }


}