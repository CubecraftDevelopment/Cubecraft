package net.cubecraft.client.registry;

import me.gb2022.commons.registry.ConstructingMap;
import me.gb2022.commons.registry.ItemRegisterFunc;
import net.cubecraft.client.gui.layout.*;
import net.cubecraft.client.gui.node.*;
import net.cubecraft.client.gui.node.component.ImageView;
import net.cubecraft.client.gui.node.component.Label;
import net.cubecraft.client.gui.node.component.SplashText;
import net.cubecraft.client.gui.node.control.Button;
import net.cubecraft.client.gui.node.control.TextInput;
import net.cubecraft.client.gui.node.control.ToggleButton;
import net.cubecraft.client.render.gui.*;

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
        nodes.registerItem("Label", Label.class);
        nodes.registerItem("Button", Button.class);
        nodes.registerItem("Panel", Panel.class);
        nodes.registerItem("ImageView", ImageView.class);
        nodes.registerItem("FontIcon", SplashText.class);

        nodes.registerItem("ListView", ListView.class);
        nodes.registerItem("ScrollPanel",ScrollPanel.class);
        nodes.registerItem("CardPanel",CardPanel.class);
        nodes.registerItem("Splash",SplashText.class);
        nodes.registerItem("Topbar",TopBar.class);
        nodes.registerItem("TextInput", TextInput.class);
        nodes.registerItem("ToggleButton", ToggleButton.class);

    }

    @ItemRegisterFunc(Layout.class)
    public static void registerLayout(ConstructingMap<Layout> layouts) {
        layouts.registerItem(OriginLayout.class);
        layouts.registerItem(ViewportLayout.class);
        layouts.registerItem(FlowLayout.class);
        layouts.registerItem(AnchorLayout.class);
    }
}
