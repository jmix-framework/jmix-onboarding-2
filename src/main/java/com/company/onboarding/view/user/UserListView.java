package com.company.onboarding.view.user;

import com.company.onboarding.entity.Document;
import com.company.onboarding.entity.User;
import com.company.onboarding.view.main.MainView;
import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.data.renderer.Renderer;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.StreamResource;
import io.jmix.core.DataManager;
import io.jmix.core.FileRef;
import io.jmix.core.FileStorage;
import io.jmix.core.FileStorageLocator;
import io.jmix.flowui.Notifications;
import io.jmix.flowui.UiComponents;
import io.jmix.flowui.component.grid.DataGrid;
import io.jmix.flowui.kit.action.ActionPerformedEvent;
import io.jmix.flowui.kit.component.button.JmixButton;
import io.jmix.flowui.view.*;
import io.jmix.reports.runner.ReportRunner;
import io.jmix.reports.yarg.reporting.ReportOutputDocument;
import io.jmix.reportsflowui.runner.UiReportRunner;
import org.apache.groovy.util.Maps;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.ByteArrayInputStream;
import java.util.Map;

@Route(value = "users", layout = MainView.class)
@ViewController("User.list")
@ViewDescriptor("user-list-view.xml")
@LookupComponent("usersDataGrid")
@DialogMode(width = "50em", height = "AUTO")
public class UserListView extends StandardListView<User> {

    @Autowired
    private UiComponents uiComponents;

    @Autowired
    private FileStorage fileStorage;
    @Autowired
    private ReportRunner reportRunner;
    @Autowired
    private UiReportRunner uiReportRunner;
    @ViewComponent
    private DataGrid<User> usersDataGrid;
    @Autowired
    private DataManager dataManager;
    @Autowired
    private FileStorageLocator fileStorageLocator;
    @Autowired
    private Notifications notifications;

    @Supply(to = "usersDataGrid.picture", subject = "renderer")
    private Renderer<User> usersDataGridPictureRenderer() {
        return new ComponentRenderer<>(user -> {
            FileRef fileRef = user.getPicture();
            if (fileRef != null) {
                Image image = uiComponents.create(Image.class);
                image.setWidth("30px");
                image.setHeight("30px");
                StreamResource streamResource = new StreamResource(
                        fileRef.getFileName(),
                        () -> fileStorage.openStream(fileRef));
                image.setSrc(streamResource);
                image.setClassName("user-picture");

                return image;
            } else {
                return null;
            }
        });
    }

    @Subscribe(id = "printReportBtn", subject = "clickListener")
    public void onPrintReportBtnClick(final ClickEvent<JmixButton> event) {
        uiReportRunner.byReportCode("usersReport")
                .runAndShow();
    }

    @Subscribe("usersDataGrid.prepareAction")
    public void onUsersDataGridPrepareAction(final ActionPerformedEvent event) {
        User entity = usersDataGrid.getSingleSelectedItem();

        if (entity == null) {
            return;
        }

        ReportOutputDocument reportOutputDocument = reportRunner.byReportCode("userReport")
                .withParams(Maps.of("entity", entity))
                .run();

        Document document = dataManager.create(Document.class);
        document.setName("Employee document: " + entity.getDisplayName());
        document.setUser(entity);

        FileStorage fileStorage = fileStorageLocator.getDefault();
        FileRef fileRef = fileStorage.saveStream(document.getName(), new ByteArrayInputStream(reportOutputDocument.getContent()));

        document.setFile(fileRef);

        dataManager.save(document);

        notifications.create("Document created!")
                .show();
    }


}