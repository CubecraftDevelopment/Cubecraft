package io.flybird.cubecraft.server.internal.network;

import io.flybird.cubecraft.auth.SessionService;
import io.flybird.cubecraft.internal.entity.EntityPlayer;
import io.flybird.cubecraft.internal.network.packet.join.PacketPlayerJoinRequest;
import io.flybird.cubecraft.internal.network.packet.join.PacketPlayerJoinResponse;
import io.flybird.cubecraft.net.NetHandlerContext;
import io.flybird.cubecraft.net.packet.PacketListener;
import io.flybird.cubecraft.register.SharedContext;
import io.flybird.cubecraft.server.ServerRegistries;
import io.flybird.cubecraft.server.event.join.PlayerLoginEvent;
import io.flybird.cubecraft.server.net.ServerNetHandler;

public class ServerHandlerPlayerJoin extends ServerNetHandler {

    @PacketListener
    public void onJoinRequest(PacketPlayerJoinRequest packet, NetHandlerContext ctx) {
        SessionService service = SharedContext.SESSION_SERVICE.get(packet.getSession().getType());
        if (service == null) {
            this.rejectPlayerJoin("join.refuse.no_session_type",ctx);
            return;
        }
        if (!service.validSession(packet.getSession())) {
            this.rejectPlayerJoin("join.refuse.session_invalid",ctx);
        }
        PlayerLoginEvent event = new PlayerLoginEvent(packet.getSession());
        this.server.getEventBus().callEvent(event);
        if (!event.isAllow()) {
            this.rejectPlayerJoin("join.refuse.session_not_allow",ctx);
            return;
        }
        ctx.sendPacket(PacketPlayerJoinResponse.accept());
        String uid = service.genUUID(packet.getSession());
        EntityPlayer p = new EntityPlayer(null, packet.getSession());
        ServerRegistries.SERVER.getPlayers().add(p, uid, ctx.from());

        ctx.sendPacket(new PacketPlayerJoinResponse("__ACCEPT__"));
    }

    public void rejectPlayerJoin(String reason, NetHandlerContext ctx) {
        ctx.sendPacket(new PacketPlayerJoinResponse(reason));
        ctx.closeConnection();
    }


}
