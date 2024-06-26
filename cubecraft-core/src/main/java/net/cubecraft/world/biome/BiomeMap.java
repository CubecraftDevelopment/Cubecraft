package net.cubecraft.world.biome;

import me.gb2022.commons.registry.RegisterMap;

import java.util.ArrayList;

public class BiomeMap extends RegisterMap<Biome> {
    public BiomeMap() {
        super(null);
    }

    public Biome match(double continental, double temperature, double humidity, double erosion, double altitude){
        ArrayList<Biome> biomes= new ArrayList<>();
        for (String biome:this.keySet()){
            biomes.add(this.get(biome));
        }
        biomes.sort((o1, o2) -> -Double.compare(
                o1.match(continental, temperature, humidity, erosion, altitude),
                o2.match(continental, temperature, humidity, erosion, altitude)
        ));
        return biomes.get(0);
    }


}
