package com.lovedise.permissiongen.compiler;

import com.google.auto.service.AutoService;
import com.lovedise.permissiongen.annotation.PermissionFail;
import com.lovedise.permissiongen.annotation.PermissionSuccess;
import java.io.IOException;
import java.io.Writer;
import java.util.HashMap;
import java.util.HashSet;
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
import javax.tools.JavaFileObject;

import static javax.tools.Diagnostic.Kind.NOTE;

@AutoService(Processor.class)
public class PermissionProcessor extends AbstractProcessor{
  private Messager messager;
  private Elements elementUtils;
  private Map<String, ProxyInfo> mProxyMap = new HashMap<String, ProxyInfo>();

  @Override public synchronized void init(ProcessingEnvironment processingEnv) {
    super.init(processingEnv);
    messager = processingEnv.getMessager();
    elementUtils = processingEnv.getElementUtils();
  }

  @Override public boolean process(Set<? extends TypeElement> annotations,
      RoundEnvironment roundEnv) {
    for (TypeElement annotation : annotations) {
      messager.printMessage(NOTE, annotation.toString());
      for(Element element : roundEnv.getElementsAnnotatedWith(annotation)){
        ExecutableElement annotatedMethod = (ExecutableElement) element;
        messager.printMessage(NOTE, annotatedMethod.toString());
        TypeElement classElement = (TypeElement) annotatedMethod.getEnclosingElement();

        String fqClassName = classElement.getQualifiedName().toString();
        //messager.printMessage(NOTE, "fqClassName : " + fqClassName);
        PackageElement packageElement = elementUtils.getPackageOf(classElement);
        String packageName = packageElement.getQualifiedName().toString();
        //messager.printMessage(NOTE, "packageName : " + packageName);
        String className = getClassName(classElement, packageName);
        //messager.printMessage(NOTE, "className : " + className);

        PermissionSuccess successAnnotation = annotatedMethod.getAnnotation(PermissionSuccess.class);
        PermissionFail failAnnotation = annotatedMethod.getAnnotation(PermissionFail.class);

        ProxyInfo proxyInfo = mProxyMap.get(fqClassName);
        if (proxyInfo == null)
        {
          proxyInfo = new ProxyInfo(packageName, className);
          mProxyMap.put(fqClassName, proxyInfo);
          messager.printMessage(NOTE, "add class : " + fqClassName);
          proxyInfo.setTypeElement(classElement);
        }

        if(successAnnotation != null) {
          proxyInfo.grantMethodMap.put(successAnnotation.requestCode(), annotatedMethod.getSimpleName().toString());
        }

        if(failAnnotation != null){
          proxyInfo.deniedMethodMap.put(failAnnotation.requestCode(), annotatedMethod.getSimpleName().toString());
        }
      }
    }

    for (String key : mProxyMap.keySet())
    {
      ProxyInfo proxyInfo = mProxyMap.get(key);
      messager.printMessage(NOTE, "key : " + key);
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
    processingEnv.getMessager().printMessage(NOTE, message, element);
  }


  @Override public Set<String> getSupportedAnnotationTypes() {
    Set<String> annotationTypes = new HashSet<>();
    annotationTypes.add(PermissionSuccess.class.getCanonicalName());
    annotationTypes.add(PermissionFail.class.getCanonicalName());
    return annotationTypes;
  }

  @Override public SourceVersion getSupportedSourceVersion() {
    return SourceVersion.latestSupported();
  }

  private static String getClassName(TypeElement type, String packageName)
  {
    int packageLen = packageName.length() + 1;
    return type.getQualifiedName().toString().substring(packageLen)
        .replace('.', '$');
  }

}
