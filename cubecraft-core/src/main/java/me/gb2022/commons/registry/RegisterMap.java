//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package me.gb2022.commons.registry;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Objects;
import me.gb2022.commons.container.NameSpaceMap;

public class RegisterMap<I> extends NameSpaceMap<I> {
    private final Class<I> templateClass;

    public RegisterMap(I fallback, Class<I> templateClass) {
        super(":", fallback);
        this.templateClass = templateClass;
    }

    public RegisterMap(Class<I> templateClass) {
        this(null, templateClass);
    }

    public void registerItem(String namespace, String id, I item) {
        this.set(namespace, id, item);
    }

    public void registerItem(String all, I item) {
        this.set(all, item);
    }

    public void registerClass(String namespace, String id, Class<? extends I> item) {
        try {
            this.set(namespace, id, item.getConstructor().newInstance());
        } catch (InvocationTargetException | InstantiationException | NoSuchMethodException | IllegalAccessException var5) {
            ReflectiveOperationException e = var5;
            throw new RuntimeException(e);
        }
    }

    public void registerClass(String all, Class<? extends I> item) {
        this.registerClass(all.split(":")[0], all.split(":")[1], item);
    }

    public void registerGetter(Class<?> clazz) {
        Method[] var2 = clazz.getMethods();
        int var3 = var2.length;

        for(int var4 = 0; var4 < var3; ++var4) {
            Method m = var2[var4];
            ItemGetter getter = (ItemGetter)m.getAnnotation(ItemGetter.class);
            if (getter != null) {
                try {
                    this.set(getter.namespace(), getter.id(), (I) m.invoke(clazz.getConstructor().newInstance()));
                } catch (InvocationTargetException | InstantiationException | NoSuchMethodException | IllegalAccessException var8) {
                    ReflectiveOperationException e = var8;
                    throw new RuntimeException(e);
                }
            }
        }

    }

    public void registerGetFunctionProvider(Class<?> clazz) {
        try {
            Method[] var8 = clazz.getMethods();
            int var3 = var8.length;

            for(int var4 = 0; var4 < var3; ++var4) {
                Method m = var8[var4];
                ItemRegisterFunc getter = m.getAnnotation(ItemRegisterFunc.class);
                if (getter != null && getter.value() == this.templateClass && m.getParameters().length == 1 && m.getParameters()[0].getType() == this.getClass()) {
                    m.invoke(clazz.getConstructor().newInstance(), this);
                }
            }

        } catch (InvocationTargetException | InstantiationException | NoSuchMethodException | IllegalAccessException var7) {
            ReflectiveOperationException e = var7;
            throw new RuntimeException(e);
        }
    }

    public void registerFieldHolder(Class<?> clazz) {
        FieldRegistryHolder holder = (FieldRegistryHolder)clazz.getAnnotation(FieldRegistryHolder.class);
        Field[] var3;
        int var4;
        int var5;
        Field f;
        FieldRegistry registry;
        if (holder == null) {
            var3 = clazz.getFields();
            var4 = var3.length;

            for(var5 = 0; var5 < var4; ++var5) {
                f = var3[var5];
                registry = (FieldRegistry)f.getAnnotation(FieldRegistry.class);
                if (registry != null && !Objects.equals(registry.namespace(), "__DEFAULT__")) {
                    try {
                        this.registerItem(registry.namespace(), registry.value(), (I) f.get(null));
                    } catch (IllegalAccessException var11) {
                        IllegalAccessException e = var11;
                        throw new RuntimeException(e);
                    }
                }
            }
        } else {
            var3 = clazz.getFields();
            var4 = var3.length;

            for(var5 = 0; var5 < var4; ++var5) {
                f = var3[var5];
                registry = (FieldRegistry)f.getAnnotation(FieldRegistry.class);
                if (registry != null) {
                    String namespace = holder.value();
                    if (!Objects.equals(registry.namespace(), "__DEFAULT__")) {
                        namespace = registry.namespace();
                    }

                    try {
                        this.registerItem(namespace, registry.value(), (I) f.get(null));
                    } catch (IllegalAccessException var10) {
                        IllegalAccessException e = var10;
                        throw new RuntimeException(e);
                    }
                }
            }
        }

    }
}
