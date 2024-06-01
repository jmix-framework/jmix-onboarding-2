package com.company.onboarding.view.salariesdashboard;


import com.company.onboarding.view.main.MainView;

import com.vaadin.flow.router.Route;
import io.jmix.flowui.view.StandardView;
import io.jmix.flowui.view.ViewController;
import io.jmix.flowui.view.ViewDescriptor;

@Route(value = "salaries-dashboard-view", layout = MainView.class)
@ViewController("SalariesDashboardView")
@ViewDescriptor("salaries-dashboard-view.xml")
public class SalariesDashboardView extends StandardView {
}