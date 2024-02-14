package com.company.onboarding.view.locationlookup;

import com.company.onboarding.entity.Location;
import com.company.onboarding.entity.LocationType;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.function.SerializableSupplier;
import com.vaadin.flow.theme.lumo.LumoUtility;
import io.jmix.core.Messages;
import io.jmix.flowui.UiComponents;
import io.jmix.flowui.kit.component.button.JmixButton;
import io.jmix.mapsflowui.component.GeoMap;
import io.jmix.mapsflowui.component.model.FitOptions;
import io.jmix.mapsflowui.kit.component.model.Easing;
import org.springframework.context.ApplicationContext;

import java.util.function.Consumer;

public class LocationCardRenderer extends ComponentRenderer<VerticalLayout, Location> {

    private static final String LOCATION_CARD_CLASSNAME = "location-card";

    private final UiComponents uiComponents;
    private final Messages messages;
    private final GeoMap map;
    private final Consumer<Location> changeCallback;

    public LocationCardRenderer(ApplicationContext applicationContext, GeoMap map, Consumer<Location> changeCallback) {
        super((SerializableSupplier<VerticalLayout>) null);

        uiComponents = applicationContext.getBean(UiComponents.class);
        messages = applicationContext.getBean(Messages.class);

        this.map = map;
        this.changeCallback = changeCallback;
    }

    @Override
    public VerticalLayout createComponent(Location location) {
        VerticalLayout cardLayout = uiComponents.create(VerticalLayout.class);
        cardLayout.setSpacing(false);
        cardLayout.addClassNames(LumoUtility.Border.ALL,
                LumoUtility.BorderColor.CONTRAST_10,
                LumoUtility.BorderRadius.MEDIUM,
                LOCATION_CARD_CLASSNAME);

        Span textAddress = uiComponents.create(Span.class);
        textAddress.setText(location.getAddress());

        Span officeType = uiComponents.create(Span.class);
        officeType.setText(messages.getMessage(location.getType()));
        officeType.getElement().getThemeList().add(location.getType() == LocationType.OFFICE ? "success" : "normal");
        officeType.getElement().getThemeList().add("badge");
        officeType.addClassNames(LumoUtility.AlignSelf.START);

        HorizontalLayout textLayout = uiComponents.create(HorizontalLayout.class);
        textLayout.setWidthFull();
        textLayout.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);
        textLayout.add(textAddress, officeType);

        Button searchBtn = uiComponents.create(JmixButton.class);
        searchBtn.setIcon(VaadinIcon.SEARCH.create());
        searchBtn.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        searchBtn.addClickListener(event -> {
            map.fit(new FitOptions(location.getBuilding())
                    .withDuration(2000)
                    .withEasing(Easing.LINEAR)
                    .withMaxZoom(20d));
        });
        Button selectBtn = uiComponents.create(JmixButton.class);
        selectBtn.setText(messages.getMessage("com.company.onboarding.view.user", "locationVirtualList.selectBtn.text"));
        selectBtn.addClickListener(event -> {
            changeCallback.accept(location);
        });

        HorizontalLayout controlLayout = uiComponents.create(HorizontalLayout.class);
        controlLayout.setWidthFull();
        controlLayout.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);
        controlLayout.add(selectBtn, searchBtn);

        cardLayout.add(textLayout, controlLayout);
        return cardLayout;
    }
}
