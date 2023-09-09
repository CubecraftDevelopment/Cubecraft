package io.flybird.cubecraft.internal.network;

public interface NetHandlerType {
    String SERVER_DATA_FETCH = "cubecraft:server_data_fetch";
    String SERVER_CONNECTION = "cubecraft:server_connection";
    String SERVER_WORLD_LISTENER = "cubecraft:server_world_listener";
    String SERVER_PING="cubecraft:server_ping";

    String CLIENT_WORLD_LISTENER = "cubecraft:client_world_listener";
    String CLIENT_CONNECTION = "cubecraft:client_connection";
    String CLIENT_DATA_RECEIVE = "cubecraft:client_data_receive";



}