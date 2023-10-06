package ink.flybird.cubecraft.client.gui.layout;

import ink.flybird.fcommon.registry.TypeItem;

@TypeItem("origin")
public final class OriginLayout extends Layout {
    public Origin origin;
    private int rx, ry;

    public void setRelativeX(int rx) {
        this.rx = rx;
    }

    public void setRelativeY(int ry) {
        this.ry = ry;
    }

    public int getRelativeX() {
        return rx;
    }

    public int getRelativeY() {
        return ry;
    }

    public int getRelativeWidth() {
        return width;
    }

    public int getRelativeHeight() {
        return height;
    }

    public void setOrigin(Origin origin) {
        this.origin = origin;
    }

    @Override
    public void initialize(String[] metadata) {
        this.origin = Origin.from(metadata[0]);
        this.rx = Integer.parseInt(metadata[1]);
        this.ry = Integer.parseInt(metadata[2]);
        this.width = Integer.parseInt(metadata[3]);
        this.height = Integer.parseInt(metadata[4]);
    }

    @Override
    public void resize(int x, int y, int scrWidth, int scrHeight) {
        int ox = x, oy = y;
        switch (this.origin) {
            case LEFT_TOP -> {
            }
            case LEFT_MIDDLE -> oy = scrHeight / 2 - this.height / 2 + x;
            case LEFT_BOTTOM -> oy = scrHeight - this.height + y;
            case MIDDLE_TOP -> ox = scrWidth / 2 - this.width / 2 + x;
            case MIDDLE_MIDDLE -> {
                ox = scrWidth / 2 - this.width / 2 + x;
                oy = scrHeight / 2 - this.height / 2 + y;
            }
            case MIDDLE_BOTTOM -> {
                ox = scrWidth / 2 - this.width / 2 + x;
                oy = scrHeight - this.height + y;
            }
            case RIGHT_TOP -> ox = scrWidth - this.width + x;
            case RIGHT_MIDDLE -> {
                ox = scrWidth - this.width + x;
                oy = scrHeight / 2 - this.height / 2 + y;
            }
            case RIGHT_BOTTOM -> {
                ox = scrWidth - this.width + x;
                oy = scrHeight - this.height + y;
            }
        }
        this.setAbsoluteX(ox + rx);
        this.setAbsoluteY(oy + ry);

        if (this.scale.left()) {
            this.setAbsoluteX(x);
        }
        if (this.scale.top()) {
            this.setAbsoluteY(y);
        }

        if (this.scale.right()) {
            this.setAbsoluteWidth(scrWidth - this.getAbsoluteX() + x);
        } else {
            this.setAbsoluteWidth(this.width);
        }
        if (this.scale.bottom()) {
            this.setAbsoluteHeight(scrHeight - this.getAbsoluteY() + y);
        } else {
            this.setAbsoluteHeight(this.height);
        }

        this.setAbsoluteX(this.getAbsoluteX() + this.getBorder().left());
        this.setAbsoluteY(this.getAbsoluteY() + this.getBorder().top());
        this.setAbsoluteWidth(this.getAbsoluteWidth() - (this.getBorder().right() + this.getBorder().left()));
        this.setAbsoluteHeight(this.getAbsoluteHeight() - (this.getBorder().bottom() + this.getBorder().top()));
    }


    public enum Origin {
        LEFT_TOP,
        LEFT_MIDDLE,
        LEFT_BOTTOM,
        MIDDLE_MIDDLE,
        MIDDLE_TOP,
        MIDDLE_BOTTOM,
        RIGHT_TOP,
        RIGHT_MIDDLE,
        RIGHT_BOTTOM;

        public static Origin from(String meta) {
            return switch (meta) {
                case "left_top" -> LEFT_TOP;
                case "left_middle" -> LEFT_MIDDLE;
                case "left_bottom" -> LEFT_BOTTOM;
                case "middle_top" -> MIDDLE_TOP;
                case "middle_bottom" -> MIDDLE_BOTTOM;
                case "right_top" -> RIGHT_TOP;
                case "right_middle" -> RIGHT_MIDDLE;
                case "right_bottom" -> RIGHT_BOTTOM;
                default -> MIDDLE_MIDDLE;
            };
        }
    }

    /*
    public OriginLayout(int rx, int ry, int width, int height, Origin origin, int layer) {
        this.rx = rx;
        this.ry = ry;
        this.width = width;
        this.height = height;
        this.origin = origin;
        this.layer = layer;
    }

    public static class XMLDeserializer implements FAMLDeserializer<OriginLayout> {
        @Override
        public OriginLayout deserialize(Element element, XmlReader reader) {
            Origin side = Origin.from(element.getElementsByTagName("type").item(0).getTextContent());
            int[] l = new Gson().fromJson(element.getElementsByTagName("layout").item(0).getTextContent(), int[].class);
            int layer = 0;
            if (element.hasAttribute("layer")) {
                layer = Integer.parseInt(element.getAttribute("layer"));
            }
            return new OriginLayout(l[0], l[1], l[2], l[3], side, layer);
        }
    }

     */
}
