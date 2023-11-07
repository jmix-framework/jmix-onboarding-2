package com.company.onboarding.view.locationlookup;

import com.company.onboarding.entity.Location;
import com.company.onboarding.entity.LocationType;
import com.company.onboarding.view.main.MainView;

import com.vaadin.flow.data.renderer.Renderer;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.StreamResource;
import io.jmix.flowui.component.valuepicker.EntityPicker;
import io.jmix.flowui.kit.action.ActionPerformedEvent;
import io.jmix.flowui.kit.action.BaseAction;
import io.jmix.flowui.model.CollectionContainer;
import io.jmix.flowui.view.*;
import io.jmix.mapsflowui.component.GeoMap;
import io.jmix.mapsflowui.component.model.feature.LineStringFeature;
import io.jmix.mapsflowui.component.model.feature.PointFeature;
import io.jmix.mapsflowui.component.model.feature.PolygonFeature;
import io.jmix.mapsflowui.component.model.layer.VectorLayer;
import io.jmix.mapsflowui.component.model.source.DataVectorSource;
import io.jmix.mapsflowui.component.model.source.VectorSource;
import io.jmix.mapsflowui.kit.component.model.feature.Feature;
import io.jmix.mapsflowui.kit.component.model.style.Fill;
import io.jmix.mapsflowui.kit.component.model.style.Style;
import io.jmix.mapsflowui.kit.component.model.style.image.Anchor;
import io.jmix.mapsflowui.kit.component.model.style.image.CircleStyle;
import io.jmix.mapsflowui.kit.component.model.style.image.IconOrigin;
import io.jmix.mapsflowui.kit.component.model.style.image.IconStyle;
import io.jmix.mapsflowui.kit.component.model.style.stroke.Stroke;
import io.jmix.mapsflowui.kit.component.model.style.text.Padding;
import io.jmix.mapsflowui.kit.component.model.style.text.TextStyle;
import org.locationtech.jts.geom.Coordinate;
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

    private Location selected;

    @Subscribe
    public void onInit(final InitEvent event) {
        initGeoMap();
    }

    @Subscribe(id = "locationsDc", target = Target.DATA_CONTAINER)
    public void onLocationsDcCollectionChange(final CollectionContainer.CollectionChangeEvent<Location> event) {
        VectorLayer testLayer = map.getLayer("vectorLayer");
        VectorSource source = testLayer.getSource();

        source.removeAllFeatures();

        for (Location location : event.getSource().getItems()) {
            if (location.getBuildingArea() != null) {
                source.addFeature(createBuildingAreaFeature(location));
            }
            if (location.getBuildingEntrance() != null) {
                source.addFeature(createBuildingEntranceFeature(location));
            }
            if (location.getPathToBuilding() != null) {
                source.addFeature(createPathToBuildingFeature(location));
            }
        }
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
            setMapCenter(selected.getBuilding().getCoordinate());
        }
    }

    @Supply(to = "locationVirtualList", subject = "renderer")
    private Renderer<Location> locationVirtualListRenderer() {
        return new LocationCardRenderer(getApplicationContext(), map, this::onLocationChanged);
    }

    private void initGeoMap() {
        StreamResource officeIconResource = new StreamResource("office-marker.png",
                () -> getClass()
                        .getResourceAsStream("/META-INF/resources/icons/office-marker.png"));
        StreamResource coworkingIconResource = new StreamResource("coworking-marker.png",
                () -> getClass()
                        .getResourceAsStream("/META-INF/frontend/jmix-openlayers-map/icon/marker.png"));

        VectorLayer layer = map.getLayer("dataVectorLayer");
        DataVectorSource<Location> source = layer.getSource();
        source.setStyleProvider(location -> new Style()
                .withImage(new IconStyle()
                        .withScale(0.5)
                        .withAnchorOrigin(IconOrigin.BOTTOM_LEFT)
                        .withAnchor(new Anchor(0.49, 0.12))
                        .withResource(location.getType() == LocationType.OFFICE
                                ? officeIconResource
                                : coworkingIconResource))
                .withText(new TextStyle()
                        .withBackgroundFill(new Fill("rgba(255, 255, 255, 0.6)"))
                        .withPadding(new Padding(5, 5, 5, 5))
                        .withOffsetY(15)
                        .withFont("bold 15px sans-serif")
                        .withText(location.getCity())));

        source.addGeoObjectClickListener(clickEvent -> {
            Location location = clickEvent.getItem();

            setMapCenter(location.getBuilding().getCoordinate());

            onLocationChanged(location);
        });
    }

    private Feature createBuildingAreaFeature(Location location) {
        String fillColor = "rgba(52, 216, 0, 0.2)";
        String strokeColor = "#228D00";
        if (location.getType() == LocationType.COWORKING) {
            fillColor = "rgba(1, 147, 154, 0.2)";
            strokeColor = "#123EAB";
        }
        return new PolygonFeature(location.getBuildingArea())
                .addStyles(new Style()
                        .withFill(new Fill(fillColor))
                        .withStroke(new Stroke()
                                .withWidth(2d)
                                .withColor(strokeColor)));
    }

    private Feature createBuildingEntranceFeature(Location location) {
        return new PointFeature(location.getBuildingEntrance())
                .addStyles(new Style()
                        .withImage(new CircleStyle()
                                .withRadius(4)
                                .withFill(new Fill("#000000"))
                                .withStroke(new Stroke()
                                        .withWidth(2d)
                                        .withColor("#ffffff"))));
    }

    private Feature createPathToBuildingFeature(Location location) {
        return new LineStringFeature(location.getPathToBuilding())
                .addStyles(new Style()
                        .withStroke(new Stroke()
                                .withWidth(2d)
                                .withColor("#000000")
                                .withLineDash(List.of(0.2, 8d, 0.8d))));
    }

    private void onLocationChanged(Location newLocation) {
        if (!Objects.equals(newLocation, selected)) {
            selected = newLocation;
            select.setEnabled(true);

            setMapCenter(newLocation.getBuilding().getCoordinate());

            currentLocationField.setValue(newLocation);
        }
    }

    private void setMapCenter(Coordinate center) {
        map.getMapView().setCenter(center);
        map.getMapView().setZoom(20);
    }
}