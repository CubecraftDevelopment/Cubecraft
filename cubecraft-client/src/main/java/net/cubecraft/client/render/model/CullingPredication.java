package net.cubecraft.client.render.model;

import net.cubecraft.util.register.NamedRegistry;
import net.cubecraft.util.register.Registered;
import net.cubecraft.util.register.Registry;
import net.cubecraft.world.block.access.BlockAccess;
import net.cubecraft.world.block.blocks.Blocks;

import java.util.function.BiPredicate;

@Registry
public interface CullingPredication extends BiPredicate<BlockAccess, BlockAccess> {

    NamedRegistry<Predicate> REGISTRY = new NamedRegistry<>();

    Predicate F_SOLID = (b, n) -> n.getBlock().isSolid();

    Predicate F_EQUALS = (b, n) -> n.getBlockId() == b.getBlockId();

    Predicate F_NEVER = (b, n) -> false;

    Predicate F_ALWAYS = (b, n) -> true;

    Registered<Predicate> SOLID = REGISTRY.register("solid", F_SOLID);

    Registered<Predicate> EQUALS = REGISTRY.register("equals", F_EQUALS);

    Registered<Predicate> NEVER = REGISTRY.register("never", F_NEVER);

    Registered<Predicate> ALWAYS = REGISTRY.register("always", F_ALWAYS);

    Registered<Predicate> SOLID_OR_EQUALS = REGISTRY.register("solid_or_equals", Predicate.or(F_SOLID, F_EQUALS));

    Registered<Predicate> NOT_AIR = REGISTRY.register("not_air", (p, n) -> n.getBlockId() != Blocks.AIR.getId());


    interface Predicate extends CullingPredication {
        static Predicate or(Predicate... predicates) {
            return (p, n) -> {
                for (Predicate pred : predicates) {
                    if (pred.test(p, n)) {
                        return true;
                    }
                }
                return false;
            };
        }

        static Predicate and(Predicate... predicates) {
            return (p, n) -> {
                for (Predicate pred : predicates) {
                    if (!pred.test(p, n)) {
                        return false;
                    }
                }
                return true;
            };
        }
    }
}
