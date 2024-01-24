package com.company.onboarding.view.webdavfolder;

import com.company.onboarding.entity.Classification;
import com.company.onboarding.entity.Department;
import com.company.onboarding.entity.WebdavFolder;
import com.company.onboarding.view.main.MainView;
import com.vaadin.flow.router.Route;
import io.jmix.flowui.component.select.JmixSelect;
import io.jmix.flowui.component.valuepicker.EntityPicker;
import io.jmix.flowui.view.*;

@Route(value = "webdavFolders/:id", layout = MainView.class)
@ViewController("WebdavFolder.detail")
@ViewDescriptor("webdav-folder-detail-view.xml")
@EditedEntityContainer("webdavFolderDc")
@DialogMode(width = "40em")
public class WebdavFolderDetailView extends StandardDetailView<WebdavFolder> {
    @ViewComponent
    private EntityPicker<Department> departmentField;
    @ViewComponent
    private JmixSelect<Classification> classificationField;

    @Subscribe
    public void onBeforeShow(final BeforeShowEvent event) {
        WebdavFolder folder = getEditedEntity();
        WebdavFolder parentFolder = folder.getParent();
        if (parentFolder != null) {
            departmentField.setReadOnly(true);

            Classification parentFolderClassification = parentFolder.getClassification();
            if (Classification.RESTRICTED.equals(parentFolderClassification)) {
                classificationField.setReadOnly(true);
            }
        }
    }
}