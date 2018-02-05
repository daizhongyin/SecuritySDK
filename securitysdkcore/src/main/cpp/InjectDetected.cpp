#include <cstdio>
#include <unistd.h>
#include <fstream>
#include "Util.h"
#include "InjectDetected.h"
using namespace std;

//
// Created by ffthy on 21/11/2017.
//
//存在就是1
// 不存在就是-1
int  getimagebase() {
    pid_t pid =  getpid();
    char fileName[256] = {0};
    sprintf(fileName, "/proc/%d/maps", pid);

    ifstream in(fileName);
    string line;
    char* charline;
    if(in) // 有该文件
    {
        while (getline (in, line)) // line中不包括每行的换行符
        {
            //LOGD(line.data());
            int length=strlen(line.data());
            charline=new char(length+1);
            strcpy(charline,line.data());
            strtok(charline, "");
            LOGD("ddd%s",charline);
            delete[] charline;
            if(line.find("com.saurik.substrate",0)!=-1||line.find("XposedBridge.jar",0)!=-1){
                return 1;
            }
        }
        return -1;
    }

}