/*
 *
 * MIT License
 *
 * Copyright (c) 2020 TerraForged
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.terraforged.noise.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Points {

    public static List<Vec2f> poisson(int seedX, int seedZ, int width, int height, float radius, int samples) {
        return poisson(new Random(NoiseUtil.seed(seedX, seedZ)), width, height, radius, samples);
    }

    public static List<Vec2f> poisson(Random random, int width, int height, float radius, int samples) {
        float cellSize = radius / NoiseUtil.SQRT2;
        float maxDistance = radius * 2F;
        int w = (int) Math.ceil(width / cellSize);
        int h = (int) Math.ceil(height / cellSize);

        int[][] grid = new int[w][h];
        List<Vec2f> points = new ArrayList<>();
        List<Vec2f> spawns = new ArrayList<>();
        spawns.add(new Vec2f(random.nextInt(width), random.nextInt(height)));

        while (spawns.size() > 0) {
            int index = random.nextInt(spawns.size());
            Vec2f spawn = spawns.get(index);
            boolean valid = false;

            for (int i = 0; i < samples; i++) {
                float angle = random.nextFloat() * NoiseUtil.PI2;
                float distance = radius + (random.nextFloat() * maxDistance);
                float x = spawn.x + NoiseUtil.sin(angle) * distance;
                float z = spawn.y + NoiseUtil.cos(angle) * distance;
                if (valid(x, z, width, height, cellSize, radius, points, grid)) {
                    valid = true;

                    Vec2f vec = new Vec2f(x, z);
                    points.add(vec);
                    spawns.add(vec);

                    int cellX = (int) (x / cellSize);
                    int cellZ = (int) (z / cellSize);
                    grid[cellZ][cellX] = points.size();
                    break;
                }
            }

            if (!valid) {
                spawns.remove(index);
            }
        }

        return points;
    }

    private static boolean valid(float x, float z, int width, int height, float cellSize, float radius, List<Vec2f> points, int[][] grid) {
        if (x < 0 || x >= width || z < 0 || z >= height) {
            return false;
        }

        int cellX = (int) (x / cellSize);
        int cellZ = (int) (z / cellSize);
        int searchRadius = 2;
        float radius2 = radius * radius;

        int minX = Math.max(0, cellX - searchRadius);
        int maxX = Math.min(grid[0].length - 1, cellX + searchRadius);
        int minZ = Math.max(0, cellZ - searchRadius);
        int maxZ = Math.min(grid.length - 1, cellZ + searchRadius);

        for (int dz = minZ; dz <= maxZ; dz++) {
            for (int dx = minX; dx <= maxX; dx++) {
                int index = grid[dz][dx] - 1;
                if (index == -1) {
                    continue;
                }
                Vec2f point = points.get(index);
                if (point.dist2(x, z) < radius2) {
                    return false;
                }
            }
        }
        return true;
    }
}
