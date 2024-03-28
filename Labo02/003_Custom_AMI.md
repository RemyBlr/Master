# Custom AMI and Deploy the second Drupal instance

In this task you will update your AMI with the Drupal settings and deploy it in the second availability zone.

## Task 01 - Create AMI

### [Create AMI](https://awscli.amazonaws.com/v2/documentation/api/latest/reference/ec2/create-image.html)

Note : stop the instance before

|Key|Value for GUI Only|
|:--|:--|
|Name|AMI_DRUPAL_DEVOPSTEAM[XX]_LABO02_RDS|
|Description|Same as name value|

```bash
[INPUT]
// Stop instance
aws ec2 stop-instances --instance-id i-0e9dbbdf897a46b85

// Create image
aws ec2 create-image \
    --instance-id i-0e9dbbdf897a46b85 \
    --name "AMI_DRUPAL_DEVOPSTEAM08_LABO02_RDS" \
    --description "AMI_DRUPAL_DEVOPSTEAM08_LABO02_RDS"

[OUTPUT]
// Stop instance
{
    "StoppingInstances": [
        {
            "CurrentState": {
                "Code": 64,
                "Name": "stopping"
            },
            "InstanceId": "i-0e9dbbdf897a46b85",
            "PreviousState": {
                "Code": 16,
                "Name": "running"
            }
        }
    ]
}

// Create image
{
    "ImageId": "ami-04f58f81f04d4adb7"
}

```

## Task 02 - Deploy Instances

* Restart Drupal Instance in Az1

* Deploy Drupal Instance based on AMI in Az2

|Key|Value for GUI Only|
|:--|:--|
|Name|EC2_PRIVATE_DRUPAL_DEVOPSTEAM[XX]_B|
|Description|Same as name value|

```bash
[INPUT]
// Same command as in 001_RDS, need to change ami, subnet, and private ip (refer to image)
aws ec2 run-instances \
--image-id ami-04f58f81f04d4adb7 \
--count 1 \
--instance-type t3.micro \
--key-name CLD_KEY_DRUPAL_DEVOPSTEAM08 \
--security-group-ids sg-011729d6749d296af sg-0ebc4e7a416a98258 \
--subnet-id subnet-008e58629ea147885 \
--private-ip-address 10.0.8.140 \
--tag-specifications 'ResourceType=instance,Tags=[{Key=Name,Value=EC2_PRIVATE_DRUPAL_DEVOPSTEAM08_B}]'

[OUTPUT]
{
    "Groups": [],
    "Instances": [
        {
            "AmiLaunchIndex": 0,
            "ImageId": "ami-04f58f81f04d4adb7",
            "InstanceId": "i-0ec764d036926e998",
            "InstanceType": "t3.micro",
            "KeyName": "CLD_KEY_DRUPAL_DEVOPSTEAM08",
            "LaunchTime": "2024-03-20T18:55:28+00:00",
            "Monitoring": {
                "State": "disabled"
            },
            "Placement": {
                "AvailabilityZone": "eu-west-3b",
                "GroupName": "",
                "Tenancy": "default"
            },
            "PrivateDnsName": "ip-10-0-8-140.eu-west-3.compute.internal",
            "PrivateIpAddress": "10.0.8.140",
            "ProductCodes": [],
            "PublicDnsName": "",
            "State": {
                "Code": 0,
                "Name": "pending"
            },
            "StateTransitionReason": "",
            "SubnetId": "subnet-008e58629ea147885",
            "VpcId": "vpc-03d46c285a2af77ba",
            "Architecture": "x86_64",
            "BlockDeviceMappings": [],
            "ClientToken": "424ad8f4-ec99-49f6-be43-4e6d521f4cda",
            "EbsOptimized": false,
            "EnaSupport": true,
            "Hypervisor": "xen",
            "NetworkInterfaces": [
                {
                    "Attachment": {
                        "AttachTime": "2024-03-20T18:55:28+00:00",
                        "AttachmentId": "eni-attach-0d5e9121b1c15d455",
                        "DeleteOnTermination": true,
                        "DeviceIndex": 0,
                        "Status": "attaching",
                        "NetworkCardIndex": 0
                    },
                    "Description": "",
                    "Groups": [
                        {
                            "GroupName": "SG-PRIVATE-DRUPAL-DEVOPSTEAM08-RDS",
                            "GroupId": "sg-0ebc4e7a416a98258"
                        },
                        {
                            "GroupName": "SG-PRIVATE-DRUPAL-DEVOPSTEAM08",
                            "GroupId": "sg-011729d6749d296af"
                        }
                    ],
                    "Ipv6Addresses": [],
                    "MacAddress": "0a:d9:93:b6:15:91",
                    "NetworkInterfaceId": "eni-09f78b37ff6e99123",
                    "OwnerId": "709024702237",
                    "PrivateIpAddress": "10.0.8.140",
                    "PrivateIpAddresses": [
                        {
                            "Primary": true,
                            "PrivateIpAddress": "10.0.8.140"
                        }
                    ],
                    "SourceDestCheck": true,
                    "Status": "in-use",
                    "SubnetId": "subnet-008e58629ea147885",
                    "VpcId": "vpc-03d46c285a2af77ba",
                    "InterfaceType": "interface"
                }
            ],
            "RootDeviceName": "/dev/xvda",
            "RootDeviceType": "ebs",
            "SecurityGroups": [
                {
                    "GroupName": "SG-PRIVATE-DRUPAL-DEVOPSTEAM08-RDS",
                    "GroupId": "sg-0ebc4e7a416a98258"
                },
                {
                    "GroupName": "SG-PRIVATE-DRUPAL-DEVOPSTEAM08",
                    "GroupId": "sg-011729d6749d296af"
                }
            ],
            "SourceDestCheck": true,
            "StateReason": {
                "Code": "pending",
                "Message": "pending"
            },
            "Tags": [
                {
                    "Key": "Name",
                    "Value": "EC2_PRIVATE_DRUPAL_DEVOPSTEAM08_B"
                }
            ],
            "VirtualizationType": "hvm",
            "CpuOptions": {
                "CoreCount": 1,
                "ThreadsPerCore": 2
            },
            "CapacityReservationSpecification": {
                "CapacityReservationPreference": "open"
            },
            "MetadataOptions": {
                "State": "pending",
                "HttpTokens": "optional",
                "HttpPutResponseHopLimit": 1,
                "HttpEndpoint": "enabled",
                "HttpProtocolIpv6": "disabled",
                "InstanceMetadataTags": "disabled"
            },
            "EnclaveOptions": {
                "Enabled": false
            },
            "PrivateDnsNameOptions": {
                "HostnameType": "ip-name",
                "EnableResourceNameDnsARecord": false,
                "EnableResourceNameDnsAAAARecord": false
            },
            "MaintenanceOptions": {
                "AutoRecovery": "default"
            },
            "CurrentInstanceBootMode": "legacy-bios"
        }
    ],
    "OwnerId": "709024702237",
    "ReservationId": "r-0c0f0dddf3e43c5b9"
}
```

## Task 03 - Test the connectivity

### Update your ssh connection string to test

* add tunnels for ssh and http pointing on the B Instance

```bash
//updated string connection
ssh devopsteam08@15.188.43.46 -i ~/.ssh/CLD_KEY_DMZ_DEVOPSTEAM08.pem \
-L 2223:10.0.8.10:22 \
-L 2224:10.0.8.140:22 \
-L 8080:10.0.8.10:8080 \
-L 8081:10.0.8.140:8081

// Test in new shell
ssh bitnami@localhost -p 2223 -i ~/.ssh/CLD_KEY_DRUPAL_DEVOPSTEAM08.pem
// once connected : bitnami@ip-10-0-8-10:~$ 

// Test in new shell
ssh bitnami@localhost -p 2224 -i ~/.ssh/CLD_KEY_DRUPAL_DEVOPSTEAM08.pem
// Once connected : bitnami@ip-10-0-8-140:~$ 
```

## Check SQL Accesses

```sql
[INPUT]
//sql string connection from A
// Connect on 10.0.8.10
mysql -h dbi-devopsteam08.cshki92s4w5p.eu-west-3.rds.amazonaws.com -u bn_drupal -p
// Then
show databases;

[OUTPUT]
+--------------------+
| Database           |
+--------------------+
| bitnami_drupal     |
| information_schema |
+--------------------+
```

```sql
[INPUT]
//sql string connection from B
// Connect on 10.0.8.140
mysql -h dbi-devopsteam08.cshki92s4w5p.eu-west-3.rds.amazonaws.com -u bn_drupal -p
// Then
show databases;

[OUTPUT]
+--------------------+
| Database           |
+--------------------+
| bitnami_drupal     |
| information_schema |
+--------------------+
```

### Check HTTP Accesses

```bash
//connection string updated
curl -I http://localhost:8080
```

### Read and write test through the web app

* Login in both webapps (same login)

* Change the users' email address on a webapp... refresh the user's profile page on the second and validated that they are communicating with the same db (rds).

* Observations ?

```
The email changes for both users.
```

### Change the profil picture

* Observations ?

```
The image only changes for one user, and not both like the previous test. The image is stored only on one side and the second user doesn't have access to the image.
```
