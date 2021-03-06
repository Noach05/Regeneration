package me.swirtzly.regen.data;

import me.swirtzly.regen.common.objects.RBlocks;
import me.swirtzly.regen.common.objects.REntities;
import me.swirtzly.regen.common.objects.RItems;
import me.swirtzly.regen.common.regen.transitions.TransitionTypes;
import me.swirtzly.regen.common.traits.Traits;
import me.swirtzly.regen.util.PlayerUtil;
import me.swirtzly.regen.util.RConstants;
import net.minecraft.data.DataGenerator;
import net.minecraftforge.common.data.LanguageProvider;

public class EnglishLangGen extends LanguageProvider {

    public EnglishLangGen(DataGenerator gen) {
        super(gen, RConstants.MODID, "en_us");
    }

    @Override
    protected void addTranslations() {
        // === Damages Sources ===
        add("regen.source.regen_energy", "%s was blasted by Regeneration Energy!");
        add("regen.source.regen_heal", "%s died by donating too much Regeneration energy...");
        add("regen.source.regen_crit", "%s died from holding in their regeneration for too long");
        add("regen.source.theft", "%s had their body stolen!");
        add("regen.source.lindos", "%s consumed lindos hormones! They are reborn!");
        add("regen.source.regen_killed", "%s was killed mid-regeneration...");
        add("regen.source.forced", "%s forced themselves to regenerate!");

        //Block
        add(RBlocks.BIO_CONTAINER.get(), "Bio-Container");

        //Item Group
        add("itemGroup.regen", "Regeneration");

        //Sounds
        for (int i = 0; i < 7; i++) {
            add("regen.sound.regeneration_" + i, "Regeneration");
        }
        add("regen.subtitles.regen_bubble", "Jar bubbles");
        add("regen.subtitles.hum", "Calming hums");
        add("regen.subtitles.fob_watch_dialogue", "Fob Watch Speaks");
        add("regen.subtitles.critical_stage", "Critical Condition");
        add("regen.subtitles.heart_beat", "Timelord Heartbeat");
        add("regen.subtitles.hand_glow", "Glowing Hands");
        add("regen.subtitles.fob_watch", "Ticking");
        add("regen.subtitles.regen_breath", "Regeneration exhale");
        add("regen.subtitles.alarm", "Regeneration Alarm");

        //Items
        add(RItems.FOB.get(), "Fob watch");

        //Messages
        add("regen.messages.regen_death_msg", "%s is regenerating...");
        add("regen.messages.now_timelord", "You are now a Time lord!");
        add("regen.messages.gained_regens", "Recieved Regenerations!");
        add("regen.messages.new_skin", "Your skin will change next Regeneration!");
        add("regen.messages.transfer.success", "You've transferred one of your regenerations into the watch");
        add("regen.messages.transfer.full_watch", "You can't store anymore regenerations in this watch!");
        add("regen.messages.transfer.empty_watch", "This watch is empty!");
        add("regen.messages.transfer.max_regens", "You already have the maximum number of regenerations");
        add("regen.messages.transfer.no_regens", "You don't have any regenerations left to transfer");
        add("regen.messages.jar_amount", "This container currently contains %s Lindos!");
        add("regen.messages.jar_inuse", "This is in use!");
        add("regen.messages.regen_chat_message", "%s is regenerating...");
        add("regen.messages.warning.grace", "You are in a state of grace, press %s to regenerate!");
        add("regen.messages.warning.grace_critical", "You are near death, press %s to regenerate!");
        add("regen.messages.regen_delayed", "Regeneration delayed!");
        add("regen.messages.regen_warning", "Punch a block to delay your regeneration further");
        add("regen.messages.jar", "You have gained a Regeneration from harvested lindos!");
        add("regen.messages.jar_not_enough", "There is not enough Lindos in this jar! [100 Lindos = 1 free Regeneration]");
        add("regen.messages.jar_no_break", "You cannot break this Jar while it has %s Lindos Energy!");
        add("regen.messages.not_alive", "You cannot transfer regens in this state!");
        add("regen.messages.item_taken_regens", "You have gained %s regenerations from %s");
        add("regen.messages.cannot_use", "You cannot use this right now!");
        add("regen.messages.healed", "You have given %s some of your Regeneration Energy!");
        add("regen.messages.reduced_dmg", "Reduced Damage due to Post Regen State!");
        add("regen.messages.fall_dmg", "Fall damage reduced due to post regen state! Although you're gonna feel sick..");
        add("regen.messages.post_ended", "Your Regeneration process is now entirely completed!");
        add("regen.messages.fast_forward_cmd_fail", "Nothing to fast forward to.");
        add("regen.messages.new_trait", "New Trait: %s");
        add("regen.messages.cant_glow", "You cannot activate a glowing hand without being in a grace period");

        // === Toasts ===
        add("regen.toast.regenerated", "You have regenerated!");
        add("regen.toast.regenerations_left", "%s regenerations left");
        add("regen.toast.enter_critical", "You are going critical");
        add("regen.toast.enter_critical.sub", "%s minutes left...");
        add("regen.toast.timelord", "You are now a Time lord!");
        add("regen.toast.to_use", "You have %s lives now!");

        // === GUI Elements ===
        add("regen.gui.red", "Red: ");
        add("regen.gui.green", "Green: ");
        add("regen.gui.blue", "Blue: ");
        add("regen.gui.primary", "Primary");
        add("regen.gui.secondary", "Secondary");
        add("regen.gui.undo", "Undo");
        add("regen.gui.regen_type", "Type: %s");
        add("regen.gui.close", "Close");
        add("regen.gui.input_color", "Hex");
        add("regen.gui.previous", "<");
        add("regen.gui.next", ">");
        add("regen.gui.default", "Default");
        add("regen.gui.open_folder", "Skin Dir");
        add("regen.gui.skin_choice", "Skin Settings");
        add("regen.gui.save", "Save");
        add("regen.gui.next_incarnation", "Select next incarnation");
        add("regen.gui.reset_skin", "Reset Skin");
        add("regen.gui.infinite_regenerations", "Infinite Regeneration Mode");
        add("regen.gui.remaining_regens.status", "Remaining Regenerations: %s");
        add("regen.gui.color_gui", "Color selection");
        add("regen.gui.current_skin", "Select next Incarnation");
        add("regen.gui.skintype", "Preference: %s");
        add("regen.gui.back", "Back");
        add("regen.gui.preferences", "Preferences");

        for (PlayerUtil.SkinType value : PlayerUtil.SkinType.values()) {
            add("regeneration.skin_type." + value.name().toLowerCase(), grammerNazi(value.name().toLowerCase()));
        }


        // === Regen ===
        add(TransitionTypes.ENDER_DRAGON.get().getTranslation().getKey(), "Ender-Dragon");
        add(TransitionTypes.SPARKLE.get().getTranslation().getKey(), "Sparkle");
        add(TransitionTypes.WATCHER.get().getTranslation().getKey(), "Watcher");
        add(TransitionTypes.FIERY.get().getTranslation().getKey(), "Fiery");
        add(TransitionTypes.TROUGHTON.get().getTranslation().getKey(), "Troughton");

        //Entity
        add(REntities.TIMELORD.get(), "Timelord");
        add(REntities.ITEM_OVERRIDE_ENTITY_TYPE.get(), "Item");

        //Traits
        add(Traits.BORING.get().getTranslation().getKey(), "Boring");
        add(Traits.BORING.get().getDescription().getKey(), "Plain Human!");

        add(Traits.QUICK.get().getTranslation().getKey(), "Quick");
        add(Traits.QUICK.get().getDescription().getKey(), "Sprint Faster!");

        add(Traits.SWIM_SPEED.get().getTranslation().getKey(), "Mermaid");
        add(Traits.SWIM_SPEED.get().getDescription().getKey(), "Swim Faster!");

        add(Traits.FAST_MINE.get().getTranslation().getKey(), "Fast Mine");
        add(Traits.FAST_MINE.get().getDescription().getKey(), "Mine Faster!");

        add(Traits.SMART.get().getTranslation().getKey(), "Smart");
        add(Traits.SMART.get().getDescription().getKey(), "More XP Pickup!");

        add(Traits.LONG_ARMS.get().getTranslation().getKey(), "Long Arms");
        add(Traits.LONG_ARMS.get().getDescription().getKey(), "Reach further!");

        add(Traits.STRONG.get().getTranslation().getKey(), "Strong");
        add(Traits.STRONG.get().getDescription().getKey(), "Punch Harder!");

        add(Traits.KNOCKBACK.get().getTranslation().getKey(), "KnockBack");
        add(Traits.KNOCKBACK.get().getDescription().getKey(), "Don't get flung back!");

        add(Traits.FISH.get().getTranslation().getKey(), "Fish");
        add(Traits.FISH.get().getDescription().getKey(), "Infinite Oxygen under water");

        add(Traits.LEAP.get().getTranslation().getKey(), "Leap");
        add(Traits.LEAP.get().getDescription().getKey(), "Jump a little higher");
    }


    public String grammerNazi(String text) {
        String firstLetter = text.substring(0, 1).toUpperCase();
        return firstLetter + text.substring(1);
    }
}
