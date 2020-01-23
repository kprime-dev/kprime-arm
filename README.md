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


## to do

* schema clone, builder, immutable
* schema pattern matcher
* schema variable extrator
* sql view generator

## references

https://www.baeldung.com/java-xpath
https://freemarker.apache.org/
https://github.com/ostap/relations-java
https://github.com/JSQLParser/JSqlParser/wiki/Examples-of-SQL-parsing