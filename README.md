# ghco-intraday-positions

## Description
Create an application to read the simplified trade booking information in the CSV file and process them for intraday position aggregations.

The aggregation should extract cash positions (PnL) per BBGCode, per portfolio, per strategy, per user.
Process all new trades, and handle amendments and cancels and display the position aggregation results
Create an interactive way to add new trades, on top of what is loaded by the csv


### Order Types

* A NEW Order type is a previously unseen order.

* An AMEND Order type is updating a previous order which contained incorrect information - the tradeIds will match.

* A CANCEL Order type is the removal of a previous NEW (and potentially AMENDed) order.

If an order is CANCELLED it should not be included in any calculations.

If an order is AMENDED then only the most recent AMEND should be included.

Otherwise, all NEW orders should be included in the calculations.