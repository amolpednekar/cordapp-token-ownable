package com.template

import net.corda.core.node.services.Vault
import net.corda.core.node.services.queryBy
import net.corda.core.node.services.vault.QueryCriteria
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
        userNode.startFlow(issueTokenFlow).toCompletableFuture()
        network.runNetwork()

        print("TokenState issued")
        tokenIssuerNode.transaction {
            val queriedStates = tokenIssuerNode.services.vaultService.queryBy<TokenState>()
            if(queriedStates.states.size==1) print("Issuer node" + queriedStates.states.first().state.data) else print("Issuer empty")
        }

        userNode.transaction {
            val queriedStates = userNode.services.vaultService.queryBy<TokenState>()
            println("Receiver node: " + queriedStates.states.first().state.data)
        }

        println("TokenState transferred")
        val amountToTransfer = 10
        val tokenTransferFlow = TransferTokenFlow(userNode.info.legalIdentities.first(),amountToTransfer)
        tokenIssuerNode.startFlow(tokenTransferFlow).toCompletableFuture()
        network.runNetwork()

        tokenIssuerNode.transaction {
            val queriedStates = tokenIssuerNode.services.vaultService.queryBy<TokenState>()
            if(queriedStates.states.size==1) print("Issuer node: " + queriedStates.states.first().state.data) else print("Issuer empty")
        }

        userNode.transaction {
            val queriedStates = userNode.services.vaultService.queryBy<TokenState>(QueryCriteria.VaultQueryCriteria(Vault.StateStatus.ALL))
            print("Receiver node")
            for(states in queriedStates.states){
               print(states.state.data)
           }
        }

    }
}