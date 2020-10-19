package me.swirtzly.regen.common.traits;

import com.google.common.collect.Iterables;
import me.swirtzly.regen.common.regen.IRegen;
import me.swirtzly.regen.util.RConstants;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Random;
import java.util.function.Supplier;

@Mod.EventBusSubscriber(modid = RConstants.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class Traits extends ForgeRegistryEntry<Traits> {


    public static final Traits QUICK = addTrait(new Traits(TraitQuick::new));
    public static final Traits BORING = addTrait(new Traits(() -> new TraitBase(new ResourceLocation(RConstants.MODID, "boring"))));
    public static final Traits SMART = addTrait(new Traits(() -> new TraitBase(new ResourceLocation(RConstants.MODID, "smart"))));
    public static final Traits FAST_MINE = addTrait(new Traits(() -> new TraitBase(new ResourceLocation(RConstants.MODID, "fast_mine"))));
    public static final Traits KNOCKBACK = addTrait(new Traits(() -> new TraitBase(new ResourceLocation(RConstants.MODID, "knockback"))));
    public static final Traits LONG_ARMS = addTrait(new Traits(TraitLongArms::new));
    public static final Traits STRONG = addTrait(new Traits(TraitStrong::new));
    public static final Traits SWIM_SPEED = addTrait(new Traits(TraitSwimSpeed::new));

    private static final ArrayList<Traits> TRAITS = new ArrayList<>();

    public static Traits addTrait(Traits traits){
        if (TRAITS != null) {
            TRAITS.add(traits);
        }
        return traits;
    }

    //Create Registry
    public static IForgeRegistry<ITrait> REGISTRY;

    @SubscribeEvent
    public static void onRegisterNewRegistries(RegistryEvent.NewRegistry e) {
        REGISTRY = new RegistryBuilder<ITrait>().setName(new ResourceLocation(RConstants.MODID, "regeneration_traits")).setType(ITrait.class).setIDRange(0, 2048).create();
    }

    @SubscribeEvent
    public static void onRegisterTypes(RegistryEvent.Register<ITrait> e) {
       e.getRegistry().registerAll(QUICK.get(), BORING.get(), SMART.get(), FAST_MINE.get(), LONG_ARMS.get(), STRONG.get(), SWIM_SPEED.get(), KNOCKBACK.get());
    }

    private Supplier<ITrait> supplier;

    public Traits(Supplier<ITrait> supplier) {
        this.supplier = supplier;
        this.setRegistryName(supplier.get().getRegistryName());
    }

    public static ITrait fromID(String location) {
        ResourceLocation resourceLocation = new ResourceLocation(location);
        ITrait value = REGISTRY.getValue(resourceLocation);

        if(value != null){
            return value;
        }
        return Traits.BORING.get();
    }


    public static ITrait getRandomTrait(Random random, boolean isMob) {
        Collection<ITrait> value = REGISTRY.getValues();
        //value.removeIf(trait -> trait.isPlayerOnly() && isMob);
        int i = random.nextInt(value.size());
        return Iterables.get(value, i);
    }


    public ITrait get() {
        return this.supplier.get();
    }


    //Base
    public static abstract class ITrait implements IForgeRegistryEntry<ITrait> {
        public abstract void apply(IRegen data);

        public abstract void reset(IRegen data);

        public abstract void tick(IRegen data);

        public TranslationTextComponent getTranslation() {
            ResourceLocation regName = getRegistryName();
            return new TranslationTextComponent("trait." + regName.getNamespace() + "." + regName.getPath());
        }

        public TranslationTextComponent getDescription() {
            ResourceLocation regName = getRegistryName();
            return new TranslationTextComponent("trait." + regName.getNamespace() + "." + regName.getPath() + ".description");
        }

        public abstract boolean isPlayerOnly();

        @Override
        public Traits.ITrait setRegistryName(ResourceLocation name) {
            return this;
        }

        @Override
        public Class<ITrait> getRegistryType() {
            return ITrait.class;
        }
    }

}
