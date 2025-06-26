# ibmmqtest

simple mqclient programm for continouus sending/receiving messages
to an ibm mq queuemanager

```
  usage:

   -channel <arg>          default: LPQAINT.DVLPR.CN
   -connectionList <arg>   default: localhost(1431)
   -duration <arg>         default: 5 minutes
   -queuemanager <arg>     default: LPQAINT

 ```

## Development container

The repository contains a devcontainer setup that starts a Java
development environment together with an IBM MQ sidecar container.
The MQ queue manager is called `LPQAINT` and listens on port `1431`.

To start the environment with [VS Code Dev Containers](https://containers.dev)
or the `devcontainer` CLI, open the project in a compatible tool or run:

```bash
devcontainer up
```

After the containers are running you can build and execute the client against
the queue manager:

```bash
mvn package
java -jar target/ibmmqtest-0.0.1-SNAPSHOT-jar-with-dependencies.jar
```

The program will use the defaults shown above and connect to the MQ instance on
`localhost:1431`.
