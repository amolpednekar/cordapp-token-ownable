package com.template

import net.corda.core.node.services.queryBy
import net.corda.testing.node.MockNetwork
import org.junit.After
import org.junit.Before
import org.junit.Test

class IssueTokenTest {
    private val network = MockNetwork(listOf("com.template"))
    private val tokenIssuerNode = network.createPartyNode()
    private val userNode = network.createPartyNode()


    @Before
    fun setup() = network.runNetwork()

    @After
    fun tearDown() = network.stopNodes()

    @Test
    fun `Issue a token`() {
        val issueTokenFlow = IssueTokenFlow(100);
        tokenIssuerNode.startFlow(issueTokenFlow).toCompletableFuture()
        network.runNetwork()
        tokenIssuerNode.transaction {
            val queriedStates = tokenIssuerNode.services.vaultService.queryBy<TokenState>()
            print(queriedStates.states.first().state.data)
        }
        userNode.transaction {
            val queriedStates = userNode.services.vaultService.queryBy<TokenState>()
            assert(queriedStates.states.isEmpty())
        }
    }
}