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

A [devcontainer](https://containers.dev/) setup is provided. It runs this project
alongside an IBM MQ container exposing a queue manager named `LPQAINT` on port
`1431`.

### Starting the container

1. Install the **Remote - Containers** extension for Visual Studio Code.
2. Open this repository in VS Code and choose **Reopen in Container**.

### Running the client

The MQ container is reachable from the development container using the host
`ibmmq` on port `1414`. Build and run the client with:

```bash
mvn package
java -cp target/ibmmqtest-0.0.1-SNAPSHOT-jar-with-dependencies.jar \
 JmsProducer -connectionList ibmmq\(1414\) -channel DEV.APP.SVRCONN
```

This connects to the sidecar queue manager using the default channel
`LPQAINT.DVLPR.CN` and queue manager `LPQAINT`.


see
https://hub.docker.com/r/ibmcom/mq
