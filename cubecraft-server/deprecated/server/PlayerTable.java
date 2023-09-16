package ink.flybird.cubecraft.server.server;

import ink.flybird.cubecraft.internal.entity.EntityPlayer;
import ink.flybird.fcommon.container.MultiMap;

import java.net.InetSocketAddress;
import java.util.HashSet;
import java.util.Set;

public class PlayerTable {
    public final MultiMap<String, EntityPlayer> uuid2player=new MultiMap<>();
    public final MultiMap<String, InetSocketAddress> uuid2address=new MultiMap<>();
    public final MultiMap<EntityPlayer,InetSocketAddress> player2address=new MultiMap<>();

    public String getPlayerUUID(EntityPlayer player){
        return uuid2player.of(player);
    }

    public EntityPlayer getPlayer(String uuid){
        return uuid2player.get(uuid);
    }

    public InetSocketAddress getAddr(String uuid){
        return this.uuid2address.get(uuid);
    }

    public String getUuid(InetSocketAddress addr){
        return this.uuid2address.of(addr);
    }

    public InetSocketAddress getAddr(EntityPlayer player){
        return this.player2address.get(player);
    }

    public EntityPlayer getPlayer(InetSocketAddress addr){
        return this.player2address.of(addr);
    }

    public void add(EntityPlayer player, String uuid, InetSocketAddress addr){
        this.uuid2player.put(uuid,player);
        this.uuid2address.put(uuid,addr);
        this.player2address.put(player,addr);
    }

    public void remove(String uuid){
        this.player2address.remove(this.uuid2player.get(uuid));
        this.uuid2player.remove(uuid);
        this.uuid2address.remove(uuid);
    }

    public void remove(EntityPlayer player){
        this.uuid2address.remove(this.uuid2player.of(player));
        this.uuid2player.remove(this.uuid2player.of(player));
        this.player2address.remove(player);
    }

    public void remove(InetSocketAddress addr){
        this.uuid2player.remove(this.uuid2address.of(addr));
        this.uuid2address.remove(this.uuid2address.of(addr));
        this.player2address.remove(this.player2address.of(addr));
    }

    public MultiMap<EntityPlayer, InetSocketAddress> getPlayer2address() {
        return player2address;
    }

    public MultiMap<String, InetSocketAddress> getUuid2address() {
        return uuid2address;
    }

    public MultiMap<String, EntityPlayer> getUuid2player() {
        return uuid2player;
    }


    public Set<String> getPlayerNames(){
        Set<String> set=new HashSet<>();
        for (EntityPlayer player:this.player2address.keySet()){
            set.add(player.getSession().getName());
        }
        return set;
    }

    public void clear() {
        this.uuid2player.clear();
        this.player2address.clear();
        this.uuid2address.clear();
    }
}
