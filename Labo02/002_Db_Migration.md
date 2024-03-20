# Database migration

In this task you will migrate the Drupal database to the new RDS database instance.

![Schema](./img/CLD_AWS_INFA.PNG)

## Task 01 - Securing current Drupal data

### [Get Bitnami MariaDb user's password](https://docs.bitnami.com/aws/faq/get-started/find-credentials/)

```bash
[INPUT]
// Connect with ssh in 2 different terminals
ssh devopsteam08@15.188.43.46 -i ~/.ssh/CLD_KEY_DMZ_DEVOPSTEAM08.pem -L 2223:10.0.8.10:22
ssh bitnami@localhost -p 2223 -i ~/.ssh/CLD_KEY_DRUPAL_DEVOPSTEAM08.pem

//help : path /home/bitnami/bitnami_credentials
cat /home/bitnami/bitnami_credentials

[OUTPUT]
Welcome to the Bitnami package for Drupal

******************************************************************************
The default username and password is 'user' and 'SECRET_PASSWORD:'.
******************************************************************************

You can also use this password to access the databases and any other component the stack includes.

Please refer to https://docs.bitnami.com/ for more details.
```

### Get Database Name of Drupal

```bash
[INPUT]
// Get databases
// User 'user' doesnt work, use root instead
mariadb -u root -p -e "show databases;"

[OUTPUT]
+--------------------+
| Database           |
+--------------------+
| bitnami_drupal     |
| information_schema |
| mysql              |
| performance_schema |
| sys                |
| test               |
+--------------------+
```

### [Dump Drupal DataBases](https://mariadb.com/kb/en/mariadb-dump/)

```bash
[INPUT]
// First tried to do this without writing in new file, but its huge so I redirected the output in a sql file
mariadb-dump -u root -p --databases bitnami_drupal > bitnami_drupal_dump.sql

// Check the end of file
tail bitnami_drupal_dump.sql

[OUTPUT]
// Tail output
/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2024-03-20 17:34:48
```

### Create the new Data base on RDS

```sql
[INPUT]
// Connect to RDS
mysql -h dbi-devopsteam08.cshki92s4w5p.eu-west-3.rds.amazonaws.com -u admin -p

// You could use another command to create database without connecting to RDS, you won't have to exit in the next step like i had to do

//Then
CREATE DATABASE bitnami_drupal;

[OUTPUT]
Query OK, 1 row affected (0.001 sec)
```

### [Import dump in RDS db-instance](https://mariadb.com/kb/en/restoring-data-from-dump-files/)

Note : you can do this from the Drupal Instance. Do not forget to set the "-h" parameter.

```sql
[INPUT]
// I was still connected to Mariadb so i used
exit

// Then : mysql -h <rds-end-point> -u <rds_admin_user> -p <db_target> < <pathToDumpFileToImport>.sql
mysql -h dbi-devopsteam08.cshki92s4w5p.eu-west-3.rds.amazonaws.com -u admin -p < bitnami_drupal_dump.sql

// No visible output
// I used this to check if tables were imported correctly
mariadb-show bitnami_drupal -u root -p

[OUTPUT]
+----------------------------------+
|              Tables              |
+----------------------------------+
| block_content                    |
| block_content__body              |
| block_content_field_data         |
| block_content_field_revision     |
| block_content_revision           |
| block_content_revision__body     |
| cache_bootstrap                  |
| cache_config                     |
| cache_container                  |
| cache_data                       |
| cache_default                    |
| cache_discovery                  |
| cache_dynamic_page_cache         |
| cache_entity                     |
| cache_menu                       |
| cache_page                       |
| cache_render                     |
| cache_toolbar                    |
| cachetags                        |
| comment                          |
| comment__comment_body            |
| comment_entity_statistics        |
| comment_field_data               |
| config                           |
| file_managed                     |
| file_usage                       |
| help_search_items                |
| history                          |
| key_value                        |
| menu_link_content                |
| menu_link_content_data           |
| menu_link_content_field_revision |
| menu_link_content_revision       |
| menu_tree                        |
| node                             |
| node__body                       |
| node__comment                    |
| node__field_image                |
| node__field_tags                 |
| node_access                      |
| node_field_data                  |
| node_field_revision              |
| node_revision                    |
| node_revision__body              |
| node_revision__comment           |
| node_revision__field_image       |
| node_revision__field_tags        |
| path_alias                       |
| path_alias_revision              |
| router                           |
| search_dataset                   |
| search_index                     |
| search_total                     |
| semaphore                        |
| sequences                        |
| sessions                         |
| shortcut                         |
| shortcut_field_data              |
| shortcut_set_users               |
| taxonomy_index                   |
| taxonomy_term__parent            |
| taxonomy_term_data               |
| taxonomy_term_field_data         |
| taxonomy_term_field_revision     |
| taxonomy_term_revision           |
| taxonomy_term_revision__parent   |
| user__roles                      |
| user__user_picture               |
| users                            |
| users_data                       |
| users_field_data                 |
| watchdog                         |
+----------------------------------+
```

### [Get the current Drupal connection string parameters](https://www.drupal.org/docs/8/api/database-api/database-configuration)

```bash
[INPUT]
//help : same settings.php as before
cat stack/drupal/sites/default/settings.php

[OUTPUT]
//at the end of the file you will find connection string parameters
//username = bn_drupal
//password = XXXXXXX

$databases['default']['default'] = array (
  'database' => 'bitnami_drupal',
  'username' => 'bn_drupal',
  'password' =>  ...);
```

### Replace the current host with the RDS FQDN

```
//settings.php
// I used vim because i didn't know any better method to change
sudo vim stack/drupal/sites/default/settings.php

$databases['default']['default'] = array (
   [...] 
  'host' => 'dbi-devopsteam08.cshki92s4w5p.eu-west-3.rds.amazonaws.com',
   [...] 
);
```

### [Create the Drupal Users on RDS Data base](https://mariadb.com/kb/en/create-user/)

Note : only calls from both private subnets must be approved.
* [By Password](https://mariadb.com/kb/en/create-user/#identified-by-password)
* [Account Name](https://mariadb.com/kb/en/create-user/#account-names)
* [Network Mask](https://cric.grenoble.cnrs.fr/Administrateurs/Outils/CalculMasque/)

```sql
[INPUT]
CREATE USER bn_drupal@'10.0.[XX].0/[Subnet Mask - A]]' IDENTIFIED BY '<Drupal password>';
GRANT ALL PRIVILEGES ON bitnami_drupal.* TO '<yourNewUser>';
// Connect to RDS
mariadb -h dbi-devopsteam08.cshki92s4w5p.eu-west-3.rds.amazonaws.com -u admin -p
// Then you can merge both commands given above
// Do the same for 10.0.8.128/28
GRANT ALL PRIVILEGES ON bitnami_drupal.* TO 'bn_drupal'@'10.0.8.0/28' IDENTIFIED BY 'PASSWORD_IN_SETTINGS_PHP';

[OUTPUT]
Query OK, 0 rows affected (0.002 sec)
```

```sql
//validation
[INPUT]
SHOW GRANTS for 'bn_drupal'@'10.0.8.0/28';

[OUTPUT]
+--------------------------------------------------------------------------------------------------------------------+
| Grants for bn_drupal@10.0.8.0/28                                                                                   |
+--------------------------------------------------------------------------------------------------------------------+
| GRANT USAGE ON *.* TO `bn_drupal`@`10.0.8.0/28` IDENTIFIED BY PASSWORD '*774097D0FF922910DD5E38A8BE4E6886FD3CA240' |
| GRANT ALL PRIVILEGES ON `bitnami_drupal`.* TO `bn_drupal`@`10.0.8.0/28`                                            |
+--------------------------------------------------------------------------------------------------------------------+
2 rows in set (0.000 sec)
```

### Validate access (on the drupal instance)

```sql
[INPUT]
// mysql -h dbi-devopsteam[XX].xxxxxxxx.eu-west-3.rds.amazonaws.com -u bn_drupal -p
mysql -h dbi-devopsteam08.cshki92s4w5p.eu-west-3.rds.amazonaws.com -u bn_drupal -p   

[INPUT]
show databases;

[OUTPUT]
+--------------------+
| Database           |
+--------------------+
| bitnami_drupal     |
| information_schema |
+--------------------+
2 rows in set (0.001 sec)
```

* Repeat the procedure to enable the instance on subnet 2 to also talk to your RDS instance.
