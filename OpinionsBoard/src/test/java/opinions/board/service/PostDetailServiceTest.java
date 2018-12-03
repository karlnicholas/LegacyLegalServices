package opinions.board.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.File;
import java.util.List;

import javax.ejb.EJB;

import org.hibernate.LazyInitializationException;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import opinions.board.model.BoardComment;
import opinions.board.model.BoardPost;
import opinions.board.model.BoardReply;

@RunWith(Arquillian.class)
public class PostDetailServiceTest {
	private static final String WEBAPP_SRC = "src/main/webapp";

    @Deployment
    public static Archive<?> createDeployment() {
        return ShrinkWrap.create(WebArchive.class, "test.war")
    		.addPackages(true, "opinions.board")
            .addAsResource("META-INF/persistence.xml")
            .addAsWebInfResource(new File(WEBAPP_SRC, "/WEB-INF/beans.xml"));
    }
    
    @EJB
    private PostListingService postListingService;
    @EJB
    private PostDetailService postDetailService;

    @Before
    public void cleanup() {
		// cleanup before test
		List<BoardPost> listings = postListingService.getBoardPosts(10);
		for ( BoardPost boardPost: listings ) {
			postListingService.deleteBoardPost(boardPost);
		}
    }

    @Test
	public void testDetailService() {
		BoardPost boardPost = new BoardPost();
		postListingService.createNewBoardPost(boardPost);

		List<BoardPost> listings = postListingService.getBoardPosts(3);
		assertNotNull("Board Listings NULL", listings);
		assertEquals("Listings size should equal 1", 1, listings.size());

		// get persisted boardPost 
		boardPost = listings.get(0);
		
		// create a comment
		BoardComment boardComment = new BoardComment();
		boardComment.setBoardPost(boardPost);
		postDetailService.createNewBoardComment(boardComment);
		
		// 
		List<BoardComment> comments = postDetailService.getBoardComments(boardPost, 3);
		assertNotNull("Board Comments NULL", comments);
		assertEquals("BoardComments size should equal 1", 1, comments.size());
		
		// create 3 comments
		boardComment = new BoardComment();
		boardComment.setBoardPost(boardPost);
		postDetailService.createNewBoardComment(boardComment);		
		boardComment = new BoardComment();
		boardComment.setBoardPost(boardPost);
		postDetailService.createNewBoardComment(boardComment);
		boardComment = new BoardComment();
		boardComment.setBoardPost(boardPost);
		postDetailService.createNewBoardComment(boardComment);

		// test limits
		comments = postDetailService.getBoardComments(boardPost, 3);
		assertNotNull("Board Comments NULL", comments);
		assertEquals("BoardComments size should equal 3", 3, comments.size());
		
		// test limits
		comments = postDetailService.getBoardComments(boardPost, 6);
		assertNotNull("Board Comments NULL", comments);
		assertEquals("BoardComments size should equal 4", 4, comments.size());

		// test update
		boardComment = comments.get(0);
		boardComment = postDetailService.updateBoardComment(boardComment);
		assertNotNull("Board Comment not null", boardComment);
		// make sure it wasn't inserted
		comments = postDetailService.getBoardComments(boardPost, 6);
		assertNotNull("Board Comments NULL", comments);
		assertEquals("BoardComments size should equal 4", 4, comments.size());
		
		// delete board Comment
		postDetailService.deleteBoardComment(boardComment);
		comments = postDetailService.getBoardComments(boardPost, 6);
		assertNotNull("Board Comments NULL", comments);
		assertEquals("BoardComments size should equal 3", 3, comments.size());
		
		// test BoardReplies
		boardComment = comments.get(1);
		BoardReply boardReply = new BoardReply();
		boardReply.setBoardComment(boardComment);
		postDetailService.createNewBoardReply(boardReply);
		
		// get a reply
		List<BoardReply> replies = postDetailService.getBoardReplies(boardComment, 3);
		assertNotNull("Board Replies not NULL", replies);
		assertEquals("BoardComments size should equal 1", 1, replies.size());
		
		boardReply = replies.get(0);
		postDetailService.deleteBoardReply(boardReply);
		// get a reply
		replies = postDetailService.getBoardReplies(boardComment, 3);
		assertNotNull("Board Comments NULL", replies);
		assertEquals("BoardComments size should equal 0", 0, replies.size());

		// test BoardReplies
		boardReply = new BoardReply();
		boardReply.setBoardComment(boardComment);
		postDetailService.createNewBoardReply(boardReply);

		boardReply = new BoardReply();
		boardReply.setBoardComment(boardComment);
		postDetailService.createNewBoardReply(boardReply);
		
		boardReply = new BoardReply();
		boardReply.setBoardComment(boardComment);
		postDetailService.createNewBoardReply(boardReply);

		boardReply = new BoardReply();
		boardReply.setBoardComment(boardComment);
		postDetailService.createNewBoardReply(boardReply);

		// test limits
		replies = postDetailService.getBoardReplies(boardComment, 3);
		assertNotNull("Board Comments NULL", replies);
		assertEquals("BoardComments size should equal 3", 3, replies.size());
		
		boardReply = replies.get(0);
		boardReply = postDetailService.updateBoardReply(boardReply);
		assertNotNull("Board Reply not null", boardReply);
		
		// test against constraint and orphaned boardReplies
		postDetailService.deleteBoardComment(boardComment);
		// test limits
		replies = postDetailService.getBoardReplies(boardComment, 3);
		assertNotNull("Board Comments NULL", replies);
		assertEquals("BoardComments size should equal 0", 0, replies.size());
		
		// delete board Post
		postListingService.deleteBoardPost(boardPost);
		// check against constraint and orphaned children
		comments = postDetailService.getBoardComments(boardPost, 6);
		assertNotNull("Board Comments NULL", comments);
		assertEquals("BoardComments size should equal 0", 0, comments.size());
   }

    @Test(expected=LazyInitializationException.class)
    public void testLazyFetch() {

		BoardPost boardPost = new BoardPost();
		postListingService.createNewBoardPost(boardPost);

		List<BoardPost> listings = postListingService.getBoardPosts(3);
		assertNotNull("Board Listings NULL", listings);
		assertEquals("Listings size should equal 1", 1, listings.size());

		// get persisted boardPost 
		boardPost = listings.get(0);

		BoardComment boardComment = new BoardComment();
		boardComment.setBoardPost(boardPost);
		postDetailService.createNewBoardComment(boardComment);

		List<BoardComment> comments = postDetailService.getBoardComments(boardPost, 3);
		assertNotNull("Board Listings NULL", comments);
		assertEquals("Listings size should equal 1", 1, comments.size());

		// check for LAZY Fetching
		comments.get(0).getBoardReplies().get(0);
	}
}