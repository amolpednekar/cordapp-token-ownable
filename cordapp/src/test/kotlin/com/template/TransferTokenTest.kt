package com.template

import net.corda.core.node.services.queryBy
import net.corda.testing.node.MockNetwork
import org.junit.After
import org.junit.Before
import org.junit.Test

class TransferTokenTest{
    private val network = MockNetwork(listOf("com.template"))
    private val tokenIssuerNode = network.createPartyNode()
    private val userNode = network.createPartyNode()

    @Before
    fun setup() = network.runNetwork()

    @After
    fun tearDown() = network.stopNodes()

    @Test
    fun `Transfer the token`(){
        val issueTokenFlow = IssueTokenFlow(100)
        tokenIssuerNode.startFlow(issueTokenFlow).toCompletableFuture()
        network.runNetwork()

        print("TokenState issued")
        tokenIssuerNode.transaction {
            val queriedStates = tokenIssuerNode.services.vaultService.queryBy<TokenState>()
            if(queriedStates.states.size==1) print("Issuer node" + queriedStates.states.first().state.data) else print("tokenIssuerNode empty")
        }

        userNode.transaction {
            val queriedStates = userNode.services.vaultService.queryBy<TokenState>()
            if(queriedStates.states.size==1) print("Receiver node: " + queriedStates.states.first().state.data) else print("userNode empty")
        }

        print("TokenState transferred")
        val amountToTransfer = 200
        val tokenTransferFlow = TransferTokenFlow(userNode.info.legalIdentities.first(),amountToTransfer)
        tokenIssuerNode.startFlow(tokenTransferFlow).toCompletableFuture()
        network.runNetwork()

        tokenIssuerNode.transaction {
            val queriedStates = tokenIssuerNode.services.vaultService.queryBy<TokenState>()
            if(queriedStates.states.size==1) print("Issuer node" + queriedStates.states.first().state.data) else print("tokenIssuerNode empty")
        }

        userNode.transaction {
            val queriedStates = userNode.services.vaultService.queryBy<TokenState>()
            if(queriedStates.states.size==1) print("Receiver node: " + queriedStates.states.first().state.data) else print("userNode empty")
        }

    }
}