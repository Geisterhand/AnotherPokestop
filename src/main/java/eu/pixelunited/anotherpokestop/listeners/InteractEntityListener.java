package eu.pixelunited.anotherpokestop.listeners;

import com.pixelmonmod.pixelmon.api.events.drops.CustomDropsEvent;
import com.pixelmonmod.pixelmon.config.PixelmonItemsLures;
import com.pixelmonmod.pixelmon.entities.EntityPokestop;
import eu.pixelunited.anotherpokestop.AnotherPokeStop;
import eu.pixelunited.anotherpokestop.ConfigManagement;
import eu.pixelunited.anotherpokestop.config.lang.LangConfig;
import eu.pixelunited.anotherpokestop.config.mainConfig.AnotherPokeStopConfig;
import eu.pixelunited.anotherpokestop.config.PokeStopRegistry;
import eu.pixelunited.anotherpokestop.utils.*;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.EnumHand;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.io.File;
import java.nio.file.Paths;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class InteractEntityListener {

    final AnotherPokeStop _plugin;


    public InteractEntityListener(AnotherPokeStop plugin) {
        this._plugin = plugin;
    }

    @SubscribeEvent
    public void onEntityRightClick(PlayerInteractEvent.EntityInteract event) {

        AnotherPokeStopConfig _config = ConfigManagement.getInstance().loadConfig(AnotherPokeStopConfig.class, Paths.get(AnotherPokeStop.MAIN_PATH + File.separator + "AnotherPokeStop.yml"));
        LangConfig _lang = ConfigManagement.getInstance().loadConfig(LangConfig.class, Paths.get(AnotherPokeStop.MAIN_PATH + File.separator + "Lang.yml"));
        PokeStopRegistry _registry = ConfigManagement.getInstance().loadConfig(PokeStopRegistry.class, Paths.get(AnotherPokeStop.MAIN_PATH + File.separator + "PokestopRegistry.yml"));

        if (!(event.getTarget() instanceof EntityPokestop)) {
            return;
        }

        if (!(event.getEntityPlayer() instanceof EntityPlayerMP)) {
            return;
        }

        if(event.getHand() == EnumHand.OFF_HAND) {
            return;
        }

        EntityPlayerMP player = (EntityPlayerMP) event.getEntityPlayer();
        UUID pokeStopId = event.getTarget().getUniqueID();
        EntityPokestop pokestop = (EntityPokestop) event.getTarget();
        Item eventItem = event.getItemStack().getItem();

        if(_config.lureModules && !_config.blacklistLures.contains(pokestop.getEntityWorld().getWorldInfo().getWorldName())) {
            if(eventItem == Item.getByNameOrId("lure_shiny_strong") || eventItem == Item.getByNameOrId("lure_ha_strong") || eventItem == Item.getByNameOrId("lure_ha_weak") || eventItem == Item.getByNameOrId("lure_shiny_weak")) {
                return;
            }
            if (PixelmonItemsLures.strongLures.contains(eventItem)) {
                int index = PixelmonItemsLures.strongLures.indexOf(eventItem);
                String lureType = Utils.getLureType(Objects.requireNonNull(PixelmonItemsLures.strongLures.get(index).getRegistryName()).toString());
                String toggle = "Strong";
                if(!AnotherPokeStop.getRegisteredPokeStops().get(pokeStopId).getLureRestriction().contains(lureType.toUpperCase())) {
                    DialogueUtils.genLureDialogue(pokestop, player, lureType, event.getItemStack(), toggle)
                            .open(player);
                    return;
                }
            } else if(PixelmonItemsLures.weakLures.contains(eventItem)) {
                int index = PixelmonItemsLures.weakLures.indexOf(eventItem);
                String lureType = Utils.getLureType(Objects.requireNonNull(PixelmonItemsLures.weakLures.get(index).getRegistryName()).toString());
                String toggle = "Weak";
                if(!AnotherPokeStop.getRegisteredPokeStops().get(pokeStopId).getLureRestriction().contains(lureType.toUpperCase())) {
                    DialogueUtils.genLureDialogue(pokestop, player, lureType, event.getItemStack(), toggle)
                            .open(player);
                    return;
                }
            }
        }

        if(AnotherPokeStop.getCurrentEditor().containsKey(player.getUniqueID())) {
            if(!AnotherPokeStop.getRegisteredPokeStops().containsKey(pokeStopId)) {
                pokestop.setDead();
                player.sendMessage(Utils.toText("[&dAnotherPokeStop&r] &6Removed deprecated Pokestop."));
                AnotherPokeStop.getCurrentEditor().remove(player.getUniqueID());
                return;
            }

            for (int i = 0; i <= _registry.registryList.size(); i++) {
                    boolean containsKey = AnotherPokeStop.getRegisteredPokeStops().containsKey(_registry.registryList.get(i).getPokeStopUniqueId());
                    if (containsKey) {
                        EditUtils.editPokestop(AnotherPokeStop.getCurrentEditor().get(player.getUniqueID()).get(0), pokestop, player, i);
                        return;
                    }
            }
            return;
        }

        if(!AnotherPokeStop.getRegisteredPokeStops().containsKey(pokeStopId)) {
            return;
        }

        if (Utils.hasPermission(player,"anotherpokestop.claimpokestop") || Utils.hasPermission(player, "anotherpokestop.bypass")) {
            String lootTable = AnotherPokeStop.getRegisteredPokeStops().get(pokeStopId).getLoottable();
            boolean cooldown = Utils.claimable(player, pokeStopId, lootTable);
            if (cooldown || Utils.hasPermission(player, "anotherpokestop.bypass")) {
                AnotherPokeStop.getUsedPokestop().put(player.getUniqueID(), pokeStopId);

                if (_config.rocketEvent && !_config.blackListTrainer.contains(pokestop.getEntityWorld().getWorldInfo().getWorldName())) {
                    int rocketEvent = _config.rocketChance;
                    int rocketRoll = (int) (100 * Math.random() + 1);
                    if (rocketEvent >= rocketRoll) {
                        DialogueUtils.genRocketDialogue(pokeStopId, player, lootTable)
                                .open(player);
                        return;
                    }
                }

                RewardUtils.pokestopLoot(player, false, lootTable);
                List<ItemStack> lootList = AnotherPokeStop.getCurrenDisplayItems().get(player.getUniqueID());
                Utils.dropScreen(_lang.langDropMenu.header, _lang.langDropMenu.buttonText, player, lootList);

            } else if (AnotherPokeStop.getRegisteredPokeStops().containsKey(pokeStopId)) {
                player.sendMessage(Utils.toText(Placeholders.parseRemainingTimePlacerholder(_lang.langCooldown.cooldownText, player, pokeStopId, lootTable)));
            }
        } else {
            player.sendMessage(Utils.toText(_lang.noClaimPermission));
        }
    }

    @SubscribeEvent
    public void onDropClick(CustomDropsEvent.ClickDrop event) {

        EntityPlayerMP p = event.getPlayer();

        if(AnotherPokeStop.getCurrentDrops().containsKey(p.getUniqueID())) {

            int slotIndex = event.getIndex();

            if(!AnotherPokeStop.getCurrentCommandDrops().get(p.getUniqueID()).get(slotIndex).equals("Placeholder")) {

                MinecraftServer server = FMLCommonHandler.instance().getMinecraftServerInstance();
                server.getCommandManager().executeCommand(server, AnotherPokeStop.getCurrentCommandDrops().get(p.getUniqueID()).get(slotIndex));
            } else {
                p.inventory.addItemStackToInventory(AnotherPokeStop.getCurrentDrops().get(p.getUniqueID()).get(slotIndex));
            }
            AnotherPokeStop.getCurrentLootSize().replace(p.getUniqueID(), AnotherPokeStop.getCurrentLootSize().get(p.getUniqueID()) - 1);

            if(AnotherPokeStop.getCurrentLootSize().get(p.getUniqueID()) == 0) {
                AnotherPokeStop.getCurrentDrops().remove(p.getUniqueID());
                AnotherPokeStop.getCurrentCommandDrops().remove(p.getUniqueID());
                AnotherPokeStop.getCurrenDisplayItems().remove(p.getUniqueID());
                AnotherPokeStop.getCurrentLootSize().remove(p.getUniqueID());
            }
        }
    }


    @SubscribeEvent
    public void onCloseClick(CustomDropsEvent.ClickButton event) {
        AnotherPokeStopConfig _config = ConfigManagement.getInstance().loadConfig(AnotherPokeStopConfig.class, Paths.get(AnotherPokeStop.MAIN_PATH + File.separator + "AnotherPokeStop.yml"));


        EntityPlayerMP p = event.getPlayer();

        if(AnotherPokeStop.getCurrentDrops().containsKey(p.getUniqueID()) && _config.claimRewardsOnClose) {

            for(int i = 0; i < AnotherPokeStop.getCurrentLootSize().get(p.getUniqueID()); i++) {

                if(!AnotherPokeStop.getCurrentCommandDrops().get(p.getUniqueID()).get(i).equals("Placeholder")) {
                    MinecraftServer server = FMLCommonHandler.instance().getMinecraftServerInstance();
                    server.getCommandManager().executeCommand(server, AnotherPokeStop.getCurrentCommandDrops().get(p.getUniqueID()).get(i));
                }
                else {

                    p.inventory.addItemStackToInventory(AnotherPokeStop.getCurrentDrops().get(p.getUniqueID()).get(i));
                }
            }
            AnotherPokeStop.getCurrentDrops().remove(p.getUniqueID());
            AnotherPokeStop.getCurrentCommandDrops().remove(p.getUniqueID());
            AnotherPokeStop.getCurrenDisplayItems().remove(p.getUniqueID());
            AnotherPokeStop.getCurrentLootSize().remove(p.getUniqueID());
        }
    }
}
