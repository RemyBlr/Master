### Deploy the elastic load balancer

In this task you will create a load balancer in AWS that will receive
the HTTP requests from clients and forward them to the Drupal
instances.

![Schema](./img/CLD_AWS_INFA.PNG)

## Task 01 Prerequisites for the ELB

* Create a dedicated security group

|Key|Value|
|:--|:--|
|Name|SG-DEVOPSTEAM[XX]-LB|
|Inbound Rules|Application Load Balancer|
|Outbound Rules|Refer to the infra schema|

```bash
[INPUT]
command 1: aws ec2 create-security-group --group-name SG-DEVOPSTEAM08-LB --description "Security group for DEVOPSTEAM08" --vpc-id vpc-03d46c285a2af77ba

command 2: aws ec2 authorize-security-group-ingress --group-id sg-05e8b7a4e77a14657 --protocol tcp --port 8080 --cidr 10.0.0.0/28 --tag-specifications 'ResourceType=security-group-rule,Tags=[{Key=Description,Value="Allow traffic from DMZ"}]'

[OUTPUT]
command 1 : "GroupId": "sg-05e8b7a4e77a14657"

command 2 : {  
"Return": true,  
"SecurityGroupRules": [  
{  
"SecurityGroupRuleId": "sgr-0434568e220ef04e1",  
"GroupId": "sg-05e8b7a4e77a14657",  
"GroupOwnerId": "709024702237",  
"IsEgress": false,  
"IpProtocol": "tcp",  
"FromPort": 8080,  
"ToPort": 8080,  
"CidrIpv4": "10.0.0.0/28",  
"Tags": [  
{  
"Key": "Description",  
"Value": "Allow traffic from DMZ"  
}  
]  
}  
]  
}

```

* Create the Target Group

|Key|Value|
|:--|:--|
|Target type|Instances|
|Name|TG-DEVOPSTEAM[XX]|
|Protocol and port|Refer to the infra schema|
|Ip Address type|IPv4|
|VPC|Refer to the infra schema|
|Protocol version|HTTP1|
|Health check protocol|HTTP|
|Health check path|/|
|Port|Traffic port|
|Healthy threshold|2 consecutive health check successes|
|Unhealthy threshold|2 consecutive health check failures|
|Timeout|5 seconds|
|Interval|10 seconds|
|Success codes|200|

```bash
[INPUT]
command 1: aws elbv2 create-target-group --name TG-DEVOPSTEAM08 --target-type instance --protocol HTTP --protocol-version HTTP1 --port 8080 --vpc-id vpc-03d46c285a2af77ba --health-check-path / --health-check-interval-seconds 10 --health-check-timeout-seconds 5 --healthy-threshold-count 2 --unhealthy-threshold-count 2 --matcher HttpCode=200

command 2: aws elbv2 create-listener --load-balancer-arn arn:aws:elasticloadbalancing:eu-west-3:709024702237:loadbalancer/app/ELB-DEVOPSTEAM08/d021932fb8464ea5 --protocol HTTP --port 8080 --default-actions Type=forward,TargetGroupArn=  
arn:aws:elasticloadbalancing:eu-west-3:709024702237:targetgroup/TG-DEVOPSTEAM08/368289472925ebbc

[OUTPUT]

command 1: {  
"TargetGroups": [  
{  
"TargetGroupArn": "arn:aws:elasticloadbalancing:eu-west-3:709024702237:targetgroup/TG-DEVOPSTEAM08/368289472925ebbc",  
"TargetGroupName": "TG-DEVOPSTEAM08",  
"Protocol": "HTTP",  
"Port": 8080,  
"VpcId": "vpc-03d46c285a2af77ba",  
"HealthCheckProtocol": "HTTP",  
"HealthCheckPort": "traffic-port",  
"HealthCheckEnabled": true,  
"HealthCheckIntervalSeconds": 10,  
"HealthCheckTimeoutSeconds": 5,  
"HealthyThresholdCount": 2,  
"UnhealthyThresholdCount": 2,  
"HealthCheckPath": "/",  
"Matcher": {  
"HttpCode": "200"  
},  
"TargetType": "instance",  
"ProtocolVersion": "HTTP1",  
"IpAddressType": "ipv4"  
}  
]  
}

command 2 :  {  
"Listeners": [  
{  
"ListenerArn": "arn:aws:elasticloadbalancing:eu-west-3:709024702237:listener/app/ELB-TG-DEVOPSTEAM08/368289472925ebbc/d1077cd208f9594b",  
"LoadBalancerArn": "arn:aws:elasticloadbalancing:eu-west-3:709024702237:loadbalancer/app/ELB-DEVOPSTEAM06/97a578c36ad66a3a",  
"Port": 8080,  
"Protocol": "HTTP",  
"DefaultActions": [  
{  
"Type": "forward",  
"TargetGroupArn": "arn:aws:elasticloadbalancing:eu-west-3:709024702237:targetgroup/TG-DEVOPSTEAM08/368289472925ebbc",  
"ForwardConfig": {  
"TargetGroups": [  
{  
"TargetGroupArn": "arn:aws:elasticloadbalancing:eu-west-3:709024702237:targetgroup/TG-DEVOPSTEAM08/368289472925ebbc",  
"Weight": 1  
}  
],  
"TargetGroupStickinessConfig": {  
"Enabled": false  
}  
}  
}  
]  
}  
]  
}

```


## Task 02 Deploy the Load Balancer

[Source](https://aws.amazon.com/elasticloadbalancing/)

* Create the Load Balancer

|Key|Value|
|:--|:--|
|Type|Application Load Balancer|
|Name|ELB-DEVOPSTEAM99|
|Scheme|Internal|
|Ip Address type|IPv4|
|VPC|Refer to the infra schema|
|Security group|Refer to the infra schema|
|Listeners Protocol and port|Refer to the infra schema|
|Target group|Your own target group created in task 01|

Provide the following answers (leave any
field not mentioned at its default value):

```bash
[INPUT]
command 1: aws elbv2 create-load-balancer --name ELB-DEVOPSTEAM08 --scheme internal --ip-address-type ipv4 --subnets subnet-0a66b85e2520f0b7e subnet-008e58629ea147885 --security-group sg-05e8b7a4e77a14657 --type application

command 2: aws elbv2 create-listener --load-balancer-arn arn:aws:elasticloadbalancing:eu-west-3:709024702237:loadbalancer/app/ELB-DEVOPSTEAM08/d021932fb8464ea5 --protocol HTTP --port 8080 --default-actions Type=forward,TargetGroupArn=arn:aws:elasticloadbalancing:eu-west-3:709024702237:targetgroup/TG-DEVOPSTEAM08/368289472925ebbc

command 3: aws elbv2 register-targets --target-group-arn arn:aws:elasticloadbalancing:eu-west-3:709024702237:targetgroup/TG-DEVOPSTEAM08/368289472925ebbc --targets Id=i-0ec764d036926e998 Id=i-02c7d2ab079418a9c

[OUTPUT]
command 1: {  
{  
"LoadBalancers": [  
{  
"LoadBalancerArn": "arn:aws:elasticloadbalancing:eu-west-3:709024702237:loadbalancer/app/ELB-DEVOPSTEAM08/d021932fb8464ea5",  
"DNSName": "internal-ELB-DEVOPSTEAM08-976923715.eu-west-3.elb.amazonaws.com",  
"CanonicalHostedZoneId": "Z3Q77PNBQS71R4",  
"CreatedTime": "2024-03-28T16:53:24.420000+00:00",  
"LoadBalancerName": "ELB-DEVOPSTEAM08",  
"Scheme": "internal",  
"VpcId": "vpc-03d46c285a2af77ba",  
"State": {  
"Code": "provisioning"  
},  
"Type": "application",  
"AvailabilityZones": [  
{  
"ZoneName": "eu-west-3a",  
"SubnetId": "subnet-0a66b85e2520f0b7e",  
"LoadBalancerAddresses": []  
},  
{  
"ZoneName": "eu-west-3b",  
"SubnetId": "subnet-008e58629ea147885",  
"LoadBalancerAddresses": []  
}  
],  
"SecurityGroups": [  
"sg-05e8b7a4e77a14657"  
],  
"IpAddressType": "ipv4"  
}  
]  
}

command 2 : 

{  
"Listeners": [  
{  
"ListenerArn": "arn:aws:elasticloadbalancing:eu-west-3:709024702237:listener/app/ELB-DEVOPSTEAM08/d021932fb8464ea5",  
"LoadBalancerArn": "arn:aws:elasticloadbalancing:eu-west-3:709024702237:loadbalancer/app/ELB-DEVOPSTEAM08/d021932fb8464ea5",  
"Port": 8080,  
"Protocol": "HTTP",  
"DefaultActions": [  
{  
"Type": "forward",  
"TargetGroupArn": "arn:aws:elasticloadbalancing:eu-west-3:709024702237:targetgroup/TG-DEVOPSTEAM08/368289472925ebbc",  
"ForwardConfig": {  
"TargetGroups": [  
{  
"TargetGroupArn": "arn:aws:elasticloadbalancing:eu-west-3:709024702237:targetgroup/TG-DEVOPSTEAM08/368289472925ebbc",  
"Weight": 1  
}  
],  
"TargetGroupStickinessConfig": {  
"Enabled": false  
}  
}  
}  
]  
}  
]  
}


```

* Get the ELB FQDN (DNS NAME - A Record)

```bash
[INPUT]
Running this command with powershell:
aws elbv2 describe-load-balancers --names ELB-DEVOPSTEAM08 | Select-String -Pattern '"DNSName": "' | ForEach-Object { $_ -replace '.*"DNSName": "(.*?)".*', '$1' }

[OUTPUT]
internal-ELB-DEVOPSTEAM08-976923715.eu-west-3.elb.amazonaws.com

```

* Get the ELB deployment status

Note : In the EC2 console select the Target Group. In the
       lower half of the panel, click on the **Targets** tab. Watch the
       status of the instance go from **unused** to **initial**.

* Ask the DMZ administrator to register your ELB with the reverse proxy via the private teams channel

* Update your string connection to test your ELB and test it

```bash
ssh devopsteam08@15.188.43.46 -i C:\Users\alans\.ssh\CLD_KEY_DMZ_DEVOPSTEAM08.pem -L 1234:internal-ELB-DEVOPSTEAM08-976923715.eu-west-3.elb.amazonaws.com:8080
```

* Test your application through your ssh tunneling

```bash
[INPUT]
curl -svo /dev/null localhost:1234

[OUTPUT]
*   Trying 127.0.0.1:1234...
* connect to 127.0.0.1 port 1234 failed: Connection refused
*   Trying [::1]:1234...
* connect to ::1 port 1234 failed: Connection refused
* Failed to connect to localhost port 1234 after 0 ms: Couldn't connect to server
* Closing connection 0
```

#### Questions - Analysis

* On your local machine resolve the DNS name of the load balancer into
  an IP address using the `nslookup` command (works on Linux, macOS and Windows). Write
  the DNS name and the resolved IP Address(es) into the report.

```
Command used:
nslookup internal-ELB-DEVOPSTEAM08-976923715.eu-west-3.elb.amazonaws.com

Output:
Dns name :    internal-ELB-DEVOPSTEAM08-976923715.eu-west-3.elb.amazonaws.com
Address:  10.0.8.142
	        10.0.8.4


```

* From your Drupal instance, identify the ip from which requests are sent by the Load Balancer.

Help : execute `tcpdump port 8080`

```
tcpdump: enX0: You don't have permission to perform this capture on that device
(socket: Operation not permitted)
//TODO
```

* In the Apache access log identify the health check accesses from the
  load balancer and copy some samples into the report.

```
10.0.8.139 - - [28/Mar/2024:19:27:27 +0000] "GET / HTTP/1.1"  200  5147  
10.0.8.7 - - [28/Mar/2024:19:27:27 +0000] "GET / HTTP/1.1"  200  5147  
10.0.8.139 - - [28/Mar/2024:19:27:37 +0000] "GET / HTTP/1.1"  200  5147  
10.0.8.7 - - [28/Mar/2024:19:27:37 +0000] "GET / HTTP/1.1"  200  5147  
10.0.8.139 - - [28/Mar/2024:19:27:47 +0000] "GET / HTTP/1.1"  200  5147  
10.0.8.7 - - [28/Mar/2024:19:27:47 +0000] "GET / HTTP/1.1"  200  5147
```
