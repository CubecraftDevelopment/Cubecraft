//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package me.gb2022.commons.registry;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

@SuppressWarnings("unchecked")
public class ConstructingMap<I> extends RegisterMap<Class<? extends I>> {
    private final Class<?>[] params;
    private final Class<I> template;

    public ConstructingMap(Class<I> templateClass, Class<?>... params) {
        super(null);
        this.params = params;
        this.template = templateClass;
    }

    public I create(String all, Object... initArgs) {
        try {
            I o = (I) ((Class) this.get(all)).getDeclaredConstructor(this.params).newInstance(initArgs);
            this.getEventBus().callEvent(new ItemConstructEvent(this, all, o));
            return o;
        } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException | InstantiationException var4) {
            ReflectiveOperationException e = var4;
            throw new RuntimeException(e);
        }
    }

    public I create(String id, String namespace, Object... initArgs) {
        try {
            return (I) ((Class) this.get(namespace, id)).getDeclaredConstructor(this.params).newInstance(initArgs);
        } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException | InstantiationException var5) {
            ReflectiveOperationException e = var5;
            throw new RuntimeException(e);
        }
    }

    public Map<String, I> createAll(Object... initArgs) {
        Map<String, I> map = new HashMap();

        for (String all : this.keySet()) {
            map.put(all, this.create(all, initArgs));
        }

        return map;
    }

    public void registerItem(Class<? extends I> item) {
        TypeItem a = item.getDeclaredAnnotation(TypeItem.class);
        if (a == null) {
            throw new RuntimeException("item does not contains TypeItem annotation,so can`t auto reg.");
        } else {
            this.registerItem(a.value(), item);
        }
    }




    public void registerFieldHolderWithClassRegistry(Class<?> clazz) {
        Field[] var2 = clazz.getFields();
        int var3 = var2.length;

        for (Field f : var2) {
            ClassRegistry registry = f.getAnnotation(ClassRegistry.class);
            if (registry != null) {
                try {
                    this.registerItem((Class<I>) f.get(null));
                } catch (IllegalAccessException var8) {
                    IllegalAccessException e = var8;
                    throw new RuntimeException(e);
                }
            }
        }

    }

    @Override
    public void registerGetFunctionProvider(Class<?> clazz) {
        try {
            Method[] var8 = clazz.getMethods();
            int var3 = var8.length;

            for (Method m : var8) {
                ItemRegisterFunc getter = m.getAnnotation(ItemRegisterFunc.class);
                if (getter != null && getter.value() == this.template && m.getParameters().length == 1 && m.getParameters()[0].getType() == this.getClass()) {
                    m.invoke(clazz.getConstructor().newInstance(), this);
                }
            }

        } catch (InvocationTargetException | InstantiationException | NoSuchMethodException | IllegalAccessException var7) {
            ReflectiveOperationException e = var7;
            throw new RuntimeException(e);
        }
    }
}
