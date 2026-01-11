package io.nightfis.createstellarexoduscore.filter;

import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;

public class IsEntityIdFilter extends TargetFilter {

    private static final String ID = "IsEntityIdFilter";

    @Override
    public String getId() {
        return ID;
    }

    @Override
    public boolean match(LivingEntity entity, String arg) {
        return entity.getType().getDescriptionId().equals(arg);
    }

    @Override
    public boolean needArg() {
        return true;
    }

    @Override
    public Component title() {
        return Component.translatable("screen.createstellarexoduscore.auto_aim_turret.filter.is_entity_id");
    }

    @Override
    public String tooltip(String arg) {
        return Component.translatable(arg).getString();
    }
}
