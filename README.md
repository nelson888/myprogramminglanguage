# groovy_compiler

## Description
This a simple compiler written in Groovy

## How to compile
This project compiles with gradle
Run 
```
./gradlew uberjar
```
Note that the gradlew script was made for an Unix environment. It might not work on other OS.

## How to run
Once you've compiled the jar file, you can execute it with java such as below
```
java -jar build/libs/interpreter-1.0.jar
```

This will pop a window where you can write code and execute it
directly. A program consists of a list of functions. There has to be a main function, this is the function
that will be executed. 

The output will be display on the console, in the bottom of the window

## What is supported (currently)

### Types

## Integer

## Float

## Character

## String

## Boolean
TODO

## Constants variables
not for array
example:
const int a = 2;
## Arrays
array declaration: taille optionnelle

### Arithmetic expressions
It supports basic and boolean operators
e.g:
```python
4 + 2
4 - (6 * 4 / 2)
5 % (4 - 4 * (5 - 3))
(1 >= 2) or (3 != 2)
(3 and 0) == (0 or 0)
```

### Variables
You can declare, and assign values to variables:
```javascript
var x;
x = 14;
var y = 3;
var z = x + 5* y;
```
### for and while loops
for and while loops are handled, like in the following example
```java
for (int i = 0; i < 10; i++) {
    print(i);
}
```

```javascript
var i = 0;
while (i < 10) {
    print(i);
    i = i + 1;
}
```
You can print the value of variable with the `print` function


## Other features

### final variables
TODO

### switch
You can use `switch` like in the following example:


```java
switch (a) {
  default:
    print("No match");
  case 1:
    print("1 case");
    break;
  case 2:
  case 3:
    print("2 and 3case");
  case 4:
    print("4 case");
}
```

### String as char[]
string can be considered as char array, and are therefore mutable

```java
string s = "hello";
s[0] = 'y';
char c = s[2];
print(s);
```
TODO: itialization of assignement between char[] string and vice-versa
### Break, Continue
`break` and `continue` are handled. Here are a few example
```java
int a = 2;
while(a < 10) {
  if (a % 2 == 0) {
    continue;
  }
  if (a == 5) {
    break;
  }
  a = somethingElse();
}
```


```java
switch (a) {
  case 1:
    print("1");
    break;
  case 2:
    print("2");
    break;
}
```

### Ternary
You can also use expressions like
```java
a >= b ? a : b
```

## Error handling
When there is an error while compiling, the error is displayed on the console
You can see what caused the error (the line and column of the error is displayed but it is not working, sometimes there is a little offset for the column).

##Write a program
A source code should have functions, with one named main() with no parameters like in the following example

```
add1(a) {
    return a + 1;
}

main() {
   print add1(6);
}
```

The main() function will be executed