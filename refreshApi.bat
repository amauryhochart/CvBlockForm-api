cd "C:\\dev\\projets\\2024\\cvblockform-api"
call mvn install
call d
cd ..
call docker-compose stop
call docker stop meedz-cvblockform-api-1
call docker rm meedz-cvblockform-api-1
call docker-compose up -d --no-recreate --no-build
call docker stop meedz-cvblockform-db-1