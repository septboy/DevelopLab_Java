package kr.arawn.springframework.data.sqlmap.repository.config;

import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;

import org.junit.Test;
import org.springframework.beans.PropertyValue;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.xml.XmlBeanFactory;
import org.springframework.core.io.ClassPathResource;

public class SqlmapRepositoryConfigDefinitionParserTest {

    @Test
    public void SqlmapExecutor가_주입되었는가() throws Exception {
        
        XmlBeanFactory factory = new XmlBeanFactory(new ClassPathResource(
                "kr/arawn/springframework/data/sqlmap/repository/config/" +
                "SqlmapRepositoryConfigDefinitionParserTest-context.xml"));

        BeanDefinition definition = factory.getBeanDefinition("springSproutRepository");
        assertThat(definition, is(notNullValue()));

        PropertyValue sqlmapExecutor = definition.getPropertyValues().getPropertyValue("sqlmapExecutor");
        assertThat(sqlmapExecutor, is(notNullValue()));
        
    }
    
    @Test
    public void DoNothingPersistenceExceptionTranslator가_등록되었는가() throws Exception {
        
        XmlBeanFactory factory = new XmlBeanFactory(new ClassPathResource(
                "kr/arawn/springframework/data/sqlmap/repository/config/" +
                "SqlmapRepositoryConfigDefinitionParserTest-context.xml"));

        DoNothingPersistenceExceptionTranslator translator = factory.getBean(DoNothingPersistenceExceptionTranslator.class);
        assertThat(translator, is(notNullValue()));
        
    }

}
