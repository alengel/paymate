paymate
=======
AWS Deploy Instructions

Click "Connect" on the instance.
In the console: 
1) ssh into the instance with: ssh -i ar414-key-pair-ireland .pem ubuntu@*IP_Address_from_EC2*
2) Start DB with: sh /home/ubuntu/glassfish4/javadb/bin/./startNetworkServer &
3) Go to: cd /home/ubuntu/glassfish4/bin/
4) Start domain: ./asadmin start-domain domain1
5) Enter password
6) Navigate to: *IP_Address_from_EC2*:8080/paymate 



Netbeans Deploy Instructions

1) Deploy paymateWS
2) Deploy paymateRS
3) Deploy paymate
4) Run paymate
