#include "File.h"
#include <stdio.h>
#include <iomanip>
#include <iostream>
#include <fstream>
#include <sstream>

using namespace std;

/*
Program 7

Programmer: Joey Domino
Due Date: 12/1/14 11:59 PM

This program will simulate a FAT-12 allocation table. It will take in a given command at the start of each
file's lines to then access the rest of the lines data correctly. The program will look for the following:


N: New File
	Create entry with it's name and size and allocate space

C: copy
	Copy file of given name and it's contents to the next open cluster

D: Deallocate
	Look for given name to be deleted along with the allocated space given to it

R: Rename File
	Search for old file name and new. If old is found and new is not, change old name to new name.
		Else, return eror.

M: Modify File
	Check given file name and size. If not found, error. Else, allocate or deallocate difference of desired
		space. If the size is equal, do nothing.

*/


#define LINE 12					//Line counter check
#define CLUSTER_SIZE 512			//Cluster constant for seperation of size into clusters
#define HOW_OFTEN 6				//Print after this many runs
#define CLUSTER_MAX 252				//Preset total number of clusters for easy printing
#define FILE_MAX 31				//Max number of files for easy printing

void PrintTable(int *);
void PrintDir(File *);

int main()
{
	File Files[FILE_MAX];			//File class array

	string Command;				//Command to be checked
	string NewFileName;			//New file entry
//	int NewFileSize;

	int Clusters[253] = {-2};		//Create array of clusters. We never use all 4096 clusters
	bool FileCheck = 0;			//Check for if file already exists
	int ClusterCount = 1;			//Count for how many clusters a file needs
	int ClusterNum = 1;			//Number for each cluster in the table
	int PCount = 6;				//Print count

	Files[0].FileName = ".";		//Set the first 2 directories
	Files[0].FileSize = 512;
	Files[0].FileClusters[0] = 0;
	Files[0].ClusterCnt = 1;
	Files[1].FileName = "..";
	Files[1].FileSize = 0;
	Files[1].FileClusters[0] = -1;


	Clusters[0] = -1;			//Set first number to -1 for first file

	cout << "\nRunning simulation.\n";

	PrintDir(Files);		//Print the tables for the first run
	PrintTable(Clusters);

	ifstream MyFile("data7.txt");		//Set, open, and loop file until eof
	string line;
	while(!MyFile.eof()){
		getline(MyFile, line);		//Get contents of the current line

		istringstream ss(line);

		ss >> Command;

		if(Command == "?"){		//End loop on ?
			break;
		}

		ClusterCount = 0;

		ss >> NewFileName;


//The remaining portion of the loop is for command checks and running said command

		if(Command == "N"){
			cout << "Request to add " << NewFileName << endl;
			for(int i = 0; i < FILE_MAX; i++){
				if(Files[i].FileName == NewFileName){		//See if file exists
					FileCheck = 1;
					break;
				}
			}
			if(FileCheck == 1){
				cout << "File already exists. Moving to next command\n";
			}

			else{

//I believe this for loop is messed up in some way. It is causing every class in my array to repeat this loop. I commented it out to save my program from a seg fault
//This shows that my loop at least gathers all the correct data. I just had a few pieces missing and some things to move around.

				for(int i = 0; i < FILE_MAX; i++){
					if(Files[i].FileName == ""){			//Replace the file and store the contents if the name is blank
						Files[i].FileName = NewFileName;
						ss >> Files[i].FileSize;

						if(Files[i].FileSize > CLUSTER_SIZE){
							int j = Files[i].FileSize;
							while(j > CLUSTER_SIZE){   	//Start at filesize and go down by 512 each loop
								//Files[i].ClusterCnt++;
								ClusterCount++;
								j -= CLUSTER_SIZE;
							}
						}
						else{
							//Files[i].ClusterCnt = 1;
							ClusterCount = 1;
						}
/*
						for(int j = 0; j < ClusterCount; j++){	//Store the used clusters into the class
							Files[i].FileClusters[j] = ClusterNum;	//Set cluster num to the class's cluster array
							ClusterNum++;				//Incremet cluster number
						}
						for(int x = 0; x < CLUSTER_SIZE; x++){	//Go through the cluster array and look for -2's
							for(int k = 0; k < ClusterCount; k++){
								if(Clusters[x] == -2){
									if(Files[i].FileClusters[k] != -1){	//take content of cluster in array until you find -1
										Clusters[x] = Files[i].FileClusters[k];
									}
								}
							}
						}*/

						break;	//End loop after the first empty spot is found
					}

				Files[i].ClusterCnt = ClusterCount;

				}

			}
			FileCheck = 0;	//Reset check
			ClusterCount = 0;
		}
/*
		else if(Command == "C"){
			cout << "Request to copy " << NewFileName << endl;
		}
		else if(Command == "D"){
			cout << "Request to deallocate " << NewFileName << endl;
		}
		else if(Command == "R"){
			cout << "Request to rename " << NewFileName << endl;
		}
		else if(Command == "M"){
			cout << "Request to modify " << NewFileName << endl;
		}
*/
		if(PCount == HOW_OFTEN){
			PrintDir(Files);
			PrintTable(Clusters);		//Print table after whatever run is done
			PCount = 0;
		}
		PCount++;
	}

}



/*

Function: PrintDir

Use: This function pritns the contents of the table. AKA: all the files within it and their info

Return:

*/

void PrintDir(File Files[])
{
	int TotSize = 0;
	int FileCount = 0;

	for(int i = 0; i < FILE_MAX; i++){
		if(Files[i].FileName == ""){
			break;
		}
		cout << "\nFile Name: " << setw(23) << left << setfill(' ') << Files[i].FileName << "File Size: " << Files[i].FileSize << endl;
		cout << "Cluster(s) in use: ";
		for(int j = 0; j < Files[i].ClusterCnt; j++){
			if(Files[i].FileClusters[j] != -1){
				cout << setw(6) << Files[i].FileClusters[j];
			}
			else if(Files[i].ClusterCnt == 0 || Files[i].FileClusters[j] == -1){
				cout << setw(9) << "(none)";
			}
		}
		cout << endl;
		TotSize += Files[i].FileSize;
		FileCount++;
	}
	cout << "\nTotal Files: " << setw(5) << left << FileCount << setw(13) << left << " Total Size: " << setw(5)
	     << right << setfill(' ') << TotSize <<  " bytes\n";
}

/*

Function: PrintTable

Use: The function prints the File Allocation Table when called.

Retrun:

*/


void PrintTable(int Clusters[])
{
	int TableRow1 = 0, TableRow2 = 11;	//Initialize table row number. Seperated for added dash later
	int count = 0;

	for(int i = 0; i < 55; i++){		//Setfill didnt want to work. So i made my divider crudely
		cout << "-";
	}

	for(int i = 0; i < CLUSTER_SIZE; i++){	//Swap -2's for 0's for printing purposes
		if(Clusters[i] == -2){
			Clusters[i] = 0;
		}
	}

	cout << "\n";

	cout << "\nContents of the File Allocation Table\n";

	cout << "#" << setw(3) << setfill('0') << TableRow1
	     << "-" << setw(3) << setfill('0') << TableRow2 << setw(5) << setfill(' '); //Print address for line

	for(int i = 0; i < CLUSTER_MAX; i++){		//Print table
		cout << setw(3) << setfill(' ') << Clusters[i] << setw(3) << setfill(' ');

		count++;

		if(count == LINE && i < CLUSTER_MAX - 1){
			TableRow1 += 12;
			TableRow2 += 12;

			cout << "\n#" << setw(3) << setfill('0') << TableRow1
			     << "-" << setw(3) << setfill('0') << TableRow2 << setw(5) << setfill(' ');

			count = 0;		//Reset counter
		}

	}
	cout << "\n\n";
}
