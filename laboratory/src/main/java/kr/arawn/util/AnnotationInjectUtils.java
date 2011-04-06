package kr.arawn.util;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * Annotation 을 Runtime 에 주입하는 유틸 클래스입니다.
 * 사용법은 {@link AnnotationInjectUtilsTest} 를 보세요.
 * 
 * @author arawn
 */
public class AnnotationInjectUtils {
    
    private AnnotationInjectUtils(){}
    
    private static ClassAnnotationsMapAccessor classAnnotationsMapAccessor;
    private static MethodAnnotationsMapAccessor methodAnnotationsMapAccessor;
    private static FieldAnnotationsMapAccessor fieldAnnotationsMapAccessor;
    
    /**
     * 클래스에 애노테이션이 존재하는지 검사
     * 
     * @param clazz 대상 클래스
     * @param annotation 조사할 애노테이션
     * @return
     */
    public static boolean isExists(Class<?> clazz, Annotation annotation) {
        Annotation existingAnnotation = clazz.getAnnotation(annotation.annotationType());
        return existingAnnotation != null;
    }
    
    /**
     * 메소드에 애노테이션이 존재하는지 검사
     * 
     * @param method 대상 메소드
     * @param annotation 조사할 애노테이션
     * @return
     */
    public static boolean isExists(Method method, Annotation annotation) {
        Annotation existingAnnotation = method.getAnnotation(annotation.annotationType());
        return existingAnnotation != null;
    }
    
    /**
     * 필드에 애노테이션이 존재하는지 검사
     * 
     * @param field 대상 필드
     * @param annotation 조사할 애노테이션
     * @return
     */
    public static boolean isExists(Field field, Annotation annotation) {
        Annotation existingAnnotation = field.getAnnotation(annotation.annotationType());
        return existingAnnotation != null;
    }
    
    /**
     * 클래스에 애노테이션 주입
     * target, annotation 이 null 이면 {@link IllegalArgumentException} 이 발생한다.
     * 애노테이션이 이미 존재하면 {@link AnnotationInjectionException} 이 발생한다.
     * 
     * @param target 대상 클래스
     * @param annotation 주입할 애노테이션
     * @throws AnnotationInjectionException
     */
    public static void injectAnnotation(Class<?> target, Annotation annotation) throws AnnotationInjectionException {
        validateInjectAnnotation(target, annotation);
        if(isExists(target, annotation))
            throw new AnnotationInjectionException("이재 존재하는 Annotation입니다.");
        
        _injectAnnotation(target, annotation);
    }
    
    /**
     * 메소드에 애노테이션 주입
     * target, annotation 이 null 이면 {@link IllegalArgumentException} 이 발생한다.
     * 애노테이션이 이미 존재하면 {@link AnnotationInjectionException} 이 발생한다.
     * 
     * @param target 대상 메소드
     * @param annotation 주입할 애노테이션
     * @throws AnnotationInjectionException
     */    
    public static void injectAnnotation(Method target, Annotation annotation) throws AnnotationInjectionException {
        validateInjectAnnotation(target, annotation);
        if(isExists(target, annotation))
            throw new AnnotationInjectionException("이재 존재하는 Annotation입니다.");
        
        _injectAnnotation(target, annotation);
    }
    
    /**
     * 필드에 애노테이션 주입
     * target, annotation 이 null 이면 {@link IllegalArgumentException} 이 발생한다.
     * 애노테이션이 이미 존재하면 {@link AnnotationInjectionException} 이 발생한다.
     * 
     * @param target 대상 필드
     * @param annotation 주입할 애노테이션
     * @throws AnnotationInjectionException
     */       
    public static void injectAnnotation(Field target, Annotation annotation) throws AnnotationInjectionException {
        validateInjectAnnotation(target, annotation);
        if(isExists(target, annotation))
            throw new AnnotationInjectionException("이재 존재하는 Annotation입니다.");
        
        _injectAnnotation(target, annotation);
    }
    
    /**
     * 대상에 애노테이션 주입
     * 
     * @param target 대상
     * @param annotation 주입할 애노테이션
     * @throws AnnotationInjectionException
     */       
    private static void _injectAnnotation(Object target, Annotation annotation) throws AnnotationInjectionException {
        AbstractAnnotationsMapAccessor accessor = getAnnotationsMapAccessor(target);

        Map<Class<? extends Annotation>, Annotation> annotationMap = accessor.getAnnotationsMap(target);
        annotationMap.put(annotation.annotationType(), annotation);
        
        accessor.saveAnnotationsMap(target, annotationMap);
    }
    
    /**
     * target, annotation null 검사
     * 
     * @param target
     * @param annotation
     */
    private static void validateInjectAnnotation(Object target, Annotation annotation) {
        if(target == null)
            throw new IllegalArgumentException("target의 값이 올바르지 않습니다. [value : null]");
        if (annotation == null)
            throw new IllegalArgumentException("annotation의 값이 올바르지 않습니다. [value : null]");
    }
    
    /**
     * 애노테이션을 주입하기 위한 접근자를 생성해서 반환
     * 
     * @param target 애노테이션을 주입할 대상(Class, Method, Field)
     * @return AbstractAnnotationsMapAccessor
     */
    private static AbstractAnnotationsMapAccessor getAnnotationsMapAccessor(Object target) {
        if(target.getClass() == Class.class) {
            if(classAnnotationsMapAccessor == null)
                classAnnotationsMapAccessor = new ClassAnnotationsMapAccessor();
            
            return classAnnotationsMapAccessor;
        }
        else if(target.getClass() == Method.class) {
            if(methodAnnotationsMapAccessor == null)
                methodAnnotationsMapAccessor = new MethodAnnotationsMapAccessor();
            
            return methodAnnotationsMapAccessor;
        }
        else if(target.getClass() == Field.class) {
            if(fieldAnnotationsMapAccessor == null)
                fieldAnnotationsMapAccessor = new FieldAnnotationsMapAccessor();
            
            return fieldAnnotationsMapAccessor;
        }
        
        throw new AnnotationInjectionException("적합한 AnnotationsMapAccessor를 찾지 못했습니다. [target class : " + target.getClass() + "]");
    }
    
    /**
     * 애노테이션을 꺼내거나 저장하기 위한 접근자 클래스
     * 
     * @author arawn
     */
    private abstract static class AbstractAnnotationsMapAccessor {
        private Field annotations;
        
        private Field getAnnotationsMapAccessor() throws AnnotationInjectionException {
            if(this.annotations != null)
                return this.annotations;
            
            try {
                this.annotations = findAnnotations();
                this.annotations.setAccessible(true);
                return this.annotations;
            } catch (Exception e) {
                throw new AnnotationInjectionException("annotations 를 찾지 못했습니다.", e);
            }
        }
        
        /**
         * 대상의 애노테이션 정보가 담겨 있는 Map 을 반환
         * 
         * @param source
         * @return 
         */
        public Map<Class<? extends Annotation>, Annotation> getAnnotationsMap(Object source) {
            if (source == null) {
                throw new IllegalArgumentException("source의 값이 올바르지 않습니다. [value : null]");
            }
            
            try {
                @SuppressWarnings("unchecked")
                Map<Class<? extends Annotation>, Annotation> annotationMap = 
                    (Map<Class<? extends Annotation>, Annotation>) getAnnotationsMapAccessor().get(source);
                if (annotationMap == null || annotationMap.isEmpty()) {
                    annotationMap = new HashMap<Class<? extends Annotation>, Annotation>();
                }
                
                return annotationMap;
            } catch (IllegalAccessException e) {
                throw new AnnotationInjectionException("annotationMap에 접근 중 예외가 발생했습니다.", e);
            }
        }
        
        /**
         * 대상에게 애노테이션 정보를 주입
         * 
         * @param source
         * @param annotationMap
         */
        public void saveAnnotationsMap(Object source, Map<Class<? extends Annotation>, Annotation> annotationMap) {
            try {
                getAnnotationsMapAccessor().set(source, annotationMap);
            } catch (IllegalAccessException e) {
                throw new AnnotationInjectionException("annotationMap을 저장 중 예외가 발생했습니다.", e);
            }
        }    
        
        protected abstract Field findAnnotations() throws Exception;
    }
    
    /**
     * {@link Class} 애노테이션 접근자
     * 
     * @author arawn
     */
    private static class ClassAnnotationsMapAccessor extends AbstractAnnotationsMapAccessor {
        protected Field findAnnotations() throws Exception {
            return Class.class.getDeclaredField("annotations");
        }
    }

    /**
     * {@link Method} 애노테이션 접근자
     * 
     * @author arawn
     */
    private static class MethodAnnotationsMapAccessor extends AbstractAnnotationsMapAccessor {
        protected Field findAnnotations() throws Exception {
            return Method.class.getDeclaredField("declaredAnnotations");
        }
    }
    
    /**
     * {@link Field} 애노테이션 접근자
     * 
     * @author arawn
     */    
    private static class FieldAnnotationsMapAccessor extends AbstractAnnotationsMapAccessor {
        protected Field findAnnotations() throws Exception {
            return Field.class.getDeclaredField("declaredAnnotations");
        }
    }

    /**
     * AnnotationInjectUtils 사용 중 발생하는 예외
     * 
     * @author arawn
     */    
    static class AnnotationInjectionException extends RuntimeException {
        private static final long serialVersionUID = 633903686314675972L;
        public AnnotationInjectionException(String message) {
            super(message);
        }
        public AnnotationInjectionException(String message, Throwable throwable) {
            super(message, throwable);
        }
    }
}
