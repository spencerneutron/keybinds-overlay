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
import net.runelite.client.eventbus.EventBus;

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

	@Inject
	private EventBus eventBus;

	@Override
	protected void startUp()
	{
		overlayManager.add(overlay);
		eventBus.register(overlay);
	}

	@Override
	protected void shutDown()
	{
		overlayManager.remove(overlay);
		eventBus.unregister(overlay);
	}

	@Provides
	KeybindsOverlayConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(KeybindsOverlayConfig.class);
	}
}
