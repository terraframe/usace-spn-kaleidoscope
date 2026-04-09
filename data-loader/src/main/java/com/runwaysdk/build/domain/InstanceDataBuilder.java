package com.runwaysdk.build.domain;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.context.ConfigurableApplicationContext;

import net.geoprism.climate.InstanceDataBuilderService;
import net.geoprism.climate.InstanceDataBuilderService.Params;

public class InstanceDataBuilder
{
  public static void main(String[] args) throws Throwable
  {
    // create Options object
    Options options = new Options();
    options.addOption(Option.builder("l").longOpt("local").desc("use the local file system for source data").hasArg().build());
    options.addOption(Option.builder("s").longOpt("source").desc("path to local source folder").hasArg().build());
    options.addOption(Option.builder("f").longOpt("ignoreFeedback").desc("flag denoting if the system should ignore feedback errors").hasArg().build());
    options.addOption(Option.builder("t").longOpt("types").desc("flag denoting if the system should import the type data").hasArg().build());
    options.addOption(Option.builder("e").longOpt("edges").desc("flag denoting if the system should impor the edge data").hasArg().build());

    CommandLineParser parser = new DefaultParser();
    CommandLine cmd = parser.parse(options, args);
    Params params = InstanceDataBuilderService.Params.parse(cmd);

    SpringApplication application = new SpringApplication(SpringBootConfiguration.class);
    // application.setWebApplicationType(WebApplicationType.NONE);

    ConfigurableApplicationContext ctx = application.run(args);

    try
    {
      InstanceDataBuilderService obj = ctx.getBean(InstanceDataBuilderService.class);
      obj.build(params);
    }
    finally
    {
      ctx.close();
    }

  }

}
