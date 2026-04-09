package net.geoprism.climate;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan(basePackages = { "net.geoprism.spring.core", "net.geoprism.registry.service.business", "net.geoprism.registry.service.permission", "net.geoprism.climate" })
public class ClimateAppConfig
{

}
