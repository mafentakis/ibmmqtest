# ibmmqtest

simple mqclient programm for continouus sending/receiving messages
to an ibm mq queuemanager

```
  usage:

   -channel <arg>          default: LPQAINT.DVLPR.CN
   -connectionList <arg>   default: localhost(1431)
  -duration <arg>         default: 5 minutes
  -queuemanager <arg>     default: LPQAINT
  -user <arg>             MQ user name
  -password <arg>         MQ password
  -destination <arg>      default: ADMI.INITADM
  -destinationType <arg>  queue or topic (default: queue)

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

java -cp target/ibmmqtest-0.0.1-SNAPSHOT-jar-with-dependencies.jar  JmsProducer -connectionList ibmmq\(1414\) -channel DEV.APP.SVRCONN  -user app -password passw0rd -destination DEV.QUEUE.1
```

This connects to the sidecar queue manager using the default channel
`LPQAINT.DVLPR.CN` and queue manager `LPQAINT`.



# MQ Developer Defaults

see https://github.com/ibm-messaging/mq-docker/?tab=readme-ov-file#mq-developer-defaults

This image includes the MQ Developer defaults scripts which are automatically run during Queue Manager startup. This configures your Queue Manager with a set of default objects that you can use to quickly get started developing with IBM MQ. If you do not want the default objects to be created you can set the MQ_DEV environment variable to false.

Users
Userid: admin Groups: mqm Password: passw0rd

Userid: app Groups: mqclient Password:

Queues
DEV.QUEUE.1
DEV.QUEUE.2
DEV.QUEUE.3
DEV.DEAD.LETTER.QUEUE - Set as the Queue Manager's Dead Letter Queue.
Channels
DEV.ADMIN.SVRCONN - Set to only allow the admin user to connect into it and a Userid + Password must be supplied.
DEV.APP.SVRCONN - Does not allow Administrator users to connect.

see
https://hub.docker.com/r/ibmcom/mq
