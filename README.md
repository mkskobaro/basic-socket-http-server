# basic-socket-http-server
***Educational project***


## Docker
[Docker hub mkskobaro/bhttpd](https://hub.docker.com/r/mkskobaro/bhttpd "mkskobaro/bhttpd docker image")

This image run the server on 8080 port, so we need use `-p` to publish it on desired port (80 for example):
```bash
docker run -p 80:8080 mkskobaro/bhttpd
```
