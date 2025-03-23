package testutils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.TypeDeclaration;
import com.github.javaparser.ast.expr.MethodCallExpr;

public class AutoGrader {

	// Test if the code implements multilevel inheritance correctly
	public boolean testMultilevelInheritance(String filePath) throws IOException {
		System.out.println("Starting testMultilevelInheritance with file: " + filePath);

		File participantFile = new File(filePath); // Path to participant's file
		if (!participantFile.exists()) {
			System.out.println("File does not exist at path: " + filePath);
			return false;
		}

		FileInputStream fileInputStream = new FileInputStream(participantFile);
		JavaParser javaParser = new JavaParser();
		CompilationUnit cu;
		try {
			cu = javaParser.parse(fileInputStream).getResult()
					.orElseThrow(() -> new IOException("Failed to parse the Java file"));
		} catch (IOException e) {
			System.out.println("Error parsing the file: " + e.getMessage());
			throw e;
		}

		System.out.println("Parsed the Java file successfully.");

		// Use AtomicBoolean to allow modifications inside lambda expressions
		AtomicBoolean animalClassFound = new AtomicBoolean(false);
		AtomicBoolean mammalClassFound = new AtomicBoolean(false);
		AtomicBoolean dogClassFound = new AtomicBoolean(false);
		AtomicBoolean mammalExtendsAnimal = new AtomicBoolean(false);
		AtomicBoolean dogExtendsMammal = new AtomicBoolean(false);
		AtomicBoolean speakMethodImplemented = new AtomicBoolean(false);
		AtomicBoolean moveMethodImplemented = new AtomicBoolean(false);
		AtomicBoolean methodsExecuted = new AtomicBoolean(false);

		// Check for class implementation and inheritance (Dog extends Mammal, Mammal
		// extends Animal)
		System.out.println("------ Inheritance and Class Implementation Check ------");
		for (TypeDeclaration<?> typeDecl : cu.findAll(TypeDeclaration.class)) {
			if (typeDecl instanceof ClassOrInterfaceDeclaration) {
				ClassOrInterfaceDeclaration classDecl = (ClassOrInterfaceDeclaration) typeDecl;

				if (classDecl.getNameAsString().equals("Animal")) {
					System.out.println("Class 'Animal' found.");
					animalClassFound.set(true);
				}

				if (classDecl.getNameAsString().equals("Mammal")) {
					System.out.println("Class 'Mammal' found.");
					mammalClassFound.set(true);
					// Check if Mammal extends Animal
					if (!classDecl.getExtendedTypes().isEmpty()
							&& classDecl.getExtendedTypes(0).getNameAsString().equals("Animal")) {
						mammalExtendsAnimal.set(true);
						System.out.println("Mammal extends 'Animal'.");
					} else {
						System.out.println("Error: 'Mammal' does not extend 'Animal'.");
					}
				}

				if (classDecl.getNameAsString().equals("Dog")) {
					System.out.println("Class 'Dog' found.");
					dogClassFound.set(true);
					// Check if Dog extends Mammal
					if (!classDecl.getExtendedTypes().isEmpty()
							&& classDecl.getExtendedTypes(0).getNameAsString().equals("Mammal")) {
						dogExtendsMammal.set(true);
						System.out.println("Dog extends 'Mammal'.");
					} else {
						System.out.println("Error: 'Dog' does not extend 'Mammal'.");
					}
				}
			}
		}

		// Ensure all classes are found
		if (!animalClassFound.get() || !mammalClassFound.get() || !dogClassFound.get()) {
			System.out.println("Error: One or more classes (Animal, Mammal, Dog) are missing.");
			return false; // Early exit if class creation is missing
		}

		// Ensure Mammal extends Animal, and Dog extends Mammal
		if (!mammalExtendsAnimal.get()) {
			System.out.println("Error: 'Mammal' must extend 'Animal'.");
			return false;
		}
		if (!dogExtendsMammal.get()) {
			System.out.println("Error: 'Dog' must extend 'Mammal'.");
			return false;
		}

		// Check for method overriding (speak and move methods)
		System.out.println("------ Method Override Check ------");
		for (MethodDeclaration method : cu.findAll(MethodDeclaration.class)) {
			if (method.getNameAsString().equals("speak") && method.getParentNode().get().toString().contains("Dog")) {
				speakMethodImplemented.set(true);
				System.out.println("Method 'speak' overridden in 'Dog' class.");
			}
			if (method.getNameAsString().equals("move") && method.getParentNode().get().toString().contains("Mammal")) {
				moveMethodImplemented.set(true);
				System.out.println("Method 'move' overridden in 'Mammal' class.");
			}
		}

		if (!speakMethodImplemented.get() || !moveMethodImplemented.get()) {
			System.out.println("Error: One or more methods ('speak', 'move') not overridden in appropriate classes.");
			return false;
		}

		// Check if both methods are executed in main
		System.out.println("------ Method Execution Check in Main ------");
		for (MethodDeclaration method : cu.findAll(MethodDeclaration.class)) {
			if (method.getNameAsString().equals("main")) {
				if (method.getBody().isPresent()) {
					method.getBody().get().findAll(MethodCallExpr.class).forEach(callExpr -> {
						if (callExpr.getNameAsString().equals("speak") || callExpr.getNameAsString().equals("move")) {
							methodsExecuted.set(true);
							System.out.println("Methods 'speak' and 'move' are executed in the main method.");
						}
					});
				}
			}
		}

		if (!methodsExecuted.get()) {
			System.out.println("Error: Methods 'speak' and 'move' not executed in the main method.");
			return false;
		}

		// If inheritance, method overriding, and method execution are correct
		System.out.println("Test passed: Multilevel inheritance is correctly implemented.");
		return true;
	}
}
