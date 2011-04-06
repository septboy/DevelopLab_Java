package kr.arawn.util;

import static org.junit.Assert.*;

import java.lang.annotation.Annotation;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

import kr.arawn.util.AnnotationInjectUtils.AnnotationInjectionException;

import org.junit.Before;
import org.junit.Test;


public class AnnotationInjectUtilsTest {
    
    InjectAnnotation annotation;
    Method arawnMethod;
    Field arawnField;
    Method outsiderMethod;
    Field outsiderField;
    
    @Before
    public void 초기화() throws NoSuchMethodException, NoSuchFieldException {
        this.annotation = new InjectAnnotation() {
            @Override
            public Class<? extends Annotation> annotationType() {
                return InjectAnnotation.class;
            }
            @Override
            public String value() {
                return "";
            }
        };
        
        this.arawnMethod = Arawn.class.getDeclaredMethod("action");
        this.arawnField = Arawn.class.getDeclaredField("nickName");
        
        this.outsiderMethod = Outsider.class.getDeclaredMethod("action");
        this.outsiderField = Outsider.class.getDeclaredField("nickName");
    }
    
    @Test
    public void 클레스_애노테이션_검사() {
        assertFalse(AnnotationInjectUtils.isExists(Arawn.class, this.annotation));
        assertTrue(AnnotationInjectUtils.isExists(Outsider.class, this.annotation));
    }
    
    @Test
    public void 메소드_애노테이션_검사() throws NoSuchMethodException {
        assertFalse(AnnotationInjectUtils.isExists(this.arawnMethod, this.annotation));
        assertTrue(AnnotationInjectUtils.isExists(this.outsiderMethod, this.annotation));
    }
    
    @Test
    public void 필드_애노테이션_검사() throws NoSuchFieldException {
        assertFalse(AnnotationInjectUtils.isExists(this.arawnField, this.annotation));
        assertTrue(AnnotationInjectUtils.isExists(this.outsiderField, this.annotation));
    }
    
    @Test(expected=AnnotationInjectionException.class)
    public void 클래스_애노테이션_주입_실패() {
        AnnotationInjectUtils.injectAnnotation(Outsider.class, this.annotation);
    }
    
    @Test
    public void 클래스_애노테이션_주입_성공() {
        AnnotationInjectUtils.injectAnnotation(Arawn.class, this.annotation);
        
        if(Arawn.class.getAnnotation(annotation.annotationType()) == null)
            fail("애노테이션 주입 실패");
    }
    
    @Test(expected=AnnotationInjectionException.class)
    public void 메소드_애노테이션_주입_실패() {
        AnnotationInjectUtils.injectAnnotation(this.outsiderMethod, this.annotation);
    }
    
    @Test
    public void 메소드_애노테이션_주입_성공() {
        AnnotationInjectUtils.injectAnnotation(this.arawnMethod, this.annotation);
        
        if(this.arawnMethod.getAnnotation(annotation.annotationType()) == null)
            fail("애노테이션 주입 실패");
    }
    
    @Test(expected=AnnotationInjectionException.class)
    public void 필드_애노테이션_주입_실패() {
        AnnotationInjectUtils.injectAnnotation(this.outsiderField, this.annotation);
    }
    
    @Test
    public void 필드_애노테이션_주입_성공() {
        AnnotationInjectUtils.injectAnnotation(this.arawnField, this.annotation);
        
        if(this.arawnField.getAnnotation(annotation.annotationType()) == null)
            fail("애노테이션 주입 실패");
    }
    
    class Arawn {
        public String nickName;
        public void action() {}
    }
    
    @InjectAnnotation
    class Outsider {
        @InjectAnnotation
        public String nickName;
        @InjectAnnotation
        public void action() {}
    }
    
    @Retention(RetentionPolicy.RUNTIME)
    @Target({ElementType.TYPE, ElementType.METHOD, ElementType.FIELD})
    @Documented
    @interface InjectAnnotation {
        String value() default "";
    }
}
