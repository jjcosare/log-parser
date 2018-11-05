package com.jjcosare.parser.config;

import org.springframework.beans.TypeConverter;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.QualifierAnnotationAutowireCandidateResolver;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.stereotype.Component;

import java.lang.annotation.Annotation;

/**
 * Created by jjcosare on 10/29/18.
 */
@Component
public class AutowireCandidateResolverConfigurer implements BeanFactoryPostProcessor {
    private static class EnvironmentAwareQualifierAnnotationAutowireCandidateResolver extends QualifierAnnotationAutowireCandidateResolver {

        private static class ResolvedQualifier implements Qualifier {
            private final String value;
            ResolvedQualifier(String value) { this.value = value; }
            @Override
            public String value() { return this.value; }
            @Override
            public Class<? extends Annotation> annotationType() { return Qualifier.class; }
        }

        @Override
        protected boolean checkQualifier(BeanDefinitionHolder beanDefinitionHolder, Annotation annotation, TypeConverter typeConverter) {
            if (annotation instanceof Qualifier) {
                Qualifier qualifier = (Qualifier) annotation;
                if (qualifier.value().startsWith("${") && qualifier.value().endsWith("}")) {
                    DefaultListableBeanFactory defaultListableBeanFactory = (DefaultListableBeanFactory) this.getBeanFactory();
                    ResolvedQualifier resolvedQualifier = new ResolvedQualifier(defaultListableBeanFactory.resolveEmbeddedValue(qualifier.value()));
                    return super.checkQualifier(beanDefinitionHolder, resolvedQualifier, typeConverter);
                }
            }
            return super.checkQualifier(beanDefinitionHolder, annotation, typeConverter);
        }
    }

    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) {
        DefaultListableBeanFactory defaultListableBeanFactory = (DefaultListableBeanFactory) beanFactory;
        defaultListableBeanFactory.setAutowireCandidateResolver(new EnvironmentAwareQualifierAnnotationAutowireCandidateResolver());
    }

}
