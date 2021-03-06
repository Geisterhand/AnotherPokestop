package eu.pixelunited.anotherpokestop.lureModule;

import com.pixelmonmod.pixelmon.entities.EntityPokestop;
import eu.pixelunited.anotherpokestop.AnotherPokeStop;
import eu.pixelunited.anotherpokestop.config.lureModule.LureModuleConfig;
import eu.pixelunited.anotherpokestop.config.lureModule.LurePokeSettings;
import eu.pixelunited.anotherpokestop.utils.EconomyUtils;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.fml.common.FMLCommonHandler;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class LureScheduler {



    public static void startScheduler(LureModuleConfig settings, EntityPokestop pokestop, EntityPlayerMP player, String toggle, int cost, List<LurePokeSettings> list) {


        EconomyUtils.takeMoney(player, cost);

        if(AnotherPokeStop.getConfig().particles) {
            Animation.startAnimation(settings, pokestop);
        }
        Timer timer = new Timer();
        int interval;

        if (toggle.equals("Weak")) {
            interval = settings.spawnTicksWeak;
        } else {
            interval = settings.spawnTicksStrong;
        }
        final int[] scheduledTick = {0};

        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                if(scheduledTick[0] >= interval) {
                    timer.cancel();
                    timer.purge();
                    AnotherPokeStop.getInstance()._activeLure.remove(pokestop.getUniqueID());
                }
                else if(!AnotherPokeStop.getInstance()._activeLure.contains(pokestop.getUniqueID())) {
                    timer.cancel();
                    timer.purge();

                } else {
                    int doSpawn = (int) (Math.random() * 100) + 1;

                    if (doSpawn <= settings.spawnChance) {
                        int raritySum = list.stream().mapToInt(pokeSettings -> pokeSettings.rarity).sum();
                        for (int i = 0; i < settings.spawnQuantity; i++) {
                            int pickedRarity = (int) (raritySum * Math.random());
                            int index = -1;

                            for (int b = 0; b <= pickedRarity; ) {
                                index = index + 1;
                                b = list.get(index).rarity + b;

                            }
                            LureScheduler.runSync(settings, pokestop, index);
                        }
                    }
                    scheduledTick[0] = scheduledTick[0] + 1;
                }
            }
        },0, settings.spawnInterval* 1000L);
    }

    private static void runSync(LureModuleConfig settings, EntityPokestop pokestop, int index) {
        MinecraftServer server = FMLCommonHandler.instance().getMinecraftServerInstance();
        server.addScheduledTask(() -> PokeFactory.spawnLuredPokemon(settings.pokeList.get(index), pokestop));
    }



}
