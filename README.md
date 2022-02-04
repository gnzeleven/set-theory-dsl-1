### Implementation of a Domain Specific Language to Create and Evaluate Set Theory Operations

<b><i>Anandavignesh Velangadu Sivakumar
662139789</i></b>

#### Description: 

Create a language for users of the set theory to create and evaluate binary operations on sets using variables and scopes where elements of the sets can be objects of any type. The goal is to implement a domain-specific language(DSL) for writing and evaluating set operation expressions. Through the DSL, users must be able to describe and evaluate binary operations on sets using variables and scopes and the elements of the set can be objects of any type.

#### Overview

Following operations are to be implemented

- Insert into and delete an object from a set.
- Union of the sets A and B, denoted A ∪ B, is the set of all objects that are a member of A, or B, or both.
- Intersection of the sets A and B, denoted A ∩ B, is the set of all objects that are members of both A and B.
- Set difference of U and A, denoted U \ A, is the set of all members of U that are not members of A.
- Symmetric difference of sets A and B, denoted A ⊖ B, is the set of all objects that are a member of exactly one of A and B (elements which are in one of the sets, but not in both). For instance, for the sets {1, 2, 3} and {2, 3, 4}, the symmetric difference set is {1, 4}. It is the set difference of the union and the intersection, (A ∪ B) \ (A ∩ B) or (A \ B) ∪ (B \ A). 
- Cartesian product of A and B, denoted A × B, is the set whose members are all possible ordered pairs (a, b), where a is a member of A and b is a member of B.

Here's an example,
```scala
//creating a set and populating it with objects. The operation Assign locates a set object
//given its name or creates a new one if it does not exist. The second parameter is the
//operation Insert that adds objects to the set. The first parameter of the operation Insert
//is an object that is referenced by the variable var, the second is an integer and the third is a string.
Assign(Variable("someSetName"), Insert(Variable("var"), Value(1)), Value("somestring"))
//check if an object is in the set
Check("someSetName", Value(1)) //it should return the boolean value true
//in this example we define a macro and use it in the set operation to delete an object
//referenced by the variable "var"
Macro("someName", Delete(Variable("var")))
Assign(Variable("someSetName"), Macro("someName"))
//this example shows how users can create scope definition and use
Scope("scopename", Scope("othername", Assign(Variable("someSetName"), Insert(Variable("var"), Value(1)), Value("somestring"))))
Assign(Scope("scopename", Scope("othername", Variable("someSetName"))), Insert(Value("x")))
```

#### Functionality

A set as a collection of well defined objects which are distinct from each other, thus a set can be defined as a function - S: Object => Boolean. That is, given an Object, it maps the Object to a Boolean, **true** if the object is in the set and **false** otherwise. Hence, the underlying data structure can be some collection classes like List or Map to store the objects.

In the DSL, scopes can be created dynamically as part of the expressions in addition to being predefined in the environment. Macros will use lazy evaluation to substitute the expression for a given macro name in the expressions where the macro name is used.

This requirement can be divided roughly into five steps. 
1) Design a DSL for set theory operations evaluation. Add the logic for binding set objects to variables. 
2) Create an implementation of scopes, named and anonymous with scoping rules for obscuring and shadowing that you define to resolve the values of variables that have the same names in expressions. 
3) Create macros to substitute macro definitions for the used macro names in expressions. 
4) Add Scalatest tests to verify the correctness of your implementation. 
5) Write a report to explain your implementation and the semantics of your language.

#### Installation

!----------------- ToDo -------------------------!

#### Project Structure

!----------------- ToDo -------------------------!

#### Implementation

!----------------- ToDo -------------------------!

#### Results

!----------------- ToDo -------------------------!

#### Resources

!----------------- ToDo -------------------------!