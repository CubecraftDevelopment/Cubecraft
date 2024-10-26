package me.gb2022.quantum3d.render.command;


import java.util.ArrayDeque;

public abstract class CommandPool {
    private ArrayDeque<Command> commandQueue = new ArrayDeque<>();


    public void addCommand() {

    }

}
