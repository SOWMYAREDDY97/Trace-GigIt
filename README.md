# App Name:-  Trace-GigIt

## App Logo :- 
![Hosted image](https://github.com/SOWMYAREDDY97/TraceAndGigIt/blob/master/APP_logo.PNG "app logo")

### Purpose:
To book an appointment in any business areas, we usually contact them by phone or go there in person. In our everyday life, we often come across such issues. So, our main aim of this project is to make appointments online based on the location and available time slots of those business areas. Application has both perspectives like user can place an appointment and the service provider can accept or reject the appointment request based on their availability.

### Scope:
New user opens the application and register using his/her details. After registering, the user needs to give permission to location access. If the user is already registered, he/she needs to login using his/her credentials. Based on the location, for all users i.e. existing and new users the application displays nearby salons where an appointment can be made online by selecting particular saloon. The appointment is fixed once the user clicks the submit button. Later he/she can view the appointments which were booked by him/her. These appointments can be edited or deleted.The application displays the salons which were recently and mostly visited. User can go through his/her profile in settings where he/she can edit his/her credentials including his/her mobile number. If he/she wants to change password he/she gets a confirmation message to his/her  registered mail or mobile number and then after confirmation the password is updated.
Service provider starts with registering himself/herself  with his/her details and exact location and he/she needs to give permission to location access. Once he/she is registered, the business is visible to the nearest users to book the appointments. Once the user appointment is submitted then service provider can accept based on his/her availability. He/She can edit saloon profile and employee details and his/her availabilities. He/She has the ability to lock the appointment timings where there will be no possibility to book an appointment at that particular mentioned time. So that user can only select the appointment timings which were not locked by the service provider.
User can pay through online after the appointment  is confirmed..He/She has option to save his/her card details during payment for future use.

The application can be used within six miles of radius around the globe for users.It displays saloons to the user based on the radius.It can be used within a city with utmost fifty users. 


### Backend Setup
- download eclipse IDE for java developers
- download python 2.7 32 bit and install the pip package installer
- make sure that environment variables are set
- install mysql server database and set environmental variables for it
- from the project directory install django and south and requests and mysqlconnecter
- follow this article to enable the python in eclipse [enabling the python on eclipse](https://jimstechblog.wordpress.com/2014/10/28/setting-up-eclipse-for-django-on-windows/)
- make sure that a database with name traceandgigit is created
- update the database credentials in settings.py 
- run the project as django project so that you can see server running in console 

