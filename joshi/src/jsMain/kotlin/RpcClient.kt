package com.zenmo.joshi

import io.ktor.client.*
import kotlinx.rpc.RpcClient
import kotlinx.rpc.krpc.ktor.client.installKrpc
import kotlinx.rpc.krpc.ktor.client.rpc
import kotlinx.rpc.krpc.serialization.json.json

/**
 * This should only be called once so all services share the same websocket.
 */
fun createRpcClient(
    rpcUrl: String = js("import.meta.env.VITE_ZTOR_RPC_URL") ?: "ws://localhost:8082/rpc"
): RpcClient {
    val ktorClient = HttpClient {
        installKrpc {
            serialization {
                json()
            }
        }
    }

    return ktorClient.rpc(rpcUrl)
}

val rpcClient by lazy { createRpcClient() }
