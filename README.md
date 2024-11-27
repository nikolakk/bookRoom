Project Cleaning Room Rest API

Project uses Java 11 , maven 3.9.5 and Spring Boot


1. in order to build the application and also run the tests use the following command
   `mvn clean install`
2. In order to run the project use the following command 

    `cd ../roomBooking java -jar target/booking-0.0.1-SNAPSHOT.jar`


In order to access the API implemented you can use the postman collection
(Cleaning Room.postman_collection.json) included in ..the robot/soapui folder.


Use Booking.postman_collection.json in order to test all the APIs created.

1. Get Bookings for room

url : http://localhost:8080/api/rooms/1/bookings?date=2024-11-26

Response : 

`[
{
"email": "john.doe@example.com",
"room": "Conference Room A",
"bookingDate": "2024-11-26",
"timeFrom": "11:30",
"timeTo": "12:36",
"bookingId": 1
},
{
"email": "john.doe@example.com",
"room": "Conference Room A",
"bookingDate": "2024-11-26",
"timeFrom": "13:30",
"timeTo": "14:36",
"bookingId": 2
}
]`



2. url : http://localhost:8080/api/create/booking

Request
`{
"roomId": 1,
"employeeEmail": "john.doe@example.com",
"bookingDate": "2024-11-26",
"timeFrom": "13:30",
"timeTo": "14:36"
}`


Response 

`{
"email": "john.doe@example.com",
"room": "Conference Room A",
"bookingDate": "2024-11-26",
"timeFrom": "13:30",
"timeTo": "14:36",
"bookingId": 1
}`

3. http://localhost:8080/api/booking/1
