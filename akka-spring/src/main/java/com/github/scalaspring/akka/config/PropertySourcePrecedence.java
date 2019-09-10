package com.github.scalaspring.akka.config;

import org.springframework.core.env.PropertySource;

public enum PropertySourcePrecedence {

    /**
     * Indicates that Akka configuration should be added with highest precedence to the list of property sources.
     *
     * @see AkkaConfigPropertySourceBeanFactoryPostProcessor
     * @see org.springframework.core.env.MutablePropertySources#addFirst(PropertySource)
     */
    FIRST(false),

    /**
     * Indicates that Akka configuration should be added with lowest precedence to the list of property sources.
     *
     * @see AkkaConfigPropertySourceBeanFactoryPostProcessor
     * @see org.springframework.core.env.MutablePropertySources#addLast(PropertySource)
     */
    LAST(false),

    /**
     * Indicates that Akka configuration should be added with precedence immediately higher than a given named
     * relative property source.
     *
     * @see AkkaConfigPropertySourceBeanFactoryPostProcessor
     * @see org.springframework.core.env.MutablePropertySources#addBefore(String, PropertySource)
     */
    BEFORE(true),

    /**
     * Indicates that Akka configuration should be added with precedence immediately lower than a given named
     * relative property source.
     *
     * @see AkkaConfigPropertySourceBeanFactoryPostProcessor
     * @see org.springframework.core.env.MutablePropertySources#addAfter(String, PropertySource)
     */
    AFTER(true);

    private final boolean relative;

    PropertySourcePrecedence(boolean relative) {
        this.relative = relative;
    }

    public boolean isRelative() {
        return relative;
    }

    public boolean isAbsolute() {
        return !relative;
    }

}
