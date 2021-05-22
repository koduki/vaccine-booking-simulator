## Distribute Load Testing Using GKE Autopilot 

## Introduction

Load testing is key compornent to trust system reliability. However, it's required huge machine resouces to replay real world traffic and it only one-time resources. So, It's difficult to secure such resouces permantetly.
This project provide flexible and elastic load testing with Locust and GKE Autopilot, thank for greate technology. We can just type few commands and distribute load.

This project is based on [GoogleCloudPlatform/distributed-load-testing-using-kubernetes](https://github.com/GoogleCloudPlatform/distributed-load-testing-using-kubernetes).

## Prepare Load testing script

You can find a test script from `dockerfiles/locust-tasks/tasks.py`. Please update for your target application. You can test a this script with `locustio/locust` container on your local PC. This project is also use this container.

```bash
$ docker run -p 8089:8089 -v $PWD/locust-tasks:/locust-tasks locustio/locust -f /locust-tasks/tasks.py -H ${YOUR_TARGET_APPLICATION}
```

## Setup GKE environments

We use GKE Autopilot. Autopilot manages actual Kubernetes nodes instead of us. We can only manage Pod. These pods are distributed and load branced automatically. If ndoe resouce is not enough for number of Pods, autopilot extend nodes by themself.

### Setup Cluster

If this is your first operation in this GCP project, please type below for enable container API.

```bash 
$ gcloud services enable container.googleapis.com
```

```bash
PROJECT=$(gcloud config get-value project)
REGION=us-central1
ZONE=${REGION}-b
CLUSTER=load-test-cluster

gcloud container clusters create-auto $CLUSTER --create-subnetwork name=gke --region us-central1
gcloud container clusters get-credentials $CLUSTER --region $REGION --project $PROJECT
```

## Ship and Run

### Build and Push Test-Task image

```bash
docker build -t gcr.io/${PROJECT}/locust-tasks load-tasks
docker push gcr.io/${PROJECT}/locust-tasks
```

### Deply Application and Service

You can deploy application and service by below command. Before deply, please update `image` and `TARGET_HOST` for your application in `kubernetes-config/*.yaml`.

```bash
kubectl apply -f kubernetes-config
```

YOu can see deploy progress by below command.

```bash
watch -n 1 kubectl get po
kubectl get nodes
```

After running containers, please open Locust management console.

```bash
EXTERNAL_IP=$(kubectl get svc locust-master -o jsonpath="{.status.loadBalancer.ingress[0].ip}")
echo "http://${EXTERNAL_IP}:8089"
```

If you need to extend pod size, you can type below command.

```bash
kubectl scale deployment/locust-worker --replicas=20
```

Node is also add automatically. But you need take care about limit of quotas.

https://console.cloud.google.com/iam-admin/quotas

## Clean up

After your testing, you should remove pod and cluster to reduce cost.

```bash
kubectl delete -f kubernetes-config
gcloud container clusters delete $CLUSTER --region $REGION
```
