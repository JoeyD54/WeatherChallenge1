//Class for each file to hold x number of clusters

#include <string>

using namespace std;

#ifndef FILE_H
#define FILE_H

class File
{
	public:
		string FileName;
		int FileSize;
		int FileClusters[];
		int ClusterCnt;

		File();

	private:

};

#endif

