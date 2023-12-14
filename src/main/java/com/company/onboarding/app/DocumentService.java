package com.company.onboarding.app;

import com.company.onboarding.entity.Document;
import com.company.onboarding.entity.DocumentStatus;
import com.company.onboarding.entity.User;
import com.company.onboarding.entity.UserDetailInfo;
import io.jmix.core.DataManager;
import io.jmix.core.FileRef;
import io.jmix.core.FileStorage;
import io.jmix.core.FileStorageLocator;
import io.jmix.reports.runner.ReportRunner;
import io.jmix.reports.yarg.reporting.ReportOutputDocument;
import org.apache.groovy.util.Maps;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.util.Map;

@Service
public class DocumentService {

    private final DataManager dataManager;
    private final FileStorageLocator fileStorageLocator;
    private final ReportRunner reportRunner;

    public DocumentService(DataManager dataManager, FileStorageLocator fileStorageLocator, ReportRunner reportRunner) {
        this.dataManager = dataManager;
        this.fileStorageLocator = fileStorageLocator;
        this.reportRunner = reportRunner;
    }

    public void preparePackageDocuments(User user, UserDetailInfo detailInfo) {
        createNonDisclosureAgreementDocument(user);
        createJ9Document(user, detailInfo);
        createEmployeeHandbookDocument(user, detailInfo);
    }

    private void createEmployeeHandbookDocument(User user, UserDetailInfo userDetailInfo) {
        Map<String, Object> params = Maps.of(
                "user", user,
                "userDetail", userDetailInfo
        );
        createDocument(user, "employee-handbook", "pdf", "Employee-Handbook-Report.pdf", params);
    }

    private void createNonDisclosureAgreementDocument(User user) {
        Map<String, Object> params = Maps.of("user", user);
        createDocument(user, "non-disclosure-agreement", "pdf", "Non-Disclosure-Agreement.pdf", params);
    }

    private void createJ9Document(User user, UserDetailInfo userDetailInfo) {
        Map<String, Object> params = Maps.of(
                "user", user,
                "userDetail", userDetailInfo
        );
        createDocument(user, "i-9", "pdf", "I-9.pdf", params);
    }

    private void createDocument(User user, String reportCode, String templateCode, String reportName, Map<String, Object> params) {
        ReportOutputDocument outputDocument = reportRunner.byReportCode(reportCode)
                .withParams(params)
                .withTemplateCode(templateCode)
                .run();

        byte[] report = outputDocument.getContent();

        Document document = dataManager.create(Document.class);
        document.setName(reportName);
        document.setUser(user);
        document.setStatus(DocumentStatus.INCOMPLETE);

        FileStorage fileStorage = fileStorageLocator.getDefault();
        FileRef fileRef = fileStorage.saveStream(reportName, new ByteArrayInputStream(report));

        document.setFile(fileRef);

        dataManager.save(document);
    }
}
