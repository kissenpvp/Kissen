-- This SQL script is responsible for setting up initial database and user configurations for our application.

-- The first line of the script is creating a new database named `kissen`.
-- The `IF NOT EXISTS` clause in the `CREATE DATABASE` statement is used to
-- prevent an error in case the database already exists. So running this
-- script multiple times doesn't cause an error, it ensures idempotent behaviour.
-- If the `kissen` database does not exist, this command creates it:
CREATE DATABASE IF NOT EXISTS kissen;
CREATE DATABASE IF NOT EXISTS test;

-- The second line of the script is granting all possible privileges for
-- database operations on the newly created `kissen` database to a new user,
-- also named `kissen`. The '%' symbol is a wildcard character that matches
-- any host. This means that the user can connect from anywhere.
-- Warning: In a production environment, it is recommended to replace '%'
-- with the specific hostname or IP address for security reasons.
-- Also note that using `GRANT ALL` gives wide range of permissions like
-- modification of table data and table structures, creation or dropping
-- of tables and databases, and other administrative operations. In a
-- real-world scenario, you'd want to be very deliberate about granting
-- only the necessary permissions to a user.
-- Note: The password 'kissen' is hardcoded here for simplicity.
-- In a production scenario you'll want to handle sensitive information
-- such as passwords in a secure manner:
GRANT ALL PRIVILEGES ON kissen.* TO 'kissen'@'%';
GRANT ALL PRIVILEGES ON test.* TO 'kissen'@'%';

-- The FLUSH PRIVILEGES statement is used in MySQL to reload the
-- privileges from the grant tables in the mysql database enabling
-- the changes to take effect without reloading or restarting mysql service.
-- This will make sure that our new user `kissen` can interact
-- with `kissen` database immediately after its creation:
FLUSH PRIVILEGES;
