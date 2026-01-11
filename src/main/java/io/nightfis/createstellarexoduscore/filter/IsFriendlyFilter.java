package io.nightfis.createstellarexoduscore.filter;

import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;

public class IsFriendlyFilter extends TargetFilter {

    private static final String ID = "IsFriendlyFilter";

    @Override
    public String getId() {
        return ID;
    }

    @Override
    public boolean match(LivingEntity entity, String arg) {
        return entity.getType().getCategory().isFriendly();
    }

    @Override
    public Component title() {
        return Component.translatable("screen.createstellarexoduscore.auto_aim_turret.filter.is_friendly");
    }
}
