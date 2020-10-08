# fixme

This project implements simulation tools for the financial markets that exchange a simplified version of FIX messages. The tools are be able to communicate over a network using the TCP protocol.

#Router
The router is the central component of applications. All other components connect to it in order to send messages to other components. The router perform no buiness logic, it just dispatches messages to the destination component(s).The router must accept incomming connections from multiple brokers and markets. We call the router a market connetivity provider, because it allows brokers to send messages (in FIX format) to markets, without depending on specific implementation of the market.

#Broker
The Broker will send two types of messages:

Buy. - An order where the broker wants to buy an instrument
Sell. - An order where the broker want to sell an instrument

and will receive from the market messages of the following types:

Exeuted - when the order was accepted by the market and the action succeeded
Rejected - when the order could not be met

#Market
A market has a list of instruments that can be traded. When orders are received from brokers the market tries to execute it. If the execution is successfull, it updates the internal instrument list and sends the broker an Executed message. If the order can’t be met, the market sends a Rejected message.

#Fix Messages
All messages start with the ID asigned by the router and are ended by the checksum.

Buy and Sell messages have the following fields:

Message Type
Message Action
Instrument
Quantity
Market
Price
