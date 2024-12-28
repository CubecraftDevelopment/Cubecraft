package net.cubecraft.client.gui;

import net.cubecraft.client.CubecraftClient;
import net.cubecraft.client.gui.event.component.ButtonClickedEvent;
import net.cubecraft.client.gui.event.component.TextInputSubmitEvent;
import net.cubecraft.client.gui.node.Node;
import net.cubecraft.client.gui.node.control.Button;
import net.cubecraft.client.gui.node.control.TextInput;
import net.cubecraft.client.gui.screen.Screen;
import net.cubecraft.client.gui.screen.ScreenBuilder;
import net.cubecraft.client.registry.ResourceRegistry;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.METHOD})
public @interface UI {
    String INIT = "_INIT";

    String value() default "";

    interface HandlerBinder<C> {
        void bind(C component, Object controller, Method method);
    }

    //todo:bind all
    @Retention(RetentionPolicy.RUNTIME)
    @interface ViewModel {
    }

    interface Controller {
        default void setScreen(ScreenBuilder sb) {
            CubecraftClient.getInstance()
                    .getClientGUIContext()
                    .setScreen(sb);
        }
    }

    //todo
    final class Binder {
        public static final Map<Class<?>, HandlerBinder<?>> BINDERS = new HashMap<>();

        static {
            addBinder(ButtonClickedEvent.class, Button.class, (component, controller, method) -> {
                component.setActionListener((b) -> {
                    try {
                        method.invoke(controller, b);
                    } catch (IllegalAccessException | InvocationTargetException e) {
                        throw new RuntimeException(e);
                    }
                });
            });

            addBinder(TextInputSubmitEvent.class, TextInput.class, (component, controller, method) -> {

            });
        }

        public static <C extends Node, E> void addBinder(Class<E> evt, Class<C> comp, HandlerBinder<C> binder) {
            BINDERS.put(evt, binder);
        }

        static void bindRefs(Object controller, Screen screen) throws Exception {
            var controllerType = controller.getClass();
            var components = screen.collectNodes();
            var vm = controllerType.isAnnotationPresent(ViewModel.class);

            for (var field : controllerType.getDeclaredFields()) {
                if (!vm) {
                    if (!field.isAnnotationPresent(UI.class)) {
                        continue;
                    }
                }

                var name = field.getName();
                if (field.isAnnotationPresent(UI.class)) {
                    name = field.getAnnotation(UI.class).value();
                }
                if (name.isEmpty()) {
                    name = field.getName();
                }

                for (var id : components.keySet()) {
                    if (!Objects.equals(id, name)) {
                        continue;
                    }
                    field.setAccessible(true);
                    field.set(controller, components.get(id));
                    break;
                }
            }
        }

        static void bindHandlers(Object controller, Screen screen) throws Throwable {
            var controllerType = controller.getClass();
            var components = screen.collectNodes();
            var vm = controllerType.isAnnotationPresent(ViewModel.class);

            for (var method : controllerType.getDeclaredMethods()) {
                if (!method.isAnnotationPresent(UI.class) && !vm) {
                    continue;
                }

                var name = method.getName();
                if (method.isAnnotationPresent(UI.class)) {
                    name = method.getAnnotation(UI.class).value();
                }
                if (name.isEmpty()) {
                    name = method.getName();
                }

                method.setAccessible(true);

                if (name.equals(INIT)) {
                    method.invoke(controller, screen);
                    continue;
                }

                for (var id : components.keySet()) {
                    if (!Objects.equals(id, name)) {
                        continue;
                    }


                    var component = components.get(id);
                    @SuppressWarnings("unchecked") HandlerBinder<Node> binder = (HandlerBinder<Node>) BINDERS.get(method.getParameterTypes()[0]);
                    binder.bind(component, controller, method);

                    break;
                }
            }
        }

        public static void bind(Object controller, Screen screen) {
            try {
                bindRefs(controller, screen);
                bindHandlers(controller, screen);
            } catch (Throwable e) {
                throw new RuntimeException(e);
            }
        }
    }
}
