package com.fuckolympus.arc.camera.command;

import android.content.Context;
import org.apache.commons.lang3.Validate;
import org.apache.commons.lang3.builder.Builder;

import java.util.Iterator;
import java.util.LinkedList;

/**
 * Created by alex on 2.8.17.
 */
public class CommandChain {

    private Command<?> firstCommand;

    public void run(Context context) {
        firstCommand.execute(context);
    }

    public static class CommandChainBuilder implements Builder<CommandChain> {

        private LinkedList<Command<?>> commands = new LinkedList<>();

        public CommandChainBuilder addCommand(Command<?> command) {
            commands.add(command);
            return this;
        }

        @Override
        public CommandChain build() {
            Validate.notEmpty(commands);

            Iterator<Command<?>> iterator = commands.descendingIterator();
            Command<?> prevCommand = null;
            while (iterator.hasNext()) {
                Command<?> command = iterator.next();
                if (prevCommand != null) {
                    command.nextCommand = prevCommand;
                }
                prevCommand = command;
            }

            CommandChain commandChain = new CommandChain();
            commandChain.firstCommand = commands.getFirst();
            return commandChain;
        }
    }
}
