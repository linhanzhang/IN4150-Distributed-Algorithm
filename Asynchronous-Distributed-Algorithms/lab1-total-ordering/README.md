# IN4150-Lab1

IN4150 Distributed Algorithms Lab Exercise 1C:

Implementation of total message ordering of broadcast messages with Java/RMI(Algorithm 3.18 of the lecture notes)

Group 9:
- Hanzhang Lin
- Katriel Ester Amanda

Instructions:
1. Compile
```console
me:IN4150-Lab1 me$ javac *.java
```
2. Run main file
```console
me:IN4150-Lab1 me$ java -Djava.security.policy=my.policy DA_TMO_main.java
```
3. Enter command:
- 'exit'
- 'test1': 3 processes, no delay
- 'test2': 3 processes, P0 no delay, P1 and P2 with random delays
- 'test3': 5 processes, 5 processes, all with random delays
- 'test4': 3 processes, each sends 2 messages (with random delays)
4. Check process logs in the corresponding txt files (process-n.txt). Note: process number starts at 0, message number starts at 1.