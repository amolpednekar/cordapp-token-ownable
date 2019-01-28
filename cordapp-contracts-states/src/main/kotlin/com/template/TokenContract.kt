package com.template

import net.corda.core.contracts.CommandData
import net.corda.core.contracts.Contract
import net.corda.core.contracts.requireSingleCommand
import net.corda.core.transactions.LedgerTransaction

class TokenContract: Contract {
    override fun verify(tx: LedgerTransaction) {
        val command = tx.commands.requireSingleCommand<CommandData>()
        when(command.value){
            is Commands.Issue -> {

            }
            is Commands.Transfer -> {

            }
            is Commands.Spend -> {

            }
        }
    }

    interface Commands: CommandData{
        class Issue: Commands
        class Transfer: Commands
        class Spend: Commands
    }
}