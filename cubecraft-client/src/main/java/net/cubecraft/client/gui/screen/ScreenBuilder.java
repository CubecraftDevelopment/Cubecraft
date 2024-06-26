package net.cubecraft.client.gui.screen;

import net.cubecraft.client.resource.UIAsset;

public interface ScreenBuilder {
    static ScreenBuilder xml(UIAsset screen) {
        return new XMLScreenBuilder(screen);
    }

    static ScreenBuilder xml(UIAsset screen, UIAsset parent) {
        return new XMLScreenBuilder(screen, parent);
    }

    static ScreenBuilder object(Class<? extends Screen> screen) {
        return new ObjectScreenBuilder(screen);
    }

    static ScreenBuilder object(Class<? extends Screen> screen, Class<? extends Screen> parent) {
        return new ObjectScreenBuilder(screen, parent);
    }


    Screen build();


    class XMLScreenBuilder implements ScreenBuilder {
        private final UIAsset screen;
        private final UIAsset parent;

        public XMLScreenBuilder(UIAsset screen) {
            this.screen = screen;
            this.parent = null;
        }

        public XMLScreenBuilder(UIAsset screen, UIAsset parent) {
            this.screen = screen;
            this.parent = parent;
        }

        @Override
        public Screen build() {
            Screen scr = new Screen(screen.getDom().getDocumentElement());
            if (this.parent != null) {
                scr.setParentScreen(new Screen(this.parent.getDom().getDocumentElement()));
            }
            return scr;
        }
    }

    class ObjectScreenBuilder implements ScreenBuilder {
        private final Class<? extends Screen> clazz;
        private final Class<? extends Screen> parent;

        public ObjectScreenBuilder(Class<? extends Screen> screen, Class<? extends Screen> parent) {
            if (screen == Screen.class || parent == Screen.class) {
                throw new IllegalArgumentException("wtf?");
            }
            this.clazz = screen;
            this.parent = parent;
        }

        public ObjectScreenBuilder(Class<? extends Screen> screen) {
            if (screen == Screen.class) {
                throw new IllegalArgumentException("wtf?");
            }
            this.clazz = screen;
            this.parent = null;
        }

        @Override
        public Screen build() {
            try {
                Screen scr = this.clazz.getDeclaredConstructor().newInstance();
                if (this.parent != null) {
                    scr.setParentScreen(this.parent.getDeclaredConstructor().newInstance());
                }
                return scr;
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }
}

