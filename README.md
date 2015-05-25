#Distributed Hashmap
This is a distributed hash table implementation. It is a peer to peer system. All peers share the responsibilities.
##Usage
A new *DistributedHashMap* instance is a cluster by itself. You can join this one node cluster to an existing one by
*connect* method. You need to provide ip address and port number of one of the nodes in existing cluster to join a new one
to it. Multicast feature will be implemented in new versions.

You can manipulate one hashtable from different nodes in one cluster. You can add, remove and get entries from any cluster
like they share one hashtable in the same computer in the same memory.
##Issues
Right now new peers cannot retrieve the entries created before they joined the cluster.