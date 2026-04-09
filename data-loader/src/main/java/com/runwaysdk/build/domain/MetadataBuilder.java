package com.runwaysdk.build.domain;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.context.ConfigurableApplicationContext;

import net.geoprism.climate.MetadataBuilderService;

public class MetadataBuilder
{

  public static void main(String[] args) throws Throwable
  {
    SpringApplication application = new SpringApplication(SpringBootConfiguration.class);
    // application.setWebApplicationType(WebApplicationType.NONE);

    ConfigurableApplicationContext ctx = application.run(args);

    try
    {
      MetadataBuilderService obj = ctx.getBean(MetadataBuilderService.class);
      obj.build();
    }
    finally
    {
      ctx.close();
    }
  }

}
