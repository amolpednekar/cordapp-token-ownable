package com.template

import net.corda.core.node.services.Vault
import net.corda.core.node.services.queryBy
import net.corda.core.node.services.vault.QueryCriteria
import net.corda.testing.node.MockNetwork
import org.junit.After
import org.junit.Before
import org.junit.Test
import kotlin.test.assert

class TransferTokenTest{
    private val network = MockNetwork(listOf("com.template"))
    private val tokenIssuerNode = network.createPartyNode()
    private val receiverNode = network.createPartyNode()

    @Before
    fun setup() = network.runNetwork()

    @After
    fun tearDown() = network.stopNodes()

    @Test
    fun `Transfer the token`(){
        // Pre-requisite: Issue the token
        val amountToIssue = 100
        val amountToTransfer = 10
        val issueTokenFlow = IssueTokenFlow(amountToIssue)
        tokenIssuerNode.startFlow(issueTokenFlow).toCompletableFuture()
        network.runNetwork()

        val tokenTransferFlow = TransferTokenFlow(receiverNode.info.legalIdentities.first(),amountToTransfer)
        tokenIssuerNode.startFlow(tokenTransferFlow).toCompletableFuture()
        network.runNetwork()

        tokenIssuerNode.transaction {
            val queriedStates = tokenIssuerNode.services.vaultService.queryBy<TokenState>().states
            assert(queriedStates.size==1)
            assert(queriedStates.first().state.data.amount == amountToIssue-amountToTransfer)
        }

        receiverNode.transaction {
            val queriedStates = receiverNode.services.vaultService.queryBy<TokenState>(QueryCriteria.VaultQueryCriteria()).states
            assert(queriedStates.size == 1)
            assert(queriedStates.first().state.data.amount == amountToTransfer)
        }

        receiverNode.startFlow(CombineTokensFlow())
        network.runNetwork()
        receiverNode.transaction {
            val queriedStates = receiverNode.services.vaultService.queryBy<TokenState>(QueryCriteria.VaultQueryCriteria())
            print("Receiver node after combine")
            for(states in queriedStates.states){
                print(states.state.data)
            }
        }

    }
}