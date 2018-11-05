Deliverables
------------

(1) Java program that can be run from command line
	
	java -jar parser-0.0.1-SNAPSHOT.jar --accesslog=/path/to/file --startDate=2017-01-01.13:00:00 --duration=hourly --threshold=100 

	> etc/parser-0.0.1-SNAPSHOT.jar : java program command line
	> Please follow instructions in Deliverables#2 before running the above command 
	> To view other possible commands: java -jar parser-0.0.1-SNAPSHOT.jar help

(2) Source Code for the Java program

	> Please create mysql user "demo" with password also "demo" and 2 mysql databases with name "log" and "log_test"
	
	# run on mysql database
	CREATE USER 'demo'@'localhost' IDENTIFIED BY 'demo';
	CREATE DATABASE log;
	GRANT ALL PRIVILEGES ON log.* to 'demo'@'localhost';
	FLUSH PRIVILEGES;
	CREATE DATABASE log_test;
	GRANT ALL PRIVILEGES ON log_test.* to 'demo'@'localhost';
	FLUSH PRIVILEGES;

	> Code recompilation, basic tests and test coverage

	# run on terminal with maven
	mvn clean package jacoco:report
	# target/site/jacoco/index.html : test coverage report
    
(3) MySQL schema used for the log data

	> etc/logSchema.png : erd of schema
	> etc/logSchema.mwb : mysql workbench file for the erd
	> etc/logDump20181029.sql: dump of parsed access log sample

(4) SQL queries for SQL test

	> etc/SQL_Test_Queries.sql : native sql query for sample testing