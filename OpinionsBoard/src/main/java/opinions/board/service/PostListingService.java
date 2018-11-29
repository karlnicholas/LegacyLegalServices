package opinions.board.service;

import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;

import opinions.board.model.*;

@Stateless
public class PostListingService {
	@Inject private EntityManager em;
	
	public List<BoardPost> getBoardPosts(long count) {
		return null;
	}
	public void createNewBoardPost(BoardPost boardPost) {
		em.persist(boardPost);
	}
	
	public BoardPost deleteBoardPost(BoardPost boardPost) {
		return null;
	}
	
}
