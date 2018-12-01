package opinions.board.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.util.List;

import javax.ejb.EJB;
import javax.enterprise.inject.Produces;
import javax.persistence.EntityManager;
import javax.persistence.Persistence;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.*;
import org.junit.runner.RunWith;

import opinions.board.model.BoardComment;
import opinions.board.model.BoardPost;

@RunWith(Arquillian.class)
public class PostListingServiceTest {
    @Deployment
    public static Archive<?> createDeployment() {
        return ShrinkWrap.create(WebArchive.class, "test.war")
    		.addClasses(BoardPost.class, BoardComment.class, PostListingService.class)
            .addAsResource("META-INF/persistence.xml")
            .addAsWebInfResource("jbossas-ds.xml")
            .addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml");
    }
    
    @Produces
    EntityManager getEm() {
    	return Persistence.createEntityManagerFactory("test").createEntityManager();

    }
    // tests go here
    
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

		// check against LAZY Fetching
		boardPost = listings.get(0);
		List<BoardComment> comments = boardPost.getBoardComments();
		assertNull("Board Listings should be NULL", comments);
		
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

	}

}
