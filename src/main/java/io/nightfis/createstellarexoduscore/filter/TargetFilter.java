package io.nightfis.createstellarexoduscore.filter;

import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;

import java.util.Collection;
import java.util.HashMap;

public abstract class TargetFilter {

    private static final TargetFilter EMPTY_FILTER = new TargetFilter() {
        @Override
        public String getId() {
            return "";
        }

        @Override
        public boolean match(LivingEntity entity, String arg) {
            return false;
        }

        @Override
        public Component title() {
            return Component.translatable("screen.createstellarexoduscore.auto_aim_turret.filter.unknown");
        }
    };

    private static final HashMap<String, TargetFilter> FILTER_MAP = new HashMap<>();

    public static void register(TargetFilter filter) {
        FILTER_MAP.put(filter.getId(), filter);
    }

    public static TargetFilter getFilter(String id) {
        return FILTER_MAP.getOrDefault(id, EMPTY_FILTER);
    }

    public static Collection<TargetFilter> all() {
        return FILTER_MAP.values();
    }

    public abstract String getId();

    public abstract boolean match(LivingEntity entity, String arg);

    public boolean needArg() {
        return false;
    }

    public abstract Component title();

    public String tooltip(String arg) {
        return "";
    }

    public static void init() {
        FILTER_MAP.clear();
        register(new IsPlayerFilter());
        register(new IsPlayerIdFilter());
        register(new IsPetFilter());
        register(new IsOnVehicleFilter());
        register(new IsFriendlyFilter());
        register(new IsEntityIdFilter());
        register(new IsBabyFriendlyFilter());
        register(new HasCustomNameFilter());
    }
}
