package com.yx.annotation_compiler;

import com.google.auto.service.AutoService;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;
import com.yx.annotations.BindView;

import com.yx.annotations.OnClick;


import java.io.IOException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeMirror;

import static javax.lang.model.element.Modifier.FINAL;
import static javax.lang.model.element.Modifier.PUBLIC;

/**
 * Author by YX, Date on 2019/8/7.
 */
@AutoService(Processor.class)
public class EBKAnnotationCompiler extends AbstractProcessor{


    Filer filer;

    //初始化
    @Override
    public synchronized void init(ProcessingEnvironment processingEnvironment) {
        super.init(processingEnvironment);
        filer = processingEnvironment.getFiler();
    }


    @Override
    public Set<String> getSupportedAnnotationTypes() {
        Set<String> types = new HashSet<>();
        types.add(BindView.class.getCanonicalName());
        types.add(OnClick.class.getCanonicalName());
        return types;
    }


    @Override
    public SourceVersion getSupportedSourceVersion() {
        return processingEnv.getSourceVersion();
    }


    @Override
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnvironment) {
        parseAnnotation(roundEnvironment);
        return false;
    }
    private void goWriteByJavaPoet(Map<String,List<Element>> map) {
        Iterator<String> iterator = map.keySet().iterator();
        while (iterator.hasNext()){
            String key = iterator.next();
            List<Element> elementList = map.get(key);
            ClassName  IBindClass = ClassName.bestGuess("com.yx.easybutterknifelib.IBind");
            String packageName = processingEnv.getElementUtils().getPackageOf((TypeElement) elementList.get(0).getEnclosingElement()).toString();
            ClassName name = ClassName.get(packageName,key);

            MethodSpec.Builder methodOnClickBuilder = MethodSpec.methodBuilder("onClick")
                        .addAnnotation(Override.class)
                        .addModifiers(PUBLIC)
                        .addParameter(name,"target",FINAL)
                        .returns(TypeName.VOID);

            MethodSpec.Builder methodBindBuilder = MethodSpec.methodBuilder("bind")
                        .addAnnotation(Override.class)
                        .addModifiers(PUBLIC)
                        .addParameter(name,"target")
                        .returns(TypeName.VOID);

            for (Element element : elementList) {
                if(element.getKind()== ElementKind.METHOD){
                    ExecutableElement executableElement = (ExecutableElement) element;
                    String methodName = executableElement.getSimpleName().toString();
                    int[] value = executableElement.getAnnotation(OnClick.class).value();
                    ClassName viewClass = ClassName.bestGuess("android.view.View");
                    ClassName debClass = ClassName.bestGuess("com.yx.easybutterknifelib.DebouncingOnClickListener");
                    for (int id : value) {
                        methodOnClickBuilder.addStatement("$T view_$L = target.findViewById($L)",viewClass,id,id);
                        methodOnClickBuilder.addStatement("view_$L.setOnClickListener(new $T() {\n" +
                                "                    @Override\n" +
                                "                    public void doClick(View p0) {\n" +
                                "                        target.$N(p0);\n" +
                                "                    }\n" +
                                "                })",id,debClass,methodName);
                    }
                }else if(element.getKind()==ElementKind.FIELD){
                    VariableElement variableElement = (VariableElement) element;
                    String fieldName = variableElement.getSimpleName().toString();
                    int id = variableElement.getAnnotation(BindView.class).value();
                    TypeMirror type = variableElement.asType();
                    methodBindBuilder.addStatement("target.$N = ($T)target.findViewById($L)",fieldName,ClassName.get(type),id);
                }
            }

            TypeSpec typeSpec = TypeSpec.classBuilder(key+"_ViewBinding")
                    .addSuperinterface(ParameterizedTypeName.get(IBindClass,name))
                    .addModifiers(PUBLIC)
                    .addMethod(methodBindBuilder.build())
                    .addMethod(methodOnClickBuilder.build())
                    .build();

            try {
                JavaFile.builder(packageName,typeSpec).build().writeTo(filer);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void parseAnnotation(RoundEnvironment roundEnvironment){
        Set<? extends Element> bindViewElementSet = roundEnvironment.getElementsAnnotatedWith(BindView.class);
        Set<? extends Element> onClickElementSet = roundEnvironment.getElementsAnnotatedWith(OnClick.class);
        Map<String,List<Element>> elmentMap = new HashMap<>();
        for (Element element : bindViewElementSet) {
            VariableElement variableElement = (VariableElement) element;
            String className = variableElement.getEnclosingElement().getSimpleName().toString();
            List<Element> variableElementList = elmentMap.get(className);
            if(variableElementList==null){
                variableElementList = new ArrayList<>();
                elmentMap.put(className,variableElementList);
            }
            variableElementList.add(variableElement);
        }
        for (Element element : onClickElementSet) {
            ExecutableElement executableElement = (ExecutableElement) element;
            String className = executableElement.getEnclosingElement().getSimpleName().toString();
            List<Element> methodManagerList = elmentMap.get(className);
            if(methodManagerList==null){
                methodManagerList = new ArrayList<>();
                elmentMap.put(className,methodManagerList);
            }
            methodManagerList.add(executableElement);
        }

        if(elmentMap.size()>0){
          goWriteByJavaPoet(elmentMap);
        }

    }
}
