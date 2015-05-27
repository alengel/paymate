paymate
=======
AWS deploy instructions:

1. Start the instance

2. Click "Connect" on the instance.


------------------------------------


In the terminal:

1. ssh into the instance with: ssh -i ar414-key-pair-ireland.pem ubuntu@54.72.185.162

2. Start DB with: sh /home/ubuntu/glassfish4/javadb/bin/./startNetworkServer &

3. Go to: cd /home/ubuntu/glassfish4/bin/

4. Start domain: ./asadmin start-domain domain1

5. Enter password

6. Wait a while...

7. In the browser navigate to: http://54.72.185.162:8080/paymate
 

The applications are configured to start in the right sequence by assigning them deployment numbers in the admin console. 

A file catalogue is available and can be found [here](https://goo.gl/AZxuq6) 