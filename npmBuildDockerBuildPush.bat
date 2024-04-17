@REM Verify npm is installed 
CALL mvn --version

@REM Verify docker is installed
CALL docker --version

@REM Build the production app 
CALL mvn install

@REM Build the production docker image 
CALL docker build -t meedz/cvblockform-api:1 .

@REM Tag the production docker image to the good repo name
CALL docker tag meedz/cvblockform-api:1 maumau11/cvblockform-api:1

@REM Push the tagged production docker image 
CALL docker push maumau11/cvblockform-api:1
