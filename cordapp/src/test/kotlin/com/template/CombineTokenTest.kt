package com.template

import net.corda.core.node.services.queryBy
import net.corda.core.node.services.vault.QueryCriteria
import net.corda.testing.node.MockNetwork
import org.junit.After
import org.junit.Before
import org.junit.Test
import kotlin.test.assert

class CombineTokenTest{
    private val network = MockNetwork(listOf("com.template"))
    private val tokenIssuerNode = network.createPartyNode()
    private val receiverNode = network.createPartyNode()

    @Before
    fun setup() = network.runNetwork()

    @After
    fun tearDown() = network.stopNodes()

    @Test
    fun `Combine existing tokens`(){
        // Pre-requisite: Issue the token
        val amountToIssue = 100
        val amountToTransfer = 10
        val issueTokenFlow = IssueTokenFlow(amountToIssue)
        // Issuer sends 100 tokens to itself
        tokenIssuerNode.startFlow(issueTokenFlow).toCompletableFuture()
        // Receiver issues 100 tokens to itself
        receiverNode.startFlow(issueTokenFlow).toCompletableFuture()
        network.runNetwork()

        val tokenTransferFlow = TransferTokenFlow(receiverNode.info.legalIdentities.first(),amountToTransfer)
        tokenIssuerNode.startFlow(tokenTransferFlow).toCompletableFuture()
        network.runNetwork()

        receiverNode.startFlow(CombineTokensFlow())
        network.runNetwork()
        receiverNode.transaction {
            val queriedStates = receiverNode.services.vaultService.queryBy<TokenState>(QueryCriteria.VaultQueryCriteria()).states
            for(states in queriedStates){
                print(states.state.data)
            }
            assert(queriedStates.first().state.data.amount == amountToIssue + amountToTransfer )
        }
    }
}