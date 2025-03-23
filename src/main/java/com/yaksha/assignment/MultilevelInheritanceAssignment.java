package com.yaksha.assignment;

// Animal class - Base class in the hierarchy
class Animal {
    String species;

    public Animal() {
        species = "Unknown species"; // Default species
    }

    public void speak() {
        System.out.println("The animal makes a sound.");
    }
}

// Mammal class - Inherits from Animal
class Mammal extends Animal {

    public void move() {
        System.out.println("The mammal moves.");
    }
}

// Dog class - Inherits from Mammal
class Dog extends Mammal {

    @Override
    public void speak() {
        System.out.println("The dog barks.");
    }
}

public class MultilevelInheritanceAssignment {
    public static void main(String[] args) {
        Dog dog = new Dog(); // Creating a Dog object
        dog.speak(); // Should print "The dog barks." as overridden in Dog
        dog.move();  // Should print "The mammal moves." from Mammal class
    }
}
