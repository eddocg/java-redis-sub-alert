# Notification Service using Java and Redis pub/sub feature with velocity
This service sends email notifications when miners are down. It will read messages from redis queue and then trigger an email using a veolicty template. This service is expected to be executed in background using a crontab service or any other scheduler. Redis and producer service need to be implemented separately.

### [Redis instance][https://redis.io/topics/quickstart]
You need to have a redis instance running so you can publish and suscribe to the message broker.

### Consumer
Notification Service uses `MessageConsumer` that will be listening for new messages to be consumed once it runs, then it will trigger an email notification using a Velocity template. Consumer service runs only on demand so you need to run it every time you want to pick up new messages from queue.

### Producer
You need to implement a service that publish messages (Alerts) to Redis where our Notification Service will be listening and then trigger an email notification based on the defined template. But if you want to test it, you can use `MessageProducer`.

### Provide configuration information

Use the property file `config.properties` to provide redis and email information:

```
mail.from = support@hotmail.com
mail.username =
mail.password =
mail.template = templates/disconnectedWorkerEmail.vm
mail.subject = System Alert
mail.smtp.auth = false
mail.smtp.starttls.enable = false
mail.smtp.host = 192.168.1.214
mail.smtp.port = 25
```