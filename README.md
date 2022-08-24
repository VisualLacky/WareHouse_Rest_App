## WareHouse_Rest_App

WareHouse training rest application by <b>Yurii Semyk.</b><br/>
This application was created as part of a test task implementation.<br/><br/>



## In a nutshell
Rest application with <b>persisting layer</b> (database) and running on <b>Java 8</b>. Interaction with the outside world is implemented through <b>JSON</b> files.
The application controls products in various stores. <b>CRUD</b> operations are available for entities.
<br/><br/>App can save and give information (documents) 
about events such as income, selling or moving products from store to store. In addition, there is an invoice with all the necessary additional information for each document.<br/>
When event is performed through API, all additional actions (such as persisting documents info in the databese or products/store updates) processed automatically.<br/>
<br/>API is fully documented via <b>Swagger 2.0</b> and availiable for testing in local environment.<br/><br/>
The code was written in one week - some test still missing due to lack of time for this testing task.<br/>
Note: One of the requirements was not to use spring framework, so application is based on plain <b>servlets</b> :)<br/><br/>


## Deployment instructions
- Run <b>SQL_BD_INIT_SCRIPT</b> in database to initialize data<br/>
- Connect database in <b>META-INF/context.xml</b><br/>
- Run application on a tomcat server ( <b>/WareHouseAppBE</b> is a standart context path for this app)<br/>
- You are good to go!<br/><br/>


## API documentation
You can view and test api via local link on a running server:<br/> 
<b><</b> http://localhost:8080/WareHouseAppBE/swagger/index.html <b>></b><br/><br/>


## Technologies
<b>Java core</b><br/>
<b>Java EE</b> (servlets)<br/>
<b>JUnit, Mockito</b> (testing)<br/>
<b>PostgreSQL</b> (persisting layer)<br/>
<b>Swagger</b> (API specification)
