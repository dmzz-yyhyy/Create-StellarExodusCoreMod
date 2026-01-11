package io.nightfis.createstellarexoduscore.filter;

import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.TamableAnimal;

public class IsPetFilter extends TargetFilter {

    private static final String ID = "IsPetFilter";

    @Override
    public String getId() {
        return ID;
    }

    @Override
    public boolean match(LivingEntity entity, String arg) {
        return entity instanceof TamableAnimal tamableAnimal && tamableAnimal.getOwner() != null;
    }

    @Override
    public Component title() {
        return Component.translatable("screen.createstellarexoduscore.auto_aim_turret.filter.is_pet");
    }
}
