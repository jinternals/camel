package com.jinternals.camel.file.routes;

import com.jinternals.camel.file.dto.Order;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.dataformat.bindy.csv.BindyCsvDataFormat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class OrderRoute extends RouteBuilder {

    public static final String ORDER_ENDPOINT = "direct:order";
    public static final String ACCOUNT_ENDPOINT = "direct:account";
    public static final String COLLECTION_ENDPOINT = "direct:collection";
    private String sourceDir;

    @Autowired
    public OrderRoute(@Value("${source.dir}") String sourceDir)
    {
        this.sourceDir = sourceDir;
    }

    @Override
    public void configure() throws Exception {

        from(sourceRoute(sourceDir))
                .routeId(ORDER_ENDPOINT)
                .split(body().tokenize())
                .streaming()
                .unmarshal(new BindyCsvDataFormat(Order.class))
                .multicast()
                .to(ACCOUNT_ENDPOINT, COLLECTION_ENDPOINT)
                .end();


        from(ACCOUNT_ENDPOINT)
                .routeId(ACCOUNT_ENDPOINT)
                .log("ACCOUNT : ${body}");

        from(COLLECTION_ENDPOINT)
                .routeId(COLLECTION_ENDPOINT)
                .log("COLLECTION : ${body}");

    }


    private String sourceRoute(String dir){
        return String.format("file:%s?preMove=.inProgress&move=.done",dir);
    }

}
