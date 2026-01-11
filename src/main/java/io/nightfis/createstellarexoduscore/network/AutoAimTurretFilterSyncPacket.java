package io.nightfis.createstellarexoduscore.network;

import io.nightfis.createstellarexoduscore.client.gui.screen.AutoAimTurretScreen;
import io.nightfis.createstellarexoduscore.filter.TargetFilter;
import it.unimi.dsi.fastutil.Pair;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class AutoAimTurretFilterSyncPacket {

    private final List<Pair<TargetFilter, String>> filters;

    public AutoAimTurretFilterSyncPacket(List<Pair<TargetFilter, String>> filters) {
        this.filters = new ArrayList<>(filters);
    }

    public static void encode(AutoAimTurretFilterSyncPacket msg, FriendlyByteBuf buf) {
        buf.writeCollection(msg.filters.stream().map(it -> it.left().getId()).toList(), FriendlyByteBuf::writeUtf);
        buf.writeCollection(msg.filters.stream().map(Pair::right).toList(), FriendlyByteBuf::writeUtf);
    }

    public static AutoAimTurretFilterSyncPacket decode(FriendlyByteBuf buf) {
        List<String> ids = buf.readList(FriendlyByteBuf::readUtf);
        List<String> args = buf.readList(FriendlyByteBuf::readUtf);
        ArrayList<Pair<TargetFilter, String>> filters = new ArrayList<>();
        for (int i = 0; i < args.size() && i < ids.size(); i++) {
            filters.add(Pair.of(TargetFilter.getFilter(ids.get(i)), args.get(i)));
        }
        return new AutoAimTurretFilterSyncPacket(filters);
    }

    public static void handle(AutoAimTurretFilterSyncPacket msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            if (Minecraft.getInstance().screen instanceof AutoAimTurretScreen screen) {
                screen.handleSync(msg.filters);
            }
        });
        ctx.get().setPacketHandled(true);
    }
}
