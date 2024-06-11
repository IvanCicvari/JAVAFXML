package com.example.java2project.utils;

import javafx.scene.control.Alert;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class DocumentationUtils {
    public static void generateHtmlDocumentationFile() {

        try {
            List<String> listOfClassFilePaths = Files.walk(Paths.get("target"))
                    .map(Path::toString)
                    .filter(f -> f.endsWith(".class"))
                    .filter(f -> !f.endsWith("module-info.class"))
                    .toList();

            String htmlHeader = """
                <!DOCTYPE html>
                <html>
                <head>
                <title>Page Title</title>
                <style>
                    /* Add your CSS styles here */
                    .class-section {
                        margin-bottom: 20px;
                    }
                    .class-header {
                        cursor: pointer;
                        background-color: #f0f0f0;
                        padding: 10px;
                        border: 1px solid #ddd;
                        background-color: #ff7f7f;
                        color: white;
                        
                    }
                    .class-body {
                        display: none;
                        padding: 10px;
                        border: 2px solid #ddd;
                        
                    }
                </style>
                <script>
                    function toggleClass(className) {
                        var classBody = document.getElementById(className);
                        classBody.style.display === 'none' ? classBody.style.display = 'block' : classBody.style.display = 'none';
                    }
                </script>
                </head>
                <body>
                """;

            for (String classFilePath : listOfClassFilePaths) {
                String[] pathTokens = classFilePath.split("classes");
                String secondToken = pathTokens[1];
                String fqnWithSlashes = secondToken.substring(1, secondToken.lastIndexOf('.'));
                String fqn = fqnWithSlashes.replace('\\', '.');
                Class<?> deserializedClass = Class.forName(fqn);

                String className = deserializedClass.getSimpleName();
                htmlHeader += "<div class=\"class-section\">";
                htmlHeader += "<div class=\"class-header\" onclick=\"toggleClass('" + className + "')\">" + className + "</div>";
                htmlHeader += "<div id=\"" + className + "\" class=\"class-body\">";
                htmlHeader += "<p>Package: " + deserializedClass.getPackageName() + "</p>";
                htmlHeader += "<p>Superclass: " + deserializedClass.getSuperclass().getName() + "</p>";


                // Retrieve methods and write them to HTML
                Method[] methods = deserializedClass.getDeclaredMethods();
                for (Method method : methods) {
                    htmlHeader += "<p>";
                    int modifiers = method.getModifiers();
                    if (Modifier.isPublic(modifiers)) {
                        htmlHeader += "public ";
                    } else if (Modifier.isPrivate(modifiers)) {
                        htmlHeader += "private ";
                    } else if (Modifier.isProtected(modifiers)) {
                        htmlHeader += "protected ";
                    }
                    if (Modifier.isStatic(modifiers)) {
                        htmlHeader += "static ";
                    }
                    String returnType = method.getReturnType().getName();
                    htmlHeader += returnType + " ";
                    htmlHeader += method.getName() + "(";
                    Class<?>[] parameterTypes = method.getParameterTypes();
                    for (int i = 0; i < parameterTypes.length; i++) {
                        htmlHeader += parameterTypes[i].getName();
                        if (i < parameterTypes.length - 1) {
                            htmlHeader += ", ";
                        }
                    }
                    htmlHeader += ")</p>";
                }

                // Retrieve fields and write them to HTML as before
                Field[] fields = deserializedClass.getDeclaredFields();
                for (Field field : fields) {
                    int modifiers = field.getModifiers();

                    if (Modifier.isPublic(modifiers)) {
                        htmlHeader += "public ";
                    } else if (Modifier.isPrivate(modifiers)) {
                        htmlHeader += "private ";
                    } else if (Modifier.isProtected(modifiers)) {
                        htmlHeader += "protected ";
                    }

                    if (Modifier.isStatic(modifiers)) {
                        htmlHeader += "static ";
                    }

                    if (Modifier.isFinal(modifiers)) {
                        htmlHeader += "final ";
                    }

                    String type = field.getType().getTypeName();
                    htmlHeader += type + " ";

                    String name = field.getName();
                    htmlHeader += name + "<br>";
                }
                htmlHeader += "</div>"; // Close class-body div
                htmlHeader += "</div>"; // Close class-section div
            }

            String htmlFooter = """
                </body>
                </html>
                """;
            Path htmlDocumentationFile = Path.of("documentation.html");
            String fullHtml = htmlHeader + htmlFooter;
            Files.write(htmlDocumentationFile, fullHtml.getBytes());
            DialogUtils.showDialog(Alert.AlertType.INFORMATION, "File created!",
                    "Creation of HTML documentation file succeeded!");
        } catch (IOException | ClassNotFoundException e) {
            DialogUtils.showDialog(Alert.AlertType.INFORMATION, "File not created!",
                    "Creation of HTML documentation file failed!");
        }
    }
}
