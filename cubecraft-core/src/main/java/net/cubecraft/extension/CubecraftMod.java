package net.cubecraft.extension;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


/**
 * manifest of a mod.
 *
 * @author GrassBlock2022
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface CubecraftMod {
    ModSide side();
    int apiVersion() default 1;
}
