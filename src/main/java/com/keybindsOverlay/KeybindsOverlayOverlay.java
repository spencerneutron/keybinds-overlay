package com.keybindsOverlay;

import net.runelite.client.config.Keybind;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.OverlayPriority;
import net.runelite.client.ui.overlay.components.ComponentOrientation;
import net.runelite.client.ui.overlay.components.ImageComponent;
import net.runelite.client.ui.overlay.components.LineComponent;
import net.runelite.client.ui.overlay.components.PanelComponent;

import javax.inject.Inject;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.stream.Collectors;

public class KeybindsOverlayOverlay extends Overlay {

    private final KeybindsOverlayConfig config;
    private final PanelComponent panelComponent = new PanelComponent();

    @Inject
    private KeybindsOverlayOverlay(KeybindsOverlayConfig config)
    {
        setPosition(OverlayPosition.ABOVE_CHATBOX_RIGHT);
        setPriority(OverlayPriority.HIGH);
        this.config = config;

    }


    @Override
    public Dimension render(Graphics2D graphics) {

        panelComponent.getChildren().clear();

        panelComponent.setOrientation(ComponentOrientation.HORIZONTAL);
        panelComponent.setGap(new Point(20, 0));


        for (sidePanelTab tab: getOrderOfTabs())
        {
            if (isKeybindingDefined(getKeybinding(tab))) {
                addTabToPanel(tab);
            }
        }

        return panelComponent.render(graphics);
    }

    private void addLine(String text)
    {
        panelComponent.getChildren().add(LineComponent.builder()
                .right(text.substring(0, Math.min(3, text.length())).toUpperCase())
                .rightColor(Color.PINK)
                .build());
    }

    private void addIcon(BufferedImage icon)
    {
        panelComponent.getChildren()
                .add(new ImageComponent(icon));
    }

    private void addTabToPanel(sidePanelTab tab)
    {
        addIcon(tab.getIcon());
        addKeybinding(getKeybinding(tab));
    }

    private Keybind getKeybinding(sidePanelTab tab) {
        try {
            return tab.getKeybinding(config);
        } catch (Exception e) {
            return new Keybind(KeyEvent.VK_UNDEFINED, 0);
        }
    }

    private int getLocation(sidePanelTab tab)
    {
        try {
            return tab.getLocation(config);
        } catch (Exception e) {
            return 0;
        }
    }

    private void addKeybinding(Keybind keybind)
    {
        addLine(String.valueOf(keybind));
    }

    private boolean isKeybindingDefined(Keybind keybind)
    {
        return keybind.getKeyCode() != KeyEvent.VK_UNDEFINED;
    }

    private Set<sidePanelTab> getOrderOfTabs()
    {
        Map<sidePanelTab, Integer> orderOfTabs = new HashMap<>();
        for (sidePanelTab tab: sidePanelTab.values()){
            int loc = getLocation(tab);
            orderOfTabs.put(tab, loc);
        }

        return orderOfTabs.entrySet()
                .stream()
                .sorted((Map.Entry.comparingByValue()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new)).keySet();


    }

}
