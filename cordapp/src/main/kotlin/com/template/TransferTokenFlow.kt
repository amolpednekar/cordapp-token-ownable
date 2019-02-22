package com.template

import co.paralleluniverse.fibers.Suspendable
import net.corda.core.flows.*
import net.corda.core.identity.Party
import net.corda.core.node.services.queryBy
import net.corda.core.transactions.SignedTransaction
import net.corda.core.transactions.TransactionBuilder

@StartableByRPC
@InitiatingFlow
class TransferTokenFlow(private val receiver: Party,
                        private val amountToTransfer: Int):FlowLogic<SignedTransaction>(){

    @Suspendable
    override fun call(): SignedTransaction {
        val notary = serviceHub.networkMapCache.notaryIdentities.first()

        val inputTokenStateReference = serviceHub.vaultService.queryBy<TokenState>().states.first()
        val inputTokenState = inputTokenStateReference.state.data

        val updatedInputTokenState = inputTokenState.copy(participants = listOf(ourIdentity), amount = inputTokenState.amount - amountToTransfer )
        val updatedOutputTokenState = inputTokenState.copy(participants = listOf(receiver), amount = amountToTransfer)
        val (command, updatedOwnerState) = updatedOutputTokenState.withNewOwner(receiver)
        val transactionBuilder = TransactionBuilder(notary)
                .addInputState(inputTokenStateReference)
                .addOutputState(updatedOwnerState, TokenContract.ID)
                .addOutputState(updatedInputTokenState, TokenContract.ID)
                .addCommand(command, listOf(inputTokenState.owner.owningKey, updatedOwnerState.owner.owningKey))

        transactionBuilder.verify(serviceHub)

        val partiallySignedTransaction = serviceHub.signInitialTransaction(transactionBuilder)

        val otherPartySession = initiateFlow(receiver)

        val fullySignedTransaction = subFlow(CollectSignaturesFlow(partiallySignedTransaction, listOf(otherPartySession)))

        return subFlow(FinalityFlow(fullySignedTransaction))
    }

}

@InitiatedBy(TransferTokenFlow::class)
class TransferTokenResponderFlow(val otherPartySession: FlowSession): FlowLogic<Unit>(){
    @Suspendable
    override fun call() {
        subFlow(object: SignTransactionFlow(otherPartySession){
            override fun checkTransaction(stx: SignedTransaction) {
                // sanity checks on this transaction
            }})
    }
}