package jbyoshi.sponge.pickaxe.command;

import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.CommandElement;

import java.lang.reflect.Type;

public final class ParameterElement {
    final CommandElement element;
    private final Packer packer;

    public ParameterElement(CommandElement commandElement, Packer packer) {
        this.element = commandElement == null ? null : packer.wrap(commandElement);
        this.packer = packer;
    }

    Object getValue(CommandSource source, CommandContext context) {
        if (element == null) {
            return source;
        }
        return packer.pack(context.getAll(element.getKey()));
    }
}
