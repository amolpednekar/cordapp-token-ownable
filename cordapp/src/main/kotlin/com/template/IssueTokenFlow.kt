package com.template

import net.corda.core.flows.FinalityFlow
import net.corda.core.flows.FlowLogic
import net.corda.core.flows.InitiatingFlow
import net.corda.core.flows.StartableByRPC
import net.corda.core.identity.Party
import net.corda.core.transactions.SignedTransaction
import net.corda.core.transactions.TransactionBuilder

@InitiatingFlow
@StartableByRPC
class IssueTokenFlow(val issuer: Party):FlowLogic<SignedTransaction>(){
    override fun call(): SignedTransaction {
        val notary = serviceHub.networkMapCache.notaryIdentities.first()

        val tokenState = TokenState(50, ourIdentity, listOf(ourIdentity))
        val transactionBuilder = TransactionBuilder(notary)
                .addOutputState(tokenState, TokenContract.ID)
                .addCommand(TokenContract.Commands.Issue())

        val signedTransaction = serviceHub.signInitialTransaction(transactionBuilder)

        transactionBuilder.verify(serviceHub)

        return subFlow(FinalityFlow(signedTransaction))
    }

}