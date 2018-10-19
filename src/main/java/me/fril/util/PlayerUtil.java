package me.fril.util;

import me.fril.client.sound.MovingSoundPlayer;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.ai.*;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemArmor;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * Created by Sub
 * on 20/09/2018.
 */
public class PlayerUtil {

    public static void sendMessage(EntityPlayer player, String message, boolean hotBar) {
        if (!player.world.isRemote) {
            player.sendStatusMessage(new TextComponentTranslation(message), hotBar);
        }
    }

    public static void sendMessage(EntityPlayer player, TextComponentTranslation translation, boolean hotBar) {
        if (!player.world.isRemote) {
            player.sendStatusMessage(translation, hotBar);
        }
    }


    @SideOnly(Side.CLIENT)
    public static void playMovingSound(EntityPlayer player, SoundEvent soundIn, SoundCategory categoryIn, boolean playerOnly) {
        if (playerOnly) {
            if (player.getUniqueID() == Minecraft.getMinecraft().player.getUniqueID()) {
                return;
            }
        }
        Minecraft.getMinecraft().getSoundHandler().playSound(new MovingSoundPlayer(player, soundIn, categoryIn));
    }

    public static boolean canEntityAttack(Entity entity) {
        if (entity instanceof EntityLiving) {
            EntityLiving ent = (EntityLiving) entity;
            for (EntityAITasks.EntityAITaskEntry task : ent.tasks.taskEntries) {
                if (task.action instanceof EntityAIAttackMelee || task.action instanceof EntityAIAttackRanged || task.action instanceof EntityAIAttackRangedBow
                        || task.action instanceof EntityAINearestAttackableTarget || task.action instanceof EntityAIZombieAttack || task.action instanceof EntityAIOwnerHurtByTarget)
                    return true;
            }
        }
        return false;
    }

    public static void damagePlayerArmor(EntityPlayerMP playerMP) {
        for (EntityEquipmentSlot type : EntityEquipmentSlot.values()) {
            if (!type.equals(EntityEquipmentSlot.MAINHAND) && !type.equals(EntityEquipmentSlot.OFFHAND)) {
                if (playerMP.getItemStackFromSlot(type).getItem() instanceof ItemArmor) {
                    ItemArmor armor = (ItemArmor) playerMP.getItemStackFromSlot(type).getItem();
                    armor.setDamage(playerMP.getItemStackFromSlot(type), playerMP.getItemStackFromSlot(type).getItemDamage() - playerMP.world.rand.nextInt(3));
                }
            }
        }
    }

}
