package com.company.onboarding.view.salarydashboard;


import com.company.onboarding.view.main.MainView;

import com.vaadin.flow.router.Route;
import io.jmix.flowui.view.StandardView;
import io.jmix.flowui.view.ViewController;
import io.jmix.flowui.view.ViewDescriptor;

@Route(value = "salary-dashboard-view", layout = MainView.class)
@ViewController("SalaryDashboardView")
@ViewDescriptor("salary-dashboard-view.xml")
public class SalaryDashboardView extends StandardView {
}