# Quarkus with Hibernate :bear: and Panache 

This tutorial explains how to create a microservice :whale: with Panache and Hibernate to store and retrieve some data to/from a postgres database :elephant:.

# What is Hibernate?
Great question. #TODO :construction_worker:
# What is Panache?
Good question. #TODO :construction_worker:

# Postgres :elephant:

Download the [docker postgres image :elephant:](https://hub.docker.com/_/postgres):
 * `docker pull postgres`

Run the docker image with specific username, password and database name:
 * `docker run --name person-postgres -p 5432:5432 -e POSTGRES_USER=sarah -e POSTGRES_PASSWORD=connor -e POSTGRES_DB=people -d postgres`

Right now the database is up with user `sarah`, password `connor` and a database called `people`. As we configured in our `application.properties`:
```bash
# configure your datasource
quarkus.datasource.url=jdbc:postgresql://localhost:5432/people
quarkus.datasource.driver=org.postgresql.Driver
quarkus.datasource.username=sarah
quarkus.datasource.password=connor
# drop and create the database at startup (use `update` to only update the schema)
quarkus.hibernate-orm.database.generation=drop-and-create
```

 But there is no table `person` in database `people`. That table is needed in order to persist our `Panache` entity `Person`. Even `Quarkus` will notify this, but thanks to the statement `quarkus.hibernate-orm.database.generation=drop-and-create` in `application.properties` all the `Panache` entities will be created as tables in database `people`.

In case you want to check the database run the [`psql`](https://www.postgresql.org/docs/9.2/app-psql.html) interactive terminal:
 * `docker exec -it person-postgres psql -U sarah -d people`
 * `\dt` (show all tables)
 * `SELECT * FROM person;` (show all rows)
 * Learn more from [here](https://www.postgresql.org/docs/) :stuck_out_tongue_closed_eyes:

# Quarkus
We'll be using the dependencies `quarkus-hibernate-orm-panache` and `quarkus-jdbc-postgresql`. Your dependencies pom should look like this:

```xml
<dependencies>
    <!-- Hibernate ORM specific dependencies -->
    <dependency>
        <groupId>io.quarkus</groupId>
        <artifactId>quarkus-hibernate-orm-panache</artifactId>
    </dependency>

    <!-- JDBC driver dependencies -->
    <dependency>
        <groupId>io.quarkus</groupId>
        <artifactId>quarkus-jdbc-postgresql</artifactId>
    </dependency>
</dependencies>
```

# Requests

Here are some request that can be used to verify the application:
 * Insert default person:
   * `curl -v http://localhost:8080/dbc/create`
