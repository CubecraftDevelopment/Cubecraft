package com.terraforged.noise.modifier;

import com.terraforged.cereal.spec.DataFactory;
import com.terraforged.cereal.spec.DataSpec;
import com.terraforged.cereal.value.DataValue;
import com.terraforged.noise.Module;
import com.terraforged.noise.util.NoiseUtil;

import java.util.Arrays;

public class Terrace extends Modifier {

    private static final float MIN_NOISE_VALUE = 0.0F;
    private static final float MAX_NOISE_VALUE = 0.999999F;

    private final float blend;
    private final float length;
    private final int maxIndex;
    private final Step[] steps;
    private final Module ramp;
    private final Module cliff;
    private final Module rampHeight;

    public Terrace(Module source, Module ramp, Module cliff, Module rampHeight, int steps, float blendRange) {
        super(source);
        this.blend = blendRange;
        this.maxIndex = steps - 1;
        this.steps = new Step[steps];
        this.length = steps * MAX_NOISE_VALUE;
        this.ramp = ramp;
        this.cliff = cliff;
        this.rampHeight = rampHeight;
        float min = source.minValue();
        float max = source.maxValue();
        float range = max - min;
        float spacing = range / (steps - 1);

        for (int i = 0; i < steps; i++) {
            float value = i * spacing;
            this.steps[i] = new Step(value, spacing, blendRange);
        }
    }

    @Override
    public String getSpecName() {
        return "Terrace";
    }

    @Override
    public float getValue(int seed, float x, float y) {
        float value = source.getValue(seed, x, y);
        value = NoiseUtil.clamp(value, MIN_NOISE_VALUE, MAX_NOISE_VALUE);
        return modify(seed, x, y, value);
    }

    @Override
    public float modify(int seed, float x, float y, float noiseValue) {
        int index = NoiseUtil.floor(noiseValue * steps.length);
        Step step = steps[index];

        if (index == maxIndex) {
            return step.value;
        }

        if (noiseValue < step.lowerBound) {
            return step.value;
        }

        if (noiseValue > step.upperBound) {
            Step next = steps[index + 1];
            return next.value;
        }

        float ramp = 1F - (this.ramp.getValue(seed, x, y) * 0.5F);
        float cliff = 1F - (this.cliff.getValue(seed, x, y) * 0.5F);
        float alpha = (noiseValue - step.lowerBound) / (step.upperBound - step.lowerBound);

        float value = step.value;
        if (alpha > ramp) {
            Step next = steps[index + 1];
            float rampSize = 1 - ramp;
            float rampAlpha = (alpha - ramp) / rampSize;
            float rampHeight = this.rampHeight.getValue(seed, x, y);
            value += (next.value - value) * rampAlpha * rampHeight;
        }

        if (alpha > cliff) {
            Step next = steps[index + 1];
            float cliffAlpha = (alpha - cliff) / (1 - cliff);
            value = NoiseUtil.lerp(value, next.value, cliffAlpha);
        }

        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        Terrace terrace = (Terrace) o;

        if (Float.compare(terrace.blend, blend) != 0) return false;
        if (Float.compare(terrace.length, length) != 0) return false;
        if (maxIndex != terrace.maxIndex) return false;
        // Probably incorrect - comparing Object[] arrays with Arrays.equals
        if (!Arrays.equals(steps, terrace.steps)) return false;
        if (!ramp.equals(terrace.ramp)) return false;
        if (!cliff.equals(terrace.cliff)) return false;
        return rampHeight.equals(terrace.rampHeight);
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (blend != 0.0f ? Float.floatToIntBits(blend) : 0);
        result = 31 * result + (length != 0.0f ? Float.floatToIntBits(length) : 0);
        result = 31 * result + maxIndex;
        result = 31 * result + Arrays.hashCode(steps);
        result = 31 * result + ramp.hashCode();
        result = 31 * result + cliff.hashCode();
        result = 31 * result + rampHeight.hashCode();
        return result;
    }

    private static class Step {

        private final float value;
        private final float lowerBound;
        private final float upperBound;

        private Step(float value, float distance, float blendRange) {
            this.value = value;
            float blend = distance * blendRange;
            float bound = (distance - blend) / 2F;
            lowerBound = value - bound;
            upperBound = value + bound;
        }
    }

    private static final DataFactory<Terrace> factory = (data, spec, context) -> new Terrace(
            spec.get("source", data, Module.class, context),
            spec.get("ramp", data, Module.class, context),
            spec.get("cliff", data, Module.class, context),
            spec.get("ramp_height", data, Module.class, context),
            spec.get("steps", data, DataValue::asInt),
            spec.get("blend_range", data, DataValue::asFloat)
    );

    public static DataSpec<Terrace> spec() {
        return Modifier.specBuilder(Terrace.class, factory)
                .add("steps", 1, s -> s.steps.length)
                .add("blend_range", 1, s -> s.blend)
                .addObj("source", Module.class, s -> s.source)
                .addObj("ramp", Module.class, s -> s.ramp)
                .addObj("cliff", Module.class, s -> s.cliff)
                .addObj("ramp_height", Module.class, s -> s.rampHeight)
                .build();
    }
}
