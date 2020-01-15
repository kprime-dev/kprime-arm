# README


## required

    mvn 3
    jdk 8


## compile

    mvn compile


## test

    mvn test
    
    
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

    domain/     where aggregates, entities, values lives
    usecase/    where domain components play toghether using services, repositories interfaces

technology depenent packages

    adapter/    where services, repositories implementations lives
    support/    where domain indipendent sherable implementation components lives
    

## classes conventions

* A UseCase will return allways a UseCaseResult.
* A UseCase may use only domain, service, repository components.
* An Adapter ha always to implement a service or repository interface.
* A domain component has to depends on nothing. 
