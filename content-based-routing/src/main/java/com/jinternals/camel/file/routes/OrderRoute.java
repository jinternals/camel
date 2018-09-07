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
    public static final String MOBILE_ENDPOINT = "direct:mobile";
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
                .choice()
                    .when(isSku("laptop"))
                        .to(LAPTOP_ENDPOINT)
                    .when(isSku("mobile"))
                        .to(MOBILE_ENDPOINT)
                    .otherwise()
                        .to(OTHER_ENDPOINT)
                .end();


        from(LAPTOP_ENDPOINT)
                .routeId(LAPTOP_ENDPOINT)
                .log("Laptop : ${body}");

        from(MOBILE_ENDPOINT)
                .routeId(MOBILE_ENDPOINT)
                .log("Mobile : ${body}");

        from(OTHER_ENDPOINT)
                .routeId(OTHER_ENDPOINT)
                .log("Other : ${body}");
    }

    private Predicate isSku(String sku) {
        return new Predicate() {
            @Override
            public boolean matches(Exchange exchange) {
              Order order =  exchange.getIn().getBody(Order.class);
                return order.getSku().equalsIgnoreCase(sku);
            }
        };
    }


    private String sourceRoute(String dir){
        return String.format("file:%s?preMove=.inProgress&move=.done",dir);
    }

    private String destinatonRoute(String dir){
        return String.format("file:%s",dir);
    }

}
