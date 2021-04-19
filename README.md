# Kotlin Coroutines Playground

A small project to play around with Kotlin Coroutines, Ktor, DI, Confluenct Kafka

### Build the Container 
The dockerfile is a multistage build (cache, build, run)
Run it initially with 

```docker build -t m4cki3/user-service .```

To build it with cached gradle dependencies run

```docker build --target builder -t m4cki3/user-service .```


## Deploy on Kubernetes

Create a Helm values file ```charts/values.yaml``` and copy the content from ```charts/doc_values.yaml```  
Enter bootstrapServers, username and password of your Kafka cluster

### Install Helm Chart:

```helm upgrade --install user-service charts --values charts/values.yaml```

### Uninstall Helm Chart:

```helm uninstall user-service```