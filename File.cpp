#include "File.h"

using namespace std;

File::File()
{
	FileName = "";
	FileSize = 0;
	ClusterCnt = 0;

	for(int i = 0; i < 31; i++){
		FileClusters[i] = -1;
	}
}
