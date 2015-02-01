# Simple Key Store (Scala)

A simple key-value (SKS) store exposing a HTTP API. It was also implemented in Go [here](https://github.com/lucastorri/golang-sks).


## Setting the Project

Just check it out from github and install SBT (0.13.7).


## Compile & Run

```
cd $project

sbt assembly

java -cp target/scala-2.11/scala-sks-assembly-1.0.jar -server -Xms512m -Xmx512m -XX:PermSize=128m -XX:MaxPermSize=256m sks
```

### Options

The following flags can be used:
  
  * `-port=<number>`: set the HTTP port to be used
  * `-store=<details>`: how files will be store, with two options:
    * `men`: store files in memory;
    * `dir:<path>`: store in the given path and access them through memory mapped files.


### Generate JVM Settings

<http://jvmmemory.com/>


## API

* `GET /{key}`: return 200 and body with value for that key, or 404 if key not defined;
* `POST /key`: save body content to key.


## Testing

Load some files into the ska, generate a list with URLs and use it as an input to [siege](http://www.joedog.org/siege-home/).

```
for i in {1..1000}; do 
    url=http://localhost:12121/$i
    curl -X POST --data "@/path/to/some/file.txt" $url
    echo $url >> urls.txt
done

siege -f urls.txt -i -t 10S -d 0 -c 10 -b
```

Results (more or less around this):

```
Transactions:		       16368 hits
Availability:		      100.00 %
Elapsed time:		        9.34 secs
Data transferred:	       29.80 MB
Response time:		        0.00 secs
Transaction rate:	     1752.46 trans/sec
Throughput:		        3.19 MB/sec
Concurrency:		        5.35
Successful transactions:       16368
Failed transactions:	           0
Longest transaction:	        0.01
Shortest transaction:	        0.00
```
