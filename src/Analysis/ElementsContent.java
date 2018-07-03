package Analysis;

import java.util.ArrayList;

//This contains variables and methods feature
class Feature {
    protected String modifier;
    protected String type;
    protected String name;
    public Feature(){
        
    }
    public Feature(String a,String b,String c){
        modifier = a;
        type = b;
        name = c;
    }
    public void printFeature() {
        System.out.print( (modifier == null ? "" : modifier + " ") + type + " " + name);
    }
    public String getFeature(){
        return (modifier == null ? "" : modifier + " ") + type + " " + name;
    }
    public void setModifier(String modifier) {
        this.modifier = modifier;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getModifier() {
        return modifier;
    }

    public String getType() {
        return type;
    }

    public String getName() {
        return name;
    }
}

//This contains class feature
class ClassContents {
    protected String className;
    protected String classORinterface;
    protected ArrayList<String> extendsInheritance;
    protected ArrayList<String> implementsInheritance;
    protected ArrayList<Feature> variables;
    protected ArrayList<Feature> methods;    
    
    public ClassContents(){
        
    }
    public ClassContents(String a,String b,ArrayList<String> c,ArrayList<String> d,ArrayList<Feature> e,ArrayList<Feature> f){
        className = a;
        classORinterface = b;
        extendsInheritance = c;
        implementsInheritance = d;
        variables = e;
        methods = f;
    }
//    public void showClassContent() {
//        System.out.println( ( "\t" + classORinterface + ": ") + className);
//        if (extendsInheritance != null) {
//            System.out.println("\t\tExtends:"); 
//            for(String str: extendsInheritance) {
//                System.out.println("\t\t\t" + str);
//            }            
//        }
//        //print implements
//        if (implementsInheritance != null) {
//            System.out.println("\t\tImplements: ");
//            for(String str: implementsInheritance) {
//                System.out.println("\t\t\t" + str);
//            }
//        }        
//        //print variables
//        if(variables != null && variables.size() > 0) {
//            System.out.println("\t\tVariables: ");
//            for(Feature var: variables) {
//                System.out.print("\t\t\t");
//                var.printFeature();
//                System.out.println();
//            }
//        }        
//        //print methods
//        if(methods != null && methods.size() > 0) {
//            System.out.println("\t\tMethods: ");
//            for(Feature method: methods) {
//                System.out.print("\t\t\t");
//                method.printFeature();
//                System.out.print("()\n");
//            }     
//        }           
//    }

    public String getClassName() {
        return className;
    }

    public String getclassORinterface() {
        return classORinterface;
    }
    
    public ArrayList<String> getExtendsInheritance() {
        return extendsInheritance;
    }

    public ArrayList<String> getImplementsInheritance() {
        return implementsInheritance;
    }

//    public ArrayList<Feature> getVariables() {
//        return variables;
//    }
    public ArrayList<Feature> getVariables() {
        ArrayList<Feature> a = new ArrayList();
        a.add(new Feature(" "," "," "));
        return (variables != null && variables.size() > 0)? variables: a ;
    }
    
//    public ArrayList<Feature> getMethods() {
//        return methods;
//    }
    public ArrayList<Feature> getMethods() {
        ArrayList<Feature> a = new ArrayList();
        a.add(new Feature(" "," "," "));
        return (methods != null && methods.size() > 0)? methods : a ;
    }

    public void setClassName(String className) {
        this.className = className;
    }
    
    public void setclassORinterface(String str) {
        this.classORinterface = str;
    }

    public void setExtendsInheritance(ArrayList<String> extendsInheritance) {
        this.extendsInheritance = extendsInheritance;
    }

    public void setImplementsInheritance(ArrayList<String> implementsInheritance) {
        this.implementsInheritance = implementsInheritance;
    }

    public void setVariables(ArrayList<Feature> variables) {
        this.variables = variables;
    }

    public void setMethods(ArrayList<Feature> methods) {
        this.methods = methods;
    }     
    public String isRelate(ClassContents a){
        String test = null;
        if(extendsInheritance != null){
            for(String extend: extendsInheritance){
                if(extend.equals(a.getClassName())){
                    test = "extends";
                }
            }
        }
        if(test == null){
            if(implementsInheritance != null){
                for(String implement: implementsInheritance){
                    if(implement.equals(a.getClassName())){
                        test = "implements";
                    }
                } 
            }
        } 
        return test;
    }
}    