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

* Unix/Mac OSX: `./gradlew runClient`
* Windows: `gradlew runClient`

This will run the CorDapp client defined [HERE](clients/src/main/kotlin/com/template/Client.kt). This client will:

1. Issuer node issues 200 tokens to receiver node.
```
Issuer's vault

[StateAndRef(state=TransactionState(data=TokenState(amount=200, owner=O=PartyA, L=London, C=GB, participants=[O=PartyA, L=London, C=GB]), contract=com.template.TokenContract, notary=O=Notary Service, L=Zurich, C=CH, ...]
```

2. Issuers transfer token amounts of 100 and 50 to receiver, which creates 2 entries in receiver's node
```
Issuer's vault

[StateAndRef(state=TransactionState(data=TokenState(amount=50, owner=O=PartyA, L=London, C=GB, participants=[O=PartyA, L=London, C=GB]), contract=com.template.TokenContract, notary=O=Notary Service, L=Zurich, C=CH, ... ]
```
```
Receiver's vault 

[StateAndRef(state=TransactionState(data=TokenState(amount=100, owner=O=PartyB, L=New York, C=US, participants=[O=PartyB, L=New York, C=US]), contract=com.template.TokenContract, notary=O=Notary Service, L=Zurich, C=CH .. ), 

StateAndRef(state=TransactionState(data=TokenState(amount=50, owner=O=PartyB, L=New York, C=US, participants=[O=PartyB, L=New York, C=US]), contract=com.template.TokenContract, notary=O=Notary Service, L=Zurich, C=CH ... ]
```

3. Receiver combines all Token inputs into a single token
```
Receiver's vault

[StateAndRef(state=TransactionState(data=TokenState(amount=150, owner=O=PartyB, L=New York, C=US, participants=[O=PartyB, L=New York, C=US]), contract=com.template.TokenContract, notary=O=Notary Service, L=Zurich, C=CH, ...]
```

## Running the nodes

See https://docs.corda.net/tutorial-cordapp.html#running-the-example-cordapp.

## TODO

* Add Flow/State/Tests to 'Spend' tokens
* Add contract checks for 'Combine' command