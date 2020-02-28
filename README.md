# README


## required

    mvn 3
    jdk 8


## compile

    mvn compile


## test

    mvn test
    

## test integration (manual invocation)

for person vplit scenario:
 
    mvn -Dtest=PersonVSplitScenarioTI test

for sakila scenario (it requires postgres db access and configuration):

    mvn -Dtest=SakilaScenarioTI test
    
## edit

Mostly required IntelliJ IDEA for Kotlin.

    idea . &


## source

### package / naming conventions

main package

    unibz.      univerity name
    cs.         computer science faculty name
    semint.     project name
    kprime      subproject name

main class entry point

    Starter     where all it starts and components get put toghether

technology free packages

    domain/     where aggregates, entities, values lives.
    usecase/    where domain components play toghether using services, repositories interfaces.

technology depenent packages

    adapter/    where services, repositories implementations lives.
    support/    where domain indipendent sherable implementation components lives.
    scenario/   where usecases are applied to real technology dependent case.    

### classes conventions

* A UseCase will return always a UseCaseResult.
* A UseCase may use only domain, service, repository components.
* An Adapter ha always to implement a service or repository interface.
* A domain component has to depends on nothing. 


## v0.1.0

from object database meta representation as input
apply kotlin transformer vertical or horizontal
obtain a second database meta representation as output


## v0.2.0

from xml object database meta representation as input
apply kotlin transformer vertical or horizontal
obtain a changeset representation as output


## v0.3.0

from xml object database meta representation as input
a generic xpath engine extraction plus freemarker template to generate
obtain a database meta representation as output

## v0.4.0

from xml object database meta representation as input
a xml transfomer descriptor

### to fix

* film_id doppio nella tabella film_core.
* redirect FOREIGN_KEY from splitted table.
* remove select PRIMARY KEY not by name, but by source table.

### to do

* horizontal simple decomposition to remove null columns
* merge selected input database out template process
* (probably) xpath with use of result of previous xpath computation as parameter
* (probably) sql xml representation to add database representation
* (probably) sql xml for relational algebra representation for selection

### references

https://www.baeldung.com/java-xpath
https://freemarker.apache.org/
https://github.com/ostap/relations-java
https://github.com/JSQLParser/JSqlParser/wiki/Examples-of-SQL-parsing