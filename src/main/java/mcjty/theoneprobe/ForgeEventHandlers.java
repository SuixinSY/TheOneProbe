package mcjty.theoneprobe;

import mcjty.theoneprobe.config.Config;
import mcjty.theoneprobe.items.ModItems;
import mcjty.theoneprobe.playerdata.PlayerGotNote;
import mcjty.theoneprobe.playerdata.PlayerProperties;
import mcjty.theoneprobe.playerdata.PropertiesDispatcher;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class ForgeEventHandlers {


    @SubscribeEvent
    public void registerItems(RegistryEvent.Register<Item> event) {
        ModItems.init();

        event.getRegistry().register(ModItems.probe);
        event.getRegistry().register(ModItems.creativeProbe);
        event.getRegistry().register(ModItems.probeNote);

        event.getRegistry().register(ModItems.diamondHelmetProbe);
        event.getRegistry().register(ModItems.goldHelmetProbe);
        event.getRegistry().register(ModItems.ironHelmetProbe);

        if (TheOneProbe.baubles) {
            event.getRegistry().register(ModItems.probeGoggles);
        }
    }

    @SubscribeEvent
    public void onConfigChanged(ConfigChangedEvent.OnConfigChangedEvent event) {
        Config.setupStyleConfig(TheOneProbe.config);
        Config.updateDefaultOverlayStyle();

        if (TheOneProbe.config.hasChanged()) {
            TheOneProbe.config.save();
        }
    }

    @SubscribeEvent
    public void onEntityConstructing(AttachCapabilitiesEvent<Entity> event){
        if (event.getObject() instanceof EntityPlayer) {
            if (!event.getObject().hasCapability(PlayerProperties.PLAYER_GOT_NOTE, null)) {
                event.addCapability(new ResourceLocation(TheOneProbe.MODID, "Properties"), new PropertiesDispatcher());
            }
        }
    }

    @SubscribeEvent
    public void onPlayerCloned(PlayerEvent.Clone event) {
        if (event.isWasDeath()) {
            // We need to copyFrom the capabilities
            if (event.getOriginal().hasCapability(PlayerProperties.PLAYER_GOT_NOTE, null)) {
                PlayerGotNote oldStore = event.getOriginal().getCapability(PlayerProperties.PLAYER_GOT_NOTE, null);
                PlayerGotNote newStore = PlayerProperties.getPlayerGotNote(event.getEntityPlayer());
                newStore.copyFrom(oldStore);
            }
        }
    }

    @SubscribeEvent
    public void onPlayerLoggedIn(net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent event) {
        if (Config.spawnNote) {
            PlayerGotNote note = PlayerProperties.getPlayerGotNote(event.player);
            if (!note.isPlayerGotNote()) {
                boolean success = event.player.inventory.addItemStackToInventory(new ItemStack(ModItems.probeNote));
                if (success) {
                    note.setPlayerGotNote(true);
                }
            }
        }
    }
}