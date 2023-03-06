#!/bin/bash
echo "updating yum..."
sudo yum update -y
echo "upgrading yum..."
sudo yum upgrade -y
echo "cleaning yum..."
sudo yum clean all
echo "remove jdk..."
sudo rpm -qa | grep -i java | xargs -n1 rpm -e --nodeps
echo "installing jdk..."
sudo yum install -y java-1.8.0-openjdk
echo "installing nginx"
sudo amazon-linux-extras install nginx1.12
echo "Installing MySQL server..."
sudo yum remove mysql-server mysql
sudo rm -rf /var/lib/mysql
sudo rm -rf /etc/mysql
sudo rm -rf /var/log/mysql
sudo deluser -r mysql
sudo rm /etc/apparmor.d/abstractions/mysql
sudo yum install https://dev.mysql.com/get/mysql80-community-release-el7-5.noarch.rpm -y
sudo yum install mysql-community-server -y
echo "Starting the MySQL service..."
sudo systemctl start mysqld
sudo rm -rf /var/lib/mysql
sudo systemctl restart mysqld
echo "Installing maven..."
sudo yum install maven -y
echo "Installing git..."
sudo yum install git -y
echo "Installing expect..."
sudo yum install -y tcl
sudo yum install -y expect


echo "Configure MySQL server..."
password=`sudo grep 'temporary password' /var/log/mysqld.log`
psw="${password##*localhost: }"
echo $psw

expect -c "
    spawn mysql -u root -p
    expect \"Enter password: \"
    send \"${psw}\r\"
    expect \"mysql> \"
    send \"ALTER USER 'root'@'localhost' IDENTIFIED BY 'Password123#@!'; \r\"
    expect \"mysql> \"
    send \"SET GLOBAL validate_password.length=4; \r\"
    expect \"mysql> \"
    send \"SET GLOBAL validate_password.policy=0; \r\"
    expect \"mysql> \"
    send \"ALTER USER 'root'@'localhost' IDENTIFIED BY 'aabcd'; \r\"
    expect \"mysql> \"
    send \"FLUSH PRIVILEGES; \r\"
    expect \"mysql> \"
    send \"EXIT;\r\"
    expect eof
"


