# CorDapp - Token CorDapp using OwnableState

## Overview
This CorDapp models tokens using OwnableState

The TokenState implements OwnableState and has one additional attribute - Amount of type Int


There are three flows
1. IssueTokenFlow - This flow is triggered by the Issuer node and takes in a single argument, amount.
2. TransferTokenFlow - This flow is triggered by the Issuer node and takes in two arguments -> Party to which the token must be transferred and the amount of tokens to be transferred.
3. CombineTokensFlow - This flow can be triggered by any node to aggregate all their TokenStates into a single TokenState

# Pre-Requisites

See https://docs.corda.net/getting-set-up.html.

# Usage
Run the following command from the project's root folder:

* Unix/Mac OSX: `./gradlew runUpgradeContractStateClient`
* Windows: `gradlew runUpgradeContractStateClient`

This will run the CorDapp client defined [HERE](clients/src/main/kotlin/com/template/Client.kt). This client will:

1. Issuer node issues 200 tokens to receiver node.


## Running the nodes

See https://docs.corda.net/tutorial-cordapp.html#running-the-example-cordapp.
