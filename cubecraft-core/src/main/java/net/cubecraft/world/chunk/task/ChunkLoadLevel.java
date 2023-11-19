package net.cubecraft.world.chunk.task;

public enum ChunkLoadLevel {
    Entity_TICKING(4),
    Block_Entity_TICKING(3),
    Block_Random_TICKING(2),
    Block_TICKING(1),
    None_TICKING(0);

    final int order;

    ChunkLoadLevel(int order){
        this.order=order;
    }

    public boolean containsLevel(ChunkLoadLevel loadLevel){
        return this.order>=loadLevel.order;
    }

    public int getOrder() {
        return order;
    }

    public static ChunkLoadLevel fromOrder(int order){
        return switch (order){
            case 0->None_TICKING;
            case 1->Block_TICKING;
            case 2->Block_Random_TICKING;
            case 3->Block_Entity_TICKING;
            case 4->Entity_TICKING;
            default -> throw new IllegalStateException("Unexpected value: " + order);
        };
    }
}
