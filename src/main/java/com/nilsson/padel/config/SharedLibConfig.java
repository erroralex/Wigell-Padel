package com.nilsson.padel.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * Konfiguration för att ladda in komponenter från teamets gemensamma bibliotek.
 * Placeras här istället för på main-klassen för att inte förstöra Spring Boots @WebMvcTest.
 */
@Configuration
@ComponentScan(basePackages = "com.groupc.shared")
public class SharedLibConfig {
}