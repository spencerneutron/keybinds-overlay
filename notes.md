RuneLite Plugin Performance Improvement Summary
Observed Problem

Your RuneLite overlay plugin, though seemingly simple, causes a notable FPS drop (~120 → ~105). A profiler or measurement would confirm, but the primary issues stem from expensive per-frame work in the hot render path of the plugin.

Areas with Costly Code
1. sidePanelTab.java

getIcon()

Calls ImageUtil.loadImageResource(...) on every render.

Calls ImageUtil.resizeImage(...) every time.

This results in repeated image IO, scaling, and allocation of large BufferedImages.

Reflection lookups

Methods like getMethod() scan all methods in KeybindsOverlayConfig.class.

Regex matching on every lookup.

2. KeybindsOverlayOverlay.java

render(Graphics2D)

Clears and rebuilds the PanelComponent children every frame.

Iterates over tabs each frame and repeatedly performs heavy work.

getOrderOfTabs()

Calls getLocation(tab) twice per iteration (duplicate work).

Per-frame reflection

getKeybinding(tab) and getLocation(tab) use reflection every frame.

Component allocation

addTabToPanel, addIcon, addLine – create UI components each frame.

Why This Hurts Performance

Image loading + resizing is heavy: disk/IO (resources), CPU work, allocation, GC pressure.

Reflection method lookup + invocation is expensive, especially with regex scanning and dynamic method invocation every frame.

Rebuilding UI hierarchy per frame allocates objects and forces unnecessary work on every render.

Duplicate work (calling the same method multiple times per loop) compounds the problem.

High-Level Fix Approach

Make the render loop as lightweight as possible. Anything that can be computed once and reused should be moved out of the render path.

1. Cache Icons

Load and resize images once (e.g., at plugin start, enum init, or first use).

Reuse the cached BufferedImage in render().

Avoid calling image loading/resizing in the hot path.

2. Replace per-frame Reflection Lookup

Do not scan methods or regex-match every frame.

Resolve reflective targets once ahead of time:

During plugin initialization, or

When configuration changes.

Store either:

Direct Method references, or

Preferably language constructs like function references (Supplier, Function, lambda) that avoid reflection entirely.

3. Avoid Per-Frame Reflection Invocation

Instead of invoking a Method via reflection in render(), use method references or lambdas.

Eliminate overhead from both lookup and invocation.

4. Cache Tab Order

Compute tab order once and cache it.

Invalidate and recompute only when relevant configuration changes.

Remove duplicate lookups (e.g., avoid calling getLocation(tab) twice).

5. Reuse UI Components

Avoid rebuilding the entire PanelComponent tree every frame.

Update existing components rather than recreating them.

This reduces allocation pressure and keeps rendering fast.

6. Confirm Fix Impact with Measurement

Add timing instrumentation around render/critical paths.

Use profilers (e.g., VisualVM, async-profiler, JFR) to confirm improvements and identify remaining bottlenecks.

Refactoring Best Practices

Treat the render loop as performance-critical: only cheap, non-allocating work should happen there.

Any non-trivial computation should occur once and be reused.

Use RuneLite’s config change events to trigger cache invalidation.

Aim to eliminate reflection entirely if direct method references can be used.

Code Snippet Examples (Do Not Paste Into the Summary Above)
Icon Caching Approach (Snippet)
// Enum with cached image
public enum SidePanelTab {
    INVENTORY(loadAndResize("inventory.png")),
    SETTINGS(loadAndResize("settings.png"));

    private final BufferedImage icon;

    SidePanelTab(BufferedImage icon) {
        this.icon = icon;
    }

    public BufferedImage getIcon() {
        return icon;
    }

    private static BufferedImage loadAndResize(String resource) {
        BufferedImage img = ImageUtil.loadImageResource(MyPlugin.class, resource);
        return ImageUtil.resizeImage(img, 16, 16); // example size
    }
}

Reflection → Function Mapping (Snippet)
// Instead of reflective lookup every frame:
private final Supplier<Keybind> keybindSupplier;
private final Supplier<LocationEnum> locationSupplier;

public SidePanelTab(Supplier<Keybind> keybindSupplier,
                    Supplier<LocationEnum> locationSupplier) {
    this.keybindSupplier = keybindSupplier;
    this.locationSupplier = locationSupplier;
}

public Keybind getKeybinding(KeybindsOverlayConfig config) {
    return keybindSupplier.get(config);
}

Cache Invalidation Example (Snippet)
@Subscribe
public void onConfigChanged(ConfigChanged event) {
    if (event.getGroup().equals("keybindsoverlay")) {
        recalculateTabOrder();
    }
}