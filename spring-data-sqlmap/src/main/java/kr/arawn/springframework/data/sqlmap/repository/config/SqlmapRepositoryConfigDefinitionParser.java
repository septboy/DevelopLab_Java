package kr.arawn.springframework.data.sqlmap.repository.config;

import kr.arawn.springframework.data.sqlmap.repository.config.DefaultSqlmapRepositoryConfiguration.SqlmapRepositoryConfiguration;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.data.repository.config.AbstractRepositoryConfigDefinitionParser;
import org.w3c.dom.Element;


public class SqlmapRepositoryConfigDefinitionParser
        extends
        AbstractRepositoryConfigDefinitionParser<DefaultSqlmapRepositoryConfiguration, SqlmapRepositoryConfiguration> {
    
    private static final Class<?> DO_NOTHING_PERSISTENCE_EXCEPTION_TRANSLATOR = 
        DoNothingPersistenceExceptionTranslator.class;

    @Override
    protected DefaultSqlmapRepositoryConfiguration getGlobalRepositoryConfigInformation(
            Element element) {
        return new DefaultSqlmapRepositoryConfiguration(element);
    }

    @Override
    protected void postProcessBeanDefinition(
            SqlmapRepositoryConfiguration context,
            BeanDefinitionBuilder builder, BeanDefinitionRegistry registry,
            Object beanSource) {
        BeanDefinition beanDefinition = registry.getBeanDefinition(context.getSqlmapExecutorRef());
        builder.addPropertyValue("sqlmapExecutor", beanDefinition);
    }
    
    @Override
    protected void registerBeansForRoot(BeanDefinitionRegistry registry, Object source) {

        super.registerBeansForRoot(registry, source);

        if (!hasBean(DO_NOTHING_PERSISTENCE_EXCEPTION_TRANSLATOR, registry)) {

            AbstractBeanDefinition definition =
                    BeanDefinitionBuilder
                            .rootBeanDefinition(DO_NOTHING_PERSISTENCE_EXCEPTION_TRANSLATOR)
                            .getBeanDefinition();

            registerWithSourceAndGeneratedBeanName(registry, definition, source);
        }

    }

}
