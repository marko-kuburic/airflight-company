-- Initialize MySQL with mysql_native_password authentication
-- This ensures consistent behavior across Linux and Windows Docker environments

-- Set the user authentication plugin to mysql_native_password
ALTER USER 'aircompany'@'%' IDENTIFIED WITH mysql_native_password BY 'aircompany123';

-- Flush privileges to apply changes
FLUSH PRIVILEGES;
