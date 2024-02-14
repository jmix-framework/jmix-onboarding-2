package com.company.onboarding.view.locationlookup;

import com.company.onboarding.entity.Location;
import com.company.onboarding.entity.LocationType;
import com.company.onboarding.view.main.MainView;

import com.vaadin.flow.data.renderer.Renderer;
import com.vaadin.flow.router.Route;
import io.jmix.flowui.component.valuepicker.EntityPicker;
import io.jmix.flowui.kit.action.ActionPerformedEvent;
import io.jmix.flowui.kit.action.BaseAction;
import io.jmix.flowui.view.*;
import io.jmix.mapsflowui.component.GeoMap;
import io.jmix.mapsflowui.component.model.FitOptions;
import io.jmix.mapsflowui.component.model.source.DataVectorSource;
import io.jmix.mapsflowui.kit.component.model.Easing;
import io.jmix.mapsflowui.kit.component.model.Padding;
import io.jmix.mapsflowui.kit.component.model.style.Fill;
import io.jmix.mapsflowui.kit.component.model.style.Style;
import io.jmix.mapsflowui.kit.component.model.style.image.Anchor;
import io.jmix.mapsflowui.kit.component.model.style.image.CircleStyle;
import io.jmix.mapsflowui.kit.component.model.style.image.IconOrigin;
import io.jmix.mapsflowui.kit.component.model.style.image.IconStyle;
import io.jmix.mapsflowui.kit.component.model.style.stroke.Stroke;
import io.jmix.mapsflowui.kit.component.model.style.text.TextStyle;
import org.locationtech.jts.geom.Geometry;
import org.springframework.lang.Nullable;

import java.util.List;
import java.util.Objects;

@Route(value = "LocationLookupView", layout = MainView.class)
@ViewController("LocationLookupView")
@ViewDescriptor("location-lookup-view.xml")
@DialogMode(width = "60em", height = "45em")
public class LocationLookupView extends StandardView {
    @ViewComponent
    private GeoMap map;
    @ViewComponent
    private BaseAction select;
    @ViewComponent
    private EntityPicker<Location> currentLocationField;

    @ViewComponent("map.pathToBuildingLayer.pathToBuildingSource")
    private DataVectorSource<Location> pathToBuildingSource;
    @ViewComponent("map.buildingLayer.buildingSource")
    private DataVectorSource<Location> buildingSource;
    @ViewComponent("map.buildingEntranceLayer.buildingEntranceSource")
    private DataVectorSource<Location> buildingEntranceSource;
    @ViewComponent("map.buildingAreaLayer.buildingAreaSource")
    private DataVectorSource<Location> buildingAreaSource;

    private Location selected;

    @Subscribe
    public void onInit(final InitEvent event) {
        initBuildingSource();
        initBuildingAreaSource();
        initBuildingEntranceSource();
        initPathToBuildingSource();
    }

    @Subscribe("select")
    public void onSelect(final ActionPerformedEvent event) {
        close(StandardOutcome.SELECT);
    }

    public Location getSelected() {
        return selected;
    }

    public void setSelected(@Nullable Location selected) {
        this.selected = selected;

        currentLocationField.setValue(selected);

        if (selected != null) {
            setMapCenter(selected.getBuilding());
        }
    }

    @Supply(to = "locationVirtualList", subject = "renderer")
    private Renderer<Location> locationVirtualListRenderer() {
        return new LocationCardRenderer(getApplicationContext(), map, this::onLocationChanged);
    }

    private void initBuildingSource() {
        buildingSource.setStyleProvider(location -> new Style()
                .withImage(new IconStyle()
                        .withScale(0.5)
                        .withAnchorOrigin(IconOrigin.BOTTOM_LEFT)
                        .withAnchor(new Anchor(0.49, 0.12))
                        .withSrc(location.getType() == LocationType.OFFICE
                                ? "map-icons/office-marker.png"
                                : "map-icons/coworking-marker.png"))
                .withText(new TextStyle()
                        .withBackgroundFill(new Fill("rgba(255, 255, 255, 0.6)"))
                        .withPadding(new Padding(5, 5, 5, 5))
                        .withOffsetY(15)
                        .withFont("bold 15px sans-serif")
                        .withText(location.getCity())));

        buildingSource.addGeoObjectClickListener(clickEvent -> {
            Location location = clickEvent.getItem();

            setMapCenter(location.getBuilding());

            onLocationChanged(location);
        });
    }

    private void initBuildingAreaSource() {
        buildingAreaSource.setStyleProvider(location -> {
            String fillColor = location.getType() == LocationType.COWORKING
                    ? "rgba(52, 216, 0, 0.2)"
                    : "rgba(1, 147, 154, 0.2)";
            String strokeColor = location.getType() == LocationType.COWORKING
                    ? "#228D00"
                    : "#123EAB";
            return new Style()
                    .withFill(new Fill(fillColor))
                    .withStroke(new Stroke()
                            .withWidth(2d)
                            .withColor(strokeColor));
        });
    }

    private void initBuildingEntranceSource() {
        buildingEntranceSource.setStyleProvider((location ->
                new Style()
                        .withImage(new CircleStyle()
                                .withRadius(4)
                                .withFill(new Fill("#000000"))
                                .withStroke(new Stroke()
                                        .withWidth(2d)
                                        .withColor("#ffffff")))));
    }

    private void initPathToBuildingSource() {
        pathToBuildingSource.setStyleProvider(location ->
                new Style()
                        .withStroke(new Stroke()
                                .withWidth(2d)
                                .withColor("#000000")
                                .withLineDash(List.of(0.2, 8d, 0.8d))));
    }

    private void onLocationChanged(Location newLocation) {
        if (!Objects.equals(newLocation, selected)) {
            selected = newLocation;
            select.setEnabled(true);

            setMapCenter(newLocation.getBuilding());

            currentLocationField.setValue(newLocation);
        }
    }

    private void setMapCenter(Geometry center) {
        map.fit(new FitOptions(center)
                .withDuration(3000)
                .withEasing(Easing.LINEAR)
                .withMaxZoom(20d));
    }
}