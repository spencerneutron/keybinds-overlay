package com.keybindsOverlay;

import net.runelite.client.util.ImageUtil;

import java.awt.image.BufferedImage;
import java.util.function.Function;
import java.util.function.ToIntFunction;
import net.runelite.client.config.Keybind;

public enum sidePanelTab {

    COMBAT(KeybindsOverlayConfig::combatKey, KeybindsOverlayConfig::combatLocation),
    SKILLS(KeybindsOverlayConfig::skillsKey, KeybindsOverlayConfig::skillsLocation),
    QUESTS(KeybindsOverlayConfig::questsKey, KeybindsOverlayConfig::questsLocation),
    INVENTORY(KeybindsOverlayConfig::inventoryKey, KeybindsOverlayConfig::inventoryLocation),
    EQUIPMENT(KeybindsOverlayConfig::equipmentKey, KeybindsOverlayConfig::equipmentLocation),
    PRAYER(KeybindsOverlayConfig::prayerKey, KeybindsOverlayConfig::prayerLocation),
    SPELLBOOK(KeybindsOverlayConfig::spellbookKey, KeybindsOverlayConfig::spellbookLocation);

    // Not in use tabs rn.
    // ACCOUNT_MANAGEMENT, CLAN_CHAT, EMOTE,  FRIENDS, LOGOUT, MUSIC, OPTIONS

    private final BufferedImage icon;
    private final Function<KeybindsOverlayConfig, Keybind> keySupplier;
    private final ToIntFunction<KeybindsOverlayConfig> locationSupplier;

    sidePanelTab(Function<KeybindsOverlayConfig, Keybind> keySupplier,
                 ToIntFunction<KeybindsOverlayConfig> locationSupplier)
    {
        BufferedImage img = null;
        try {
            img = ImageUtil.loadImageResource(getClass(), "/sidePanel/tabs/" + name().toLowerCase() + ".png");
            img = ImageUtil.resizeImage(img, 18, 18);
        } catch (Exception e) {
            System.err.println("errorLoading image for tab: " + name());
        }
        this.icon = img;
        this.keySupplier = keySupplier;
        this.locationSupplier = locationSupplier;
    }

    public BufferedImage getIcon() {
        if (this.icon != null) {
            return this.icon;
        }
        throw new RuntimeException("Icon not available for tab: " + name());
    }

    public Keybind getKeybinding(KeybindsOverlayConfig config) {
        try {
            return keySupplier.apply(config);
        } catch (Exception e) {
            return new Keybind(java.awt.event.KeyEvent.VK_UNDEFINED, 0);
        }
    }

    public int getLocation(KeybindsOverlayConfig config) {
        try {
            return locationSupplier.applyAsInt(config);
        } catch (Exception e) {
            return 0;
        }
    }

}
