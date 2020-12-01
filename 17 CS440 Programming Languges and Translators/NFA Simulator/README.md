# CS440 Project 2

## Introduction

In this project, we implemented an NFA simulator that could perform the following functionalities:
* Read a NFA description and an input string
* Runs the NFA to see if the input string is accepted
* At each NFA step, print out a set of state(s) the NFA is in and the current input symbol
* At the end of the input, print out `accepted` if the final set of state(s) contains the accpeting state(s), `rejected` otherwise..

## Implementation Principles

The program consists two main parts:

* Reading an NFA description and an input string:
    * Reads `<.txt file>` line by line
    * Performs description sanity check. Exception raised if format of the description or the input string violated.

* Parsing the input string:
    * Print out the the starting set of states `esilon-clossure(0)`
    * Read the input string character by character
    * Print out the next set of state(s) giving the current state(s) and input character
    * Print out `accepted/rejected` upon the end of the input string

## How to run

* Put the `simulate_nfa.ml` and `.txt` file in the same directory

* Open `utop` or `ocaml` UI in the same folder by excuting command `utop` or `ocaml`

* In the iteractive UI, run `#use "simulate_nfa.ml";;` and run `simulate_nfa "<name of the .txt file>";;`

## How to test

* Execute command `ocamlbuild -pkgs oUnit simulate_nfa_test.byte`
* Execute command `./simulate_nfa_test.byte`   
