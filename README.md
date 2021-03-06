# Quarkus with Hibernate :bear: and Panache 

This tutorial explains how to create a microservice :whale: with Panache and Hibernate to store and retrieve some data to/from a postgres database :elephant:.

# What is Hibernate?
As [wikipedia says](https://en.wikipedia.org/wiki/Hibernate_(framework)):

>Hibernate ORM (or simply Hibernate) is an object-relational mapping tool for the Java programming language. It provides a framework for mapping an object-oriented domain model to a relational database. Hibernate handles object-relational impedance mismatch problems by replacing direct, persistent database accesses with high-level object handling functions. 

# What is Panache?
Panache focuses on making your entities trivial and fun to write and use with Quarkus.

With Panache, we took an opinionated approach to make hibernate as easy as possible. Hibernate ORM with Panache offers the following:

> By extending PanacheEntity in your entities, you will get an ID field that is auto-generated. If you require a custom ID strategy, you can extend PanacheEntityBase instead and handle the ID yourself. 
>
>By using Use public fields, there is no need for functionless getters and setters (those that simply get or set the field). You simply refer to fields like Person.name without the need to write a Person.getName() implementation. Panache will auto-generate any getters and setters you do not write, or you can develop your own getters/setters that do more than get/set, which will be called when the field is accessed directly.
>
>The PanacheEntity superclass comes with lots of super useful static methods and you can add your own in your derived entity class, and much like traditional object-oriented programming it's natural and recommended to place custom queries as close to the entity as possible, ideally within the entity definition itself. Users can just start using your entity Person by typing Person, and getting completion for all the operations in a single place.
>
>You don't need to write parts of the query that you don’t need: write Person.find("order by name") or Person.find("name = ?1 and status = ?2", "stef", Status.Alive) or even better Person.find("name", "stef").

You can check all this info at [Red Hat Developer](https://developers.redhat.com/courses/quarkus/panache/).

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
We'll be using the dependencies:
 * `quarkus-hibernate-orm-panache` and `quarkus-jdbc-postgresql` for database persistance.
 * `quarkus-resteasy-jsonb` for serialization.
 * `quarkus-smallrye-metrics` for Pantheos metrics.
 
 Your dependencies pom should look like this:

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

    <!-- JSONb dependencies -->
    <dependency>
      <groupId>io.quarkus</groupId>
      <artifactId>quarkus-resteasy-jsonb</artifactId>
    </dependency>

    <!-- Metrics dependencies -->
    <dependency>
      <groupId>io.quarkus</groupId>
      <artifactId>quarkus-smallrye-metrics</artifactId>
    </dependency>
</dependencies>
```

# Prometheus :fire:

Let's add [Prometheus](https://prometheus.io/) for the metrics.

To install run the docker image as in the [official guide](https://prometheus.io/docs/prometheus/latest/installation/)

 * `docker pull prom/prometheus`
 * `docker run -p 9090:9090 -v ABSOLUTE_PATH/infrastucture/prometheus.yml:/etc/prometheus/prometheus.yml prom/prometheus`

**Note**: [What is Prometheus?](https://prometheus.io/docs/introduction/overview/)

## Configure prometheus.yml
```yml
global:
  scrape_interval:     15s # By default, scrape targets every 15 seconds.

# tag::adocPrometheus[]
scrape_configs:
  - job_name: 'prometheus'
    static_configs:
      - targets: ['localhost:9090']
  - job_name: 'hibernate'
    static_configs:
      - targets: ['172.26.221.240:8081']
# end::adocPrometheus[]
```
  **NOTE :skull:**: The default `yml` file in this repository won't work because of the ip in `hibernate` scrape_config job. You'll need to change it for your default inet. To do so simply:
   * `ifconfig`
   * copy your interface `inet ip`:
   ```xml
   enp0s31f6: flags=4163<UP,BROADCAST,RUNNING,MULTICAST>  mtu 1500
        inet 172.26.221.240  netmask 255.255.254.0  broadcast 172.26.221.255
        inet6 fe80::4684:1d:18c3:baba  prefixlen 64  scopeid 0x20<link>
        ether 80:ce:62:98:95:c9  txqueuelen 1000  (Ethernet)
        RX packets 456264  bytes 568836913 (542.4 MiB)
        RX errors 0  dropped 15  overruns 0  frame 0
        TX packets 269978  bytes 31562651 (30.1 MiB)
        TX errors 0  dropped 0 overruns 0  carrier 0  collisions 0
        device interrupt 16  memory 0xe1200000-e1220000  
   ```
# Functioning

There are just two endpoints:
 * One to persist persons in the database:
   * `/dbc/create/person`
 * Another to retrieve them:
   * `/dbc/retrieve/{name}`

Here are some request that can be used to verify the application:
 * Insert a person:   
   * `curl -v -H "Content-Type: application/json" --data '{"name": "John", "surname": "Connor", "age": 30}' -X POST http://localhost:8080/dbc/create/person`
 * Retrieve a person:
    * `curl -v http://localhost:8080/dbc/retrieve/John`
