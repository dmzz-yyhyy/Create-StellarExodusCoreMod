package io.nightfis.createstellarexoduscore.network;

import io.nightfis.createstellarexoduscore.inventory.AutoAimTurretMenu;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class AddAutoAimTurretFilterPacket {

    private final String id;
    private final String arg;

    public AddAutoAimTurretFilterPacket(String id, String arg) {
        this.id = id;
        this.arg = arg;
    }

    public static void encode(AddAutoAimTurretFilterPacket msg, FriendlyByteBuf buf) {
        buf.writeUtf(msg.id);
        buf.writeUtf(msg.arg);
    }

    public static AddAutoAimTurretFilterPacket decode(FriendlyByteBuf buf) {
        return new AddAutoAimTurretFilterPacket(buf.readUtf(), buf.readUtf());
    }

    public static void handle(AddAutoAimTurretFilterPacket msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayer player = ctx.get().getSender();
            if (player != null && player.containerMenu instanceof AutoAimTurretMenu menu) {
                menu.addFilter(msg.id, msg.arg);
            }
        });
        ctx.get().setPacketHandled(true);
    }
}
