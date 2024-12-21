package net.cubecraft.client.gui.node;

import me.gb2022.commons.registry.TypeItem;

@TypeItem("panel")
public class Panel extends Container {
    @Override
    public String getStatement() {
        return super.getStatement();
        //return this.style + ":normal";
    }
}
