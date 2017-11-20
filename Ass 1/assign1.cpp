#include <iostream>
#include <fstream>
#include <stdio.h>
#include <stdlib.h>

/******************************

Programmer: Joey Domino
Due Date: 9/4/2015 at 11:59 pm

This program will take a file at a given directory and copy its contents into a local file

*****************************/

using namespace std;

int main()
{
	ifstream inFile;			/* Set input Variable */
	inFile.open("/proc/cpuinfo");		/* Open given file */

	ofstream outFile;			/* Set output variable */
	outFile.open("cpuinfo.txt");		/* Open output location */

	string line;				/* Initialize string to get input lines */

	if(!inFile)
	{
		cerr << "Error, unable to open input file.\n";
	}

	if(!outFile)
	{
		cerr << "Error, unable to oepn output file. \n";
	}

	while(getline(inFile,line))
	{
		outFile << line << "\n";		/* Put contents of input to output location */
	}

	inFile.close();				/* Close the files */
	outFile.close();

}
