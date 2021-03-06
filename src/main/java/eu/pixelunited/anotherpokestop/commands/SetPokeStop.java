package eu.pixelunited.anotherpokestop.commands;

import com.pixelmonmod.pixelmon.entities.EntityPokestop;
import eu.pixelunited.anotherpokestop.AnotherPokeStop;
import eu.pixelunited.anotherpokestop.ConfigManagement;
import eu.pixelunited.anotherpokestop.config.PokeStopRegistry;
import eu.pixelunited.anotherpokestop.config.lang.LangConfig;
import eu.pixelunited.anotherpokestop.config.presets.PresetConfig;
import eu.pixelunited.anotherpokestop.config.presets.PresetTrainer;
import eu.pixelunited.anotherpokestop.objects.PokeStopData;
import eu.pixelunited.anotherpokestop.objects.RGBStorage;
import eu.pixelunited.anotherpokestop.utils.Utils;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.World;

import java.io.File;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;


public class SetPokeStop extends CommandBase {

    LangConfig _lang = AnotherPokeStop.getLang();


    @Override
    public String getName() {
        return "setpokestop";
    }

    @Override
    public boolean checkPermission(MinecraftServer server, ICommandSender sender) {
        return sender.canUseCommand(4, "anotherpokestop.set");
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return "/setpokestop <Preset>";
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        if (!(sender instanceof EntityPlayerMP)) {
            sender.sendMessage(Utils.toText("[AnotherPokeStop] Hello GlaDOS, get a ingame Player!"));
            return;
        }

        EntityPlayerMP p = (EntityPlayerMP) sender;
        RGBStorage color;
        String loottable;
        List<PresetTrainer> trainer;
        PresetConfig _preset;

        if(args.length > 1) {
            p.sendMessage(Utils.toText("/setpokestop <Preset>"));
            return;
        }

        //Loading settings from preset
        if(args.length == 1) {
            _preset = Utils.getPreset(args[0]);

            if (_preset == null) {
                p.sendMessage(Utils.toText("[&dAnotherPokeStop&r] &4Preset not found."));
                return;
            }
        } else {
            _preset = AnotherPokeStop.getPreset();
        }
        color = new RGBStorage(_preset.red, _preset.green, _preset.blue);

        if(AnotherPokeStop.getInstance()._availableLoottables.contains(_preset.loottable)) {
            loottable = _preset.loottable;
        } else {
            p.sendMessage(Utils.toText("[&dAnotherPokeStop&r] &4Invalid Loottable, check your configs!"));
            return;
        }

        for(int i = 0; i < _preset.trainerList.size(); i++) {
            if(!AnotherPokeStop.getInstance()._availableTrainer.contains(_preset.trainerList.get(i).trainer)) {
                p.sendMessage(Utils.toText("[&dAnotherPokeStop&r] &4Invalid Trainer, check your configs!"));
                return;
            }
        }
            trainer = _preset.trainerList;

        //Creating Pokestop
        World playerWorld = p.getEntityWorld();
        EntityPokestop pokestop = new EntityPokestop(playerWorld, p.posX, p.posY, p.posZ);
        pokestop.setColor(color.getR(), color.getG(), color.getB());
        pokestop.setAlwaysAnimate(true);
        pokestop.setNoGravity(true);
        pokestop.setCubeRange(_preset.cubeRange);
        pokestop.setSize(_preset.pokestopSize);
        playerWorld.spawnEntity(pokestop);
        int version = 2;
        p.sendMessage(Utils.toText("[&dAnotherPokeStop&r] &6New Pokestop set."));
        List<String> lureRestriction = new ArrayList<>();

        PokeStopData newPokeStopData = new PokeStopData(pokestop.getUniqueID(), version, color, playerWorld.getWorldInfo().getWorldName(), p.posX, p.posY, p.posZ, loottable, trainer, lureRestriction);
        PokeStopRegistry registry = ConfigManagement.getInstance().loadConfig(PokeStopRegistry.class, Paths.get(AnotherPokeStop.MAIN_PATH + File.separator + "PokestopRegistry.yml"));
        AnotherPokeStop.getRegisteredPokeStops().put(pokestop.getUniqueID(), newPokeStopData);
        registry.registryList.add(newPokeStopData);
        AnotherPokeStop.getInstance().saveRegistry(registry);
    }
}

