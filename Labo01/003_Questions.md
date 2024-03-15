* What is the smallest and the biggest instance type (in terms of
  virtual CPUs and memory) that you can choose from when creating an
  instance?

```
The smallest instance type is the t2.nano, it uses only 1 vCPU and 0.5 GiB of memory.
The biggest one is the u-6tb1.112xlarge, it uses 448 vCPU and 6144 GiB of memory.
```

* How long did it take for the new instance to get into the _running_
  state?

```
It only took a few seconds for the instance to be in the running state
```

* Using the commands to explore the machine listed earlier, respond to
  the following questions and explain how you came to the answer:

    * What's the difference between time here in Switzerland and the time set on
      the machine?
      
    ```
    Once you're logged in with ssh you can use the 'date' command and it tells you the date and time for the machine.
    It shows that the machine is one hour behind us (Switzerland time).
    ```

    * What's the name of the hypervisor?
    
    ```
    This time we used an aws command the get the hypervisor. You can use the following command with the right instance id:
    aws ec2 describe-instances --instance-ids INSTANCE_ID --query 'Reservations[*].Instances[*].Hypervisor'
    The result is 'xen'.
    ```

    * How much free space does the disk have?
    
    ```
    Again once logged with ssh you can use the following command to get the different spaces left on the disk: df -hT .
    The '.' represents the current mounted disk. The entire space is 7.7G and 5.9G is currently left.
    ```


* Try to ping the instance ssh srv from your local machine. What do you see?
  Explain. Change the configuration to make it work. Ping the
  instance, record 5 round-trip times.

```
TODO
```

* Determine the IP address seen by the operating system in the EC2
  instance by running the `ifconfig` command. What type of address
  is it? Compare it to the address displayed by the ping command
  earlier. How do you explain that you can successfully communicate
  with the machine?

```
TODO
```
