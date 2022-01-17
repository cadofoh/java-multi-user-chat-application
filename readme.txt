Student Number : 200189635
==== Simple Multi User Chat Service =====


== Description ===
This is a simple java multiple-user chat service that allows multiple clients to connect to a server and send messages while receiving messages from other connected clients.
Because this chat application uses a client-server architecture, clients will only be able to see messages sent by others for the time that they are connected to the server.


== Folder Contents ===
.idea
CA
Keys
out
src
.classpath
.project
readme.txt
SNS_Coursework.iml




== Instructions==


1. Run Server.java file to create a server listening on the port 43000
2. After this, run multiple instances of the Client.java file in order to see the functionality of the multi user aspect.
3. After running the client, a console window will pop up asking for a password. (Password is used as a furhter security control for authentication)
4. Enter "SNS_Code69" as the password. 
5. Another message will pop up asking for a username
6. Enter any username of choice and a connection with the server will be establisshed.
7. Clients can now send messages to the server which will then be relayed to all the other clients connected to the server.
8. To end the connection, simply terminate the program
