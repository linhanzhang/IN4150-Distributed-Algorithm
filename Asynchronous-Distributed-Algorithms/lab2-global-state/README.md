# IN4150-Lab2
IN4150 Distributed Algorithms Lab Exercise 2C:

Implementation of Chandy's and Lamport's algorithm for detecting global states in a distributed system with Java/RMI.

Group 9:

- Hanzhang Lin
- Katriel Ester Amanda

Instructions:
1. Compile
```console
me:IN4150-Lab2 me$ javac *.java
```
2. Run main file
```console
me:IN4150-Lab2 me$ java -Djava.security.policy=my.policy DA_CL_main.java
```
3. Enter command:
- 'exit'
- 'test1': 3 processes, each process send out 1 message
- 'test2': 3 processes, each sends multiple messages (P0:2, P1:4, P2:2), all with random delays
- 'test3': 5 processes, all with random delays

In all test cases, the Chandy-Lamport algorithm is called by P0.

4. Program will print out global state