# Testforshift

Console application — a file content filtering utility.
When the utility is launched from the command line, several files are provided as input, containing a mix of integers, strings, and floating-point numbers.
The newline character is used as the delimiter. The task of the utility is to write different data types into separate files:
- integers into one output file,
- floating-point numbers into another,
- strings into a third file.

## Requirements
- Java 21
- Maven 3.x

## Build
Clone the repository and run:

```
mvn clean package
```

This will produce a runnable jar in the target folder:

```
target/testforshift-1.0.0.jar
```

## Run

Prepare your input files (for example in1.txt and in2.txt) and place them in the same folder as the jar.
Then run:

```
java -jar target/testforshift-1.0.0.jar [options] in1.txt in2.txt
```

## Command line options

**-o <path>** — set output directory for output files (default: current working directory).

**-p <prefix>** — set prefix for output file names.

Example: -o /some/path -p output_ → /some/path/output_integers.txt, /some/path/output_floats.txt, etc.

**-a** — append mode. Adds new lines to existing output files instead of overwriting them.

**-s** — collect and print short statistics. 

**-f** — collect and print full statistics.

Short statistics contain only the number of elements written to the output files. 

Full statistics for numbers additionally include the minimum and maximum values, the sum, and the average. Full statistics for strings, in addition to their count, also include the length of the shortest string and the longest string.

Options can be combined. For example:

```
java -jar target/testforshift-1.0.0.jar -o results -p sample_ -f in1.txt in2.txt
```

## Technologies
- Java version: 21
- Build system: Apache Maven 3.8.6
- Framework: Spring Boot 4.0.0-M2
- Third-party libraries:
  - Apache Commons CLI 1.9.0
  - Lombok 1.18.24 (managed by Spring Boot BOM)
