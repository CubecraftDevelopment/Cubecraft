package net.cubecraft;

import net.cubecraft.util.register.NamedRegistry;
import net.cubecraft.world.item.Item;

public interface CoreRegistries {
    NamedRegistry<Item> ITEMS = new NamedRegistry<>();
}
