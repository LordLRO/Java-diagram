package Analysis;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.File;
import java.util.ArrayList;
import java.util.regex.Matcher; 
import java.util.regex.Pattern; 

//containing package features
public class PackageContents {
    protected String packageName;
    protected String fileContent;
    protected ArrayList<ClassContents> classList = new ArrayList();
    
//    public void showClassList() {
//        for(ClassContents clazz: classList) {
//            clazz.showClassContent();
//            System.out.println();   
//        }
//    }

    public ArrayList<ClassContents> getClassList() {
        return classList;
    }
    
    public String getPackageName() {
        return packageName;
    }

    public void readFile(final File file) {
        String str = "";
        try {
            String temp;
            BufferedReader reader = new BufferedReader(new FileReader(file));
            while ((temp = reader.readLine()) != null) {
                str += temp + "\n";
            }
        } catch (IOException e) {
            System.out.println("File not found");
        }
        fileContent = str;      
        handleFileContent();
        setPackageName();
        findClass(new StringBuilder(fileContent));
//        showFileContent();
    }
    
    void showFileContent() {
        System.out.println(fileContent);
    }
    
    //Delete something 
    public void handleFileContent() { 
        String eraseStringPattern;
        
        eraseStringPattern = "\\\\\""; // delete " in String for example String a = " \" "; -> String a = " "; 
        deleteSomething(eraseStringPattern);
        
        eraseStringPattern = "(\"[^(\n)]*?\")|(//.*?\n)";  // delete comment after // and a part of content inside " " 
        deleteSomething(eraseStringPattern);
        
        eraseStringPattern = "(\".*?\")";    //delete remaining content in " "
        deleteSomething(eraseStringPattern);
        
        eraseStringPattern = "(/\\*(.*\n)*?.*\\*/)"; // delete comment in /* */     expected: (/\\*(.*\n)*(.*)\\*/)
        deleteSomething(eraseStringPattern);
        
        eraseStringPattern = "^\\s*\n$";  //delete blank line
        deleteSomething(eraseStringPattern);
        
        eraseStringPattern = "throws\\s+[^{]+";    // delete all throws
        deleteSomething(eraseStringPattern);
                
        eraseInsideMethodBlock();   
        
        eraseInsideEnumBlock();
        
        eraseStringPattern = "return.*?;";
        deleteSomething(eraseStringPattern);
        
        eraseStringPattern = "\\([^;]*?\\)"; // erase all inside ( )
        deleteSomething(eraseStringPattern, "()");
        
        eraseStringPattern = "=[^;]+,";     //erase variable value such as int a = 10, b; -> int a, b;
        deleteSomething(eraseStringPattern, ",");
        
        eraseStringPattern = "=[^;]+;";      //erase variable value such as int a =10; -> int a;
        deleteSomething(eraseStringPattern, ";");     
//        showFileContent();
        
    }
    
    public void deleteSomething(String regexPattern) {
        Pattern eraseString = Pattern.compile(regexPattern);
        Matcher m = eraseString.matcher(fileContent);
        fileContent = m.replaceAll(""); 
    }
    public void deleteSomething(String regexPattern, String replacedBy) {
        Pattern eraseString = Pattern.compile(regexPattern);
        Matcher m = eraseString.matcher(fileContent);
        fileContent = m.replaceAll(replacedBy); 
    }

    public void eraseInsideMethodBlock() {
        String methodPattern = "(\\s+|^|\\{)((?<modifier>public|private|protected)\\s+)?(?<static>static\\s+)?(?<type>((\\w|\\.)+\\s*(<[^>]*?(<[^<]*>)?[^<]*?>)?(\\[\\])?))\\s+(?<methodName>\\w+)\\s*\\([^\\(]*\\)\\s*\\{";
        Pattern method = Pattern.compile(methodPattern);
        Matcher m = method.matcher(fileContent);
        String temp = new String(fileContent);
        while (m.find()) {
//            System.out.println(m.group("methodName"));
            int count = 1;
            for (int i = m.end(); ; i++) {
                if (fileContent.charAt(i) == '{') count++;
                if (fileContent.charAt(i) == '}') {
                    count--;
                    if (count == 0) {
//                        System.out.println(fileContent.substring(m.end(), i - 1));
                        temp = temp.replace(fileContent.substring(m.end(), i), "");
                        break;
                    }
                }
            }
        }
        fileContent = temp;
    }
    
    public void eraseInsideEnumBlock() {
        String methodPattern = "(\\s+|^|\\{)((?<modifier>public|private|protected)\\s+)?(?<static>static\\s+)?enum\\s*(?<enumName>\\w+)\\s*\\{";
        Pattern method = Pattern.compile(methodPattern);    
        Matcher m = method.matcher(fileContent);
        String temp = new String(fileContent);
        while (m.find()) {
//            System.out.println(m.group("enumName"));
            int count = 1;
            for (int i = m.end();  ; i++) {
                if (fileContent.charAt(i) == '{') count++; else
                if (fileContent.charAt(i) == '}') {
                    count--;
                    if (count == 0) {
//                        System.out.println(fileContent.substring(m.end(), i - 1));
                        temp = temp.replace(fileContent.substring(m.end(), i), "");
                        break;
                    }
                }
            }
        }
        fileContent = temp;
    }
    
    private void setPackageName() {
        String packagenamePattern = "(^|\\s)package\\s+(?<packageName>[^;]+)\\s*;";
        Pattern className = Pattern.compile(packagenamePattern);
        Matcher m = className.matcher(fileContent);
        if (m.find()) 
            packageName = m.group("packageName");
        else 
            packageName = "default";
    }
    
    public void findClass(StringBuilder content) { // implements multiple interfaces not solved yet
        String classNamePattern = "(?<interfaceORclass>(class|interface))\\s+(?<className>\\w+(\\s*<.*?>)?)\\s*(extends\\s+(?<extendsParent>[^{]+?)\\s*)?(implements\\s+(?<implementsParent>[^{]+)\\s*)?\\{";
        Pattern className = Pattern.compile(classNamePattern);
        StringBuilder contentClone = new StringBuilder(content);
        Matcher m = className.matcher(contentClone);
        while (m.find()) {
            //check if class was found or not
            if ( ! classWasFound(m.group("className"))) {
                ClassContents classFound = new ClassContents();
                //set class name
                classFound.setClassName(m.group("className"));
//                System.out.println(classFound.getClassName());
                //set isAbstract
                classFound.setclassORinterface(m.group("interfaceORclass"));
                //set extends
                if (m.group("extendsParent") != null){
                    ArrayList<String> extendList = new ArrayList<>();
                    if (m.group("extendsParent").contains(",")) {
                        String[] tokens = m.group("extendsParent").split(",");
                        for (String token: tokens) 
                            extendList.add(token.trim());
                    } else
                        extendList.add(m.group("extendsParent").trim());
                    classFound.setExtendsInheritance(extendList);
                }
                //set implements
                if (m.group("implementsParent") != null) {
                    ArrayList<String> implementList = new ArrayList<>();
                    if (m.group("implementsParent").contains(",")) {
                        String[] tokens = m.group("implementsParent").split(",");
                        for (String token: tokens) 
                            implementList.add(token.trim());
                    } else
                        implementList.add(m.group("implementsParent").trim());
                    classFound.setImplementsInheritance(implementList);
                }
                //find position of class in fileContent
                //and create a String that contains only class content
                int count = 1;
                StringBuilder classContent;
                for (int i = m.end();  ; i++) {   
//                    System.out.print(contentClone.charAt(i));                 
                    if (contentClone.charAt(i) == '{') count++;
                    if (contentClone.charAt(i) == '}') {
                        count--;
                        if (count == 0) {
                            classContent = new StringBuilder(contentClone.substring(m.end(), i));
                            //find any subclass of current found class
                            findClass(classContent);
                            /*  delete this found class 
                             *  to prevent the class containing current class from 
                             *  finding methods and vars of this class
                             */
                            String sub = contentClone.substring(m.end(), i); 
                            content.delete(content.indexOf(sub), content.indexOf(sub) + sub.length());
//                            System.out.println(classFound.getClassName() + "\n" + content + "\n\n\n\n\n\n\n");
                            break;
                        }
                    }
                }
            
                //set methods
                classFound.setMethods(getMethod(classContent.toString(), classFound.getClassName()));
                //set varaables
                classFound.setVariables(getVariable(classContent.toString()));
                //add found class to class List
                classList.add(classFound);
            }            
        }
    }
    
    public boolean classWasFound(String className) {
        boolean found = false;
        for(ClassContents clazz: classList) {
            if (clazz.getClassName().equals(className))
                found = true;
        }
        return found;
    }
    
    public ArrayList<Feature> getMethod (String clazz, String className) {
        ArrayList<Feature> methodsList = new ArrayList<>();
        String methodNamePattern = "(\\s+|^|\\{)((?<modifier>public|private|protected)\\s+)?(?<static>static\\s+)?(?<type>((\\w|\\.)+\\s*(<[^>]*?(<[^<]*>)?[^<]*?>)?(\\[\\])?))\\s+(?<methodName>\\w+)\\s*\\([^\\(]*\\)\\s*[\\{;]";    //not getting parameter
//        String methodNamePattern = "(\\s+|^|\\{)((?<modifier>public|private|protected)\\s+)?(?<static>static\\s+)?(?<type>(\\w+\\s*((<.*>)|(\\[\\]))?))\\s+(?<methodName>(\\w+\\s*\\([^\\(]*\\)))\\s*\\{";    //getting parameter
        Pattern methodName = Pattern.compile(methodNamePattern);
        Matcher m = methodName.matcher(clazz);
        while (m.find()) {
            if (!m.group("methodName").equals(className)) {
                Feature methodFound = new Feature();
                methodFound.setModifier( m.group("modifier") == null ? "default" : m.group("modifier") );
                methodFound.setName(m.group("methodName"));
                methodFound.setType(m.group("type"));
                methodsList.add(methodFound);
            }            
        }
        return methodsList;
    }
    
    public ArrayList<Feature> getVariable(String clazz) {
        ArrayList<Feature> variablesList = new ArrayList<>();
        String variablePattern = "((?<modifier>public|private|protected)\\s+)?(static\\s+)?(final\\s+)?(?<type>((\\w|\\.)+s*(<[^>]*?(<[^<]*>)?[^<]*?>)?(\\[\\])?))\\s+(?<variableName>[\\w,]+)\\s*;";
        Pattern variable = Pattern.compile(variablePattern);
        Matcher m = variable.matcher(clazz);
        while (m.find()) {
            if (m.group("variableName").contains(",")) {
                String[] tokens = m.group("variableName").split(",");
                for(String token: tokens) {
                    Feature variableFound = new Feature();
                    variableFound.setModifier(m.group("modifier"));
                    variableFound.setName(token.trim());
                    variableFound.setType(m.group("type"));
                    variablesList.add(variableFound);
                }
            } else {
                Feature variableFound = new Feature();
                variableFound.setModifier(m.group("modifier"));
                variableFound.setName(m.group("variableName").trim());
                variableFound.setType(m.group("type"));
                variablesList.add(variableFound);
            }
        }
        return variablesList;
    }
}