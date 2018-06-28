# Critical Functions
Collect critical function calls in error code context

## DATA

The data we use is from [VulDeePecker](https://github.com/CGCL-codes/VulDeePecker).

It collected several code slices from open source projects and SARD.
Then divide data set into 2 groups according to error type, CWE119 and CWE399.

## Prerequest

ANTLR 

## Process

Use ANTLR to parse code slices and extract function calls from it.
