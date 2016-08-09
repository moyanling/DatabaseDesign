# DatabaseDesign
<br>A command line based simple database.
<br>
<br>--help or -h:
<br>usage:
<br> &emsp;-a,--all    Show all supported Database commands.
<br> &emsp;-h,--help   Help description.
<br> &emsp;-r,--run    Start to run database.
<br>
<br>--all or -a:
<br>Supported commands: 
<br>	&emsp;SHOW SCHEMAS; 
<br>		&emsp;&emsp;- Displays all schemas defined in your database.
<br>	&emsp;DELETE SCHEMA; 
<br>		&emsp;&emsp;- Remove the current activated schema from the archive, and all of its containing tables. Use it carefully.
<br>	&emsp;USE &lt;SCHEMA_NAME&gt;; 
<br>		&emsp;&emsp;- Chooses a schema.
<br>	&emsp;CREATE SCHEMA &lt;SCHEMA_NAME&gt;; 
<br>		&emsp;&emsp;- Creates a new schema to hold tables.
<br>	&emsp;SHOW TABLES; 
<br>		&emsp;&emsp;- Displays all tables in the currently chosen schema.
<br>	&emsp;DROP TABLE &lt;TABLE_NAME&gt;; 
<br>		&emsp;&emsp;- Remove a table in current schema, and all of its contained data.
<br>	&emsp;CREATE TABLE &lt;TABLE_NAME> (...); 
<br>		&emsp;&emsp;- Creates a new table schema, i.e. a new empty table.
<br>	&emsp;INSERT INTO TABLE ... ; 
<br>		&emsp;&emsp;- Inserts a row/record into a table.
<br>	&emsp;SELECT ... ; 
<br>		&emsp;&emsp;- SELECT &lt;FULL-QUALIFIED-JAVA-BEAN-CLASS-NAME&gt; FROM-WHERE style query. This will match the columns to bean properties.
<br>	&emsp;EXIT; 
<br>		&emsp;&emsp;- Cleanly exits the program and saves all table and index information in non-volatile files.
<br>
<br>Supported Data Types: 
<br>	&emsp;BYTE
<br>		&emsp;&emsp;- A signed two’s compliment byte: range -127 to 127
<br>	&emsp;INT
<br>		&emsp;&emsp;- A signed two’s compliment integer: range -2147483647 to 2147483647
<br>	&emsp;LONG
<br>		&emsp;&emsp;- A signed two’s compliment long integer: range –2^63 + 1 to 2^63 – 1
<br>	&emsp;VARCHAR
<br>		&emsp;&emsp;- A variable length ASCII string with a maximum of n characters. n may be 0-127. Each instance is prepended with an unsigned byte indicating the number of ASCII characters that follow.
<br>
<br>Supported constraints: 
<br>	&emsp;PRIMARY KEY
<br>		&emsp;&emsp;- Assign the column as the primary key.
<br>	&emsp;NOT NULL
<br>		&emsp;&emsp;- Indicate that NULL values are not permitted for a particular column.
<br>
<br>NOTE:
<br>	&emsp;1. All database commands should end with ';'. 
<br>	&emsp;2. One database command should be less than ten lines. 
<br>	&emsp;3. Please use letter, number, underscore and dash only. And please start with letter and don't end with underscore or dash for naming conventions.
<br>
<br>
<br>--run or -r EXAMPLE:
<br>See TestRunExample.java
