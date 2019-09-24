# Distributed File Storage System

This project implements a secure distributed database by creating a java application that will automatically secure any files you choose to upload to the service. It provides end-to-end encryption, meaning that it encrypts the file with the appropriate key as it is uploaded, while also decrypting files with the appropriate key when it is downloaded. The keys themselves are stored entirely on the client. Furthermore, even a super computer will be unable to decrypt the file in the average human lifetime. In fact, it may even prove difficult to find the location of the encrypted data, because the implementation supports a distributed network of storage devices. 

A simple user interface with self-describing buttons perform all of the work, minimizing the amount of communication the server, as only files and lists are ever transmitted between client and server.

#### System Architecture

![Image of System Architecture](https://lh3.googleusercontent.com/TdTwUhiX9zc9gI2ku6Rimq9iB8OwrK3JmlEamKF1tWW6WVNik_z4-Uhex3vO4ILACMIZ9kg7DHHJ83mtQA1yRWrR6enbUEiO9CJdsyOeWdfP7zN1A4WBd6A28wgk4dG6b_tnM98N)

Author: Navjot Aulakh, 100488741

## Installation Guide
CMD 
	<p>1 - start rmiregistry
	<p>2 - compile server folder with javac *.java 
	<p>3 - run Server with java -Djava.security.policy.policy.txt Server
	<p>4 - compile client folder with javac -cp forms_rt.jar; *.java 
	<p>5 - run Client&GUI with java -cp forms_rt.jar; ClientGui
	
MD5 Password Hashing Implemented
Pre-created LoginInfo(Can be deleted as Application will auto create at register or login)  Username:navi Password:navi(hashed in MD5 in loginInfo)
