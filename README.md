# social-trading-app

## Details
Building social trading app as part of Akka Serverless Hackathon. ```Architecture will be added later```
This repository contains the Trade Service. Trade has the following properties:

#### State
trade_id (string) </br>
buyer_user_id(string)</br>
seller_user_id(string)</br>
buyer_item_ids(string list)</br>
seller_item_ids(string list)</br>
trade_offered_timestamp(int64)</br>
status[CREATED, ACCEPTED, REJECTED)</br>

#### Command
CreateTrade</br>
AcceptTrade</br>
RejectTrade</br>
GetTrade</br>

#### Event
TradeOffered</br>
TradeAccepted</br>
TradeRejected</br>

#### Eventing (Publish)
TradeOffered</br>
TradeAccepted</br>
TradeRejected</br>

#### View
getTradeByUser
+ Query: select* from trade_by_user where user_id = :user_id

## Building

To build, at a minimum you need to generate and process sources, particularly when using an IDE.
A convenience is compile your project:

```
mvn compile
```


## Running Locally

In order to run your application locally, you must run the Akka Serverless proxy. The included `docker-compose` file contains the configuration required to run the proxy for a locally running application.
It also contains the configuration to start a local Google Pub/Sub emulator that the Akka Serverless proxy will connect to.
To start the proxy, run the following command from this directory:


```
docker-compose up
```


> On Linux this requires Docker 20.10 or later (https://github.com/moby/moby/pull/40007),
> or for a `USER_FUNCTION_HOST` environment variable to be set manually.

```
docker-compose -f docker-compose.yml -f docker-compose.linux.yml up
```

To start the application locally, the `exec-maven-plugin` is used. Use the following command:

```
mvn compile exec:java
```

## Exercise the service - Locally

### Note - You can also directly use http call to do the same actions. For example to get the cart: localhost:9000/carts/3232

#### Create a trade
```
grpcurl \
  -d '{"tradeId": "1212", "buyerUserId": "3", "sellerUserId": "5", "buyerItemIds" : [{"itemId" : "1"}, {"itemId" : "2"}], "sellerItemIds" : [{"itemId" : "10"}, {"itemId" : "12"}]}' \
  -plaintext localhost:9000 \
  com.lightbend.gsa.trade.TradeService.CreateTrade
```

Output:

```json
{

}
```

#### Get the trade
```
grpcurl \
  -d '{"tradeId": "1212"}' \
  -plaintext localhost:9000 \
  com.lightbend.gsa.trade.TradeService.GetTrade
```

Output:

```json
{
  "trade_id": "1212",
  "buyer_user_id": "3",
  "seller_user_id": "5",
  "buyer_item_ids": [
    {
      "item_id": "1"
    },
    {
      "item_id": "2"
    }
  ],
  "seller_item_ids": [
    {
      "item_id": "10"
    },
    {
      "item_id": "12"
    }
  ],
  "trade_offered_timestamp": "1624642878834"
}
```


#### Accept the trade
```
grpcurl \
  -d '{"tradeId": "1212"}' \
  -plaintext localhost:9000 \
  com.lightbend.gsa.trade.TradeService.AcceptTrade
```

Output:

```json
{

}
```

#### Now, get the trade
```
grpcurl \
  -d '{"tradeId": "1212"}' \
  -plaintext localhost:9000 \
  com.lightbend.gsa.trade.TradeService.GetTrade
```

Output:

```json
{
  "trade_id": "1212",
  "buyer_user_id": "3",
  "seller_user_id": "5",
  "buyer_item_ids": [
    {
      "item_id": "1"
    },
    {
      "item_id": "2"
    }
  ],
  "seller_item_ids": [
    {
      "item_id": "10"
    },
    {
      "item_id": "12"
    }
  ],
  "trade_offered_timestamp": "1624642920511",
  "status": "ACCEPTED"
}
```

#### Create a trade
```
grpcurl \
  -d '{"tradeId": "113", "buyerUserId": "1", "sellerUserId": "3", "buyerItemIds" : [{"itemId" : "8"}, {"itemId" : "11"}], "sellerItemIds" : [{"itemId" : "2"}, {"itemId" : "6"}]}' \
  -plaintext localhost:9000 \
  com.lightbend.gsa.trade.TradeService.CreateTrade
```

Output:

```json
{

}
```

#### Get the trade
```
grpcurl \
  -d '{"tradeId": "113"}' \
  -plaintext localhost:9000 \
  com.lightbend.gsa.trade.TradeService.GetTrade
```

Output:

```json
{
  "trade_id": "113",
  "buyer_user_id": "1",
  "seller_user_id": "3",
  "buyer_item_ids": [
    {
      "item_id": "8"
    },
    {
      "item_id": "11"
    }
  ],
  "seller_item_ids": [
    {
      "item_id": "2"
    },
    {
      "item_id": "6"
    }
  ],
  "trade_offered_timestamp": "1624643005560"
}
```


#### Reject the trade
```
grpcurl \
  -d '{"tradeId": "113"}' \
  -plaintext localhost:9000 \
  com.lightbend.gsa.trade.TradeService.RejectTrade
```

Output:

```json
{

}
```

#### Now, get the trade
```
grpcurl \
  -d '{"tradeId": "113"}' \
  -plaintext localhost:9000 \
  com.lightbend.gsa.trade.TradeService.GetTrade
```

Output:

```json
{
  "trade_id": "113",
  "buyer_user_id": "1",
  "seller_user_id": "3",
  "buyer_item_ids": [
    {
      "item_id": "8"
    },
    {
      "item_id": "11"
    }
  ],
  "seller_item_ids": [
    {
      "item_id": "2"
    },
    {
      "item_id": "6"
    }
  ],
  "trade_offered_timestamp": "1624643005560",
  "status": "REJECTED"
}
```

#### Now, get the trade on buyer id
```
grpcurl \
  -d '{"userId": "1"}' \
  -plaintext localhost:9000 \
  com.lightbend.gsa.trade.view.TradeViewService.GetTradeByUserId
```

Output:

```json
{
  "trade_id": "113",
  "buyer_user_id": "1",
  "seller_user_id": "3",
  "buyer_item_ids": [
    {
      "item_id": "8"
    },
    {
      "item_id": "11"
    }
  ],
  "seller_item_ids": [
    {
      "item_id": "2"
    },
    {
      "item_id": "6"
    }
  ],
  "trade_offered_timestamp": "1624643005571",
  "status": "REJECTED"
}
```

#### Now, get the trade on seller id
```
grpcurl \
  -d '{"userId": "5"}' \
  -plaintext localhost:9000 \
  com.lightbend.gsa.trade.view.TradeViewService.GetTradeByUserId
```

Output:

```json
{
  "trade_id": "1212",
  "buyer_user_id": "3",
  "seller_user_id": "5",
  "buyer_item_ids": [
    {
      "item_id": "1"
    },
    {
      "item_id": "2"
    }
  ],
  "seller_item_ids": [
    {
      "item_id": "10"
    },
    {
      "item_id": "12"
    }
  ],
  "trade_offered_timestamp": "1624643493608",
  "status": "ACCEPTED"
}
```


## Deploying - to Akka Serverless

To deploy your service, install the `akkasls` CLI as documented in

#### To skip tests:

```
mvn clean install -Dmaven.test.skip=true
```

#### else

```
mvn clean install
```

#### Push the image to the repository:
```
docker push {docker-username}/{project-name}
```

#### Authorize akka serverless:
```
akkasls auth login
```

#### Set the project:
```
akkasls config set project <project-name>
```

#### Deploy the service to akka serverless
```
akkasls services deploy <servicename> docker.io/{docker-username}/{project-name}
```

#### Expose the service
```
akkasls service expose <servicename> --enable-cors
```

Output:
```
Service <servicename> was successfully exposed at: exampleABCD.us-east1.apps.akkaserverless.io
```

## Exercise the example - Service exposed on Akka Serverless

#### Create a trade
```
grpcurl -d '{"tradeId": "1212", "buyerUserId": "3", "sellerUserId": "5", "buyerItemIds" : [{"itemId" : "1"}, {"itemId" : "2"}], "sellerItemIds" : [{"itemId" : "10"}, {"itemId" : "12"}]}' exampleABCD.us-east1.apps.akkaserverless.io:443 com.lightbend.gsa.trade.TradeService.CreateTrade
```

Output:

```json
{

}
```

#### Get the trade
```
grpcurl -d '{"tradeId": "1212"}' exampleABCD.us-east1.apps.akkaserverless.io:443 com.lightbend.gsa.trade.TradeService.GetTrade
```

Output:

```json
{
  "trade_id": "1212",
  "buyer_user_id": "3",
  "seller_user_id": "5",
  "buyer_item_ids": [
    {
      "item_id": "1"
    },
    {
      "item_id": "2"
    }
  ],
  "seller_item_ids": [
    {
      "item_id": "10"
    },
    {
      "item_id": "12"
    }
  ],
  "trade_offered_timestamp": "1624645398129"
}
```


#### Accept the trade
```
grpcurl -d '{"tradeId": "1212"}' exampleABCD.us-east1.apps.akkaserverless.io:443 com.lightbend.gsa.trade.TradeService.AcceptTrade
```

Output:

```json
{

}
```

#### Now, get the trade
```
grpcurl -d '{"tradeId": "1212"}' exampleABCD.us-east1.apps.akkaserverless.io:443 com.lightbend.gsa.trade.TradeService.GetTrade
```

Output:

```json
{
  "trade_id": "1212",
  "buyer_user_id": "3",
  "seller_user_id": "5",
  "buyer_item_ids": [
    {
      "item_id": "1"
    },
    {
      "item_id": "2"
    }
  ],
  "seller_item_ids": [
    {
      "item_id": "10"
    },
    {
      "item_id": "12"
    }
  ],
  "trade_offered_timestamp": "1624645457811",
  "status": "ACCEPTED"
}
```

#### Create a trade
```
grpcurl -d '{"tradeId": "113", "buyerUserId": "1", "sellerUserId": "3", "buyerItemIds" : [{"itemId" : "8"}, {"itemId" : "11"}], "sellerItemIds" : [{"itemId" : "2"}, {"itemId" : "6"}]}' exampleABCD.us-east1.apps.akkaserverless.io:443 com.lightbend.gsa.trade.TradeService.CreateTrade
```


Output:

```json
{

}
```

#### Get the trade
```
grpcurl -d '{"tradeId": "113"}' exampleABCD.us-east1.apps.akkaserverless.io:443 com.lightbend.gsa.trade.TradeService.GetTrade
```

Output:

```json
{
  "trade_id": "113",
  "buyer_user_id": "1",
  "seller_user_id": "3",
  "buyer_item_ids": [
    {
      "item_id": "8"
    },
    {
      "item_id": "11"
    }
  ],
  "seller_item_ids": [
    {
      "item_id": "2"
    },
    {
      "item_id": "6"
    }
  ],
  "trade_offered_timestamp": "1624645631208"
}
```


#### Reject the trade
```
grpcurl -d '{"tradeId": "113"}' exampleABCD.us-east1.apps.akkaserverless.io:443 com.lightbend.gsa.trade.TradeService.RejectTrade
```

Output:

```json
{

}
```

#### Now, get the trade
```
grpcurl -d '{"tradeId": "113"}' exampleABCD.us-east1.apps.akkaserverless.io:443 com.lightbend.gsa.trade.TradeService.GetTrade
```

Output:

```json
{
  "trade_id": "113",
  "buyer_user_id": "1",
  "seller_user_id": "3",
  "buyer_item_ids": [
    {
      "item_id": "8"
    },
    {
      "item_id": "11"
    }
  ],
  "seller_item_ids": [
    {
      "item_id": "2"
    },
    {
      "item_id": "6"
    }
  ],
  "trade_offered_timestamp": "1624645678491",
  "status": "REJECTED"
}
```

#### Now, get the trade on buyer id
```
grpcurl -d '{"userId": "1"}' exampleABCD.us-east1.apps.akkaserverless.io:443 com.lightbend.gsa.trade.view.TradeViewService.GetTradeByUserId
```

Output:

```json
{
  "trade_id": "113",
  "buyer_user_id": "1",
  "seller_user_id": "3",
  "buyer_item_ids": [
    {
      "item_id": "8"
    },
    {
      "item_id": "11"
    }
  ],
  "seller_item_ids": [
    {
      "item_id": "2"
    },
    {
      "item_id": "6"
    }
  ],
  "trade_offered_timestamp": "1624645600890",
  "status": "REJECTED"
}
```

#### Now, get the trade on seller id
```
grpcurl -d '{"userId": "5"}' exampleABCD.us-east1.apps.akkaserverless.io:443 com.lightbend.gsa.trade.view.TradeViewService.GetTradeByUserId
```

Output:

```json
{
  "trade_id": "1212",
  "buyer_user_id": "3",
  "seller_user_id": "5",
  "buyer_item_ids": [
    {
      "item_id": "1"
    },
    {
      "item_id": "2"
    }
  ],
  "seller_item_ids": [
    {
      "item_id": "10"
    },
    {
      "item_id": "12"
    }
  ],
  "trade_offered_timestamp": "1624645328653",
  "status": "ACCEPTED"
}
```