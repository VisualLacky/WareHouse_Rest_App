# WareHouse_Rest_App
## Description

WareHouse training rest application by <b>Yurii Semyk.</b><br/>
This application was created as part of a test task implementation. <br/><br/>

<b>In a nutshell:</b><br/> Rest application with <b>persisting layer</b> (database). Interaction with the outside world is implemented through <b>JSON</b> files.
The application controls products in various stores. <b>CRUD</b> operations are available for entities.
<br/><br/>App can save and give information (documents) 
about events such as income, selling or moving products from store to store. In addition, there is an invoice with all the necessary additional information for each document.<br/>
When event is performed through API, all additional actions (such as persisting documents info in the databese or products/store updates) processed automatically.<br/>
<br/>API is fully documented via Swagger 2.0 and availiable for testing in local environment.<br/><br/>
The code was written in 7 days - some test still missing due to lack of time for this testing task.<br/>
Note: One of the requirements was not to use spring framework :)

<br/>DEPLOYMENT INSTRUCTIONS:<br/>
- Run SQL_BD_INIT_SCRIPT in database for data initiation<br/>
- Connect database in META-INF/context.xml<br/>
- Run application on a tomcat server (/WareHouseAppBE is a standart context path for app)<br/>
- You are good to go!<br/>

<br/><br/>You can test api via local link:<br/> 
< http://localhost:8080/WareHouseAppBE/swagger/index.html >

<br/><br/>
Technologies:  <br/>
<b>Java core</b><br/>
<b>Java EE</b> (servlets)<br/>
<b>JUnit, Mockito</b> (testing)<br/>
<b>PostgreSQL</b> (persisting layer)<br/>
<b>Swagger</b> (API specification)
