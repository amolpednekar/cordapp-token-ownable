package com.template

import net.corda.core.contracts.*
import net.corda.core.transactions.LedgerTransaction
import java.security.PublicKey

class TokenContract: Contract {

    companion object {
        var ID = TokenContract::class.qualifiedName!!
    }

    override fun verify(tx: LedgerTransaction) {
        val commands = tx.commands.requireSingleCommand<CommandData>()
        val setOfSigners = commands.signers.toSet()
        when(commands.value){
            is Commands.Issue -> verifyIssue(tx, setOfSigners)
            is Commands.Transfer -> verifyTransfer(tx, setOfSigners)
            is Commands.Combine -> verifyCombine(tx, setOfSigners)
        }
    }

    private fun verifyIssue(tx: LedgerTransaction, setOfSigners: Set<PublicKey>) {
        requireThat {
            "There are no inputs" using (tx.inputs.isEmpty())
            "There is a single output" using (tx.outputs.size == 1)
            val output = tx.outputsOfType<TokenState>().single()
            "Request for issuance must only be signed by the owner" using(setOfSigners.contains((output.owner.owningKey)) && setOfSigners.size == 1)
            "Only token owner must be added to the list of participants when requesting issuance of a token on the ledger" using(output.participants.containsAll(listOf(output.owner)) && output.participants.size == 1)
        }
    }

    private fun verifyTransfer(tx: LedgerTransaction, setOfSigners: Set<PublicKey>) {
        requireThat {

            // Check that there is only one input
            val input = tx.inputsOfType<TokenState>().single()
            "There are two outputs" using (tx.outputs.size == 2)
            val outputs = tx.outputsOfType<TokenState>()

            val outputOfIssuer: TokenState
            val outputOfReceiver: TokenState

            // Check which of the two output states belong to issuer and receiver
            if(outputs[0].owner == input.owner) {
                outputOfIssuer = outputs[0]
                outputOfReceiver = outputs[1]
            }else{
                outputOfIssuer = outputs[1]
                outputOfReceiver = outputs[0]
            }

            "Issuer must have sufficient balance to transfer amount" using (outputOfIssuer.amount > 0)
            "Total amount of outputs must match input" using (outputOfIssuer.amount + outputOfReceiver.amount == input.amount)
        }
    }

    private fun verifyCombine(tx: LedgerTransaction, ofSigners: Set<PublicKey>) {
        //  accept all right now - Add checks TBD!
    }

    interface Commands: CommandData{
        class Issue: Commands
        class Transfer: Commands
        class Combine: Commands
    }
}