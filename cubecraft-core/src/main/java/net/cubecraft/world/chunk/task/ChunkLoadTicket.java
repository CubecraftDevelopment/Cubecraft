package net.cubecraft.world.chunk.task;

public class ChunkLoadTicket {
    public static final ChunkLoadTicket LOAD_DATA = new ChunkLoadTicket(ChunkLoadLevel.None_TICKING,10);
    public static final ChunkLoadTicket ENTITY = new ChunkLoadTicket(ChunkLoadLevel.Entity_TICKING, 10);


    private ChunkLoadLevel chunkLoadLevel;
    private int time;

    public ChunkLoadTicket(ChunkLoadLevel loadLevel,int ticks){
        this.time=ticks;
        this.chunkLoadLevel=loadLevel;
    }

    public void setTime(int time) {
        this.time = time;
    }

    public int getTime() {
        return time;
    }


    public void addToTask(ChunkProcessTask task) {
        if(this.chunkLoadLevel.containsLevel(ChunkLoadLevel.Entity_TICKING)){
            task.addTime(ChunkLoadTaskType.ENTITY_TICK,this.time);
        }
        if(this.chunkLoadLevel.containsLevel(ChunkLoadLevel.Block_Entity_TICKING)){
            task.addTime(ChunkLoadTaskType.ENTITY_TICK,this.time);
        }
        if(this.chunkLoadLevel.containsLevel(ChunkLoadLevel.Block_Random_TICKING)){
            task.addTime(ChunkLoadTaskType.BLOCK_RANDOM_TICK,this.time);
        }
        if(this.chunkLoadLevel.containsLevel(ChunkLoadLevel.Block_TICKING)){
            task.addTime(ChunkLoadTaskType.BLOCK_TICK,this.time);
        }
        if(this.chunkLoadLevel.containsLevel(ChunkLoadLevel.None_TICKING)){
            task.addTime(ChunkLoadTaskType.DATA_KEEP,this.time);
        }
    }

    public ChunkLoadLevel getChunkLoadLevel() {
        return chunkLoadLevel;
    }

    public void setChunkLoadLevel(ChunkLoadLevel chunkLoadLevel) {
        this.chunkLoadLevel = chunkLoadLevel;
    }
}
