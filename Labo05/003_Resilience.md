# Task 3 - Add and exercise resilience

By now you should have understood the general principle of configuring, running and accessing applications in Kubernetes. However, the above application has no support for resilience. If a container (resp. Pod) dies, it stops working. Next, we add some resilience to the application.

## Subtask 3.1 - Add Deployments

In this task you will create Deployments that will spawn Replica Sets as health-management components.

Converting a Pod to be managed by a Deployment is quite simple.

  * Have a look at an example of a Deployment described here: <https://kubernetes.io/docs/concepts/workloads/controllers/deployment/>

  * Create Deployment versions of your application configurations (e.g. `redis-deploy.yaml` instead of `redis-pod.yaml`) and modify/extend them to contain the required Deployment parameters.

  * Again, be careful with the YAML indentation!

  * Make sure to have always 2 instances of the API and Frontend running. 

  * Use only 1 instance for the Redis-Server. Why?

    > Reids-Server can't be scalled horizontally as it is a statefull service. When using a single instance of Redis, it simplifies the management of data consistency since there's no need to handle synchronization between multiple instances. A single instance is often sufficient to handle a significant amount of load.

  * Delete all application Pods (using `kubectl delete pod ...`) and replace them with deployment versions.

  * Verify that the application is still working and the Replica Sets are in place. (`kubectl get all`, `kubectl get pods`, `kubectl describe ...`)

## Subtask 3.2 - Verify the functionality of the Replica Sets

In this subtask you will intentionally kill (delete) Pods and verify that the application keeps working and the Replica Set is doing its task.

Hint: You can monitor the status of a resource by adding the `--watch` option to the `get` command. To watch a single resource:

```sh
$ kubectl get <resource-name> --watch
```

To watch all resources of a certain type, for example all Pods:

```sh
$ kubectl get pods --watch
```

You may also use `kubectl get all` repeatedly to see a list of all resources.  You should also verify if the application stays available by continuously reloading your browser window.

  * What happens if you delete a Frontend or API Pod? How long does it take for the system to react?
    > When you delete a Frontend or API Pod, the following happens:
    > - The Pod is immediately terminated and removed from the list of running Pods
    > - The Replica Set associated with the Deployment detects that the desired number of replicas is not being met.
    > - The Replica Set then creates a new Pod to replace the one that was deleted.
    
    > The reaction time for Kubernetes to create a new Pod can vary but typically takes a few seconds.
    
  * What happens when you delete the Redis Pod?

    > When you delete the Redis Pod, the following happens:
    > - The Pod is immediately terminated and removed from the list of running Pods.
    > - The Replica Set associated with the Redis Deployment (which is set to 1 replica) detects that there is no running instance.
    > - The Replica Set then creates a new Redis Pod to replace the one that was deleted.
    
    > This process should also take a few seconds to complete.
    
  * How can you change the number of instances temporarily to 3? Hint: look for scaling in the deployment documentation

    > To temporarily change the number of instances to 3, you can scale the Deployment using the `kubectl scale` command:
    > ```shell
    > kubectl scale deployment <deployment-name> --replicas=3
    > ```
    
  * What autoscaling features are available? Which metrics are used?

    > Kubernetes provides the Horizontal Pod Autoscaler (HPA) for autoscaling Deployments, ReplicaSets, and other resources. HPA adjusts the number of replicas based on observed CPU utilization or other select metrics.The most common metrics used for autoscaling include:
    > - CPU utilization
    > - Memory usage (with the appropriate metrics server)
    > - Custom metrics provided via the Kubernetes API
    
  * How can you update a component? (see "Updating a Deployment" in the deployment documentation)

    > Use `kubectl set image deployment/<deployment-name> <container-name>=<new-image>` to update the image directly. Kubernetes will handle the rolling update, replacing the old Pods with new ones that use the updated image.

## Subtask 3.3 - Put autoscaling in place and load-test it

On the GKE cluster deploy autoscaling on the Frontend with a target CPU utilization of 30% and number of replicas between 1 and 4. 

Load-test using Vegeta (500 requests should be enough).

> [!NOTE]
>
> - The autoscale may take a while to trigger.
>
> - If your autoscaling fails to get the cpu utilization metrics, run the following command
>
>   - ```sh
>     $ kubectl apply -f https://github.com/kubernetes-sigs/metrics-server/releases/latest/download/components.yaml
>     ```
>
>   - Then add the *resources* part in the *container part* in your `frontend-deploy` :
>
>   - ```yaml
>     spec:
>       containers:
>         - ...:
>           env:
>             - ...:
>           resources:
>             requests:
>               cpu: 10m
>     ```
>

## Deliverables

Document your observations in the lab report. Document any difficulties you faced and how you overcame them. Copy the object descriptions into the lab report.

> We didn't encounter any difficulties in this last part.


```````sh
// TODO autoscaling description
```````

```yaml
# redis-deploy.yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: redis-deployment
  labels:
    app: todo
spec:
  replicas: 1
  selector:
    matchLabels:
      component: redis
  template:
    metadata:
      labels:
        component: redis
        app: todo
    spec:
      containers:
      - name: redis
        image: redis
        ports:
        - containerPort: 6379
        args:
        - redis-server
        - --requirepass ccp2
        - --appendonly yes
```

```yaml
# api-deploy.yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: api-deployment
  labels:
    app: todo
spec:
  replicas: 2
  selector:
    matchLabels:
      component: api
  template:
    metadata:
      labels:
        component: api
        app: todo
    spec:
      containers:
      - name: api
        image: icclabcna/ccp2-k8s-todo-api
        ports:
        - containerPort: 8081
        env:
        - name: REDIS_ENDPOINT
          value: redis-svc
        - name: REDIS_PWD
          value: ccp2
```

```yaml
# frontend-deploy.yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: frontend-deployment
  labels:
    app: todo
spec:
  replicas: 2
  selector:
    matchLabels:
      component: frontend
  template:
    metadata:
      labels:
        component: frontend
        app: todo
    spec:
      containers:
      - name: frontend
        image: icclabcna/ccp2-k8s-todo-frontend
        ports:
        - containerPort: 8080
        env:
        - name: API_ENDPOINT_URL
          value: http://api-svc:8081
        resources:
          requests:
            cpu: 10m
```
