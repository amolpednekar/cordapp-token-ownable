package com.template

import net.corda.client.rpc.CordaRPCClient
import net.corda.core.utilities.NetworkHostAndPort.Companion.parse
import net.corda.core.utilities.loggerFor

/**
 * Connects to a Corda node via RPC and performs RPC operations on the node.
 *
 * The RPC connection is configured using command line arguments.
 */
fun main(args: Array<String>) = Client().main(args)

public class Client {
    companion object {
        val logger = loggerFor<Client>()
    }

    fun main(args: Array<String>) {
        // Create an RPC connection to the node.
        require(args.size == 2) { "Usage: Client <node address> <rpc username> <rpc password>" }

        // Create a connection to PartyA and PartyB.
        val (issuerProxy, receiverProxy) = args.map { arg ->
            val nodeAddress = parse(arg)
            val client = CordaRPCClient(nodeAddress)
            client.start("user1", "test").proxy
        }

        val receiverIdentity = receiverProxy.nodeInfo().legalIdentities.first()

        println("\n\nStarted IssueTokenFlow, issuing 200 tokens.")
        issuerProxy.startFlowDynamic(IssueTokenFlow::class.java, 200).returnValue.toCompletableFuture().get()
        println("\n\nIssuer's vault " + issuerProxy.vaultQuery(TokenState::class.java).states)

        println("\n\nStarted TransferTokenFlow, transferring 100 and 50 tokens to receiver")
        issuerProxy.startFlowDynamic(TransferTokenFlow::class.java, receiverIdentity, 100).returnValue.toCompletableFuture().get();
        issuerProxy.startFlowDynamic(TransferTokenFlow::class.java, receiverIdentity, 50).returnValue.toCompletableFuture().get();

        println("\n\nIssuer's vault " + issuerProxy.vaultQuery(TokenState::class.java).states)
        println("\n\nReceiver's vault " + receiverProxy.vaultQuery(TokenState::class.java).states)

        println("\n\nStarted CombineTokensFlow, combining all of receiver's tokens.")
        receiverProxy.startFlowDynamic(CombineTokensFlow::class.java).returnValue.toCompletableFuture().get();
        println("\n\nReceiver's vault " + receiverProxy.vaultQuery(TokenState::class.java).states)
    }
}