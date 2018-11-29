package opinions.board.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.FileNotFoundException;
import java.sql.SQLException;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.junit.*;

import opinions.board.model.BoardComment;
import opinions.board.model.BoardPost;

public class PostListingServiceTest {
    protected static EntityManagerFactory emf;
    protected static EntityManager em;
    protected static PostListingService postListingService;


    @BeforeClass
    public static void init() throws FileNotFoundException, SQLException {
        emf = Persistence.createEntityManagerFactory("mnf-pu-test");
        em = emf.createEntityManager();
        postListingService = new PostListingService(); 
    }

    @AfterClass
    public static void tearDown(){
        em.clear();
        em.close();
        emf.close();
    }
	
	@Test
	public void testListingService() {
		List<BoardPost> listings = postListingService.getBoardPosts(3);
		assertNotNull("Board Listings NULL", listings);
		assertEquals(listings.size(), 0);

		BoardPost boardPost = new BoardPost();
		postListingService.createNewBoardPost(boardPost);

		listings = postListingService.getBoardPosts(3);
		assertNotNull("Board Listings NULL", listings);
		assertEquals("Listings size should equal 1", listings.size(), 1);

		// check against LAZY Fetching
		boardPost = listings.get(0);
		List<BoardComment> comments = boardPost.getBoardComments();
		assertEquals("Comments size should equal 0", comments.size(), 0);
		
		
		
	}

}
