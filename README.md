# Spring Security Login Tutorial

Tutorial / Full Information
https://medium.com/@gustavo.ponce.ch/spring-boot-spring-mvc-spring-security-mysql-a5d8545d837d

1. mvn clean
2. mvn clean install
3. Go to the target folder
4. java -jar login-0.0.1-SNAPSHOT.jar


- http://localhost:8080/registration
- http://localhost:8080/login

## Travis CI Configuration

This project is set up to run a CICD pipeline through TravisCI and deploy to AWS EBS.  
TravisCI will first build the Docker image to ensure that the image builds and tests pass. The Dockerfile has steps to run tests.  
It then pushes the code to AWS EBS.
