For a "Message Board"

Comments and threads in the style of YouTube where comments can be made and replied to but there is no deeper hierarchy of replies.

Question: Is it better to have Comment -> Reply -> Response ?
Question: What about "Post" with Comments, ... 
Question: Is a Opinion Report a "Post"? Yes.
Question: What kind of Posts can users make? Links? Image posts with Imager or the like? Twitter? Instagram?
Question: What about Sharing? No!!!
Question: Strictly lawyers. 
Question: WHat search capabilities for posts should there be?
Question: What is MVP? 

Functional Section:

Use Cases:

    User Actor
      Login or Logout
      Browse Posts
      Open Post
      Create New Post
      Browse Comments for Post
      Create New Comment for Post
      Reply to Comment in Post
  
Use Case Descriptions:

 * **Login or Logout**: A user selects the accounts tab and enters email for account. Password requested as per Login/Logout use case.
 * **Browse Posts**: : A limited list of Posts is presented with "Next" and "Previous" page buttons at the bottom. Each Post  is attributed to an author. A post is shown in shortened format. The number of comments in shown. How long ago it was submitted is shown.  
 * **Open Post**: : Clicking on a post opens a page. The post is shown in full.
 * **Create New Post**: A create new post is in the top of the navigation bar. Clicking opens the create post page. The allows for input of comments (and links?). Possible category selection.  
 * **Browse Comments for Post**: A limited list of Comments is presented with a "Show More" button at the bottom. Each comment is attributed to an author (?) and a show replies button is associated with comments that have replies. If a comment is long then only the first three lines are displayed with a read more indicator displayed. Clicking on "read more" show the full comment.
 * **Create new comment for POst**: A "Add New Comment" input area is always at the top of the Browse Comments Page with the prompt "add comment" is displayed. Any logged in user can add a comment. If the user is not logged in then user the input is grayed out and a notice that user must login first is displayed. A new comment is displayed at the top of the list.
 * **Reply to Comment in Post**: Each comment has a reply button. Replying to a comment brings up a reply input area. A user's reply goes to the bottom of the replies list. 
         
CRC Cards:

    Post Class:
        Responsibilities: Hold Post details and User and List of Comments.
        Collaborators: User, Comment.
        Attributes: User, Date, List of Comments
        
    PostController Class:
        Responsibilities: Hold paged list of posts for for session. Accept prev, next, first, last inputs and scroll paged list. Accept select a post and pass control to CommentController class
        Collaborators: Post. 
        Attributes: Page.
        
    CreateNewPostController class:
        Responsibilities: Present and validate a form for creating a new post.
        Collaborators:  
        
    Post Category Class:
        Responsibilities: Hold categories that posts can be created into. Probably not a first version feature.
        Collaborators: None.
        Attribute: Set of Categories and descriptions.

    Comment Class: 
        Responsibilities: Hold Comment and User and List of Replies.
        Collaborators: User, Reply
        Attributes: Comment, User, List<Reply>, Date.
        
    CommentController class: 
   
    Reply Class:
        Responsibilities: Hold User and Reply to Comment.
        Collaborators: User, Comment
        Attributes: Reply, User, Date, Comment
        
Behavioral Section:


