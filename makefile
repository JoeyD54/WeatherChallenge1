# Compiler variables
CCFLAGS = -ansi -Wall

# Rule to link object code files to create executable file
Assign7: assign7.o File.o
	g++ $(CCFLAGS) -o Assign7 assign7.o File.o

# Rules to compile source code files to object code
assign7.o: assign7.cpp File.h
	g++ $(CCFLAGS) -c assign7.cpp

#Queue.o: Queue.h Queue.cpp
#	g++ $(CCFLAGS) -c Queue.cpp

File.o: File.cpp File.h
	g++ $(CCFLAGS) -c File.cpp

# Pseudo-target to remove object code and executable files
clean:
	-rm *.o Assign7

