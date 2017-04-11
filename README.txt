First, download the latest release of Cockroach Database from http://cockroachlabs.com/ for your platform.  I rename the
executable to 'crdb' to keep things crisp.  Then start the server on your developer host, I run it in /tmp just because.

$ cd /tmp
$ crdb start --background --http-port=9090
CockroachDB node starting at 2017-03-16 15:04:25.280278938 -0400 EDT
build:      CCL beta-20170309 @ 2017/03/09 16:34:35 (go1.8)
  admin:      http://localhost:9090
  sql:        postgresql://root@localhost:26257?sslmode=disable
  logs:       cockroach-data/logs
  store[0]:   path=cockroach-data
  status:     restarted pre-existing node
  clusterID:  59fcea0b-e206-44e2-9071-f8164a52bb6b
  nodeID:     1

First:
$ gradle gradlew

To change jvm arguments:
$ ./gradlew bootRun -PjvmArgs="-Dwhatever1=value1 -Dwhatever2=value2"

To rebuild the lock file:
$ ./gradlew clean generateLock saveLock

Otherwise just:
$ ./gradlew bootRun

Basic tests are in a file 'test-api', first install HTTPie and JasonQuery(jq).
$ brew install httpie jq

Then run the tests:
$ ./test-api

Of course you can exercise the API with CuRL:

$ # Create:
$ curl -i -X POST -H "Content-Type:application/json" -d '{ "firstName" : "Karl", "lastName" : "Penzhorn" }' localhost:8443/persons
HTTP/1.1 201
Location: http://localhost:8443/persons/215677213022060545
Content-Type: application/hal+json;charset=UTF-8
Transfer-Encoding: chunked
Date: Tue, 31 Jan 2017 19:11:02 GMT

{
  "firstName" : "Karl",
  "lastName" : "Penzhorn",
  "_links" : {
    "self" : {
      "href" : "http://localhost:8443/persons/215677213022060545"
    },
    "person" : {
      "href" : "http://localhost:8443/persons/215677213022060545"
    }
  }
}
$ # Read:
$ curl localhost:8443/persons/215677213022060545
{
  "firstName" : "Karl",
  "lastName" : "Penzhorn",
  "_links" : {
    "self" : {
      "href" : "http://localhost:8443/persons/215677213022060545"
    },
    "person" : {
      "href" : "http://localhost:8443/persons/215677213022060545"
    }
  }
}
$ # Update:
$ curl -i -X PUT -H "Content-Type:application/json" -d '{ "firstName" : "Karl", "lastName" : "Zen" }' localhost:8443/persons/215677213022060545
HTTP/1.1 200
Location: http://localhost:8443/persons/215677213022060545
Content-Type: application/hal+json;charset=UTF-8
Transfer-Encoding: chunked
Date: Tue, 31 Jan 2017 19:18:22 GMT

{
  "firstName" : "Karl",
  "lastName" : "Zen",
  "_links" : {
    "self" : {
      "href" : "http://localhost:8443/persons/215677213022060545"
    },
    "person" : {
      "href" : "http://localhost:8443/persons/215677213022060545"
    }
  }
}
$
$ # Delete:
$ curl -i -X DELETE localhost:8443/persons/215677213022060545
HTTP/1.1 204
Date: Tue, 31 Jan 2017 19:14:51 GMT

$ curl localhost:8443/persons/215677213022060545
$

You can update the log levels at runtime.

$ curl http://localhost:8443/loggers

Then, to change the ROOT configured level to TRACE you'd simply:

$ curl -i -X POST -H 'Content-Type: application/json' -d '{"configuredLevel": "TRACE"}' http://localhost:8080/loggers/ROOT

or:

$ HTTP POST localhost:8080/loggers/ROOT configuredLevel:TRACE


Also, you can get the set of REST endpoints mapped (not including model objects) via:

curl http://localhost:8443/mappings | jq
