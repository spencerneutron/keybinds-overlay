package com.keybindsOverlay;

import net.runelite.client.util.ImageUtil;

import java.awt.image.BufferedImage;
import java.lang.reflect.Method;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public enum sidePanelTab {

     COMBAT, SKILLS, QUESTS, INVENTORY, EQUIPMENT, PRAYER, SPELLBOOK;

    // Not in use tabs rn.
    // ACCOUNT_MANAGEMENT, CLAN_CHAT, EMOTE,  FRIENDS, LOGOUT, MUSIC, OPTIONS

    private final BufferedImage icon;
    private final Method keybindingMethod;
    private final Method locationMethod;

    sidePanelTab()
    {
        BufferedImage img = null;
        try {
            img = ImageUtil.loadImageResource(getClass(), "/sidePanel/tabs/" + name().toLowerCase() + ".png");
            img = ImageUtil.resizeImage(img, 18, 18);
        } catch (Exception e) {
            System.err.println("errorLoading image for tab: " + name());
        }
        this.icon = img;

        Method kb = null;
        Method loc = null;
        try {
            kb = getMethod(name(), "Key");
        } catch (RuntimeException ignored) {
        }
        try {
            loc = getMethod(name(), "Location");
        } catch (RuntimeException ignored) {
        }
        this.keybindingMethod = kb;
        this.locationMethod = loc;
    }

    public BufferedImage getIcon() {
        if (this.icon != null) {
            return this.icon;
        }
        throw new RuntimeException("Icon not available for tab: " + name());
    }

    public Method getKeybindingMethod() {
        return this.keybindingMethod;
    }

    public Method getLocationMethod() {
        return this.locationMethod;
    }

    private Method getMethod(String mainSpecifier, String secondSpecifier)
    {
        Method[] methods = KeybindsOverlayConfig.class.getMethods();
        Pattern pattern = Pattern.compile(mainSpecifier.toLowerCase() + secondSpecifier);
        for (Method method : methods) {
            Matcher matcher = pattern.matcher(method.getName());
            if (matcher.lookingAt()) {
                return method;
            }
        }
        throw new RuntimeException("Programming error. \n" + mainSpecifier + secondSpecifier);
    }

}
