- Define the data model for MongoDB (direct table-collection mapping is not the best idea).
  
  Data model for MongoDB is under ua.epam.mishchenko.ticketbooking.model. Below sreenshot of event document (this is how it is looks like after migration). Ticket, User and UserAccount was designed as embedded object of event document.

  ![model](https://github.com/user-attachments/assets/2bb2ebf8-ac93-46e1-81f1-e00c6f7ae65e)

  
- Write data migration job (via SQL and MongoDriver operations).
  
  Firstly, Postgres tables are created based on script located in database folder. Then data are inserted using script from src/test/resources/sql/insert-data.sql

  ![postgres](https://github.com/user-attachments/assets/38d352f6-2ac3-408b-81bd-d3e2212db8cf)

  Migration will be initialized once the application starts and properties.migration_enabled param is set to true.
  After completion of migration process the data in mongo looks as on below screenshot.
  
  ![mongo](https://github.com/user-attachments/assets/2f9376d0-2a3b-489f-96e9-2be023d67b58)

- Use an aggregation mechanism to get grouped results from the database.

  Aggegatiom mechanism was implementes in UserAccountCustomMongoRepository.
