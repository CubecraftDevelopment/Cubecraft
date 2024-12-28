package net.cubecraft.client.gui.node;

import net.cubecraft.client.gui.layout.AnchorLayout;
import net.cubecraft.client.gui.node.component.Label;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public final class ListView<I> extends Container {
    private final List<I> items = new ArrayList<>();

    @SuppressWarnings("unchecked")
    private Renderer<I> renderer = (Renderer<I>) Renderer.DEFAULT;

    public List<I> getItems() {
        return items;
    }

    public void add(I item) {
        this.items.add(item);
        this.updateUI();
    }

    public void remove(I item) {
        this.items.remove(item);
        this.updateUI();
    }

    public void setRenderer(Renderer<I> renderer) {
        this.renderer = renderer;
        this.updateUI();
    }

    public void updateUI() {
        if (this.renderer == null) {
            throw new IllegalStateException("need a Renderer!@#%^");
        }

        this.nodes.clear();

        var y = 0;

        for (var i = 0; i < this.items.size(); i++) {
            var item = this.items.get(i);
            if (item == null) {
                continue;
            }
            var node = this.renderer.render(item, i);
            node.setContext(this.screen, this, this.context);
            node.id = node.id + "#" + i;

            var layout = node.getLayout();

            if (!(layout instanceof AnchorLayout a)) {
                throw new IllegalArgumentException("rendered component in ListView must be AnchorLayout");
            }

            a.setPrefY(y);
            this.nodes.put(node.id, node);
            y += a.getPrefHeight();
        }
    }

    @Override
    public void onResize(int x, int y, int w, int h) {
        if (!isVisible()) {
            x = -999999;
            y = -999999;
            w = 1;
            h = 1;//cannot see whatever
        }

        var renderer = getRenderer();

        var xo = 0;
        var yo = 0;


        if (renderer != null) {
            var offset = renderer.getOffset(this);

            xo = offset.x();
            yo = offset.y();
        }

        if (this.layout != null) {
            this.layout.resize(x, y, w, h);
            for (Node node : this.nodes.values()) {
                node.onResize(
                        this.layout.getAbsoluteX() + xo,
                        this.layout.getAbsoluteY() + yo,
                        this.layout.getAbsoluteWidth(),
                        this.layout.getAbsoluteHeight()
                );
            }
        } else {
            for (Node node : this.nodes.values()) {
                node.onResize(x + xo, y + yo, w, h);
            }
        }
    }

    public void setData(Collection<I> data) {
        this.items.clear();
        this.items.addAll(data);
        this.updateUI();
    }


    @Override
    public void addNode(String name, Node node) {
        throw new UnsupportedOperationException("ListView");
    }

    @Override
    public void addNode(Node node) {
        throw new UnsupportedOperationException("ListView");
    }

    @Override
    public void removeNode(String id) {
        throw new UnsupportedOperationException("ListView");
    }

    @Override
    public Node getNode(String id) {
        throw new UnsupportedOperationException("ListView");
    }

    public interface Renderer<I> {
        Renderer<?> DEFAULT = (Renderer<Object>) (data, n) -> {
            var node = new Label();
            var layout = new AnchorLayout();

            layout.setPrefHeight(8);
            layout.setLeft(0);
            layout.setRight(0);
            layout.setTop(-1);
            layout.setBottom(-1);

            node.setText(data.toString());
            node.setLayout(layout);
            return node;
        };

        Node render(I data, int n);
    }
}

