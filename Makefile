MVN := mvn
BUILD_ARGS := clean package -DskipTests
INSTALL_ARGS := clean install

all: build up

libs:
	@echo "--- Building Libraries ---"
	$(MVN) -f events-contract/pom.xml $(INSTALL_ARGS)
	$(MVN) -f couriers-contract/pom.xml $(INSTALL_ARGS)
	$(MVN) -f grpc-contract/pom.xml $(INSTALL_ARGS)

build: libs
	@echo "--- Building Microservices ---"
	$(MVN) -f couriers/pom.xml $(BUILD_ARGS)
	$(MVN) -f audit-service/pom.xml $(BUILD_ARGS)
	$(MVN) -f notification-service/pom.xml $(BUILD_ARGS)
	$(MVN) -f pricing-service/pom.xml $(BUILD_ARGS)
	$(MVN) -f statistics-service/pom.xml $(BUILD_ARGS)

up:
	@echo "--- Starting Docker Environment ---"
	docker-compose up -d --build

down:
	@echo "--- Stopping Docker Environment ---"
	docker-compose down

restart: down build up

logs:
	docker-compose logs -f

ps:
	docker-compose ps

clean:
	$(MVN) -f events-contract/pom.xml clean
	$(MVN) -f couriers-contract/pom.xml clean
	$(MVN) -f grpc-contract/pom.xml clean
	$(MVN) -f couriers/pom.xml clean
	$(MVN) -f audit-service/pom.xml clean
	$(MVN) -f notification-service/pom.xml clean
	$(MVN) -f pricing-service/pom.xml clean
	$(MVN) -f statistics-service/pom.xml clean

help:
	@echo "Available commands:"
	@echo "  make        - Build everything and start Docker (default)"
	@echo "  make libs   - Install only shared libraries (contracts)"
	@echo "  make build  - Build all JARs (libs + services)"
	@echo "  make up     - Start Docker containers"
	@echo "  make down   - Stop Docker containers"
	@echo "  make restart- Stop, Rebuild JARs, Start"
	@echo "  make logs   - Follow Docker logs"
	@echo "  make ps     - Show running containers"
	@echo "  make clean  - Clean target directories"