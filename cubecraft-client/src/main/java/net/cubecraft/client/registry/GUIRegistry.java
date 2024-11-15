package net.cubecraft.client.registry;

import me.gb2022.commons.registry.ConstructingMap;
import me.gb2022.commons.registry.ItemRegisterFunc;
import net.cubecraft.client.gui.layout.FlowLayout;
import net.cubecraft.client.gui.layout.Layout;
import net.cubecraft.client.gui.layout.OriginLayout;
import net.cubecraft.client.gui.layout.ViewportLayout;
import net.cubecraft.client.gui.node.*;
import net.cubecraft.client.render.gui.*;
import net.cubecraft.client.render.gui.ComponentRendererPart;

public class GUIRegistry {
    @ItemRegisterFunc(ComponentRendererPart.class)
    public static void registerComponentRenderers(ConstructingMap<ComponentRendererPart> parts) {
        parts.registerItem(BorderImage.class);
        parts.registerItem(HorizontalBoarderImage.class);
        parts.registerItem(VerticalBorderImage.class);
        parts.registerItem(Font.class);
        parts.registerItem(ImageAnimation.class);
        parts.registerItem(Color.class);
    }

    @ItemRegisterFunc(Node.class)
    public static void registerComponents(ConstructingMap<Node> nodes) {
        nodes.registerItem(Label.class);
        nodes.registerItem(Button.class);
        nodes.registerItem(Panel.class);
        nodes.registerItem(Image.class);
        nodes.registerItem(Icon.class);

        nodes.registerItem(ScrollPanel.class);
        nodes.registerItem(CardPanel.class);
        nodes.registerItem(SplashText.class);
        nodes.registerItem(TopBar.class);
        nodes.registerItem(TextBar.class);
        nodes.registerItem(ToggleButton.class);
    }

    @ItemRegisterFunc(Layout.class)
    public static void registerLayout(ConstructingMap<Layout> layouts) {
        layouts.registerItem(OriginLayout.class);
        layouts.registerItem(ViewportLayout.class);
        layouts.registerItem(FlowLayout.class);
    }
}
