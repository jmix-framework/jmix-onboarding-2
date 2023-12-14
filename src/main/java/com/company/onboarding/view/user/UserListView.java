package com.company.onboarding.view.user;

import com.company.onboarding.app.DocumentService;
import com.company.onboarding.entity.User;
import com.company.onboarding.entity.UserDetailInfo;
import com.company.onboarding.view.main.MainView;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.data.renderer.Renderer;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.StreamResource;
import io.jmix.core.DataManager;
import io.jmix.core.FileRef;
import io.jmix.core.FileStorage;
import io.jmix.flowui.Dialogs;
import io.jmix.flowui.UiComponents;
import io.jmix.flowui.app.inputdialog.DialogActions;
import io.jmix.flowui.app.inputdialog.DialogOutcome;
import io.jmix.flowui.app.inputdialog.InputParameter;
import io.jmix.flowui.component.grid.DataGrid;
import io.jmix.flowui.kit.action.ActionPerformedEvent;
import io.jmix.flowui.view.*;
import org.springframework.beans.factory.annotation.Autowired;

@Route(value = "users", layout = MainView.class)
@ViewController("User.list")
@ViewDescriptor("user-list-view.xml")
@LookupComponent("usersDataGrid")
@DialogMode(width = "50em", height = "37.5em")
public class UserListView extends StandardListView<User> {

    @Autowired
    private UiComponents uiComponents;
    @Autowired
    private FileStorage fileStorage;
    @Autowired
    private DocumentService documentService;
    @Autowired
    private Dialogs dialogs;
    @Autowired
    private DataManager dataManager;

    @ViewComponent
    private DataGrid<User> usersDataGrid;

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

    @Subscribe("usersDataGrid.prepareDocuments")
    public void onUsersDataGridPrepareDocuments(final ActionPerformedEvent event) {
        User selectedItem = usersDataGrid.getSingleSelectedItem();
        if (selectedItem == null) {
            return;
        }

        dialogs.createInputDialog(this)
                .withParameters(
                        InputParameter.stringParameter("address")
                                .withLabel("Address"),
                        InputParameter.intParameter("aptNumber")
                                .withLabel("Apt. Number"),
                        InputParameter.stringParameter("city")
                                .withLabel("City or Town"),
                        InputParameter.stringParameter("state")
                                .withLabel("State")
                )
                .withHeader("Prepare documents")
                .withActions(DialogActions.OK_CANCEL)
                .withCloseListener(closeEvent -> {
                    if (closeEvent.closedWith(DialogOutcome.OK)) {
                        String address = closeEvent.getValue("address");
                        Integer aptNumber = closeEvent.getValue("aptNumber");
                        String city = closeEvent.getValue("city");
                        String state = closeEvent.getValue("state");

                        UserDetailInfo userDetailInfo = createUserDetailInfo(address, aptNumber, city, state);
                        documentService.preparePackageDocuments(selectedItem, userDetailInfo);
                    }
                })
                .open();
    }

    private UserDetailInfo createUserDetailInfo(String address, Integer aptNumber, String city, String state) {
        UserDetailInfo userDetailInfo = dataManager.create(UserDetailInfo.class);
        userDetailInfo.setAddress(address);
        userDetailInfo.setState(state);
        userDetailInfo.setCity(city);
        userDetailInfo.setAptNumber(aptNumber);

        return userDetailInfo;
    }
}