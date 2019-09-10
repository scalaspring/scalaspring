package com.github.scalaspring.akka.config;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.util.function.Function;

import static java.lang.String.format;
import static org.springframework.util.Assert.isNull;
import static org.springframework.util.Assert.notNull;

/**
 * A bean factory post processor that creates and adds an Akka configuration to the list of property sources for the
 * current {@link Environment}, allowing the supplied Akka configuration to be used as a property source.
 *
 * Defaults to appending Akka configuration as the last source used to resolve properties (lowest precedence). This
 * can be adjusted by using one of the static factory methods on this class to achieve the desired precedence.
 *
 * Usage: Enable component scanning to create a default instance or create an instance explicitly to customize config
 * creation and/or precedence.
 *
 * @see ConfigurableEnvironment#getPropertySources()
 * @see ConfigFactory#load(ClassLoader)
 */
@Component
public final class AkkaConfigPropertySourceBeanFactoryPostProcessor implements BeanFactoryPostProcessor, ApplicationContextAware, EnvironmentAware {

    private static final Log log = LogFactory.getLog(AkkaConfigPropertySourceBeanFactoryPostProcessor.class);
    /**
     * Supplies a default Akka configuration using the application context's class loader.
     *
     * @see ConfigFactory#load(ClassLoader)
     */
    public static final Function<AkkaConfigPropertySourceBeanFactoryPostProcessor, Config> DEFAULT_CONFIG_FACTORY =
            postProcessor -> ConfigFactory.load(postProcessor.getApplicationContext().getClassLoader());

    private ApplicationContext applicationContext = null;
    private ConfigurableEnvironment environment = null;
    private final Function<AkkaConfigPropertySourceBeanFactoryPostProcessor, Config> configFactory;
    private final PropertySourcePrecedence propertySourcePrecedence;
    private final String relativePropertySourceName;

    public AkkaConfigPropertySourceBeanFactoryPostProcessor() {
        this(DEFAULT_CONFIG_FACTORY, PropertySourcePrecedence.LAST, null);
    }

    public AkkaConfigPropertySourceBeanFactoryPostProcessor(Function<AkkaConfigPropertySourceBeanFactoryPostProcessor, Config> configFactory, PropertySourcePrecedence propertySourcePrecedence, String relativePropertySourceName) {

        notNull(configFactory, "configuration factory must not be null");
        if (propertySourcePrecedence.isRelative())
            notNull(relativePropertySourceName, "relative property source name must be specified for relative precedence " + propertySourcePrecedence);

        this.configFactory = configFactory;
        this.propertySourcePrecedence = propertySourcePrecedence;
        this.relativePropertySourceName = relativePropertySourceName;
    }

    public static AkkaConfigPropertySourceBeanFactoryPostProcessor first() {
        return new AkkaConfigPropertySourceBeanFactoryPostProcessor(DEFAULT_CONFIG_FACTORY, PropertySourcePrecedence.FIRST, null);
    }

    public static AkkaConfigPropertySourceBeanFactoryPostProcessor last() {
        return new AkkaConfigPropertySourceBeanFactoryPostProcessor(DEFAULT_CONFIG_FACTORY, PropertySourcePrecedence.LAST, null);
    }

    public static AkkaConfigPropertySourceBeanFactoryPostProcessor before(String relativePropertySourceName) {
        return new AkkaConfigPropertySourceBeanFactoryPostProcessor(DEFAULT_CONFIG_FACTORY, PropertySourcePrecedence.BEFORE, relativePropertySourceName);
    }

    public static AkkaConfigPropertySourceBeanFactoryPostProcessor after(String relativePropertySourceName) {
        return new AkkaConfigPropertySourceBeanFactoryPostProcessor(DEFAULT_CONFIG_FACTORY, PropertySourcePrecedence.AFTER, relativePropertySourceName);
    }


    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {

        log.trace("Creating default Akka configuration");
        final Config config = configFactory.apply(this);
        final AkkaConfigPropertySource propertySource = new AkkaConfigPropertySource(config);

        log.debug(format("Adding Akka Config property source to environment (precedence=%s, relative=%s)", propertySourcePrecedence, relativePropertySourceName));
        switch (propertySourcePrecedence) {
            case FIRST:
                environment.getPropertySources().addFirst(propertySource);
                break;
            case LAST:
                environment.getPropertySources().addLast(propertySource);
                break;
            case BEFORE:
                environment.getPropertySources().addBefore(relativePropertySourceName, propertySource);
                break;
            case AFTER:
                environment.getPropertySources().addAfter(relativePropertySourceName, propertySource);
                break;
        }
    }

    public ApplicationContext getApplicationContext() {
        return applicationContext;
    }

    public ConfigurableEnvironment getEnvironment() {
        return environment;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    @Override
    public void setEnvironment(Environment environment) {
        this.environment = (ConfigurableEnvironment) environment;
    }

}
