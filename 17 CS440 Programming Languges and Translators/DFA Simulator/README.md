# CS440 Project 1

## Introduction

In this project, we implemented a DFA simulator that reads a description of a DFA and an input string and 
runs the DFA to see whether or not it accepts the string. At each DFA step, we print out the state we are 
in and the terminal symbol we saw. At the end we print out the final state and whether or not it accepted.

## Implementation Principles

The program consists two main parts:

* Reading a DFA description and an input string:
    * Reads .txt file line by line
    * Performs descriptiom sanity check. **If violation(s) stated below detected, the program will halt, 
throw an exception, and stop reading the rest of the lines**. This separate of concern design simplifies the parsing functionality: 
        * The number of states should be greater than 0
        * The number of accepting states should be less than or equal to the number of states
        * One line for each accepting states with state number
        * The number of symbols should be greater than 0
        * The number of alphabet on the line they are in should be equal to the number of symbols
        * The number of transition cases should be greater than 0
        * One line for each transition case
        * The state in each transition case should be less than the number of states; the symbol should be
in the alphabet
        * The input string on the line they are in should contains symbols in the alphabet  

* Evaluating the transition cases against the input string
   * Loops through each character in the input string
   * Performs pattern matching on the list of transition cases for each character in the input string
   * Checks if the source state is equal to the current state and if the symbol matches the current character being evaluated
      * If true, update the state to the one specified in the transition case, print out the current state along with the input character, and move on to the next 
      character
      * If false, continue to the next transition case
   * After evaluating all of the transition cases, then it checks if the final state is one of the accepting states
      * If true, print out "Accepted"
      * If false, print out "Rejected"
      
* Debugging and testing the code:
   * Tests if it raises error when the input is not valid
      * Invalid number of states, accepting stats, symbols, transitions, and alphabet
      * Invalid format of input
   * Tests if the code reads DFA description and parses the input string correctly
      * Tests if the outcome matches the DFA description
      * Tests the pattern matching and transitions updates separately and while integrated
      * Tests if the outcome is correct for different cases of one accepting state and more

## How to run

* Put the `simulate_dfa.ml` and `.txt` file in the same directory

* Open `utop` or `ocaml` UI in the same folder by excuting command `utop` or `ocaml`

* In the iteractive UI, run `#use "simulate_dfa.ml";;` and run `simulate_dfa "<name of the .txt file>";;`   	
   
