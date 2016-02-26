package com.lovedise.permissiongen.compiler;

import java.util.HashMap;
import java.util.Map;
import javax.lang.model.element.TypeElement;

public class ProxyInfo
{
    private String packageName;
    private String targetClassName;
    private String proxyClassName;
    private TypeElement typeElement;

    Map<Integer, String> grantMethodMap = new HashMap<>();
    Map<Integer, String> deniedMethodMap = new HashMap<>();

    public static final String PROXY = "PermissionProxy";

    public ProxyInfo(String packageName, String className)
    {
        this.packageName = packageName;
        this.targetClassName = className;
        this.proxyClassName = className + "$$" + PROXY;
    }


    public String getProxyClassFullName()
    {
        return packageName + "." + proxyClassName;
    }

    public String generateJavaCode()
    {
        StringBuilder builder = new StringBuilder();
        builder.append("// Generated code. Do not modify!\n");
        builder.append("package ").append(packageName).append(";\n\n");
        builder.append("import kr.co.namee.permissiongen.*;\n");
        builder.append('\n');

        builder.append("public class ").append(proxyClassName).append(" implements " + ProxyInfo.PROXY + "<" + typeElement.getSimpleName() + ">");
        builder.append(" {\n");

        generateInjectMethod(builder);
        builder.append('\n');

        builder.append("}\n");
        return builder.toString();

    }

    private String getTargetClassName()
    {
        return targetClassName.replace("$", ".");
    }

    private void generateInjectMethod(StringBuilder builder)
    {
        builder.append("@Override\n ");
        builder.append("public void grant(" + typeElement.getSimpleName() + " source , int requestCode) {\n");
        builder.append("switch(requestCode) {");
        for (int code : grantMethodMap.keySet())
        {
            builder.append("case " + code + ":");
            builder.append("source." + grantMethodMap.get(code) + "();");
            builder.append("break;");
        }

        builder.append("}");
        builder.append("  }\n");


        builder.append("@Override\n ");
        builder.append("public void denied(" + typeElement.getSimpleName() + " source , int requestCode) {\n");
        builder.append("switch(requestCode) {");
        for (int code : deniedMethodMap.keySet())
        {
            builder.append("case " + code + ":");
            builder.append("source." + deniedMethodMap.get(code) + "();");
            builder.append("break;");
        }

        builder.append("}");
        builder.append("  }\n");
    }

    public TypeElement getTypeElement()
    {
        return typeElement;
    }

    public void setTypeElement(TypeElement typeElement)
    {
        this.typeElement = typeElement;
    }


}