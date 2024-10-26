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

package com.terraforged.noise.domain;

import com.terraforged.cereal.spec.Context;
import com.terraforged.cereal.spec.DataSpec;
import com.terraforged.cereal.value.DataObject;
import com.terraforged.noise.Module;
import com.terraforged.noise.util.NoiseUtil;

public class DirectionWarp implements Domain {

    private final Module direction;
    private final Module strength;

    public DirectionWarp(Module direction, Module strength) {
        this.direction = direction;
        this.strength = strength;
    }

    @Override
    public String getSpecName() {
        return "DirectionWarp";
    }

    @Override
    public float getOffsetX(int seed, float x, float y) {
        float angle = direction.getValue(seed, x, y) * NoiseUtil.PI2;
        return NoiseUtil.sin(angle) * strength.getValue(seed, x, y);
    }

    @Override
    public float getOffsetY(int seed, float x, float y) {
        float angle = direction.getValue(seed, x, y) * NoiseUtil.PI2;
        return NoiseUtil.cos(angle) * strength.getValue(seed, x, y);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DirectionWarp that = (DirectionWarp) o;

        if (!direction.equals(that.direction)) return false;
        return strength.equals(that.strength);
    }

    @Override
    public int hashCode() {
        int result = direction.hashCode();
        result = 31 * result + strength.hashCode();
        return result;
    }

    private static DirectionWarp create(DataObject data, DataSpec<?> spec, Context context) {
        return new DirectionWarp(
                spec.get("direction", data, Module.class, context),
                spec.get("strength", data, Module.class, context)
        );
    }

    public static DataSpec<? extends Domain> spec() {
        return DataSpec.builder("DirectionWarp", DirectionWarp.class, DirectionWarp::create)
                .addObj("direction", Module.class, w -> w.direction)
                .addObj("strength", Module.class, w -> w.strength)
                .build();
    }
}
