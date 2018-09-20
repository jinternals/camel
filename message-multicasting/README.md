# Message multicasting
In this example we send message to multiple destinations for processing. By default, the multicast sends message copies sequentially. You can enable parallel processing by adding parallelProcessing method to route.

#### Parallel Processing snippet

```
    .multicast()
    .parallelProcessing()
    .to(ACCOUNT_ENDPOINT, COLLECTION_ENDPOINT)

```


### Code flow diagram:
![alt text](../images/multicasting.png)

#### Link:
http://camel.apache.org/multicast.html