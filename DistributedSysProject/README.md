# Distributed File Storage System

This project implements a secure distributed database by creating a java application that will automatically secure any files you choose to upload to the service. It provides end-to-end encryption, meaning that it encrypts the file with the appropriate key as it is uploaded, while also decrypting files with the appropriate key when it is downloaded. The keys themselves are stored entirely on the client. Furthermore, even a super computer will be unable to decrypt the file in the average human lifetime. In fact, it may even prove difficult to find the location of the encrypted data, because the implementation supports a distributed network of storage devices. 

A simple user interface with self-describing buttons perform all of the work, minimizing the amount of communication the server, as only files and lists are ever transmitted between client and server.

#### System Architecture

![Image of System Architecture](https://lh3.googleusercontent.com/TdTwUhiX9zc9gI2ku6Rimq9iB8OwrK3JmlEamKF1tWW6WVNik_z4-Uhex3vO4ILACMIZ9kg7DHHJ83mtQA1yRWrR6enbUEiO9CJdsyOeWdfP7zN1A4WBd6A28wgk4dG6b_tnM98N)

Author: Navjot Aulakh, 100488741

## Installation Guide
CMD 
	<p>1 - Start rmiregistry within Server folder
	<p>2 - Compile server folder with "javac *.java" 
	<p>3 - Run Server with "java -Djava.security.policy.policy.txt Server"
	<p>4 - Compile client folder with "javac -cp forms_rt.jar; *.java"
	<p>5 - Run Client & GUI with "java -cp forms_rt.jar; ClientGui"
	
MD5 Password Hashing Implemented
Pre-created LoginInfo(Can be deleted as Application will auto create at register or login)  Username:navi Password:navi(hashed in MD5 in loginInfo)

## Demo Images
![Image of Demo 1](https://lh4.googleusercontent.com/jEBY-NJp4gYs6-kWZxXPhb_mlUXV26Hmx1d80zglstyg95cXbLuTowVoeBiLwJ4z8racu4AklWSIv-Oz8mF4n4enKfJd8m06rcR6xCtA)
![Image of Demo 2](https://lh6.googleusercontent.com/-pCPuhToSfMAAsAaMa7BeZg1P0NQnP6zeXyq5PK0jzM2ifX8YXABtVeikJpGYmIiqT_JE3Z-Ys1P-Ectpv_3vzqER9izz5AMhnYQT46F)
![Image of Demo 3](https://lh5.googleusercontent.com/VZXPq9cMHiDdlDDR1uZjBRhCLAHOtKon4Kp1x076632exQz98TNvEt7jbTOPNTSjnBKH5BE3_96lmSCFw2QuRIpFf3KyIQX0fFwBjDmJ)
![Image of Demo 4](https://lh5.googleusercontent.com/ceqPosVOrHymAyWXbmITaKF2ip6DsSGv3yuvp25n2Xu7T8DUJcvrja0gj9SBVmulS403cslHQ-V_jS1PUm1atIT-59EY9Hkr1bfZxOpj)
![Image of Demo 5](https://lh3.googleusercontent.com/FlUt-495mgUlv75rqxP3dOQt3PX5RcFrBiISZEtQLVxq7zlONo43bn6ak_b2SOXhvvSlR5Un7Icec2JRr7E_8kMYuCeubqSMG4JuodeP)
![Image of Demo 6](https://lh3.googleusercontent.com/pUChg5C9eURIPrlmVYK-jfgk12_jbx9wzObMaz0I33CMgAbxsTF357s0m3kKvBxwQdsAKt-miquslA8PD-iyw8BhQWJ_1nGSBIPgxwQ7bl96ZMy6LIzk6AnlEGmTCK_2WT-jjH9S)
