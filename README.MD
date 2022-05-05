# NTLM Crack

Java program intended for efficiently breaking NTLM hashes. Built with JDK 17.

## Description

The program accepts input in the form of files, the path of which should be marked using the flags described below.
The necessary inputs are:
- List of NTLM hashes, seperated by newlines.
- Wordlists for breaking the hashes.
**OR**
- Pre-generated or a custom table. NB **When providing a table you need to specifiy the delimiter**

> Provided files should be formatted in a way that every word/hash/pair is on a seperate line. Ex HASH1\nHASH2\nHASH3\nHASH4\n etc..

When a wordlist is provided, a table in the form of word:hash is generated. The table generation process is realized using the external merge sort algorithm, due to the fact that the provided wordlist may not fit into memory. Memory-allocation is done automatically. Due to the use of the external merging the process creates temporary files that consists of sorted pairs of words and hashes that are later combined into a larger table with K-way-merging. The merging takes place after all of the smaller files have been generated. **This should be a one-time process**, meaning that when you have already created a table using a specified wordlist, simply provide the table not the wordlist when running it again.

Regardless of whether the table was generated or provided, its handled the same. When starting the hash cracking process the program reads a specified amount of rows( default is 90000, but can be changed using the flags) into memory and uses binary search to determine whether any provided hashes match any of the hashes in memory. This is then repeated until the table is exhausted.

### Dependencies

The project is compiled into a .jar file, when running that the only requirement is Java.
Otherwise the program utilizes a couple of libraries using Maven.

### Installing & running

- Get the JDK
- Install Maven
- mvn install - builds jar (requires signing)

OR

Simply run the .jar file in "target".

### Executing program

```
java -jar PATH_TO\NtlmCrack.jar

FLAGS:

-s or --source: 	 Source for a list of hashes
-w or --wordlist: 	 Source for a wordlist
-t or --table: 	         Source for a custom/pre-generated table
-dl or --delimiter: 	 Separator for words and hashes in provided table (DEFAULT = "::")
-d or --destination: 	 Destination for the generated table (DEFAULT = ".\")
-n or --name:            Name for the generated table (DEFAULT = "genTable.txt")
-o or --outname: 	 Name for the file that stores cracked hashes
-mt or --maxtemp: 	 Amount of allowed temporary files (DEFAULT = 1024)
-mb or --maxblock:       Amount of rows read into memory when breaking hashes (DEFAULT = 90 000)
-h or --help: 	         Display the list of flags.

Ex.
java -jar .\NtlmCrack.jar -s "hashes.txt" -w "hashkiller.txt"
java -jar .\NtlmCrack.jar -s "hashes.txt" -t "table.txt" -dl "::"
```

## Authors

Me :)

