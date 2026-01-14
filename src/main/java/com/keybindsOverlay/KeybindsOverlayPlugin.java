package com.keybindsOverlay;

import com.google.inject.Provides;
import javax.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import net.runelite.api.Client;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.OverlayManager;

@PluginDescriptor(
	name = "Keybinds Overlay"
)
public class KeybindsOverlayPlugin extends Plugin
{
    private static final Logger log = LoggerFactory.getLogger(KeybindsOverlayPlugin.class);
	@Inject
	private Client client;

	@Inject
	private OverlayManager overlayManager;

	@Inject
	private KeybindsOverlayOverlay overlay;

	@Override
	protected void startUp()
	{
		overlayManager.add(overlay);
	}

	@Override
	protected void shutDown()
	{
		overlayManager.remove(overlay);
	}

	@Provides
	KeybindsOverlayConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(KeybindsOverlayConfig.class);
	}
}
