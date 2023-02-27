
FROM vwv-docker.pkg.coding.net/mk95/open/alpine-java:17
# 配置文件解密
ENV pwd 123456
# 运行环境 test/prod
ENV env_name test

COPY ./web/target/web-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 8080

CMD java -jar app.jar --jasypt.encryptor.password=${pwd} --spring.profiles.active=${env_name}

