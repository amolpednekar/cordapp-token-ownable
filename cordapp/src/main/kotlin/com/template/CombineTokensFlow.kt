package com.template

import co.paralleluniverse.fibers.Suspendable
import net.corda.core.flows.*
import net.corda.core.node.services.Vault
import net.corda.core.node.services.queryBy
import net.corda.core.node.services.vault.QueryCriteria
import net.corda.core.transactions.SignedTransaction
import net.corda.core.transactions.TransactionBuilder

@InitiatingFlow
@StartableByRPC
class CombineTokensFlow: FlowLogic<SignedTransaction>() {
    @Suspendable
    override fun call(): SignedTransaction {
        val notary = serviceHub.networkMapCache.notaryIdentities.first()
        val transactionBuilder = TransactionBuilder(notary)

        val myTokens = serviceHub.vaultService.queryBy<TokenState>(QueryCriteria.VaultQueryCriteria(Vault.StateStatus.UNCONSUMED)).states
        var totalAmount = 0

        for(states in myTokens){
            transactionBuilder.addInputState(states)
            totalAmount += states.state.data.amount
        }
        print(totalAmount)

        val outputState = TokenState(totalAmount, ourIdentity, listOf(ourIdentity))

        transactionBuilder.addOutputState(outputState, TokenContract.ID)
                .addCommand(TokenContract.Commands.Combine(), listOf(ourIdentity.owningKey))

        val signedTx = serviceHub.signInitialTransaction(transactionBuilder)

        transactionBuilder.verify(serviceHub)

        return subFlow(FinalityFlow(signedTx))
    }
}