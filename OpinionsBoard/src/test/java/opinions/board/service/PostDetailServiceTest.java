package opinions.board.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.List;

import javax.ejb.EJB;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Test;
import org.junit.runner.RunWith;

import opinions.board.model.BoardComment;
import opinions.board.model.BoardPost;
import opinions.board.model.BoardReply;
import opinions.board.util.ImprovedNamingStrategy;
import opinions.board.util.Resources;

@RunWith(Arquillian.class)
public class PostDetailServiceTest {
	
    @Deployment
    public static Archive<?> createDeployment() {
        return ShrinkWrap.create(WebArchive.class, "test.war")
    		.addClasses(BoardPost.class, BoardComment.class, BoardReply.class, PostListingService.class, PostDetailService.class, ImprovedNamingStrategy.class, Resources.class)
            .addAsResource("META-INF/persistence.xml")
            .addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml");
    }
    
    @EJB
    private PostListingService postListingService;
    @EJB
    private PostDetailService postDetailService;

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
		postDetailService.getBoardPostDetails(boardPost);
		assertNotNull("Board Listings NULL", boardPost);
		assertEquals("BoardComments size should equal 1", 1, boardPost.getBoardComments().size());
	}

}