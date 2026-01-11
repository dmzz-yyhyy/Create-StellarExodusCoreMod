package io.nightfis.createstellarexoduscore.filter;

import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

public class IsPlayerIdFilter extends TargetFilter {

    private static final String ID = "IsPlayerIdFilter";

    @Override
    public String getId() {
        return ID;
    }

    @Override
    public boolean match(LivingEntity entity, String arg) {
        return entity instanceof Player player && player.getName().getString().equals(arg);
    }

    @Override
    public boolean needArg() {
        return true;
    }

    @Override
    public Component title() {
        return Component.translatable("screen.createstellarexoduscore.auto_aim_turret.filter.is_player_id");
    }

    @Override
    public String tooltip(String arg) {
        return arg;
    }
}
