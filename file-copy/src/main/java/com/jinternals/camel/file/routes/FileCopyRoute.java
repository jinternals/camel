package com.jinternals.camel.file.routes;

import com.jinternals.camel.file.processor.FileProcessor;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class FileCopyRoute extends RouteBuilder {

    private String sourceDir;
    private String destinationDir;

    @Autowired
    public FileCopyRoute(@Value("${source.dir}") String sourceDir, @Value("${destinaton.dir}") String destinationDir)
    {
        this.sourceDir = sourceDir;
        this.destinationDir = destinationDir;
    }

    @Override
    public void configure() throws Exception {
        from(sourceRoute(sourceDir))
                .process(new FileProcessor())
//                .to("log:com.jinternals.camel.file.routes.FileCopyRoute?level=INFO&showExchangeId=true")
//                .to("log:com.jinternals.camel.file.routes.FileCopyRoute?level=INFO&showExchangePattern=true")
//                .to("log:com.jinternals.camel.file.routes.FileCopyRoute?level=INFO&showHeaders=true")
//                .to("log:com.jinternals.camel.file.routes.FileCopyRoute?level=INFO&showProperties=true")
//                .to("log:com.jinternals.camel.file.routes.FileCopyRoute?level=INFO&showBodyType=true")
                .to(destinatonRoute(destinationDir));
    }


    private String sourceRoute(String dir){
        return String.format("file:%s?preMove=.inProgress&move=.done",dir);
    }

    private String destinatonRoute(String dir){
        return String.format("file:%s",dir);
    }

}
