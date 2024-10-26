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

package com.terraforged.noise.modifier;

import com.terraforged.cereal.spec.DataFactory;
import com.terraforged.cereal.spec.DataSpec;
import com.terraforged.noise.Module;
import com.terraforged.noise.util.NoiseUtil;

public class Modulate extends Modifier {

    private final Module direction;
    private final Module strength;

    public Modulate(Module source, Module direction, Module strength) {
        super(source);
        this.direction = direction;
        this.strength = strength;
    }

    @Override
    public String getSpecName() {
        return "Modulate";
    }

    @Override
    public float getValue(int seed, float x, float y) {
        float angle = direction.getValue(seed, x, y) * NoiseUtil.PI2;
        float strength = this.strength.getValue(seed, x, y);
        float dx = NoiseUtil.sin(angle) * strength;
        float dy = NoiseUtil.cos(angle) * strength;
        return source.getValue(seed, x + dx, y + dy);
    }

    @Override
    public float modify(int seed, float x, float y, float noiseValue) {
        return 0;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        Modulate modulate = (Modulate) o;

        if (!direction.equals(modulate.direction)) return false;
        return strength.equals(modulate.strength);
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + direction.hashCode();
        result = 31 * result + strength.hashCode();
        return result;
    }

    private static final DataFactory<Modulate> factory = (data, spec, context) -> new Modulate(
            spec.get("source", data, Module.class, context),
            spec.get("direction", data, Module.class, context),
            spec.get("strength", data, Module.class, context)
    );

    public static DataSpec<Modulate> spec() {
        return Modifier.sourceBuilder(Modulate.class, factory)
                .addObj("direction", Module.class, m -> m.direction)
                .addObj("strength", Module.class, m -> m.strength)
                .build();
    }
}
