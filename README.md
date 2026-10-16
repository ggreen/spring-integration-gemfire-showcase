

Start GemFire

```start
deployments/local/scripts/gemfire/start.sh
```


```shell
$GEMFIRE_HOME/bin/gfsh
```


Create Region

```shell
$GEMFIRE_HOME/bin/gfsh -e "connect" -e "connect"  -e "create region --name=Employee --type=PARTITION --enable-statistics=true"
```

Start mySQL

```shell
podman run -it --rm \
  --name mysql \
  --publish 3306:3306 \
  -e MYSQL_ROOT_PASSWORD='CHANGEME' \
  -e MYSQL_DATABASE='appdb' \
  mysql:latest
```


Start MySQL CLI

```shell
podman exec -it mysql mysql -u root -pCHANGEME appdb
```

Create Schema and Table 

```sql
-- Create schema if it does not exist
CREATE SCHEMA IF NOT EXISTS company;

-- Drop table if it already exists in the company schema
DROP TABLE IF EXISTS company.employees;

-- Create the employees table in the company schema
CREATE TABLE company.employees (
                                  id INT NOT NULL AUTO_INCREMENT,
                                  first_name VARCHAR(50) NOT NULL,
                                  last_name VARCHAR(50) NOT NULL,
                                  email VARCHAR(100) NOT NULL,
                                  department VARCHAR(50) NOT NULL,
                                  status INT NOT NULL DEFAULT 2,
                                  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                                  updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                                  PRIMARY KEY (id),
                                  INDEX idx_employees_status (status) -- Added index to optimize the adapter's polling query
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
```


```sql
-- Clear old test records
TRUNCATE TABLE company.employees;

INSERT INTO company.employees (first_name, last_name, email, department, status) VALUES
('Alice', 'Smith', 'alice.smith@example.com', 'Engineering', 2),
('Bob', 'Jones', 'bob.jones@example.com', 'Marketing', 2),
('Charlie', 'Brown', 'charlie.brown@example.com', 'Finance', 2);

INSERT INTO company.employees (first_name, last_name, email, department, status) VALUES
('Diana', 'Prince', 'diana.prince@example.com', 'HR', 2),         
('Evan', 'Wright', 'evan.wright@example.com', 'Sales', 2),       
('Fiona', 'Gallagher', 'fiona.g@example.com', 'Operations', 2);  

-- Verify inserted data
SELECT id, first_name, last_name, email, department, status, created_at
FROM company.employees
WHERE status = 2;
```


```sql
update company.employees set status = 2;
```

```gfsh
query --query="select value from /Employee.entries"
```