package me.gb2022.quantum3d.render.command;

import me.gb2022.commons.container.ArrayQueue;

public abstract class CommandPool {
    private ArrayQueue<Command> commandQueue = new ArrayQueue<>();


    public void addCommand() {

    }

}
