package com.yx.annotation_compiler;

import com.google.auto.service.AutoService;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;
import com.yx.annotations.BindView;

import com.yx.annotations.OnClick;


import java.io.IOException;
import java.io.Writer;

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
import javax.swing.text.View;
import javax.tools.JavaFileObject;

import static javax.lang.model.element.Modifier.PUBLIC;

/**
 * Author by YX, Date on 2019/8/7.
 */
@AutoService(Processor.class)
public class EBKAnnotationCompiler extends AbstractProcessor{

    //生成文件的对象
    Filer filer;

    //初始化
    @Override
    public synchronized void init(ProcessingEnvironment processingEnvironment) {
        super.init(processingEnvironment);
        filer = processingEnvironment.getFiler();
    }

    //声明注解处理器要处理的注解
    @Override
    public Set<String> getSupportedAnnotationTypes() {
        Set<String> types = new HashSet<>();
        types.add(BindView.class.getCanonicalName());
        types.add(OnClick.class.getCanonicalName());
        return types;
    }


    //声明注解处理器支持的JDK版本
    @Override
    public SourceVersion getSupportedSourceVersion() {
        return processingEnv.getSourceVersion();
    }

    //写文件方法
    @Override
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnvironment) {
        parseAnnotation(roundEnvironment);
        return false;
    }
    private void goWriteByJavaPoet(Map<String,List<Element>> map) {
       /* package com.yx.easybutterknife;
import com.yx.easybutterknifelib.IBind;
import android.view.View;
import com.yx.easybutterknifelib.DebouncingOnClickListener;
        public class MainActivity_ViewBinding implements IBind<MainActivity>{
            @Override
            public void onClick(final MainActivity target) {
                View view_2131165326 = target.findViewById(2131165326);
                view_2131165326.setOnClickListener(new DebouncingOnClickListener() {
                    @Override
                    public void doClick(View p0) {
                        target.go(p0);
                    }
                });
                View  view_2131165325 = target.findViewById(2131165325);
                view_2131165325.setOnClickListener(new DebouncingOnClickListener() {
                    @Override
                    public void doClick(View p0) {
                        target.go(p0);
                    }
                });
            }
            @Override
            public void bind(MainActivity target) {
                target.tv = (android.widget.TextView)target.findViewById(2131165325);
            }
        }
        */
        ClassName  IBindClass = ClassName.bestGuess("com.yx.easybutterknifelib.IBind");
        //创建方法

        Iterator<String> iterator = map.keySet().iterator();
        while (iterator.hasNext()){
            String key = iterator.next();
            List<Element> elementList = map.get(key);
            for (Element element : elementList) {
                if(element.getKind()== ElementKind.METHOD){
                    ClassName  viewip = ClassName.bestGuess("android.view.View");
                    ClassName debouncinterface = ClassName.bestGuess("com.yx.easybutterknifelib.DebouncingOnClickListener");
                    break;
                }
            }
            MethodSpec.Builder methodOnClickBuilder = MethodSpec.methodBuilder("onClick")
                    .addAnnotation(Override.class)
                    .addModifiers(PUBLIC)
                    .addParameter(ClassName.class,"target")
                    .returns(TypeName.VOID);

            MethodSpec.Builder methodBindBuilder = MethodSpec.methodBuilder("bind")
                    .addAnnotation(Override.class)
                    .addModifiers(PUBLIC)
                    .addParameter(ClassName.class,"target")
                    .returns(TypeName.VOID);
            for (Element element : elementList) {
                if(element.getKind()== ElementKind.METHOD){
                    ExecutableElement variableElement = (ExecutableElement) element;
                    String s = variableElement.getSimpleName().toString();
                    int[] value = variableElement.getAnnotation(OnClick.class).value();
                    for (int i : value) {
                        methodOnClickBuilder.addStatement("View  view_$S = target.findViewById($S)",i);


                    }
                }
            }
        }

           /* //导包
            ClassName  ARouterClass = ClassName.bestGuess("com.yx.arouterx.ARouter");
            ClassName  IRouterInterface = ClassName.bestGuess("com.yx.arouterx.IRouter");
            //创建方法
            MethodSpec.Builder methodBuilder = MethodSpec.methodBuilder("putActivity")
                    .addAnnotation(Override.class)
                    .addModifiers(PUBLIC)
                    .returns(TypeName.VOID);
            Iterator<String> iterator = map.keySet().iterator();
            while (iterator.hasNext()){
                String key = iterator.next();
                String value = map.get(key);
                methodBuilder.addStatement("$T.getInstance().putActivity($S,$N.class)",ARouterClass,key,value);
            }
            //创建类
            String utilName = "ActivityUtil_"+System.currentTimeMillis();
            TypeSpec typeSpec = TypeSpec.classBuilder(utilName)
                    .addSuperinterface(IRouterInterface)
                    .addModifiers(PUBLIC)
                    .addMethod(methodBuilder.build())
                    .build();
            //开始写
            try {
                JavaFile.builder("com.yx.util",typeSpec).build().writeTo(filer);
            } catch (IOException e) {
                e.printStackTrace();
            }*/

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
          //  goWriteByJavaPoet(elmentMap);


            Writer writer = null;
            Iterator<String> iterator = elmentMap.keySet().iterator();
            while (iterator.hasNext()){
                String ClassName = iterator.next();
                List<Element> elements = elmentMap.get(ClassName);
                TypeElement typeElement = (TypeElement) elements.get(0).getEnclosingElement();
                String packageName = processingEnv.getElementUtils().getPackageOf(typeElement).toString();
                try {
                    JavaFileObject javaFileObject = filer.createSourceFile(packageName + "." + ClassName + "_ViewBinding");
                    writer = javaFileObject.openWriter();
                    writer.write("package "+packageName+";\n");
                    writer.write("import com.yx.easybutterknifelib.IBind;\n");
                    for (Element element : elements) {
                        if(element.getKind()== ElementKind.METHOD){
                            writer.write("import android.view.View;\n");
                            writer.write("import com.yx.easybutterknifelib.DebouncingOnClickListener;\n");
                            break;
                        }
                    }
                    writer.write("public class "+ClassName+"_ViewBinding implements IBind<"+ClassName+">{\n");
                    writer.write(" @Override\n" +
                            "    public void onClick(final "+ClassName+" target) {\n"
                    );
                    for (Element element : elements) {
                        if(element.getKind()== ElementKind.METHOD){
                            ExecutableElement variableElement = (ExecutableElement) element;
                            String s = variableElement.getSimpleName().toString();
                            int[] value = variableElement.getAnnotation(OnClick.class).value();
                            for (int i : value) {
                                writer.write("View  view_"+i+" = target.findViewById("+i+");\n");
                                writer.write("view_"+i+".setOnClickListener(new DebouncingOnClickListener() {\n" +
                                        "                        @Override\n" +
                                        "                        public void doClick(View p0) {\n" +
                                        "                            target."+s+"(p0);\n" +
                                        "                        }\n" +
                                        "                    });\n");
                            }
                        }
                    }
                    writer.write("}\n  @Override\n"+"      public void bind("+ClassName+" target) {\n");
                    for (Element element : elements) {
                        if(element.getKind()== ElementKind.FIELD){
                            VariableElement variableElement = (VariableElement) element;
                            String variableName = variableElement.getSimpleName().toString();
                            int id = variableElement.getAnnotation(BindView.class).value();
                            TypeMirror type = variableElement.asType();
                            writer.write("target."+variableName+" = ("+type+")target.findViewById("+id+");\n");
                        }
                    }
                    writer.write("}\n}\n");
                } catch (IOException e) {
                    e.printStackTrace();
                }finally {
                    if(writer!=null){
                        try {
                            writer.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
    }


}
