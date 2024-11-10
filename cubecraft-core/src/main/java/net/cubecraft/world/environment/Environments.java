package net.cubecraft.world.environment;

import net.cubecraft.util.register.NamedRegistry;
import net.cubecraft.util.register.Registered;
import net.cubecraft.util.register.Registry;

@Registry
public interface Environments {
    NamedRegistry<Environment> REGISTRY = new NamedRegistry<>();

    Registered<Environment> OVERWORLD = REGISTRY.register(new OverworldEnvironment());
}
