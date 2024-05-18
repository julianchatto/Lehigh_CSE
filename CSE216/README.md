# CSE 216 - Software Engineering Tutorials
This is an individual student repository. It is intended for use during phase 0.
# CSE 216 - Software Engineering Tutorials
This is an individual student repository. It is intended for use during phase 0.

## Details
- Semester: Spring 2024
- Team Number: 03
- Team Name: ducks
- Bitbucket Repository: https://bitbucket.org/sml3/cse216_sp24_team_03
- Jira: [link](https://cse216-24sp-jel326.atlassian.net/jira/software/projects/T3P1/boards/2?assignee=unassigned&atlOrigin=eyJpIjoiMjU1OTFmYmRlYTcyNDE2MGIwOWIyMmVmYTBmYWE5OWIiLCJwIjoiaiJ9)
- Dokku: https://team-ducks.dokku.cse.lehigh.edu/
- Description: Website for employees to post short ideas (messages), and other employees can like (and remove a like from) that idea.
Team Members & Roles:
* Project Manger: <Jeffrey Lin, jel326@lehigh.edu>
* Web: <Anya Kumar, ank726@lehigh.edu>    
* Admin: <Julian Chattopadhyay, juc226@lehigh.edu>    
* Backend: <Matthew Martinez, mamf26@lehigh.edu>

### User Stories
	* As a website visitor I want to view posts so that I can learn about my company
		* Test: Ensure that a user can see posts in the database
	* As a website visitor I want to like posts so that I can learn share my interest
		* Test: Ensure that a user can like a post
	* As a website visitor I want to unlike posts so that I can change my interest in a post
		* Test: Ensure that a user can unlike a post
	* As a website visitor I want to create a post so that I can share what I am thinking about
		* Test: Ensure that a user can create a post
	* As a website visitor I want to delete a post so that I can unshare what I am thinking about
		* Test: Ensure that a user can delete a post
### Admin Stories
	* As an admin I want to disable users so that I can prevent unauthorized users
		* Test: Ensure that an admin can delete unwanted users
	* As an admin I want to delete posts so that I can delete bad content 
		* Test: Ensure that an admin can delete unwanted posts
### Tests
	* Backend 
		* Test that all routes are functional in all cases (post, put, delete, etc)
		* Ensure that the correct data is being sent
	* Admin
		*  Posts can be deleted
		* Users can be deleted
	* Web
		* Post can be created
		* Post can be liked and unliked
		* The post can be viewed
		* Can scroll on the webpage
	* Mobile 
		* N/A - team of 4
### [State Machine](https://lucid.app/lucidchart/1a98be0a-6cd4-48b1-bc2b-38ac3bc31c71/edit?viewport_loc=332%2C313%2C2115%2C1158%2C0_0&invitationId=inv_7b73268a-47e1-4abb-93ae-66ddc6420fc4)

### [System Drawing](https://lucid.app/lucidchart/678b3ec2-c09f-40ce-a6f1-1f7b20b65811/edit?viewport_loc=-1113%2C-48%2C1738%2C952%2C0_0&invitationId=inv_add27b1c-0529-482c-abae-943bccb71834)

### [ERD](https://lucid.app/lucidchart/dba15134-4c9a-4ce9-8be7-42798d359210/edit?viewport_loc=-169%2C-29%2C1905%2C871%2C0_0&invitationId=inv_a30eab99-565e-4581-a662-d0babc26dae7)

### [Mock UI](https://docs.google.com/presentation/d/1hpqKdG31cBnP7fCR4QiHCPF2tCNnR99fqpi_vC1-LFQ/edit#slide=id.p)

### [Backend/Dokku](https://team-ducks.dokku.cse.lehigh.edu/)


### Listing of Routes with Purpose,Data Formats, ERD: 
- Posting: POST route for adding a new post with post.
- Liking posts (post-side): GET route for the get to like post, provided by Spark.
- Liking posts (user-side): POST route for giving a message a like. 
- Un-liking posts: DELETE route for removing a like from the post.
- Deleting post: DELETE route for removing a post from the DataStore, a post.
- Format of the passing of any Data: All will be done through JSON format.

