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
import org.junit.*;
import org.junit.runner.RunWith;

import opinions.board.model.BoardPost;

@RunWith(Arquillian.class)
public class PostListingServiceTest {
	
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

    @Test
	public void testListingService() {
		List<BoardPost> listings = postListingService.getBoardPosts(3);
		assertNotNull("Board Listings NULL", listings);
		assertEquals(listings.size(), 0);

		BoardPost boardPost = new BoardPost();
		postListingService.createNewBoardPost(boardPost);

		listings = postListingService.getBoardPosts(3);
		assertNotNull("Board Listings NULL", listings);
		assertEquals("Listings size should equal 1", 1, listings.size());

		// check for limits
		boardPost = new BoardPost();
		postListingService.createNewBoardPost(boardPost);
		boardPost = new BoardPost();
		postListingService.createNewBoardPost(boardPost);

		// exactly 3?
		listings = postListingService.getBoardPosts(3);
		assertNotNull("Board Listings NULL", listings);
		assertEquals("Listings size should equal 3", 3, listings.size());

		// and still 3?
		boardPost = new BoardPost();
		postListingService.createNewBoardPost(boardPost);

		listings = postListingService.getBoardPosts(3);
		assertNotNull("Board Listings NULL", listings);
		assertEquals("Listings size should equal 3", 3 ,listings.size());

		// get more results
		listings = postListingService.getBoardPosts(6);
		assertNotNull("Board Listings NULL", listings);
		assertEquals("Listings size should equal 4", 4 ,listings.size());

		// delete the last post
		postListingService.deleteBoardPost(listings.get(3));
		listings = postListingService.getBoardPosts(3);
		assertNotNull("Board Listings NULL", listings);
		assertEquals("Listings size should equal 3", 3 ,listings.size());

	}

}
