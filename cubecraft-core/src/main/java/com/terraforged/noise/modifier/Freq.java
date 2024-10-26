package com.terraforged.noise.modifier;

import com.terraforged.cereal.spec.DataFactory;
import com.terraforged.cereal.spec.DataSpec;
import com.terraforged.noise.Module;

public class Freq extends Modifier {

    private final Module x;
    private final Module y;

    public Freq(Module source, Module x, Module y) {
        super(source);
        this.x = x;
        this.y = y;
    }

    @Override
    public String getSpecName() {
        return "Freq";
    }

    @Override
    public float getValue(int seed, float x, float y) {
        float fx = this.x.getValue(seed, x, y);
        float fy = this.y.getValue(seed, x, y);
        return source.getValue(seed, x * fx, y * fy);
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

        Freq freq = (Freq) o;

        if (!x.equals(freq.x)) return false;
        return y.equals(freq.y);
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + x.hashCode();
        result = 31 * result + y.hashCode();
        return result;
    }

    private static final DataFactory<Freq> factory = (data, spec, context) -> new Freq(
            spec.get("source", data, Module.class, context),
            spec.get("x", data, Module.class, context),
            spec.get("y", data, Module.class, context)
    );

    public static DataSpec<Freq> spec() {
        return sourceBuilder(Freq.class, factory)
                .addObj("x", Module.class, f -> f.x)
                .addObj("y", Module.class, f -> f.y)
                .build();
    }
}
