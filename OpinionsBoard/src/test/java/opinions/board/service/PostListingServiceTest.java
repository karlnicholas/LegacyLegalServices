package opinions.board.service;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.junit.*;

import opinions.board.model.BoardPost;

public class PostListingServiceTest {
	
    protected static EntityManagerFactory emf;
    protected static EntityManager em;

    @BeforeClass
    public static void init() throws FileNotFoundException, SQLException {
        emf = Persistence.createEntityManagerFactory("mnf-pu-test");
        em = emf.createEntityManager();
    }

    @AfterClass
    public static void tearDown(){
        em.clear();
        em.close();
        emf.close();
    }

    private PostListingService postListingService;
	
	@Test
	public void testGetBoardPosts() {
	}

	@Test
	public void testCreateNewBoardPost() {
	}
	
	@Test
	public void deleteBoardPost() {
	}

}
