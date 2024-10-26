package com.terraforged.noise.util;

import com.terraforged.cereal.spec.Context;
import com.terraforged.cereal.spec.DataAccessor;
import com.terraforged.cereal.spec.DataSpec;
import com.terraforged.cereal.spec.DataSpecs;
import com.terraforged.cereal.value.DataObject;
import com.terraforged.cereal.value.DataValue;
import com.terraforged.noise.combiner.*;
import com.terraforged.noise.domain.*;
import com.terraforged.noise.func.CurveFunc;
import com.terraforged.noise.func.Interpolation;
import com.terraforged.noise.func.MidPointCurve;
import com.terraforged.noise.func.SCurve;
import com.terraforged.noise.modifier.*;
import com.terraforged.noise.selector.*;
import com.terraforged.noise.source.*;

import java.util.function.Function;

public class NoiseSpec {

    static {
        // sources
        DataSpecs.register(Constant.spec());
        DataSpecs.register(BillowNoise.billowSpec());
        DataSpecs.register(CellNoise.spec());
        DataSpecs.register(CellEdgeNoise.spec());
        DataSpecs.register(CubicNoise.spec());
        DataSpecs.register(PerlinNoise.spec());
        DataSpecs.register(PerlinNoise2.spec());
        DataSpecs.register(RidgeNoise.ridgeSpec());
        DataSpecs.register(SimplexNoise.spec());
        DataSpecs.register(SimplexNoise2.spec());
        DataSpecs.register(SimplexRidgeNoise.ridgeSpec());
        DataSpecs.register(Sin.spec());
        DataSpecs.register(Line.spec());
        DataSpecs.register(Rand.spec());

        // combiners
        DataSpecs.register(Add.spec());
        DataSpecs.register(Max.spec());
        DataSpecs.register(Min.spec());
        DataSpecs.register(Multiply.spec());
        DataSpecs.register(Sub.spec());

        // modifiers
        DataSpecs.register(Abs.spec());
        DataSpecs.register(AdvancedTerrace.spec());
        DataSpecs.register(Alpha.spec());
        DataSpecs.register(Bias.spec());
        DataSpecs.register(Boost.spec());
        DataSpecs.register(Cache.spec());
        DataSpecs.register(Clamp.spec());
        DataSpecs.register(Curve.spec());
        DataSpecs.register(Freq.spec());
        DataSpecs.register(Grad.spec());
        DataSpecs.register(Invert.spec());
        DataSpecs.register(LegacyTerrace.spec());
        DataSpecs.register(Map.spec());
        DataSpecs.register(Modulate.spec());
        DataSpecs.register(Power.spec());
        DataSpecs.register(PowerCurve.spec());
        DataSpecs.register(Scale.spec());
        DataSpecs.register(Steps.spec());
        DataSpecs.register(Terrace.spec());
        DataSpecs.register(Threshold.spec());
        DataSpecs.register(VariableCurve.spec());
        DataSpecs.register(Warp.spec());

        // selectors
        DataSpecs.register(Base.spec());
        DataSpecs.register(Blend.spec());
        DataSpecs.register(MultiBlend.spec());
        DataSpecs.register(Select.spec());
        DataSpecs.register(VariableBlend.spec());

        // curves
        DataSpecs.register(Interpolation.spec());
        DataSpecs.registerSub(CurveFunc.class, Interpolation.spec());
        DataSpecs.registerSub(CurveFunc.class, MidPointCurve.spec());
        DataSpecs.registerSub(CurveFunc.class, SCurve.spec());

        // warps
        DataSpecs.register(AddWarp.spec());
        DataSpecs.register(CompoundWarp.spec());
        DataSpecs.register(CumulativeWarp.spec());
        DataSpecs.register(DirectionWarp.spec());
        DataSpecs.register(DomainWarp.spec());

        // utils
        DataSpecs.register(Vec2f.spec());
        DataSpecs.register(Vec2i.spec());
    }

    public static void init() {}

    // contextual seed
    public static int seed(Context context) {
        return context.getData().get("seed").asInt();
    }

    // seed offset added to contextual seed
    public static int seed(DataObject data, DataSpec<?> spec, Context context) {
        return spec.get("seed", data, DataValue::asInt) + seed(context);
    }

    // seed offset from difference between seed value & contextual seed
    public static <T> DataAccessor<T, Integer> seed(Function<T, Integer> getter) {
        return (t, context) -> getter.apply(t) - seed(context);
    }
}
