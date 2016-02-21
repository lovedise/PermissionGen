package com.zhy.m.permission;

import com.google.auto.service.AutoService;

import java.io.IOException;
import java.io.Writer;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;
import javax.tools.Diagnostic;
import javax.tools.JavaFileObject;

import static javax.lang.model.SourceVersion.latestSupported;
import static javax.tools.Diagnostic.Kind.ERROR;

@AutoService(Processor.class)
public class PermissionProcessor extends AbstractProcessor
{
    private Messager messager;
    private Elements elementUtils;
    private Map<String, ProxyInfo> mProxyMap = new HashMap<String, ProxyInfo>();

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv)
    {
        super.init(processingEnv);
        messager = processingEnv.getMessager();
        elementUtils = processingEnv.getElementUtils();
    }

    @Override
    public Set<String> getSupportedAnnotationTypes()
    {
        HashSet<String> supportTypes = new LinkedHashSet<>();
        supportTypes.add(PermissionDenied.class.getCanonicalName());
        supportTypes.add(PermissionGrant.class.getCanonicalName());
        return supportTypes;
    }

    @Override
    public SourceVersion getSupportedSourceVersion()
    {
        return latestSupported();
    }


    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv)
    {
        messager.printMessage(Diagnostic.Kind.NOTE, "process...");
        for (Element annotatedElement : roundEnv.getElementsAnnotatedWith(PermissionGrant.class))
        {
            // Our grantAnnotation is defined with @Target(value=TYPE). Therefore, we can assume that
            // this annotatedElement is a TypeElement.
            ExecutableElement annotatedMethod = (ExecutableElement) annotatedElement;
//            if (!isValidMethod(annotatedMethod))
//            {
//                return true;
//            }

            //class type
            TypeElement classElement = (TypeElement) annotatedMethod.getEnclosingElement();
            //full class name
            String fqClassName = classElement.getQualifiedName().toString();
            PackageElement packageElement = elementUtils.getPackageOf(classElement);
            String packageName = packageElement.getQualifiedName().toString();
            String className = getClassName(classElement, packageName);

            ProxyInfo proxyInfo = mProxyMap.get(fqClassName);
            if (proxyInfo == null)
            {
                proxyInfo = new ProxyInfo(packageName, className);
                mProxyMap.put(fqClassName, proxyInfo);
                proxyInfo.setTypeElement(classElement);
            }

            PermissionGrant grantAnnotation = annotatedMethod.getAnnotation(PermissionGrant.class);
            int requestCode = grantAnnotation.value();
            proxyInfo.grantMethodMap.put(requestCode, annotatedMethod.getSimpleName().toString());
        }


        for (Element annotatedElement : roundEnv.getElementsAnnotatedWith(PermissionDenied.class))
        {
            // Our grantAnnotation is defined with @Target(value=TYPE). Therefore, we can assume that
            // this annotatedElement is a TypeElement.
            ExecutableElement annotatedMethod = (ExecutableElement) annotatedElement;
//            if (!isValidMethod(annotatedMethod))
//            {
//                return true;
//            }

            //class type
            TypeElement classElement = (TypeElement) annotatedMethod.getEnclosingElement();


            //full class name
            String fqClassName = classElement.getQualifiedName().toString();
            messager.printMessage(Diagnostic.Kind.NOTE, "fqClassName == " + fqClassName);

            PackageElement packageElement = elementUtils.getPackageOf(classElement);
            String packageName = packageElement.getQualifiedName().toString();
            String className = getClassName(classElement, packageName);

            messager.printMessage(Diagnostic.Kind.NOTE, "className == " + className);

            ProxyInfo proxyInfo = mProxyMap.get(fqClassName);
            if (proxyInfo == null)
            {
                proxyInfo = new ProxyInfo(packageName, className);
                mProxyMap.put(fqClassName, proxyInfo);
                proxyInfo.setTypeElement(classElement);
            }


            PermissionDenied deniedAnnotation = annotatedMethod.getAnnotation(PermissionDenied.class);
            int requestCode = deniedAnnotation.value();
            proxyInfo.deniedMethodMap.put(requestCode, annotatedMethod.getSimpleName().toString());
        }

        for (String key : mProxyMap.keySet())
        {
            ProxyInfo proxyInfo = mProxyMap.get(key);
            try
            {
                JavaFileObject jfo = processingEnv.getFiler().createSourceFile(
                        proxyInfo.getProxyClassFullName(),
                        proxyInfo.getTypeElement());
                Writer writer = jfo.openWriter();
                writer.write(proxyInfo.generateJavaCode());
                writer.flush();
                writer.close();
            } catch (IOException e)
            {
                note(proxyInfo.getTypeElement(),
                        "Unable to write injector for type %s: %s",
                        proxyInfo.getTypeElement(), e.getMessage());
            }

        }
        return true;
    }

    private void note(Element element, String message, Object... args)
    {
        if (args.length > 0)
        {
            message = String.format(message, args);
        }
        processingEnv.getMessager().printMessage(Diagnostic.Kind.NOTE, message, element);
    }


    private static String getClassName(TypeElement type, String packageName)
    {
        int packageLen = packageName.length() + 1;
        return type.getQualifiedName().toString().substring(packageLen)
                .replace('.', '$');
    }

    private boolean isValidMethod(Element annotatedClass)
    {

        if (!ClassValidator.isPublic(annotatedClass))
        {
            String message = String.format("Classes annotated with %s must be public.",
                    annotatedClass.getSimpleName());
            messager.printMessage(ERROR, message, annotatedClass);
            return false;
        }

        if (ClassValidator.isAbstract(annotatedClass))
        {
            String message = String.format("Classes annotated with %s must not be abstract.",
                    annotatedClass.getSimpleName());
            messager.printMessage(ERROR, message, annotatedClass);
            return false;
        }

        return true;
    }


}
