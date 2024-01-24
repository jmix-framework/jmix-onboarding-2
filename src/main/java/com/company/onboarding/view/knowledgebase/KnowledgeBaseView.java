package com.company.onboarding.view.knowledgebase;


import com.company.onboarding.entity.Classification;
import com.company.onboarding.entity.WebdavDocumentWrapper;
import com.company.onboarding.entity.WebdavFolder;
import com.company.onboarding.service.WebdavManagementService;
import com.company.onboarding.view.main.MainView;
import com.company.onboarding.view.webdavdocumentwrapper.WebdavDocumentWrapperDetailView;
import com.company.onboarding.view.webdavfolder.WebdavFolderDetailView;
import com.vaadin.flow.component.grid.ItemClickEvent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.data.renderer.Renderer;
import com.vaadin.flow.data.renderer.TextRenderer;
import com.vaadin.flow.router.Route;
import io.jmix.core.AccessManager;
import io.jmix.core.FileRef;
import io.jmix.core.Messages;
import io.jmix.core.Metadata;
import io.jmix.core.accesscontext.CrudEntityContext;
import io.jmix.core.metamodel.model.MetaClass;
import io.jmix.flowui.DialogWindows;
import io.jmix.flowui.Dialogs;
import io.jmix.flowui.UiComponents;
import io.jmix.flowui.action.DialogAction;
import io.jmix.flowui.action.list.CreateAction;
import io.jmix.flowui.action.list.EditAction;
import io.jmix.flowui.action.list.RemoveAction;
import io.jmix.flowui.component.formatter.DateFormatter;
import io.jmix.flowui.component.grid.DataGrid;
import io.jmix.flowui.component.grid.TreeDataGrid;
import io.jmix.flowui.download.Downloader;
import io.jmix.flowui.kit.action.ActionPerformedEvent;
import io.jmix.flowui.kit.action.BaseAction;
import io.jmix.flowui.kit.component.button.JmixButton;
import io.jmix.flowui.model.CollectionLoader;
import io.jmix.flowui.view.*;
import io.jmix.webdav.entity.WebdavDocument;
import io.jmix.webdav.entity.WebdavDocumentVersion;
import io.jmix.webdavflowui.component.WebdavDocumentLink;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

import java.util.Date;

@Route(value = "KnowledgeBaseView", layout = MainView.class)
@ViewController("KnowledgeBaseView")
@ViewDescriptor("knowledge-base-view.xml")
public class KnowledgeBaseView extends StandardView {
    @ViewComponent
    private TreeDataGrid<WebdavFolder> webdavFoldersDataGrid;
    @ViewComponent
    private DataGrid<WebdavDocumentWrapper> webdavDocumentWrappersDataGrid;
    @ViewComponent
    private CollectionLoader<WebdavDocumentWrapper> webdavDocumentWrappersDl;
    @ViewComponent
    private CollectionLoader<WebdavFolder> webdavFoldersDl;
    @ViewComponent
    private JmixButton createFolderBtn;
    @ViewComponent
    private JmixButton removeFolderBtn;
    @ViewComponent
    private JmixButton editFolderBtn;
    @ViewComponent
    private JmixButton createDocumentBtn;
    @ViewComponent
    private JmixButton editDocumentBtn;
    @ViewComponent
    private HorizontalLayout documentsButtonsPanel;
    @ViewComponent("webdavDocumentWrappersDataGrid.create")
    private CreateAction<WebdavDocumentWrapper> webdavDocumentWrappersDataGridCreate;
    @ViewComponent("webdavDocumentWrappersDataGrid.edit")
    private EditAction<WebdavDocumentWrapper> webdavDocumentWrappersDataGridEdit;
    @ViewComponent("webdavDocumentWrappersDataGrid.remove")
    private RemoveAction<WebdavDocumentWrapper> webdavDocumentWrappersDataGridRemove;
    @ViewComponent("webdavDocumentWrappersDataGrid.download")
    private BaseAction webdavDocumentWrappersDataGridDownload;
    @ViewComponent
    private JmixButton removeDocumentBtn;
    @ViewComponent
    private JmixButton downloadDocumentBtn;
    @ViewComponent("webdavFoldersDataGrid.create")
    private CreateAction<WebdavFolder> webdavFoldersDataGridCreate;
    @ViewComponent("webdavFoldersDataGrid.edit")
    private EditAction<WebdavFolder> webdavFoldersDataGridEdit;
    @ViewComponent("webdavFoldersDataGrid.remove")
    private RemoveAction<WebdavFolder> webdavFoldersDataGridRemove;
    @Autowired
    private DialogWindows dialogWindows;
    @Autowired
    private Downloader downloader;
    @Autowired
    private ApplicationContext applicationContext;
    @Autowired
    private UiComponents uiComponents;
    @Autowired
    private Dialogs dialogs;
    @Autowired
    private Messages messages;
    @Autowired
    private WebdavManagementService webdavManagementService;
    @Autowired
    private Metadata metadata;
    @Autowired
    private AccessManager accessManager;

    protected WebdavFolder currentFolder = null;

    @Subscribe
    public void onBeforeShow(final BeforeShowEvent event) {
        setupFolderButtons();
        setupDocumentsButtons();
        onDocumentSelect(null);
        refreshFoldersData();
        refreshDocumentsButtonsAvailability(false);
    }

    @Subscribe("webdavFoldersDataGrid")
    public void onWebdavFoldersDataGridItemClick(final ItemClickEvent<WebdavFolder> event) {
        WebdavFolder folder = event.getItem();
        WebdavFolder selectedItem = webdavFoldersDataGrid.getSingleSelectedItem();
        if (selectedItem == null) {
            onFolderSelect(null);
        } else {
            onFolderSelect(folder);
        }
    }

    protected void onFolderSelect(WebdavFolder selectedFolder) {
        setCurrentFolder(selectedFolder);
        loadDocumentsInCurrentFolder();

        refreshDocumentsButtonsAvailability(selectedFolder != null);
    }

    @Subscribe("webdavDocumentWrappersDataGrid")
    public void onWebdavDocumentWrappersDataGridItemClick(final ItemClickEvent<WebdavDocumentWrapper> event) {
        onDocumentSelect(webdavDocumentWrappersDataGrid.getSingleSelectedItem());
    }

    protected void onDocumentSelect(WebdavDocumentWrapper selectedDocument) {
        boolean hasWebdavDocument = selectedDocument != null && selectedDocument.getWebdavDocument() != null;
        webdavDocumentWrappersDataGridDownload.setEnabled(hasWebdavDocument);
        downloadDocumentBtn.setEnabled(hasWebdavDocument);
    }

    protected void setupFolderButtons() {
        createFolderBtn.setText("");
        editFolderBtn.setText("");
        removeFolderBtn.setText("");

        CrudEntityContext accessContext = getFoldersAccessContext();
        createFolderBtn.setVisible(accessContext.isCreatePermitted());
        editFolderBtn.setVisible(accessContext.isUpdatePermitted());
        removeFolderBtn.setVisible(accessContext.isDeletePermitted());

        webdavFoldersDataGridCreate.setVisible(accessContext.isCreatePermitted());
        webdavFoldersDataGridEdit.setVisible(accessContext.isUpdatePermitted());
        webdavFoldersDataGridRemove.setVisible(accessContext.isDeletePermitted());
    }

    protected void setupDocumentsButtons() {
        CrudEntityContext accessContext = getDocumentsAccessContext();
        downloadDocumentBtn.setVisible(accessContext.isReadPermitted());
        createDocumentBtn.setVisible(accessContext.isCreatePermitted());
        editDocumentBtn.setVisible(accessContext.isUpdatePermitted());
        removeDocumentBtn.setVisible(accessContext.isDeletePermitted());

        webdavDocumentWrappersDataGridDownload.setVisible(accessContext.isReadPermitted());
        webdavDocumentWrappersDataGridCreate.setVisible(accessContext.isCreatePermitted());
        webdavDocumentWrappersDataGridEdit.setVisible(accessContext.isUpdatePermitted());
        webdavDocumentWrappersDataGridRemove.setVisible(accessContext.isDeletePermitted());
    }

    protected void refreshDocumentsButtonsAvailability(boolean isFolderSelected) {
        documentsButtonsPanel.setEnabled(isFolderSelected);
    }

    protected CrudEntityContext getDocumentsAccessContext() {
        MetaClass metaClass = metadata.getClass(WebdavDocumentWrapper.class);
        CrudEntityContext accessContext = new CrudEntityContext(metaClass);
        accessManager.applyRegisteredConstraints(accessContext);
        return accessContext;
    }

    protected CrudEntityContext getFoldersAccessContext() {
        MetaClass metaClass = metadata.getClass(WebdavFolder.class);
        CrudEntityContext accessContext = new CrudEntityContext(metaClass);
        accessManager.applyRegisteredConstraints(accessContext);
        return accessContext;
    }

    @Subscribe("webdavFoldersDataGrid.create")
    public void onWebdavFoldersDataGridCreate(final ActionPerformedEvent event) {
        dialogWindows.detail(this, WebdavFolder.class)
                .withViewClass(WebdavFolderDetailView.class)
                .newEntity()
                .withInitializer(folder -> {
                    folder.setClassification(Classification.PUBLIC);

                    WebdavFolder currentFolder = webdavFoldersDataGrid.getSingleSelectedItem();
                    if (currentFolder != null) {
                        folder.setParent(currentFolder);
                        folder.setDepartment(currentFolder.getDepartment());

                        Classification parentClassification = currentFolder.getClassification();
                        if (parentClassification.equals(Classification.RESTRICTED)) {
                            folder.setClassification(Classification.RESTRICTED);
                        }
                    }
                })
                .withAfterCloseListener(afterCloseEvent -> {
                    if (afterCloseEvent.closedWith(StandardOutcome.SAVE)) {
                        webdavFoldersDl.load();
                        if (currentFolder != null) {
                            webdavFoldersDataGrid.expand(currentFolder);
                        }
                    }
                })
                .build()
                .open();
    }

    @Subscribe("webdavFoldersDataGrid.remove")
    public void onWebdavFoldersDataGridRemove(final ActionPerformedEvent event) {
        dialogs.createOptionDialog()
                .withHeader(messages.getMessage("dialogs.Confirmation"))
                .withText(messages.getMessage("dialogs.Confirmation.Remove"))
                .withActions(
                        new DialogAction(DialogAction.Type.YES)
                                .withHandler(e -> {
                                    WebdavFolder selectedFolder = webdavFoldersDataGrid.getSingleSelectedItem();
                                    if (selectedFolder == null) {
                                        return;
                                    }
                                    webdavManagementService.removeWebdavFolder(selectedFolder);
                                    afterFolderRemoval();
                                }),
                        new DialogAction(DialogAction.Type.NO)
                )
                .open();
    }

    protected void afterFolderRemoval() {
        webdavFoldersDataGrid.deselectAll();
        setCurrentFolder(null);
        refreshFoldersData();
        loadDocumentsInCurrentFolder();
    }

    protected void loadDocumentsInCurrentFolder() {
        webdavDocumentWrappersDl.setParameter("folder", currentFolder);
        refreshDocumentsData();
    }

    @Subscribe("webdavDocumentWrappersDataGrid.create")
    public void onWebdavDocumentWrappersDataGridCreate(final ActionPerformedEvent event) {
        dialogWindows.detail(this, WebdavDocumentWrapper.class)
                .withViewClass(WebdavDocumentWrapperDetailView.class)
                .newEntity()
                .withInitializer(webdavDocumentWrapper -> {
                    WebdavFolder currentFolder = webdavFoldersDataGrid.getSingleSelectedItem();
                    if (currentFolder != null) {
                        webdavDocumentWrapper.setFolder(currentFolder);
                    }
                })
                .withAfterCloseListener(afterCloseEvent -> {
                    if (afterCloseEvent.closedWith(StandardOutcome.SAVE)) {
                        refreshDocumentsData();
                    }
                })
                .build()
                .open();
    }

    @Subscribe("webdavDocumentWrappersDataGrid.download")
    public void onWebdavDocumentWrappersDataGridDownload(final ActionPerformedEvent event) {
        WebdavDocumentWrapper webdavDocumentWrapper = webdavDocumentWrappersDataGrid.getSingleSelectedItem();
        if (webdavDocumentWrapper == null) {
            return;
        }

        WebdavDocument webdavDocument = webdavDocumentWrapper.getWebdavDocument();
        if (webdavDocument == null) {
            return;
        }
        WebdavDocumentVersion lastVersion = webdavDocument.getLastVersion();
        FileRef fileReference = lastVersion.getFileReference();
        downloader.download(fileReference);
    }

    @Supply(to = "webdavDocumentWrappersDataGrid.lastModifiedBy", subject = "renderer")
    private Renderer<WebdavDocumentWrapper> webdavDocumentWrappersDataGridLastModifiedByRenderer() {
        return new TextRenderer<>(documentWrapper -> {
            WebdavDocument webdavDocument = documentWrapper.getWebdavDocument();
            if (webdavDocument == null) {
                return null;
            }

            WebdavDocumentVersion lastVersion = webdavDocument.getLastVersion();
            return lastVersion.getCreatedBy();
        });
    }

    @Supply(to = "webdavDocumentWrappersDataGrid.lastModifiedDate", subject = "renderer")
    private Renderer<WebdavDocumentWrapper> webdavDocumentWrappersDataGridLastModifiedDateRenderer() {
        DateFormatter dateFormatter = applicationContext.getBean(DateFormatter.class);
        return new TextRenderer<>(documentWrapper -> {
            WebdavDocument webdavDocument = documentWrapper.getWebdavDocument();
            if (webdavDocument == null) {
                return null;
            }

            WebdavDocumentVersion lastVersion = webdavDocument.getLastVersion();
            Date lastModifiedDate = lastVersion.getCreatedDate();
            return dateFormatter.apply(lastModifiedDate);
        });
    }

    @Supply(to = "webdavDocumentWrappersDataGrid.webdavDocument", subject = "renderer")
    private Renderer<WebdavDocumentWrapper> webdavDocumentWrappersDataGridWebdavDocumentRenderer() {
        return new ComponentRenderer<>(
                () -> uiComponents.create(WebdavDocumentLink.class),
                (link, wrapper) -> {
                    WebdavDocument webdavDocument = wrapper.getWebdavDocument();
                    if (webdavDocument != null) {
                        link.setWebdavDocument(webdavDocument);
                    }
                });
    }

    protected void refreshDocumentsData() {
        webdavDocumentWrappersDl.load();
    }

    protected void refreshFoldersData() {
        webdavFoldersDl.load();
    }

    protected void setCurrentFolder(WebdavFolder folder) {
        this.currentFolder = folder;
    }

}