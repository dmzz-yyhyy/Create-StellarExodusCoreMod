package io.nightfis.createstellarexoduscore.filter;

import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.Animal;

public class IsBabyFriendlyFilter extends TargetFilter {

    private static final String ID = "IsBabyFriendlyFilter";

    @Override
    public String getId() {
        return ID;
    }

    @Override
    public boolean match(LivingEntity entity, String arg) {
        return entity.getType().getCategory().isFriendly() && entity instanceof Animal animal && animal.isBaby();
    }

    @Override
    public Component title() {
        return Component.translatable("screen.createstellarexoduscore.auto_aim_turret.filter.is_baby_friendly");
    }
}
