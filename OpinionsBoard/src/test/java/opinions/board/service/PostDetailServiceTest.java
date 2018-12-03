package opinions.board.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.File;
import java.util.List;

import javax.ejb.EJB;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Test;
import org.junit.runner.RunWith;

import opinions.board.model.BoardComment;
import opinions.board.model.BoardPost;

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
		postDetailService.getBoardPostDetail(boardPost);
		assertNotNull("Board Listings NULL", boardPost);
		assertEquals("BoardComments size should equal 1", 1, boardPost.getBoardComments().size());
	}

}