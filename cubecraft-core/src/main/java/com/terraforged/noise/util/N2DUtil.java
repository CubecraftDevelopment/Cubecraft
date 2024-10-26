package com.terraforged.noise.util;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

public class N2DUtil {

    public static BufferedImage render(int width, int height, PosVisitor<BufferedImage> visitor) {
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        iterate(width, height, image, visitor);
        return image;
    }

    public static JFrame display(int width, int height, PixelShader<BufferedImage> shader) {
        return display(width, height, (x, z, ctx) -> {
            int rgb = shader.shade(x, z, ctx);
            ctx.setRGB(x, z, rgb);
        });
    }

    public static JFrame display(int width, int height, PosVisitor<BufferedImage> visitor) {
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

        JLabel label = new JLabel(new ImageIcon(image)) {
            @Override
            public void paint(Graphics g) {
                iterate(width, height, image, visitor);
                super.paint(g);
            }
        };

        JFrame frame = new JFrame();
        frame.add(label);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        return frame;
    }

    public static <T> void iterate(int width, int height, T ctx, PosVisitor<T> visitor) {
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                visitor.visit(x, y, ctx);
            }
        }
    }

    public interface PosVisitor<T> {

        void visit(int x, int z, T ctx);
    }

    public interface PixelShader<T> {

        int shade(int x, int z, T ctx);
    }
}
