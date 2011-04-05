package kr.arawn.springframework.data.sqlmap.support;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import kr.arawn.springframework.data.sqlmap.repository.sample.SpringSproutRepository;
import kr.arawn.springframework.data.sqlmap.repository.statement.Statement;

import org.junit.Before;
import org.junit.Test;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.stereotype.Repository;


public class AnnotationInjectorTest {
    
    Field annotationsMapAccessor;
    
    @Before
    public void 초기화() throws SecurityException, NoSuchFieldException {
        annotationsMapAccessor = Class.class.getDeclaredField("annotations");
        assertThat(annotationsMapAccessor, is(notNullValue()));
        annotationsMapAccessor.setAccessible(true);    
    }
    
    private Map<Class<? extends Annotation>, Annotation> getAnnotationsMap(Object source) throws IllegalAccessException {
        @SuppressWarnings("unchecked")
        Map<Class<? extends Annotation>, Annotation> annotationMap = (Map<Class<? extends Annotation>, Annotation>) annotationsMapAccessor.get(source);
        if (annotationMap == null || annotationMap.isEmpty()) {
            annotationMap = new HashMap<Class<? extends Annotation>, Annotation>();
        }
        return annotationMap;
    }
    
    private void saveAnnotationsMap(Object source, Map<Class<? extends Annotation>, Annotation> annotationsMap) throws IllegalAccessException {
        annotationsMapAccessor.set(source, annotationsMap);
    }
    
    @Test
    public void 클래스에_애노테이션_주입하기() throws IllegalArgumentException, IllegalAccessException {
        assertThat(AnnotationUtils.findAnnotation(SpringSproutRepository.class, Repository.class), is(nullValue()));
        
        Map<Class<? extends Annotation>, Annotation> annotationMap = getAnnotationsMap(SpringSproutRepository.class);
        
        annotationMap.put(Repository.class, new Repository() {
            public Class<? extends Annotation> annotationType() {
                return Repository.class;
            }
            public String value() {
                return "";
            }
        });
        
        saveAnnotationsMap(SpringSproutRepository.class, annotationMap);
        
        assertThat(AnnotationUtils.findAnnotation(SpringSproutRepository.class, Repository.class), is(notNullValue()));
    }
    
    public void 메소드에_애노테이션_주입하기() throws IllegalAccessException {
        for (Method method : SpringSproutRepository.class.getDeclaredMethods()) {
            Statement statement = AnnotationUtils.findAnnotation(method, Statement.class);
            if (statement == null) {
                Map<Class<? extends Annotation>, Annotation> annotationMap = getAnnotationsMap(method);
                
                annotationMap.put(Statement.class, new Statement() {
                    public Class<? extends Annotation> annotationType() {
                        return Repository.class;
                    }
                    public String id() {
                        return "";
                    }
                    public boolean modifying() {
                        return false;
                    }
                });
                saveAnnotationsMap(method, annotationMap);   
            }
            
            assertThat(AnnotationUtils.findAnnotation(method, Statement.class), is(notNullValue()));
        }
    }
    
}
