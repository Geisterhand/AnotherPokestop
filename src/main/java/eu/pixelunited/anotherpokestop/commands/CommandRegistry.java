package eu.pixelunited.anotherpokestop.commands;

import net.minecraftforge.fml.common.event.FMLServerStartingEvent;

public class CommandRegistry {

    public static void registerCommands(FMLServerStartingEvent event) {
        event.registerServerCommand(new Apsreload());
        event.registerServerCommand(new RemovePokeStop());
        event.registerServerCommand(new SetPokeStop());
        event.registerServerCommand(new Pokestopedit());
        event.registerServerCommand(new Apslootmodifier());
    }
}
