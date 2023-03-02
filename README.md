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

## Run
You will need maven and java 17 in order to run the interactive application.
* mvn test exec:java
  * The main class is [Trade Launcher](src/main/java/co/uk/ghco/trade/application/TradeLauncher.java)
  * The interactive application will give you 3 options
    * Do you want to exit? please enter 1 
    * Do you want to process a trade? please enter 2 
    * Do you want to view the trade book? please enter 3