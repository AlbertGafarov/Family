# syntax=docker/dockerfile:1
# указываем какой родительский образ использовать. Этот образ включает, альпину джаву
FROM openjdk:14-alpine

# Указывем название рабочей директории
WORKDIR /app

#  Копировать содержимое папки mvn в папку mvn внутри образа
COPY .mvn/ .mvn

# Копировать файлы mvnw и pom.xml в корень образа
COPY mvnw ./
COPY pom.xml ./

# Установка зависиомстей внутрь образа
RUN /app/mvnw dependency:go-offline

# Копировать исходный код приложения внутрь образа
COPY src ./src
#RUN ./src/mvnw dependency:go-offline
# Запустить команду, когда образ начнет выполняться в контейнере
CMD ["./mvnw", "spring-boot:run", "-Dspring-boot.run.profiles=docker", "-DskipTests=true"]