package com.jinternals.camel.file.routes;

import com.jinternals.camel.file.dto.Order;
import org.apache.camel.Exchange;
import org.apache.camel.Predicate;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.dataformat.bindy.csv.BindyCsvDataFormat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class OrderRoute extends RouteBuilder {

    public static final String LAPTOP_ENDPOINT = "direct:laptop";
    public static final String PHONE_ENDPOINT = "direct:phone";
    public static final String OTHER_ENDPOINT = "direct:other";
    private String sourceDir;

    @Autowired
    public OrderRoute(@Value("${source.dir}") String sourceDir)
    {
        this.sourceDir = sourceDir;
    }

    @Override
    public void configure() throws Exception {
        from(sourceRoute(sourceDir))
                .split(body().tokenize())
                .streaming()
                .unmarshal(new BindyCsvDataFormat(Order.class))
                .filter(isTestOrder)
                .choice()
                    .when(isSku("laptop"))
                        .to(LAPTOP_ENDPOINT)
                    .when(isSku("phone"))
                        .to(PHONE_ENDPOINT)
                    .otherwise()
                        .to(OTHER_ENDPOINT)
                .end();


        from(LAPTOP_ENDPOINT)
                .routeId(LAPTOP_ENDPOINT)
                .log("Laptop : ${body}");

        from(PHONE_ENDPOINT)
                .routeId(PHONE_ENDPOINT)
                .log("Mobile : ${body}");

        from(OTHER_ENDPOINT)
                .routeId(OTHER_ENDPOINT)
                .log("Other : ${body}");
    }

    private Predicate isTestOrder = exchange -> {
            Order order =  exchange.getIn().getBody(Order.class);
            return !(order.getName().startsWith("Test") || order.getName().startsWith("test"));
    };


    private Predicate isSku(String sku) {
        return exchange -> {
          Order order =  exchange.getIn().getBody(Order.class);
            return order.getSku().equalsIgnoreCase(sku);
        };
    }


    private String sourceRoute(String dir){
        return String.format("file:%s?preMove=.inProgress&move=.done",dir);
    }

}
