package com.github.scalaspring.akka.config;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * Supplies an Akka configuration bean backed by Spring properties.
 *
 * The bean produced by this factory enables Spring-based configuration properties to be used via Akka configuration.
 * When used with {@link AkkaConfigPropertySourceBeanFactoryPostProcessor}, the combination provides bi-directional
 * properties flow.
 */
@Component
public class AkkaConfigFactoryBean implements FactoryBean<Config> {

    private final ApplicationContext applicationContext;
    private final ConfigurableEnvironment environment;

    public AkkaConfigFactoryBean(ApplicationContext applicationContext, ConfigurableEnvironment environment) {
        this.applicationContext = applicationContext;
        this.environment = environment;
    }

    @Override
    public Class<?> getObjectType() {
        return Config.class;
    }

    @Override
    public Config getObject() throws Exception {
        // Note that the environment should already contain the Akka Config-based property source
        // since the bean factory post processor runs before any factory beans
        final Map<String, String> flattened = AkkaConfigPropertySourceAdapter.flattenEnvironment(environment);
        final Map<String, Object> converted = AkkaConfigPropertySourceAdapter.convertIndexedProperties(flattened);

        return ConfigFactory.parseMap(converted);
    }

}
