package me.sub.common.handlers;

import me.sub.Regeneration;
import me.sub.common.capability.CapabilityRegeneration;
import me.sub.common.capability.IRegeneration;
import me.sub.common.capability.RegenerationProvider;
import me.sub.common.init.RObjects;
import me.sub.config.RegenConfig;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.SoundCategory;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerChangedDimensionEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerRespawnEvent;

import static me.sub.common.capability.CapabilityRegeneration.REGEN_ID;

/**
 * Created by Sub
 * on 16/09/2018.
 */
@Mod.EventBusSubscriber(modid = Regeneration.MODID)
public class RegenerationHandler {

    @SubscribeEvent
    public static void onPlayerUpdate(LivingEvent.LivingUpdateEvent event) {
        if (event.getEntityLiving() instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer) event.getEntityLiving();
            IRegeneration handler = CapabilityRegeneration.get(player);
            handler.update();
        }
    }


    @SubscribeEvent
    public static void onAttachCapabilities(AttachCapabilitiesEvent<Entity> event) {
        Entity entity = event.getObject();
        if (entity instanceof EntityPlayer) {
            event.addCapability(REGEN_ID, new RegenerationProvider(new CapabilityRegeneration((EntityPlayer) event.getObject())));
        }
    }

    @SubscribeEvent
    public static void onPlayerClone(PlayerEvent.Clone event) {
        NBTTagCompound nbt = (NBTTagCompound) CapabilityRegeneration.CAPABILITY.getStorage().writeNBT(CapabilityRegeneration.CAPABILITY, event.getOriginal().getCapability(CapabilityRegeneration.CAPABILITY, null), null);
        CapabilityRegeneration.CAPABILITY.getStorage().readNBT(CapabilityRegeneration.CAPABILITY, event.getEntityPlayer().getCapability(CapabilityRegeneration.CAPABILITY, null), null, nbt);
    }

    @SubscribeEvent
    public static void playerTracking(PlayerEvent.StartTracking event) {
        if (event.getEntityPlayer().getCapability(CapabilityRegeneration.CAPABILITY, null) != null) {
            CapabilityRegeneration.get(event.getEntityPlayer()).sync();
        }
    }

    @SubscribeEvent
    public static void onPlayerRespawn(PlayerRespawnEvent event) {
        CapabilityRegeneration.get(event.player).sync();
    }

    @SubscribeEvent
    public static void onPlayerChangedDimension(PlayerChangedDimensionEvent event) {
        CapabilityRegeneration.get(event.player).sync();
    }

    @SubscribeEvent
    public static void onPlayerLoggedIn(PlayerLoggedInEvent event) {
        CapabilityRegeneration.get(event.player).sync();
    }

    @SubscribeEvent(priority = EventPriority.HIGH)
    public static void onHurt(LivingHurtEvent e) {
        if (!(e.getEntity() instanceof EntityPlayer))
            return;

        EntityPlayer player = (EntityPlayer) e.getEntity();
        if (player.getHealth() + player.getAbsorptionAmount() - e.getAmount() > 0 || !e.getEntity().hasCapability(CapabilityRegeneration.CAPABILITY, null) || !e.getEntity().getCapability(CapabilityRegeneration.CAPABILITY, null).isCapable())
            return;


        IRegeneration handler = CapabilityRegeneration.get(player);
        e.setCanceled(true);
        handler.setRegenerating(true);

        if (handler.isRegenerating() && handler.isInGracePeriod()) {
            player.world.playSound(null, player.posX, player.posY, player.posZ, RObjects.Sounds.HAND_GLOW, SoundCategory.PLAYERS, 1.0F, 1.0F);
            handler.setInGracePeriod(false);
            handler.setSolaceTicks(200);
        }
    }


    @SubscribeEvent
    public static void onLogin(PlayerLoggedInEvent e) {
        if (!RegenConfig.Regen.startAsTimelord || e.player.world.isRemote)
            return;

        NBTTagCompound nbt = e.player.getEntityData();
        boolean loggedInBefore = nbt.getBoolean("loggedInBefore");
        if (!loggedInBefore) {
            e.player.inventory.addItemStackToInventory(new ItemStack(RObjects.Items.FOB_WATCH));
            nbt.setBoolean("loggedInBefore", true);
        }
    }
}




