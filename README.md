# LeoDistributeSys

## description
This is the first academic project of course “Advanced Operating System”. In this project, first Chandy Lamport algorithm was implemented to create a consistent snapshot of the global state of a distributed system. Second, termination detection was implemented to detect if the computation has finished doing all the work. GRASP was the main principle we use to create our main classes. Controller pattern, factory pattern, singleton pattern, state pattern are used in the project.

This project consists of three parts.

##Part 1
Implement a distributed system consisting of n nodes, numbered 0 to n − 1, arranged in a certain
topology. The topology and information about other parameters is provided in a configuration file.
All channels in the system are bidirectional, reliable and satisfy the first-in-first-out (FIFO)
property. You can implement a channel using a reliable socket connection (with TCP or SCTP).
For each channel, the socket connection should be created at the beginning of the program and
should stay intact until the end of the program. All messages between neighboring nodes are
exchanged over these connections.
All nodes execute the following protocol:

• Initially, each node in the system is either active or passive. At least one node must be
active at the beginning of the protocol.

• While a node is active, it sends anywhere from minPerActive to maxPerActive messages, and
then turns passive. For each message, it makes a uniformly random selection of one of its
neighbors as the destination. Also, if the node stays active after sending a message, then it
waits for at least minSendDelay time units before sending the next message.

• Only an active node can send a message.

• A passive node, on receiving a message, becomes active if it has sent fewer than maxNumber
messages (summed over all active intervals). Otherwise, it stays passive.

We refer to the protocol described above as the MAP protocol.

##Part 2
Implement the Chandy and Lamport’s protocol for recording a consistent global snapshot as dis-
cussed in the class. Assume that the snapshot protocol is always initiated by node 0 and all
channels in the topology are bidirectional. Use the snapshot protocol to detect the termination of
the MAP protocol described in Part 1. The MAP protocol terminates when all nodes are passive
and all channels are empty. To detect termination of the MAP protocol, augment the Chandy
and Lamport’s snapshot protocol to collect the information recorded at each node at node 0 using
a converge-cast operation over a spanning tree. The tree can be built once in the beginning or
on-the-fly for an instance using MARKER messages.
Note that, in this project, the messages exchanged by the MAP protocol are application mes-
sages and the messages exchanged by the snapshot protocol are control messages. The rules of the
MAP protocol (described in Part 1) only apply to application messages. They do not apply to
control messages.
Testing Correctness of the Snapshot Protocol Implementation
To test that your implementation of the Chandy and Lamport’s snapshot protocol is correct,
implement Fidge/Mattern’s vector clock protocol described in the class. The vector clock of a node
is part of the local state of the node and its value is also recorded whenever a node records its local
state. Node 0, on receiving the information recorded by all the nodes, uses these vector timestamps
to verify that the snapshot is indeed consistent. Note that only application messages will carry
vector timestamps.

##Part 3
Once node 0 detects that the MAP protocol has terminated, it broadcasts a FINISH message to all
processes. A process, on receiving a FINISH message, stops executing. Eventually, all processes
stop executing and the entire system is brought to a halt.

##Configuration Format
Your program should run using a configuration file in the following format:
The configuration file will be a plain-text formatted file no more than 100kB in size. Only
lines which begin with an unsigned integer are considered to be valid. Lines which are not valid
should be ignored. The configuration file will contain 2n + 1 valid lines. The first valid line of the
configuration file contains SIX tokens. The first token is the number of nodes in the system. The
2second and third tokens are values of minPerActive, and maxPerActive respectively. The fourth and
fifth tokens are values of minSendDelay and snapshotDelay, in milliseconds. snapshotDelay is the
amount of time to wait between initiating snapshots in the Chandy-Lamport algorithm. The sixth
token is the value of maxNumber. After the first valid line, the next n lines consist of three tokens.
The first token is the node ID. The second token is the host-name of the machine on which the
node runs. The third token is the port on which the node listens for incoming connections. After
the first n + 1 valid lines, the next n lines consist of a space delimited list of at most n − 1 tokens.
The k th valid line after the first line is a space delimited list of node IDs which are the neighbor
of node k. Your parser should be written so as to be robust concerning leading and trailing white
space or extra lines at the beginning or end of file, as well as interleaved with valid lines. The #
character will denote a comment. On any valid line, any characters after a # character should be
ignored.
You are responsible for ensuring that your program runs correctly when given a valid configu-
ration file. Make no additional assumptions concerning the configuration format. If you have any
questions about the configuration format, please ask the TA.
##Listing 1: Example configuration file
4 4 6 300 500 50

0 dc03 5040
1 dc04 5041
2 dc05 5042
3 dc06 5043

1 2 3  
0 2  
0 1 3   
0 2   

## compile
using the main function in MachineNode.java as the main function of the project to compile this project.

## run the application
bash ./launcher.sh

## stop all the remote process
bash ./cleanup.sh

