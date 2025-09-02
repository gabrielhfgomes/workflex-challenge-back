
# BoilerPlate WorkFlex Challenge

Boilerplate to face WorkFlex challenge

This project would be used inside a docker container, and could be escalated using Kubernates setup.
Won't set this for the challenge due to time constraints and requirements (keeping it simple and small).

## Building the Project

```bash
mvn clean install
```

### Running the Application

```bash
mvn spring-boot:run
```

### Running Tests

```bash
mvn test
```

### Packaging as a Jar

```bash
mvn package
```

### API Documentation

Visit `/swagger-ui.html` after starting the app for API docs (if Swagger is enabled).

### Configuration

Edit `src/main/resources/application.properties` for custom settings.
```