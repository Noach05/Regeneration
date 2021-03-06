package me.swirtzly.regen.common.traits;

import me.swirtzly.regen.common.regen.IRegen;
import net.minecraft.util.ResourceLocation;
import org.jetbrains.annotations.Nullable;

public class TraitBase extends Traits.ITrait {
    private ResourceLocation LOCATION;

    public TraitBase(ResourceLocation resourceLocation) {
        LOCATION = resourceLocation;
    }

    @Override
    public void apply(IRegen data) {

    }

    @Override
    public void reset(IRegen data) {

    }

    @Override
    public void tick(IRegen data) {

    }

    @Override
    public boolean isPlayerOnly() {
        return false;
    }

    @Override
    public Traits.ITrait setRegistryName(ResourceLocation name) {
        return this;
    }

    @Nullable
    @Override
    public ResourceLocation getRegistryName() {
        return LOCATION;
    }

    @Override
    public Class<Traits.ITrait> getRegistryType() {
        return Traits.ITrait.class;
    }
}
