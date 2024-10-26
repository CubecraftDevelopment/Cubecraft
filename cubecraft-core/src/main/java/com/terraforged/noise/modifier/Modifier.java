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

import java.util.function.Function;

/**
 * @author dags <dags@dags.me>
 *
 * Modifiers alter the output of one or more Modules
 */
public abstract class Modifier implements Module {

    protected final Module source;

    public Modifier(Module source) {
        this.source = source;
    }

    @Override
    public float getValue(int seed, float x, float y) {
        float value = source.getValue(seed, x, y);
        return modify(seed, x, y, value);
    }

    @Override
    public float minValue() {
        return source.minValue();
    }

    @Override
    public float maxValue() {
        return source.maxValue();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Modifier modifier = (Modifier) o;

        return source.equals(modifier.source);
    }

    @Override
    public int hashCode() {
        return source.hashCode();
    }

    @Override
    public String toString() {
        return "Modifier{" +
                "source=" + source +
                '}';
    }

    public abstract float modify(int seed, float x, float y, float noiseValue);

    protected static <M extends Modifier> DataSpec.Builder<M> specBuilder(Class<M> type, DataFactory<M> factory) {
        return DataSpec.builder(type.getSimpleName(), type, factory);
    }

    protected static <M extends Modifier> DataSpec.Builder<M> sourceBuilder(Class<M> type, DataFactory<M> factory) {
        return sourceBuilder(type.getSimpleName(), type, factory);
    }

    protected static <M extends Modifier> DataSpec.Builder<M> sourceBuilder(String name, Class<M> type, DataFactory<M> factory) {
        return DataSpec.builder(name, type, factory).addObj("source", Module.class, m -> m.source);
    }

    public static <M extends Modifier> DataSpec<M> spec(Class<M> type, Function<Module, M> constructor) {
        DataFactory<M> factory = (data, spec, context) -> constructor.apply(spec.get("source", data, Module.class, context));
        return sourceBuilder(type, factory).build();
    }
}
