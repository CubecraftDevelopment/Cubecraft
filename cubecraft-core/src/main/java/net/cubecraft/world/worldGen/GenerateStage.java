package net.cubecraft.world.worldGen;

/**
 * stages when generate terrain.
 * a chunk will flow by stages from all registered generators,
 * and final pop out a full chunk
 * these are stages(by running order)
 * <li>BASE fill content block (like air)</li>
 * <li>BIOME build biome to chunk</li>
 * <li>TERRAIN generate base terrain(base terrain)</li>
 * <li>BIOME_SURFACE build biome to chunk</li>
 * <li>BIOME_CONTENT build biome to chunk</li>
 * <li>ORE generate player`s favorite ore</li>
 * <li>STRUCTURE build structure</li>
 * <li>ENTITY generate entity</li>
 */
public enum GenerateStage {
    BASE,
    BIOME,
    TERRAIN,
    BIOME_SURFACE,
    BIOME_CONTENT,
    ORE,
    STRUCTURE,
    ENTITY;

    public static GenerateStage[] byOrder(){
        return new GenerateStage[]{
                BASE,
                BIOME,
                TERRAIN,
                BIOME_SURFACE,
                BIOME_CONTENT,
                ORE,
                STRUCTURE,
                ENTITY
        };
    }
}
