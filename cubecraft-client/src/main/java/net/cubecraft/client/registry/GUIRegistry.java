package net.cubecraft.client.registry;

import ink.flybird.fcommon.registry.ConstructingMap;
import ink.flybird.fcommon.registry.ItemRegisterFunc;
import net.cubecraft.client.context.ClientGUIContext;
import net.cubecraft.client.gui.layout.FlowLayout;
import net.cubecraft.client.gui.layout.Layout;
import net.cubecraft.client.gui.layout.OriginLayout;
import net.cubecraft.client.gui.layout.ViewportLayout;
import net.cubecraft.client.gui.node.*;
import net.cubecraft.client.render.gui.*;

public class GUIRegistry {
    public static void registerComponentRenderers() {
        ClientGUIContext.registerRendererComponentPart(BorderImage.class, new BorderImage.JDeserializer());
        ClientGUIContext.registerRendererComponentPart(HorizontalBoarderImage.class, new HorizontalBoarderImage.JDeserializer());
        ClientGUIContext.registerRendererComponentPart(VerticalBorderImage.class, new VerticalBorderImage.JDeserializer());
        ClientGUIContext.registerRendererComponentPart(Font.class, new Font.JDeserializer());
        ClientGUIContext.registerRendererComponentPart(ImageAnimation.class, new ImageAnimation.JDeserializer());
        ClientGUIContext.registerRendererComponentPart(Color.class, new Color.JDeserializer());
    }

    public static void registerComponents() {
        ClientGUIContext.registerComponent(Label.class);
        ClientGUIContext.registerComponent(Button.class);
        ClientGUIContext.registerComponent(Panel.class);
        ClientGUIContext.registerComponent(Image.class);
        ClientGUIContext.registerComponent(Icon.class);

        ClientGUIContext.registerComponent(ScrollPanel.class);
        ClientGUIContext.registerComponent(CardPanel.class);
        ClientGUIContext.registerComponent(SplashText.class);
        ClientGUIContext.registerComponent(TopBar.class);
        ClientGUIContext.registerComponent(TextBar.class);
        ClientGUIContext.registerComponent(ToggleButton.class);
    }

    @ItemRegisterFunc(Layout.class)
    public static void registerLayout(ConstructingMap<Layout> layouts) {
        layouts.registerItem(OriginLayout.class);
        layouts.registerItem(ViewportLayout.class);
        layouts.registerItem(FlowLayout.class);
    }
}
